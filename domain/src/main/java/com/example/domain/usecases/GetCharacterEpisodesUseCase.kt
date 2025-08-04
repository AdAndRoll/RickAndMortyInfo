package com.example.domain.usecases

import com.example.domain.model.RMCharacterEpisodeSummary
import com.example.domain.model.RMEpisode
import com.example.domain.repository.EpisodeRepository
import com.example.domain.utils.Result

/**
 * Use Case для получения сокращенной информации об эпизодах по списку их URL-адресов.
 * Этот Use Case использует один запрос для получения всех эпизодов, что повышает производительность.
 *
 * @property repository Зависимость от интерфейса EpisodeRepository.
 */
class GetCharacterEpisodesUseCase(private val repository: EpisodeRepository) {
    /**
     * Вызывает репозиторий для получения списка сокращенных данных эпизодов.
     * @param episodeUrls Список URL-адресов эпизодов.
     * @return Результат операции — список RMCharacterEpisodeSummary.
     */
    suspend fun execute(episodeUrls: List<String>): Result<List<RMCharacterEpisodeSummary>> {
        val episodeIds = episodeUrls.mapNotNull { it.substringAfterLast("/").toIntOrNull() }
        return if (episodeIds.isNotEmpty()) {
            repository.getEpisodesSummariesByIds(episodeIds)
        } else {
            Result.Success(emptyList())
        }
    }
}
