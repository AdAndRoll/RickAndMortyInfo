package com.example.domain.usecases

import com.example.domain.model.CharacterFilter
import com.example.domain.repository.CharacterRepository
import kotlinx.coroutines.flow.Flow

class GetCharactersUseCase(
    private val repository: CharacterRepository
) {
    fun execute(filter: CharacterFilter): Flow<List<Character>> {
        return repository.getCharacters(filter)
    }
}
