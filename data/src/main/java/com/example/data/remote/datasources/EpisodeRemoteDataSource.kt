package com.example.data.remote.datasources

import com.example.data.remote.dto.RMEpisodeDto
import com.example.data.remote.dto.RMEpisodeSummaryDto
import com.example.data.remote.episodes.api.RMEpisodeApi
import com.example.data.repository.safeApiCall

import com.example.data.utils.NetworkResult
import javax.inject.Inject

/**
 * Источник данных для удаленного API эпизодов.
 * Инкапсулирует логику сетевых запросов.
 *
 * @param api Интерфейс Retrofit для доступа к API эпизодов.
 */
class EpisodeRemoteDataSource @Inject constructor(
    private val api: RMEpisodeApi
) {
    /**
     * Выполняет сетевой запрос для получения информации об одном эпизоде.
     * @param episodeId ID эпизода.
     * @return [NetworkResult] с объектом DTO, полученным из сети.
     */
    suspend fun getEpisode(episodeId: Int): NetworkResult<RMEpisodeDto> {
        return safeApiCall { api.getEpisode(episodeId).body()!! }
    }

    /**
     * Выполняет сетевой запрос для получения сокращенной информации о нескольких эпизодах.
     * @param ids Строка с ID эпизодов, разделенных запятыми (например, "1,2,3").
     * @return [NetworkResult] со списком сокращенных объектов DTO, полученных из сети.
     */
    suspend fun getEpisodesSummariesByIds(ids: String): NetworkResult<List<RMEpisodeDto>> {
        return safeApiCall { api.getEpisodesSummariesByIds(ids).body()!! }
    }
}
