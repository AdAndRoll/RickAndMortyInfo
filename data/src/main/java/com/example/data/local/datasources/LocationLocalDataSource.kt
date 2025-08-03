package com.example.data.local.datasources

import com.example.data.db.dao.LocationDao
import com.example.data.local.entity.LocationDetailEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Локальный источник данных для работы с информацией о локациях,
 * использующий Room для хранения данных.
 *
 * @param locationDao DAO для доступа к данным о локациях в базе данных.
 */
class LocationLocalDataSource @Inject constructor(
    private val locationDao: LocationDao
) {
    /**
     * Сохраняет детали локации в локальную базу данных.
     *
     * @param location Детали локации в виде сущности Room.
     */
    suspend fun saveLocationDetails(location: LocationDetailEntity) {
        locationDao.insertLocation(location)
    }

    /**
     * Получает детали локации по её ID из локальной базы данных в виде потока.
     *
     * @param locationId ID локации.
     * @return [Flow] с [LocationDetailEntity] или null, если локация не найдена.
     */
    fun getLocationDetails(locationId: Int): Flow<LocationDetailEntity?> {
        return locationDao.getLocationById(locationId)
    }

    /**
     * Удаляет все данные о локациях из локальной базы данных.
     */
    suspend fun clearAllLocations() {
        locationDao.clearAllLocations()
    }
}
