package com.example.domain.repository

import com.example.domain.model.CharacterFilter
import kotlinx.coroutines.flow.Flow

interface CharacterRepository {

    /**
     * Возвращает поток с текущим списком персонажей.
     * Поддерживает пагинацию и фильтрацию через CharacterFilter.
     */
    fun getCharacters(
        filter: CharacterFilter
    ): Flow<List<Character>>

    /**
     * Обновить список персонажей (например, при Pull-to-Refresh).
     */
    suspend fun refreshCharacters()
}