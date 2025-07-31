// data/repository/CharacterRepositoryImpl.kt
package com.example.data.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.example.data.local.database.CharacterDatabase
import com.example.data.local.datasources.CharacterLocalDataSource
import com.example.data.mappers.toCharacter
import com.example.data.remote.datasources.CharacterRemoteDataSource
import com.example.data.remote.mediator.CharacterRemoteMediator
import com.example.domain.model.RMCharacter
import com.example.domain.model.CharacterFilter
import com.example.domain.repository.CharacterRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * Реализация репозитория для работы с данными о персонажах.
 * Координирует получение данных из удаленного и локального источников,
 * используя Paging 3 и RemoteMediator для эффективной пагинации и кэширования.
 *
 * @param characterRemoteDataSource Удаленный источник данных для сетевых запросов.
 * @param characterLocalDataSource Локальный источник данных для работы с Room.
 * @param characterDatabase Экземпляр базы данных Room, необходимый для RemoteMediator.
 */
@OptIn(ExperimentalPagingApi::class)
class CharacterRepositoryImpl @Inject constructor(
    private val characterRemoteDataSource: CharacterRemoteDataSource,
    private val characterLocalDataSource: CharacterLocalDataSource,
    private val characterDatabase: CharacterDatabase
) : CharacterRepository {

    override fun getCharacters(
        filter: CharacterFilter
    ): Flow<PagingData<RMCharacter>> {
        return Pager(
            config = PagingConfig(
                pageSize = 20,
                enablePlaceholders = true,
                initialLoadSize = 20
            ),
            remoteMediator = CharacterRemoteMediator(
                characterRemoteDataSource = characterRemoteDataSource,
                characterLocalDataSource = characterLocalDataSource,
                characterDatabase = characterDatabase,
                filter = filter // Передаем фильтр в медиатор
            ),
            pagingSourceFactory = {
                // Здесь мы теперь используем метод из LocalDataSource,
                // который возвращает PagingSource.
                // Если бы Room поддерживал фильтрацию напрямую в getAllCharacters,
                // мы могли бы передать filter сюда.
                // Но поскольку RemoteMediator управляет загрузкой,
                // а Room просто выдает то, что есть, этот метод будет брать все кэшированные данные.
                // Фильтрация будет происходить на уровне UI или в маппере,
                // если она требуется для PagingSource.
                // Для Room-backed PagingSource, фильтрация (по name, status и т.д.)
                // должна быть встроена в запрос Room DAO.
                // НО! Поскольку фильтры передаются в RemoteMediator,
                // это означает, что сетевой запрос уже будет отфильтрован,
                // и в кэш попадут только отфильтрованные данные.
                // Поэтому getAllCharacters без параметров фильтра тут логичен.
                characterLocalDataSource.getAllCharacters()
            }
        ).flow.map { pagingData ->
            pagingData.map { it.toCharacter() }
        }
    }

    override suspend fun refreshCharacters() {
        characterLocalDataSource.clearAllCharacters()
        characterLocalDataSource.clearAllRemoteKeys()
    }
}