package com.example.rickandmortyinfo.presentation.episode_detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.usecases.GetSingleEpisodeUseCase
import com.example.domain.utils.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel для экрана с детальной информацией об эпизоде.
 *
 * @property getSingleEpisodeUseCase Use Case для получения данных об одном эпизоде.
 * @property savedStateHandle Объект для сохранения и восстановления состояния.
 */
@HiltViewModel
class EpisodeDetailViewModel @Inject constructor(
    private val getSingleEpisodeUseCase: GetSingleEpisodeUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _episodeDetailState = MutableStateFlow<EpisodeDetailState>(EpisodeDetailState.Loading)
    val episodeDetailState: StateFlow<EpisodeDetailState> = _episodeDetailState

    init {
        // Получаем ID эпизода из savedStateHandle, который передается навигацией.
        savedStateHandle.get<Int>("episodeId")?.let { episodeId ->
            loadEpisodeDetails(episodeId)
        }
    }

    /**
     * Загружает полную информацию об эпизоде по его ID.
     * @param episodeId ID эпизода.
     */
    private fun loadEpisodeDetails(episodeId: Int) {
        viewModelScope.launch {
            // --- ПРАВИЛЬНО: Состояние загрузки устанавливается здесь, до начала запроса.
            _episodeDetailState.value = EpisodeDetailState.Loading

            // Вызываем Use Case и "собираем" (collect) его Flow.
            getSingleEpisodeUseCase.execute(episodeId).collect { result ->
                when (result) {
                    // Обрабатываем только Success и Error, как в вашем классе Result.
                    is Result.Success -> {
                        _episodeDetailState.value = EpisodeDetailState.Success(result.data)
                    }
                    is Result.Error -> {
                        _episodeDetailState.value = EpisodeDetailState.Error(
                            result.exception.message ?: "Неизвестная ошибка"
                        )
                    }
                }
            }
        }
    }
}
