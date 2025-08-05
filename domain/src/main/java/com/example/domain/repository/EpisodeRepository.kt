package com.example.domain.repository

import com.example.domain.model.RMCharacterEpisodeSummary
import com.example.domain.model.RMEpisode
import com.example.domain.utils.Result
import kotlinx.coroutines.flow.Flow

/**
 * Интерфейс репозитория для работы с данными об эпизодах.
 * Определяет контракт, которому должен следовать слой данных.
 */
interface EpisodeRepository {
    /**
     * Получает полную информацию об одном эпизоде по его ID в виде реактивного потока.
     */
    fun getEpisode(episodeId: Int): Flow<Result<RMEpisode>>

    /**
     * Получает сокращенную информацию о нескольких эпизодах по списку их ID.
     */
    suspend fun getEpisodesSummariesByIds(ids: List<Int>): Result<List<RMCharacterEpisodeSummary>>
}
