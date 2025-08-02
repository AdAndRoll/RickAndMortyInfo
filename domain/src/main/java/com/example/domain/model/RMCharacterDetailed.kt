package com.example.domain.model

data class RMCharacterDetailed(
    val character: RMCharacter,
    val origin: LocationRM,
    val location: LocationRM,
    val episode: List<String>
)