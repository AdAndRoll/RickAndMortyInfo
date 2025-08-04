// Файл: com/example/data/repository/EpisodeRepositoryImpl.kt

package com.example.data.repository

import android.util.Log
import com.example.data.local.episodes.datasources.EpisodeLocalDataSource
import com.example.data.mappers.toEpisode
import com.example.data.mappers.toEpisodeEntity
import com.example.data.mappers.toEpisodeSummary
import com.example.data.remote.datasources.EpisodeRemoteDataSource
import com.example.data.utils.NetworkResult
import com.example.domain.model.RMCharacterEpisodeSummary
import com.example.domain.model.RMEpisode
import com.example.domain.repository.EpisodeRepository
import com.example.domain.utils.Result
import javax.inject.Inject

/**
 * Реализация репозитория для работы с данными об эпизодах.
 * Координирует получение данных из удаленного и локального источников.
 *
 * @param remoteDataSource Источник данных для сетевых запросов.
 * @param localDataSource Источник данных для локальной базы данных.
 */
class EpisodeRepositoryImpl @Inject constructor(
    private val remoteDataSource: EpisodeRemoteDataSource,
    private val localDataSource: EpisodeLocalDataSource
) : EpisodeRepository {

    private val TAG = "EpisodeRepositoryImpl"

    /**
     * Получает полную информацию об одном эпизоде по его ID.
     */
    override suspend fun getEpisode(episodeId: Int): Result<RMEpisode> {
        Log.d(TAG, "Attempting to get episode with ID: $episodeId")
        return try {
            // 1. Попытка получить данные из локального источника
            Log.d(TAG, "Checking local data source for episode with ID: $episodeId")
            val localData = localDataSource.getEpisode(episodeId)
            if (localData != null) {
                // Если данные найдены, маппим их в доменную модель и возвращаем
                Log.d(TAG, "Local data found for episode with ID: $episodeId. Mapping to domain model.")
                Result.Success(localData.toEpisode())
            } else {
                // 2. Если данных в кэше нет, выполняем сетевой запрос через remoteDataSource
                Log.d(TAG, "Local data not found. Making network request for episode with ID: $episodeId")
                when (val networkResult = remoteDataSource.getEpisode(episodeId)) {
                    is NetworkResult.Success -> {
                        Log.d(TAG, "Network request successful for episode with ID: $episodeId. Saving to cache.")
                        // Маппим DTO в Entity и сохраняем в кэш
                        val episodeEntity = networkResult.data.toEpisodeEntity()
                        localDataSource.insertEpisode(episodeEntity)
                        Log.d(TAG, "Successfully saved episode to local database.")

                        // Маппим DTO в доменную модель и возвращаем
                        Result.Success(networkResult.data.toEpisode())
                    }
                    is NetworkResult.Error -> {
                        // Если сетевой запрос завершился ошибкой
                        Log.e(TAG, "Network error for episode with ID: $episodeId", networkResult.exception)
                        Result.Error(networkResult.exception)
                    }
                }
            }
        } catch (e: Exception) {
            // Обработка любых других неожиданных исключений
            Log.e(TAG, "An unexpected exception occurred while getting episode with ID: $episodeId", e)
            Result.Error(e)
        }
    }

    /**
     * Получает сокращенную информацию о нескольких эпизодах по списку их ID.
     * Исправлено: теперь возвращает список [RMCharacterEpisodeSummary].
     */
    override suspend fun getEpisodesSummariesByIds(ids: List<Int>): Result<List<RMCharacterEpisodeSummary>> {
        Log.d(TAG, "Attempting to get episode summaries for IDs: $ids")
        return try {
            // Преобразуем список ID в строку для API-запроса
            val idsString = ids.joinToString(",")

            // Выполняем сетевой запрос для получения сокращенных данных.
            // API возвращает полные DTO, но мы будем маппить их в сокращенную модель.
            when (val networkResult = remoteDataSource.getEpisodesSummariesByIds(idsString)) {
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
