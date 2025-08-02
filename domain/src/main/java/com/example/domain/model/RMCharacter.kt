package com.example.domain.model

data class RMCharacter(
    val id: Int,
    val name: String,
    val species: String,
    val type: String?, // Добавлено новое поле
    val status: String,
    val gender: String,
    val imageUrl: String
)