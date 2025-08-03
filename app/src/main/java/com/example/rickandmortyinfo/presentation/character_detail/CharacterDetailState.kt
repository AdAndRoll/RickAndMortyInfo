package com.example.rickandmortyinfo.presentation.character_detail

import com.example.domain.model.RMCharacterDetailed


/**
 * Запечатанный класс для представления различных состояний UI.
 * Это позволяет UI-слою реагировать на каждое состояние (загрузка, успех, ошибка).
 */
sealed class CharacterDetailState {
    object Loading : CharacterDetailState()
    data class Success(val character: RMCharacterDetailed) : CharacterDetailState()
    data class Error(val message: String) : CharacterDetailState()
}