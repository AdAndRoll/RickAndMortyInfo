package com.example.rickandmortyinfo.presentation.character_list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.domain.model.RMCharacter
import com.example.domain.model.CharacterFilter
import com.example.domain.usecases.GetCharactersUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel для экрана списка персонажей.
 * Использует более надежный паттерн для управления Paging Data с помощью SharedFlow.
 */
@HiltViewModel
class CharactersViewModel @Inject constructor(
    private val getCharactersUseCase: GetCharactersUseCase
) : ViewModel() {

    // MutableStateFlow для хранения текущих параметров фильтрации, но без прямого Flow
    // для PagingData. Он используется только для UI-состояния фильтров.
    private val _currentFilter = MutableStateFlow(CharacterFilter())
    val currentFilter: StateFlow<CharacterFilter> = _currentFilter.asStateFlow()

    // --- КЛЮЧЕВОЕ ИЗМЕНЕНИЕ: Триггер для перезагрузки PagingData ---
    // Используем SharedFlow, чтобы явно отправлять "команду" на перезагрузку Pager.
    private val _filterTrigger = MutableSharedFlow<CharacterFilter>(
        replay = 1, // Хранит последнее значение для новых подписчиков
        extraBufferCapacity = 1
    )

    // Flow<PagingData<RMCharacter>>, который реагирует на _filterTrigger
    @OptIn(ExperimentalCoroutinesApi::class)
    val characters: Flow<PagingData<RMCharacter>> = _filterTrigger
        .flatMapLatest { filter ->
            getCharactersUseCase.execute(filter)
        }
        .cachedIn(viewModelScope)

    init {
        // Инициируем первую загрузку с пустым фильтром при создании ViewModel.
        viewModelScope.launch {
            _filterTrigger.emit(CharacterFilter())
        }
    }

    /**
     * Метод для установки или обновления параметров фильтрации.
     * Обновляет StateFlow для UI и отправляет сигнал в SharedFlow для перезагрузки Paging.
     *
     * @param name Имя персонажа для фильтрации (null, чтобы оставить текущее значение).
     * @param status Статус персонажа для фильтрации (null, чтобы оставить текущее значение).
     * @param species Вид персонажа для фильтрации (null, чтобы оставить текущее значение).
     * @param gender Пол персонажа для фильтрации (null, чтобы оставить текущее значение).
     */
    fun setFilter(name: String? = null, status: String? = null, species: String? = null, gender: String? = null) {
        val newFilter = _currentFilter.value.copy(
            name = name ?: _currentFilter.value.name,
            status = status ?: _currentFilter.value.status,
            species = species ?: _currentFilter.value.species,
            gender = gender ?: _currentFilter.value.gender
        )
        // Обновляем фильтр только если он действительно изменился
        if (_currentFilter.value != newFilter) {
            _currentFilter.value = newFilter
            // Отправляем сигнал на перезагрузку Pager с новым фильтром
            viewModelScope.launch {
                _filterTrigger.emit(newFilter)
            }
        }
    }

    // Метод для очистки всех фильтров и сброса списка персонажей
    fun clearFilters() {
        val clearedFilter = CharacterFilter()
        if (_currentFilter.value != clearedFilter) {
            _currentFilter.value = clearedFilter
            viewModelScope.launch {
                _filterTrigger.emit(clearedFilter)
            }
        }
    }
}
