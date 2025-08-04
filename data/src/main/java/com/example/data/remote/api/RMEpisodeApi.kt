package com.example.data.remote.episodes.api

import com.example.data.remote.dto.RMEpisodeDto
import com.example.data.remote.dto.RMEpisodeSummaryDto

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

/**
 * Интерфейс Retrofit для API "Эпизоды".
 * Определяет методы для выполнения сетевых запросов, связанных с эпизодами.
 */
interface RMEpisodeApi {

    /**
     * Получает полную информацию об одном эпизоде по его ID.
     * @param episodeId ID эпизода.
     * @return Объект Response с DTO эпизода.
     */
    @GET("episode/{episodeId}")
    suspend fun getEpisode(@Path("episodeId") episodeId: Int): Response<RMEpisodeDto>

    /**
     * Получает информацию о нескольких эпизодах по списку их ID.
     * @param ids Строка с ID эпизодов, разделенных запятыми (например, "1,2,3").
     * @return Объект Response со списком DTO эпизодов.
     */
    @GET("episode/{ids}")
    suspend fun getEpisodesSummariesByIds(@Path("ids") ids: String): Response<List<RMEpisodeDto>>
}
