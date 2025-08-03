package com.example.rickandmortyinfo.presentation.character_detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.usecases.GetCharacterDetailsUseCase

import com.example.domain.utils.Result

import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel для детального экрана персонажа.
 * Отвечает за получение данных и управление состоянием UI.
 *
 * @param getCharacterDetailsUseCase Use case для получения деталей персонажа.
 */
@HiltViewModel
class CharacterDetailViewModel @Inject constructor(
    private val getCharacterDetailsUseCase: GetCharacterDetailsUseCase
) : ViewModel() {

    private val _characterDetailState = MutableStateFlow<CharacterDetailState>(CharacterDetailState.Loading)
    val characterDetailState: StateFlow<CharacterDetailState> = _characterDetailState

    /**
     * Загружает детали персонажа по его ID.
     *
     * @param characterId Уникальный ID персонажа.
     */
    fun loadCharacterDetails(characterId: Int) {
        viewModelScope.launch {
            _characterDetailState.value = CharacterDetailState.Loading
            when (val result = getCharacterDetailsUseCase.execute(characterId)) {
                is Result.Success -> {
                    _characterDetailState.value = CharacterDetailState.Success(result.data)
                }
                is Result.Error -> {
                    _characterDetailState.value = CharacterDetailState.Error(result.exception.message ?: "Unknown error")
                }
            }
        }
    }
}


