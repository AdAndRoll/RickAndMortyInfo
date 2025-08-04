package com.example.rickandmortyinfo.presentation.episode_detail

import com.example.domain.model.RMEpisode

/**
 * Sealed класс, представляющий различные состояния экрана деталей эпизода.
 */
sealed class EpisodeDetailState {
    object Loading : EpisodeDetailState()
    data class Success(val episode: RMEpisode) : EpisodeDetailState()
    data class Error(val message: String) : EpisodeDetailState()
}