// app/src/main/java/com/example/rickandmortyinfo/presentation/character_list/CharactersViewModel.kt
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
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged // <-- Добавьте этот импорт, если его нет
import kotlinx.coroutines.flow.flatMapLatest
import javax.inject.Inject

/**
 * [ViewModel] для экрана списка персонажей.
 * Отвечает за получение данных о персонажах с пагинацией и управление состоянием UI.
 */
@HiltViewModel
class CharactersViewModel @Inject constructor(
    private val getCharactersUseCase: GetCharactersUseCase
) : ViewModel() {

    // MutableStateFlow для хранения текущих параметров фильтрации
    private val _currentFilter = MutableStateFlow(CharacterFilter())
    val currentFilter: StateFlow<CharacterFilter> = _currentFilter.asStateFlow()

    // --- КЛЮЧЕВОЕ ИЗМЕНЕНИЕ: Только ОДИН Flow для PagingData ---
    // Flow<PagingData<RMCharacter>> для отображения списка персонажей.
    // Этот Flow будет автоматически реагировать на изменения _currentFilter
    // благодаря flatMapLatest.
    @OptIn(ExperimentalCoroutinesApi::class)
    val characters: Flow<PagingData<RMCharacter>> = _currentFilter
        .flatMapLatest { filter -> // Каждый раз, когда _currentFilter меняется, создаем новый PagingData Flow
            getCharactersUseCase.execute(filter)
        }
        .cachedIn(viewModelScope) // Кэшируем PagingData в области видимости ViewModel

    /**
     * Метод для установки или обновления параметров фильтрации.
     * Вызывается из UI при изменении параметров поиска/фильтрации.
     * При изменении _currentFilter, `characters` Flow автоматически перезапустит загрузку.
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
        // Обновляем фильтр только если он действительно изменился, чтобы избежать лишних перезагрузок PagingData
        // Эта проверка необходима, т.к. distinctUntilChanged() выше проверяет только Flow.
        // Если _currentFilter.value уже равно newFilter, мы просто не обновляем StateFlow,
        // что предотвратит его испускание и сэкономит ресурсы.
        if (_currentFilter.value != newFilter) {
            _currentFilter.value = newFilter
        }
    }

    // Метод для очистки всех фильтров и сброса списка персонажей
    fun clearFilters() {
        // Создаем пустой CharacterFilter для сброса всех полей
        val clearedFilter = CharacterFilter()
        // Обновляем фильтр только если он отличается от текущего
        if (_currentFilter.value != clearedFilter) {
            _currentFilter.value = clearedFilter
        }
    }
}