package com.example.data.remote.dto

data class CharactersResponse(
    val info: InfoDto,
    val results: List<CharacterDto>
)
