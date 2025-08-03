package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Сущность Room для хранения детальной информации о локации.
 *
 * Эта сущность представляет таблицу `location_details` в базе данных.
 *
 * @param id Уникальный идентификатор локации.
 * @param name Название локации.
 * @param type Тип локации.
 * @param dimension Измерение, к которому относится локация.
 * @param residents Список URL-адресов персонажей, проживающих в локации.
 * @param url URL локации.
 * @param created Дата создания локации.
 */
@Entity(tableName = "location_details")
data class LocationDetailEntity(
    @PrimaryKey val id: Int,
    val name: String,
    val type: String,
    val dimension: String,
    val residents: List<String>,
    val url: String,
    val created: String
) {
    /**
     * Вспомогательная функция для получения списка ID персонажей из списка URL-адресов.
     * Например, ["url1", "url2", "url3"] -> [1, 2, 3]
     */
    fun getResidentIds(): List<Int> {
        return residents.mapNotNull {
            // Извлекаем ID из URL, например, "https://rickandmortyapi.com/api/character/1" -> 1
            it.substringAfterLast("/").toIntOrNull()
        }
    }
}
