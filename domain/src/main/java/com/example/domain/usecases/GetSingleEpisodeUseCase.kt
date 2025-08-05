package com.example.domain.usecases

import com.example.domain.model.RMEpisode
import com.example.domain.repository.EpisodeRepository
import com.example.domain.utils.Result
import kotlinx.coroutines.flow.Flow

/**
 * Use Case для получения детальной информации об одном эпизоде.
 *
 * @property repository Зависимость от интерфейса EpisodeRepository.
 */
class GetSingleEpisodeUseCase(private val repository: EpisodeRepository) {
    /**
     * Вызывает метод репозитория для получения детальной информации об эпизоде
     * в виде реактивного потока.
     *
     * @param episodeId ID эпизода.
     * @return [Flow] с [Result] с [RMEpisode] в случае успеха, или [Throwable] в случае ошибки.
     */
    fun execute(episodeId: Int): Flow<Result<RMEpisode>> {
        return repository.getEpisode(episodeId)
    }
}
