// Файл: com/example/data/repository/EpisodeRepositoryImpl.kt

package com.example.data.repository

import android.util.Log
import com.example.data.local.dao.CharacterDetailsDao
import com.example.data.local.episodes.datasources.EpisodeLocalDataSource
import com.example.data.mappers.toCharacterDetailsEntity
import com.example.data.mappers.toEpisodeEntity
import com.example.data.mappers.toEpisodeSummary
import com.example.data.mappers.toRMEpisode
import com.example.data.remote.datasources.CharacterRemoteDataSource
import com.example.data.remote.datasources.EpisodeRemoteDataSource
import com.example.data.remote.dto.CharacterDto
import com.example.data.utils.NetworkResult
import com.example.domain.model.RMCharacterEpisodeSummary
import com.example.domain.model.RMEpisode
import com.example.domain.model.Resident
import com.example.domain.repository.EpisodeRepository
import com.example.domain.utils.Result
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Реализация репозитория для работы с данными об эпизодах.
 * Координирует получение данных из удаленного и локального источников.
 *
 * @param remoteDataSource Источник данных для сетевых запросов эпизодов.
 * @param localDataSource Источник данных для локальной базы данных эпизодов.
 * @param characterRemoteDataSource Источник данных для сетевых запросов персонажей.
 * @param characterDetailsDao DAO для доступа к деталям персонажей в базе данных.
 */
class EpisodeRepositoryImpl @Inject constructor(
    private val remoteDataSource: EpisodeRemoteDataSource,
    private val localDataSource: EpisodeLocalDataSource,
    private val characterRemoteDataSource: CharacterRemoteDataSource,
    private val characterDetailsDao: CharacterDetailsDao
) : EpisodeRepository {

    private val TAG = "EpisodeRepositoryImpl"

    /**
     * Получает полную информацию об одном эпизоде по его ID в виде реактивного потока.
     * Логика полностью повторяет подход из LocationRepositoryImpl:
     * 1. Сначала отправляет данные из кэша.
     * 2. Выполняет сетевой запрос.
     * 3. Загружает персонажей, сохраняет их в кэш.
     * 4. Отправляет обновленный результат.
     *
     * @param episodeId ID эпизода.
     * @return [Flow] с объектом [Result<RMEpisode>], который содержит данные
     * или информацию об ошибке.
     */
    override fun getEpisode(episodeId: Int): Flow<Result<RMEpisode>> = flow {
        Log.d(TAG, "Attempting to get episode with ID: $episodeId")

        // 1. Сначала пытаемся получить данные из локального кэша
        val localData = localDataSource.getEpisode(episodeId)
        if (localData != null) {
            Log.d(TAG, "Local data found for episode with ID: $episodeId. Emitting cached data.")
            // Получаем персонажей из локальной базы
            val characters = getCharacters(localData.characterUrls)
            // Маппим все в доменную модель и отправляем в поток
            emit(Result.Success(localData.toRMEpisode(characters)))
        }

        // 2. Выполняем сетевой запрос, чтобы получить актуальные данные
        Log.d(TAG, "Making network request for episode with ID: $episodeId")
        when (val networkResult = remoteDataSource.getEpisode(episodeId)) {
            is NetworkResult.Success -> {
                Log.d(TAG, "Network request successful. Saving to cache.")
                // Маппим DTO в Entity и сохраняем в кэш
                val episodeEntity = networkResult.data.toEpisodeEntity()
                localDataSource.insertEpisode(episodeEntity)

                // Получаем ID персонажей из URL-адресов
                val characterIds = episodeEntity.characterUrls.mapNotNull { url ->
                    url.substringAfterLast("/").toIntOrNull()
                }

                // Загружаем и сохраняем персонажей в локальную базу.
                fetchAndSaveCharacters(characterIds)

                // Получаем персонажей из локальной базы (уже сохраненных)
                val characters = getCharacters(episodeEntity.characterUrls)

                // Отправляем в поток обновленную доменную модель
                Log.d(TAG, "Successfully saved episode and characters. Emitting updated data.")
                emit(Result.Success(networkResult.data.toRMEpisode(characters)))
            }
            is NetworkResult.Error -> {
                Log.e(TAG, "Network error for episode with ID: $episodeId", networkResult.exception)
                // Если произошла ошибка сети и локальных данных не было,
                // отправляем в поток ошибку
                if (localData == null) {
                    emit(Result.Error(networkResult.exception))
                }
            }
        }
    }

    /**
     * Получает сокращенную информацию о нескольких эпизодах по списку их ID.
     * Эта функция не использует Flow, так как ее логика не требует реактивного обновления.
     */
    override suspend fun getEpisodesSummariesByIds(ids: List<Int>): Result<List<RMCharacterEpisodeSummary>> {
        Log.d(TAG, "Attempting to get episode summaries for IDs: $ids")
        return try {
            // В зависимости от количества ID, вызываем соответствующий метод
            val networkResult = if (ids.size == 1) {
                Log.d(TAG, "Fetching a single episode summary with ID: ${ids.first()}")
                when (val result = remoteDataSource.getEpisode(ids.first())) {
                    is NetworkResult.Success -> NetworkResult.Success(listOf(result.data))
                    is NetworkResult.Error -> NetworkResult.Error(result.exception)
                }
            } else {
                Log.d(TAG, "Fetching multiple episode summaries with IDs: $ids")
                val idsString = ids.joinToString(",")
                remoteDataSource.getEpisodesSummariesByIds(idsString)
            }

            when (networkResult) {
                is NetworkResult.Success -> {
                    Log.d(TAG, "Network request successful for episode summaries. Mapping DTOs.")
                    // Маппим список полных DTO в список сокращенных моделей
                    val summaries = networkResult.data.map { it.toEpisodeSummary() }
                    Log.d(TAG, "Successfully mapped to summaries. Count: ${summaries.size}")
                    Result.Success(summaries)
                }
                is NetworkResult.Error -> {
                    Log.e(TAG, "Network error for episode summaries", networkResult.exception)
                    Result.Error(networkResult.exception)
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "An unexpected exception occurred while getting episode summaries for IDs: $ids", e)
            Result.Error(e)
        }
    }

    /**
     * Вспомогательная функция для получения списка объектов Resident (ID и имя)
     * по их URL-адресам из локальной базы данных.
     *
     * @param characterUrls Список URL-адресов персонажей.
     * @return Список объектов [Resident].
     */
    private suspend fun getCharacters(characterUrls: List<String>): List<Resident> {
        return characterUrls.mapNotNull { url ->
            val characterId = url.substringAfterLast("/").toIntOrNull()
            if (characterId != null) {
                val characterEntity = characterDetailsDao.getCharacterDetailsById(characterId)
                characterEntity?.let { Resident(id = it.id, name = it.name) }
            } else {
                null
            }
        }
    }

    /**
     * Получает детали нескольких персонажей по списку ID и сохраняет их в локальной базе данных.
     * Улучшено для обработки запросов с одним и несколькими ID.
     *
     * @param characterIds Список ID персонажей.
     */
    private suspend fun fetchAndSaveCharacters(characterIds: List<Int>) {
        if (characterIds.isEmpty()) return

        val remoteResult = if (characterIds.size == 1) {
            characterRemoteDataSource.getCharacterById(characterIds.first())
        } else {
            characterRemoteDataSource.getCharactersByIds(characterIds)
        }

        when (remoteResult) {
            is NetworkResult.Success -> {
                val characters = if (remoteResult.data is List<*>) {
                    remoteResult.data as List<CharacterDto>
                } else {
                    listOf(remoteResult.data as CharacterDto)
                }
                val characterEntities = characters.map { it.toCharacterDetailsEntity() }
                characterEntities.forEach { characterDetailsDao.insertCharacterDetails(it) }
            }
            is NetworkResult.Error -> {
                Log.e(TAG, "Error fetching characters with IDs $characterIds: ${remoteResult.exception.message}")
            }
        }
    }
}


/**
 * Вспомогательная функция для безопасного вызова API,
 * которая обрабатывает исключения.
 *
 * @param call Lambda-функция, выполняющая сетевой вызов.
 * @return [NetworkResult] с данными или исключением.
 */
suspend fun <T : Any> safeApiCall(call: suspend () -> T): NetworkResult<T> {
    return try {
        NetworkResult.Success(call.invoke())
    } catch (e: Exception) {
        Log.e("safeApiCall", "API call failed", e)
        NetworkResult.Error(e)
    }
}