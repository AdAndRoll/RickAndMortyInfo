package com.example.data.local.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Сущность Room для хранения ПОЛНОЙ информации о персонаже.
 * Представляет строку в таблице "character_details".
 * Используется для кэширования данных, полученных с экрана деталей.
 *
 * @param id Уникальный идентификатор персонажа, является первичным ключом.
 * @param name Имя персонажа.
 * @param status Статус персонажа (e.g., "Alive", "Dead").
 * @param species Вид персонажа (e.g., "Human", "Alien").
 * @param type Тип персонажа.
 * @param gender Пол персонажа.
 * @param imageUrl URL изображения персонажа.
 * @param origin Информация о месте происхождения персонажа.
 * @param location Информация о текущем местоположении персонажа.
 * @param episodeUrls Список URL-адресов эпизодов, в которых появлялся персонаж.
 */
@Entity(tableName = "character_details")
data class CharacterDetailsEntity(
    @PrimaryKey val id: Int,
    val name: String,
    val status: String,
    val species: String,
    val type: String?,
    val gender: String,
    val imageUrl: String,
    @Embedded(prefix = "origin_") val origin: CharacterDetailsLocation,
    @Embedded(prefix = "location_") val location: CharacterDetailsLocation,
    val episodeUrls: List<String>
)

