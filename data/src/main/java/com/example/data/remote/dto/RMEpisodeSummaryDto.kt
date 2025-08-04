package com.example.data.remote.dto


import com.squareup.moshi.Json

/**
 * Модель данных DTO, представляющая сокращенную версию эпизода.
 * Используется, когда API возвращает только основные данные об эпизодах для персонажа.
 *
 * @property id Уникальный идентификатор эпизода.
 * @property name Название эпизода.
 * @property url URL-адрес эпизода.
 */
data class RMEpisodeSummaryDto(
    @Json(name = "id") val id: Int,
    @Json(name = "name") val name: String,
    @Json(name = "url") val url: String
)