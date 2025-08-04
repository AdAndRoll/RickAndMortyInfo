package com.example.domain.model

/**
 * Модель данных для полной информации об эпизоде.
 *
 * @property id Уникальный идентификатор эпизода.
 * @property name Название эпизода.
 * @property airDate Дата выхода в эфир.
 * @property episodeCode Код эпизода (например, S01E01).
 * @property characterUrls Список URL-адресов персонажей, появившихся в эпизоде.
 * @property url URL-адрес самого эпизода.
 * @property created Время создания записи в базе данных.
 */
data class RMEpisode(
    val id: Int,
    val name: String,
    val airDate: String?,
    val episodeCode: String,
    val characterUrls: List<String>,
    val url: String,
    val created: String
)