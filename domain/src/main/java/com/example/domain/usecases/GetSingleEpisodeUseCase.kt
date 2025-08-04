package com.example.domain.usecases

import com.example.domain.model.RMEpisode
import com.example.domain.repository.EpisodeRepository
import com.example.domain.utils.Result


/**
 * Use Case для получения детальной информации об одном эпизоде.
 *
 * @property repository Зависимость от интерфейса EpisodeRepository.
 */
class GetSingleEpisodeUseCase(private val repository: EpisodeRepository) {
    /**
     * Вызывает метод репозитория для получения детальной информации об эпизоде.
     *
     * @param episodeId ID эпизода.
     * @return [Result] с [RMEpisode] в случае успеха, или [Throwable] в случае ошибки.
     */
    suspend fun execute(episodeId: Int): Result<RMEpisode> {
        return repository.getEpisode(episodeId)
    }
}
