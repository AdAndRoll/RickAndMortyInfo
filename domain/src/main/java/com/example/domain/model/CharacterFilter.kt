package com.example.domain.model

data class CharacterFilter(
    val page: Int = 1,
    val name: String? = null,
    val status: String? = null,
    val species: String? = null,
    val gender: String? = null
)
