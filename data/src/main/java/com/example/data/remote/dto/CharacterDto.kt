package com.example.data.remote.dto


/**
 * DTO (Data Transfer Object) для одного персонажа, полученного из API.
 * Содержит все поля, которые приходят в ответе API для одного персонажа.
 */
data class CharacterDto(
    val id: Int,
    val name: String,
    val status: String,
    val species: String,
    val type: String,
    val gender: String,
    val origin: LocationDto,
    val location: LocationDto,
    val image: String,
    val episode: List<String>,
    val url: String,
    val created: String
)