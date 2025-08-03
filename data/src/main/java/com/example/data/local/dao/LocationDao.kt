package com.example.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.local.entity.LocationDetailEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO (Data Access Object) для работы с данными о локациях в базе данных Room.
 */
@Dao
interface LocationDao {

    /**
     * Сохраняет или обновляет детальную информацию о локации.
     * Если запись с таким же ID уже существует, она будет заменена.
     *
     * @param locationDetail Детали локации для сохранения.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLocation(locationDetail: LocationDetailEntity)

    /**
     * Получает детальную информацию о локации по её ID в виде реактивного потока.
     * Возвращение [Flow] позволяет получать обновления данных в реальном времени.
     *
     * @param locationId ID локации.
     * @return [Flow] с объектом [LocationDetailEntity] или null, если запись не найдена.
     */
    @Query("SELECT * FROM location_details WHERE id = :locationId")
    fun getLocationById(locationId: Int): Flow<LocationDetailEntity?>

    /**
     * Удаляет все записи о локациях из базы данных.
     */
    @Query("DELETE FROM location_details")
    suspend fun clearAllLocations()
}
