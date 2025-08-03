package com.example.rickandmortyinfo.presentation.location_detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.model.LocationDetail
import com.example.domain.usecases.GetLocationDetailsUseCase
import com.example.domain.utils.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel для экрана с детальной информацией о локации.
 * Отвечает за получение данных и управление состоянием UI.
 *
 * @param getLocationDetailsUseCase Use case для получения деталей локации.
 */
@HiltViewModel
class LocationDetailViewModel @Inject constructor(
    private val getLocationDetailsUseCase: GetLocationDetailsUseCase
) : ViewModel() {

    private val _locationDetailState = MutableStateFlow<LocationDetailState>(LocationDetailState.Loading)
    val locationDetailState: StateFlow<LocationDetailState> = _locationDetailState

    /**
     * Загружает детали локации по её ID.
     *
     * @param locationId Уникальный ID локации.
     */
    fun loadLocationDetails(locationId: Int) {
        viewModelScope.launch {
            // Устанавливаем состояние загрузки
            _locationDetailState.value = LocationDetailState.Loading

            // Собираем данные из потока (Flow), который возвращает Use Case
            // Используем метод execute, как вы и указали.
            getLocationDetailsUseCase.execute(locationId).collect { result ->
                when (result) {
                    is Result.Success -> {
                        // Используем ваш класс LocationDetail для представления данных
                        _locationDetailState.value = LocationDetailState.Success(result.data)
                    }
                    is Result.Error -> {
                        _locationDetailState.value = LocationDetailState.Error(
                            result.exception.message ?: "Unknown error"
                        )
                    }
                }
            }
        }
    }
}
