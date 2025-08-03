package com.example.domain.usecases

import com.example.domain.model.LocationDetail
import com.example.domain.repository.LocationRepository
import com.example.domain.utils.Result
import kotlinx.coroutines.flow.Flow

/**
 * Use Case для получения детальной информации о локации.
 *
 * Этот класс содержит бизнес-логику для получения полной информации о локации
 * и используется ViewModel для работы с данными.
 *
 * @param locationRepository Репозиторий для работы с данными о локациях.
 */
class GetLocationDetailsUseCase(
    private val locationRepository: LocationRepository
) {
    /**
     * Вызывает метод репозитория для получения детальной информации о локации.
     *
     * @param locationId ID локации.
     * @return [Result] с [LocationDetail] или [Throwable] в случае ошибки.
     */
    suspend fun execute(locationId: Int): Flow<Result<LocationDetail>> {
        return locationRepository.getLocationDetails(locationId)
    }
}
