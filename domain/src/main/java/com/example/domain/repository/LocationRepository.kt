package com.example.domain.repository

import com.example.domain.model.LocationDetail
import com.example.domain.utils.Result
import kotlinx.coroutines.flow.Flow

/**
 * Репозиторий для работы с данными о локациях.
 *
 */
interface LocationRepository {

    /**
     * Получает полную информацию о локации по её ID.
     *
     * @param locationId ID локации.
     * @return [Result] с [LocationDetail] в случае успеха, или [Throwable] в случае ошибки.
     */
    fun getLocationDetails(locationId: Int): Flow<Result<LocationDetail>>
}