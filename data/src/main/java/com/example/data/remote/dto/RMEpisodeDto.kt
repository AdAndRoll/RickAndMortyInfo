package com.example.data.remote.dto

import com.squareup.moshi.Json


/**
 * DTO (Data Transfer Object) для представления полной информации об эпизоде,
 * получаемой из сетевого API.
 */
data class RMEpisodeDto(
    @Json(name = "id")
    val id: Int,
    @Json(name = "name")
    val name: String,
    @Json(name = "air_date")
    val airDate: String,
    @Json(name = "episode")
    val episodeCode: String,
    @Json(name = "characters")
    val characterUrls: List<String>,
    @Json(name = "url")
    val url: String,
    @Json(name = "created")
    val created: String
)
