package com.example.domain.repository

import androidx.paging.PagingData
import com.example.domain.model.RMCharacter
import com.example.domain.model.CharacterFilter
import kotlinx.coroutines.flow.Flow

interface CharacterRepository {

    /**
     * Возвращает поток с текущим списком персонажей.
     * Поддерживает пагинацию и фильтрацию через CharacterFilter.
     */
    fun getCharacters(
        filter: CharacterFilter
    ): Flow<PagingData<RMCharacter>>

    /**
     * Обновить список персонажей (например, при Pull-to-Refresh).
     */
    suspend fun refreshCharacters()
}