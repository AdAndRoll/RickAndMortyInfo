package com.example.domain.model

/**
 * Легковесная модель данных для эпизода, используемая на экране деталей персонажа.
 * Содержит только ID и название, что достаточно для списка и навигации.
 *
 * @property id Уникальный идентификатор эпизода.
 * @property name Название эпизода.
 */
data class RMCharacterEpisodeSummary(
    val id: Int,
    val name: String,
    val url: String
)