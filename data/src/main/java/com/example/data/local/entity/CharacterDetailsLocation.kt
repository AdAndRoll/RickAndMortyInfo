package com.example.data.local.entity


/**
 * Вспомогательный класс для хранения данных о местоположении (origin и location).
 * Мы используем @Embedded для встраивания этих полей в CharacterDetailsEntity.
 *
 * @param name Название места.
 * @param url URL-адрес места.
 */
data class CharacterDetailsLocation(
    val name: String,
    val url: String
)