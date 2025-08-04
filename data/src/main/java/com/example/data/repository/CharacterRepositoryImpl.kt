package com.example.data.repository

import android.util.Log
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.example.data.local.database.CharacterDatabase
import com.example.data.local.datasources.CharacterDetailsLocalDataSource
import com.example.data.local.datasources.CharacterLocalDataSource
import com.example.data.mappers.toCharacter
import com.example.data.mappers.toCharacterDetailsRaw // Используем новый маппер
import com.example.data.mappers.toCharacterDetailsEntity
import com.example.data.remote.datasources.CharacterRemoteDataSource
import com.example.data.remote.mediator.CharacterRemoteMediator
import com.example.data.utils.NetworkResult
import com.example.domain.model.CharacterFilter
import com.example.domain.model.RMCharacter
import com.example.domain.model.RMCharacterDetailsRaw // Теперь репозиторий возвращает эту модель
import com.example.domain.repository.CharacterRepository
import com.example.domain.utils.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * Реализация репозитория для работы с данными о персонажах.
 * Координирует получение данных из удаленного и локального источников.
 */
private const val TAG = "CharacterRepositoryImpl"

@OptIn(ExperimentalPagingApi::class)
class CharacterRepositoryImpl @Inject constructor(
    private val characterRemoteDataSource: CharacterRemoteDataSource,
    private val characterLocalDataSource: CharacterLocalDataSource,
    private val characterDetailsLocalDataSource: CharacterDetailsLocalDataSource,
    private val characterDatabase: CharacterDatabase
) : CharacterRepository {


    override fun getCharacters(
        filter: CharacterFilter
    ): Flow<PagingData<RMCharacter>> {
        return Pager(
            config = PagingConfig(
                pageSize = 30,
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
            pagingSourceFactory = {
                characterLocalDataSource.getCharactersPagingSource(filter)
            }
        ).flow.map { pagingData ->
            pagingData.map { it.toCharacter() }
        }
    }

    override suspend fun refreshCharacters() {
        Log.d(TAG, "Очистка кэша списка персонажей.")
        characterLocalDataSource.clearAllCharacters()
        characterLocalDataSource.clearAllRemoteKeys()
    }


    override suspend fun getCharacterDetails(characterId: Int): Result<RMCharacterDetailsRaw> {
        Log.d(TAG, "Запрос деталей персонажа с ID: $characterId")
        return try {
            val localDetails = characterDetailsLocalDataSource.getCharacterDetails(characterId)
            if (localDetails != null) {
                Log.d(TAG, "Детали персонажа найдены в кэше.")
                Result.Success(localDetails.toCharacterDetailsRaw())
            } else {
                Log.d(TAG, "Детали персонажа не найдены в кэше. Выполняем сетевой запрос.")
                when (val remoteResult = characterRemoteDataSource.getCharacterById(characterId)) {
                    is NetworkResult.Success -> {
                        Log.d(TAG, "Сетевой запрос успешен. Сохраняем в кэш.")
                        val detailsEntity = remoteResult.data.toCharacterDetailsEntity()
                        characterDetailsLocalDataSource.insertCharacterDetails(detailsEntity)
                        Result.Success(detailsEntity.toCharacterDetailsRaw())
                    }

                    is NetworkResult.Error -> {
                        Log.e(
                            TAG,
                            "Ошибка при сетевом запросе деталей персонажа: ${remoteResult.exception.localizedMessage}"
                        )
                        Result.Error(remoteResult.exception)
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Неожиданная ошибка при получении деталей персонажа: ${e.localizedMessage}")
            Result.Error(e)
        }
    }
}
