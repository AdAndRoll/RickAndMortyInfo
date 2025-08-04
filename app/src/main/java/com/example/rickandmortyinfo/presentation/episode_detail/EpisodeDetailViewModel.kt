package com.example.rickandmortyinfo.presentation.episode_detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.model.RMEpisode
import com.example.domain.usecases.GetSingleEpisodeUseCase
import com.example.domain.utils.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject



/**
 * ViewModel для экрана с детальной информацией об эпизоде.
 * Использует Hilt для инъекции зависимостей.
 *
 * @property getEpisodeDetailsUseCase Use Case для получения данных об одном эпизоде.
 * @property savedStateHandle Объект для сохранения и восстановления состояния.
 */
@HiltViewModel
class EpisodeDetailViewModel @Inject constructor(
    private val getEpisodeDetailsUseCase: GetSingleEpisodeUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _episodeDetailState = MutableStateFlow<EpisodeDetailState>(EpisodeDetailState.Loading)
    val episodeDetailState: StateFlow<EpisodeDetailState> = _episodeDetailState

    init {
        // Получаем ID эпизода из SavedStateHandle, который будет передан из навигации
        savedStateHandle.get<Int>("episodeId")?.let { episodeId ->
            loadEpisodeDetails(episodeId)
        }
    }

    /**
     * Загружает полную информацию об эпизоде.
     * @param episodeId ID эпизода.
     */
    fun loadEpisodeDetails(episodeId: Int) {
        viewModelScope.launch {
            _episodeDetailState.value = EpisodeDetailState.Loading
            when (val result = getEpisodeDetailsUseCase.execute(episodeId)) {
                is Result.Success -> {
                    _episodeDetailState.value = EpisodeDetailState.Success(result.data)
                }
                is Result.Error -> {
                    _episodeDetailState.value =
                        EpisodeDetailState.Error("Не удалось загрузить детали эпизода: ${result.exception.message}")
                }
            }
        }
    }
}
