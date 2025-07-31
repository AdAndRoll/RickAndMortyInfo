package com.example.data.remote.mediator

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.example.data.local.database.CharacterDatabase
import com.example.data.local.datasources.CharacterLocalDataSource
import com.example.data.local.entity.CharacterEntity
import com.example.data.local.entity.RemoteKeyEntity
import com.example.data.mappers.toCharacterEntity
import com.example.data.remote.datasources.CharacterRemoteDataSource
import com.example.data.utils.NetworkResult
import com.example.domain.model.CharacterFilter
import java.io.IOException
import java.util.concurrent.TimeUnit
import retrofit2.HttpException

/**
 * [RemoteMediator] для синхронизации данных о персонажах между удаленным API и локальной базой данных Room.
 * Отвечает за загрузку данных из сети, их кэширование и управление ключами пагинации.
 */
@OptIn(ExperimentalPagingApi::class) // Необходима для RemoteMediator
class CharacterRemoteMediator(
    private val characterRemoteDataSource: CharacterRemoteDataSource,
    private val characterLocalDataSource: CharacterLocalDataSource,
    private val characterDatabase: CharacterDatabase,
    private val filter: CharacterFilter // Параметры фильтрации
) : RemoteMediator<Int, CharacterEntity>() { // <Ключ пагинации (страница), Тип данных в локальной БД>

    // Метка времени последней загрузки для кэширования (можно расширить)
    private var lastUpdated = 0L

    // Константа для "свежести" кэша (например, 30 минут)
    private val CACHE_TIMEOUT = TimeUnit.MINUTES.toMillis(30)

    /**
     * Основной метод [RemoteMediator], который вызывается Paging 3
     * для загрузки данных из сети и их кэширования.
     *
     * @param state Текущее состояние пагинации.
     * @param pager PagingSource (локальная база данных), управляемая Paging 3.
     */
    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, CharacterEntity>
    ): MediatorResult {
        return try {
            val page = when (loadType) {
                LoadType.REFRESH -> {
                    // При первом запуске или refresh.
                    // Пытаемся найти ближайший ключ в базе для определения начальной страницы.
                    // Или просто начинаем с 1, если кэш устарел/пуст.
                    val remoteKeys = characterLocalDataSource.getRemoteKeyByCharacterId(state.anchorPosition ?: -1)
                    if (remoteKeys != null) {
                        remoteKeys.nextKey ?: 1 // Если есть ключ, используем его, иначе страница 1
                    } else {
                        1 // Начальная страница
                    }
                }
                LoadType.PREPEND -> {
                    // Загрузка предыдущих данных (если поддерживается API).
                    // Нам нужен prevKey первого элемента.
                    val remoteKeys = getRemoteKeyForFirstItem(state)
                    // Если prevKey null, значит, больше нет предыдущих страниц.
                    remoteKeys?.prevKey
                        ?: return MediatorResult.Success(endOfPaginationReached = true)
                }
                LoadType.APPEND -> {
                    // Загрузка последующих данных.
                    // Нам нужен nextKey последнего элемента.
                    val remoteKeys = getRemoteKeyForLastItem(state)
                    // Если nextKey null, значит, достигнут конец пагинации.
                    remoteKeys?.nextKey
                        ?: return MediatorResult.Success(endOfPaginationReached = true)
                }
            }

            // Проверка кэша на свежесть только для LoadType.REFRESH
            if (loadType == LoadType.REFRESH && System.currentTimeMillis() - lastUpdated < CACHE_TIMEOUT) {
                return MediatorResult.Success(endOfPaginationReached = false) // Данные свежие, не загружаем
            }


            // Выполняем сетевой запрос с использованием параметров фильтрации
            // и вычисленной страницы.
            val apiResult = characterRemoteDataSource.getCharacters(
                page = page,
                name = filter.name,
                status = filter.status,
                species = filter.species,
                gender = filter.gender
            )

            val endOfPaginationReached: Boolean
            when (apiResult) {
                is NetworkResult.Success -> {
                    val characters = apiResult.data.results
                    endOfPaginationReached = characters.isEmpty() || apiResult.data.info.next == null

                    // Выполняем все операции с БД в одной транзакции
                    characterDatabase.withTransaction {
                        // Очищаем базу данных только при LoadType.REFRESH
                        if (loadType == LoadType.REFRESH) {
                            characterLocalDataSource.clearAllCharacters()
                            characterLocalDataSource.clearAllRemoteKeys()
                            lastUpdated = System.currentTimeMillis() // Обновляем время последней загрузки
                        }

                        // Вычисляем nextKey для каждой вставленной RemoteKeyEntity
                        val nextPageIndex = if (endOfPaginationReached) null else page + 1
                        val prevPageIndex = if (page == 1) null else page - 1

                        val remoteKeys = characters.map { characterDto ->
                            RemoteKeyEntity(
                                characterId = characterDto.id,
                                prevKey = prevPageIndex,
                                nextKey = nextPageIndex
                            )
                        }

                        // Вставляем RemoteKeys и CharacterEntities
                        characterLocalDataSource.insertAllRemoteKeys(remoteKeys)
                        characterLocalDataSource.insertCharacters(characters.map { it.toCharacterEntity() })
                    }
                }
                is NetworkResult.Error -> {
                    // Возвращаем ошибку, которая будет передана Paging 3
                    return MediatorResult.Error(apiResult.exception)
                }
            }

            // Возвращаем Success, указывая, достигнут ли конец пагинации
            MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)

        } catch (e: IOException) {
            // Ошибки сети
            MediatorResult.Error(e)
        } catch (e: HttpException) {
            // HTTP ошибки
            MediatorResult.Error(e)
        }
    }

    /**
     * Вспомогательный метод для получения [RemoteKeyEntity] для первого элемента в [PagingState].
     */
    private suspend fun getRemoteKeyForFirstItem(state: PagingState<Int, CharacterEntity>): RemoteKeyEntity? {
        // Получаем первый видимый элемент в PagingState
        return state.pages.firstOrNull { it.data.isNotEmpty() }?.data?.firstOrNull()
            ?.let { character ->
                // Используем ID этого элемента для получения соответствующего RemoteKeyEntity
                characterLocalDataSource.getRemoteKeyByCharacterId(character.id)
            }
    }

    /**
     * Вспомогательный метод для получения [RemoteKeyEntity] для последнего элемента в [PagingState].
     */
    private suspend fun getRemoteKeyForLastItem(state: PagingState<Int, CharacterEntity>): RemoteKeyEntity? {
        // Получаем последний видимый элемент в PagingState
        return state.pages.lastOrNull { it.data.isNotEmpty() }?.data?.lastOrNull()
            ?.let { character ->
                // Используем ID этого элемента для получения соответствующего RemoteKeyEntity
                characterLocalDataSource.getRemoteKeyByCharacterId(character.id)
            }
    }

    // Этот метод определяет, нужно ли запускать `load()` при каждом доступе к PagingSource
    // Для более агрессивного кэширования можно вернуть true всегда.
    // В данном случае, мы хотим, чтобы при первом запуске (Refresh)
    // или при сбросе данных, Room перезапросил данные через RemoteMediator.
    // А если данные в кэше есть и они свежие, то не загружать из сети.
    override suspend fun initialize(): InitializeAction {
        // Если кэш слишком старый (например, > 30 минут), то RemoteMediator
        // принудительно вызовет LoadType.REFRESH при первом запросе данных.
        return if (System.currentTimeMillis() - lastUpdated >= CACHE_TIMEOUT) {
            // Данные устарели, требуется полный refresh (LoadType.REFRESH)
            InitializeAction.LAUNCH_INITIAL_REFRESH
        } else {
            // Данные свежие, можно использовать кэш, пока не потребуется LoadType.APPEND/PREPEND
            InitializeAction.SKIP_INITIAL_REFRESH
        }
    }
}