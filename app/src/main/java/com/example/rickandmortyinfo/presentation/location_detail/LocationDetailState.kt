package com.example.rickandmortyinfo.presentation.location_detail

import com.example.domain.model.LocationDetail

/**
 * Запечатанный класс для представления различных состояний UI экрана деталей локации.
 * Это позволяет UI-слою реагировать на каждое состояние (загрузка, успех, ошибка)
 * и отображать соответствующий контент.
 */
sealed class LocationDetailState {
    /**
     * Состояние, когда данные загружаются.
     */
    object Loading : LocationDetailState()

    /**
     * Состояние, когда данные успешно загружены.
     * @param location Объект [LocationDetail], содержащий детали локации.
     */
    data class Success(val location: LocationDetail) : LocationDetailState()

    /**
     * Состояние, когда произошла ошибка.
     * @param message Сообщение об ошибке.
     */
    data class Error(val message: String) : LocationDetailState()
}
