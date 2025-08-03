package com.example.data.remote.dto

import com.google.gson.annotations.SerializedName

/**
 * Модель данных для локации, получаемая с удаленного API.
 * Используется для маппинга JSON-ответа в объект Kotlin.
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
    @SerializedName("id")
    val id: Int,
    @SerializedName("name")
    val name: String,
    @SerializedName("type")
    val type: String,
    @SerializedName("dimension")
    val dimension: String,
    @SerializedName("residents")
    val residents: List<String>,
    @SerializedName("url")
    val url: String,
    @SerializedName("created")
    val created: String
)
