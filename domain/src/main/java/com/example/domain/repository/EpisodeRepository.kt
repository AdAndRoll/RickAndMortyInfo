package com.example.domain.repository

import com.example.domain.model.RMCharacterEpisodeSummary
import com.example.domain.model.RMEpisode
import com.example.domain.utils.Result
/**
 * Интерфейс репозитория для работы с данными об эпизодах.
 * Определяет контракт, которому должен следовать слой данных.
 */
interface EpisodeRepository {
    suspend fun getEpisode(episodeId: Int): Result<RMEpisode>
    suspend fun getEpisodesSummariesByIds(ids: List<Int>): Result<List<RMCharacterEpisodeSummary>>// Новый метод
}