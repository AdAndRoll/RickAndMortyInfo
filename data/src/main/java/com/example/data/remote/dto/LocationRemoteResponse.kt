package com.example.data.remote.dto

import com.squareup.moshi.Json

/**
 * Модель данных для локации, получаемая с удаленного API.
 * Использует аннотацию @Json от Moshi для маппинга JSON-ответа в объект Kotlin.
 *
 * @property id Уникальный идентификатор локации.
 * @property name Название локации.
 * @property type Тип локации (например, "Planet", "Space station").
 * @property dimension Измерение, в котором находится локация.
 * @property residents Список URL-адресов персонажей, проживающих в этой локации.
 * @property url URL-адрес самой локации в API.
 * @property created Дата создания записи.
 */
data class LocationRemoteResponse(
    @Json(name = "id")
    val id: Int,
    @Json(name = "name")
    val name: String,
    @Json(name = "type")
    val type: String,
    @Json(name = "dimension")
    val dimension: String,
    @Json(name = "residents")
    val residents: List<String>,
    @Json(name = "url")
    val url: String,
    @Json(name = "created")
    val created: String
)
