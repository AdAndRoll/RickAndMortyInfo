package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Сущность Room для хранения информации о персонаже.
 * Представляет строку в таблице "characters" в локальной базе данных.
 *
 * @param id Уникальный идентификатор персонажа, является первичным ключом.
 * @param name Имя персонажа.
 * @param species Вид персонажа (e.g., "Human", "Alien").
 * @param status Статус персонажа (e.g., "Alive", "Dead", "unknown").
 * @param gender Пол персонажа (e.g., "Male", "Female", "Genderless", "unknown").
 * @param imageUrl URL изображения персонажа.
 */
@Entity(tableName = "characters")
data class CharacterEntity(
    @PrimaryKey val id: Int,
    val name: String,
    val species: String,
    val type: String?,
    val status: String,
    val gender: String,
    val imageUrl: String
)