package com.example.data.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.example.data.local.database.CharacterDatabase
import com.example.data.local.datasources.CharacterLocalDataSource
import com.example.data.mappers.toCharacter
import com.example.data.mappers.toCharacterDetailed
import com.example.data.remote.datasources.CharacterRemoteDataSource
import com.example.data.remote.mediator.CharacterRemoteMediator
import com.example.data.utils.NetworkResult
import com.example.domain.model.RMCharacter
import com.example.domain.model.CharacterFilter
import com.example.domain.model.RMCharacterDetailed
import com.example.domain.repository.CharacterRepository
import com.example.domain.utils.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * Реализация репозитория для работы с данными о персонажах.
 * Координирует получение данных из удаленного и локального источников.
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
                pageSize = 30,
                // !!! ИЗМЕНЕНИЕ: Включаем заглушки для стабилизации макета.
                // Это поможет LazyVerticalGrid сохранять позицию прокрутки,
                // пока данные загружаются.
                enablePlaceholders = true,
                initialLoadSize = 20,
                prefetchDistance = 20
            ),
            remoteMediator = CharacterRemoteMediator(
                characterRemoteDataSource = characterRemoteDataSource,
                characterLocalDataSource = characterLocalDataSource,
                characterDatabase = characterDatabase,
                filter = filter
            ),
            // PagingSourceFactory создает новый PagingSource каждый раз,
            // когда Pager нужно получить доступ к данным.
            pagingSourceFactory = {
                // !!! ИЗМЕНЕНИЕ: Убедимся, что PagingSource берется из DAO.
                // Это гарантирует, что Paging Library работает напрямую
                // с изменяемыми данными из Room.
                characterLocalDataSource.getCharactersPagingSource(filter)
            }
        ).flow.map { pagingData ->
            pagingData.map { it.toCharacter() }
        }
    }

    override suspend fun refreshCharacters() {
        characterLocalDataSource.clearAllCharacters()
        characterLocalDataSource.clearAllRemoteKeys()
    }


    override suspend fun getCharacterDetails(characterId: Int): Result<RMCharacterDetailed> {
        // Получаем данные из удаленного источника
        return when (val result = characterRemoteDataSource.getCharacterDetails(characterId)) {
            // Если запрос успешен, преобразуем DTO в доменную модель и оборачиваем в доменный Result
            is NetworkResult.Success -> {
                Result.Success(result.data.toCharacterDetailed())
            }
            // Если произошла ошибка, оборачиваем исключение в доменный Result
            is NetworkResult.Error -> {
                Result.Error(result.exception)
            }
        }
    }

}
