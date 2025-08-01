// app/src/main/java/com/example/data/remote/mediator/CharacterRemoteMediator.kt
package com.example.data.remote.mediator

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction // Важно для атомарных операций
import com.example.data.local.database.CharacterDatabase
import com.example.data.local.datasources.CharacterLocalDataSource
import com.example.data.local.entity.CharacterEntity
import com.example.data.local.entity.RemoteKeyEntity // Убедитесь, что это НОВАЯ RemoteKeyEntity
import com.example.data.mappers.toCharacterEntity
import com.example.data.remote.datasources.CharacterRemoteDataSource
import com.example.data.utils.NetworkResult
import com.example.domain.model.CharacterFilter
import java.io.IOException
import java.util.concurrent.TimeUnit
import retrofit2.HttpException

@OptIn(ExperimentalPagingApi::class)
class CharacterRemoteMediator(
    private val characterRemoteDataSource: CharacterRemoteDataSource,
    private val characterLocalDataSource: CharacterLocalDataSource,
    private val characterDatabase: CharacterDatabase,
    private val filter: CharacterFilter
) : RemoteMediator<Int, CharacterEntity>() {

    private val CACHE_TIMEOUT = TimeUnit.MINUTES.toMillis(30) // 30 минут для свежести кэша

    /**
     * Определяет, следует ли запускать полный REFRESH данных из сети.
     * Вызывается один раз при инициализации Paging.
     */
    override suspend fun initialize(): InitializeAction {
        // Получаем время последнего обновления из персистентного хранилища (RemoteKeyEntity)
        val lastUpdateTime = characterLocalDataSource.getRemoteKey()?.createdAt ?: 0L
        val now = System.currentTimeMillis()

        return if (now - lastUpdateTime >= CACHE_TIMEOUT) {
            // Кэш устарел, или его нет: требуется полный refresh.
            // Paging Library вызовет load(LoadType.REFRESH, ...)
            InitializeAction.LAUNCH_INITIAL_REFRESH
        } else {
            // Кэш свежий: можно использовать данные из БД.
            // Paging Library будет ждать, пока потребуется LoadType.APPEND/PREPEND.
            InitializeAction.SKIP_INITIAL_REFRESH
        }
    }

    /**
     * Основной метод, который вызывается Paging 3 для загрузки данных из сети и их кэширования.
     */
    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, CharacterEntity>
    ): MediatorResult {
        return try {
            val currentPage: Int

            when (loadType) {
                LoadType.REFRESH -> {
                    // При REFRESH всегда начинаем с первой страницы (1).
                    // Это важно для сброса состояния пагинации, например, при применении нового фильтра.
                    currentPage = 1
                }
                LoadType.PREPEND -> {
                    // API Rick and Morty не поддерживает эффективную прокрутку вверх (prev_page).
                    // Просто сообщаем, что предыдущих данных нет.
                    return MediatorResult.Success(endOfPaginationReached = true)
                }
                LoadType.APPEND -> {
                    // Для APPEND получаем 'nextKey' из нашей единственной RemoteKeyEntity.
                    val remoteKey = characterLocalDataSource.getRemoteKey()
                    val nextKey = remoteKey?.nextKey

                    // Если nextKey null, значит, мы достигли конца пагинации на предыдущем запросе,
                    // либо это первый запуск, и REFRESH не был запущен (из-за SKIP_INITIAL_REFRESH).
                    if (nextKey == null) {
                        return MediatorResult.Success(endOfPaginationReached = true)
                    }
                    currentPage = nextKey
                }
            }

            // Выполняем сетевой запрос к API
            val apiResult = characterRemoteDataSource.getCharacters(
                page = currentPage,
                name = filter.name,
                status = filter.status,
                species = filter.species,
                gender = filter.gender
            )

            // Обрабатываем результат сетевого запроса
            val endOfPaginationReached: Boolean
            when (apiResult) {
                is NetworkResult.Success -> {
                    val characters = apiResult.data.results
                    // Определяем, достигнут ли конец пагинации по полю info.next
                    endOfPaginationReached = apiResult.data.info.next == null

                    characterDatabase.withTransaction {
                        // При LoadType.REFRESH:
                        // 1. Очищаем все существующие персонажи в локальной БД.
                        // 2. Очищаем единственную запись RemoteKeyEntity.
                        // Это гарантирует, что мы начинаем с чистого листа.
                        if (loadType == LoadType.REFRESH) {
                            characterLocalDataSource.clearAllCharacters()
                            characterLocalDataSource.clearAllRemoteKeys()
                        }

                        // Вставляем или обновляем единственную RemoteKeyEntity для текущего состояния пагинации.
                        val newRemoteKey = RemoteKeyEntity(
                            id = 0, // Всегда используем ID 0
                            prevKey = if (currentPage == 1) null else currentPage - 1,
                            nextKey = if (endOfPaginationReached) null else currentPage + 1,
                            createdAt = System.currentTimeMillis() // Обновляем время кэширования
                        )
                        characterLocalDataSource.insertRemoteKey(newRemoteKey)

                        // Вставляем полученных персонажей в локальную БД.
                        characterLocalDataSource.insertCharacters(characters.map { it.toCharacterEntity() })
                    }
                }
                is NetworkResult.Error -> {
                    // Возвращаем ошибку, которая будет передана Paging 3.
                    return MediatorResult.Error(apiResult.exception)
                }
            }
            // Возвращаем Success, указывая, достигнут ли конец пагинации.
            MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)

        } catch (e: IOException) {
            // Ошибки сети (например, нет интернета)
            MediatorResult.Error(e)
        } catch (e: HttpException) {
            // HTTP ошибки (например, 404, 500)
            MediatorResult.Error(e)
        } catch (e: Exception) {
            // Любые другие неожиданные исключения
            MediatorResult.Error(e)
        }
    }
}