package com.example.rickandmortyinfo.presentation.character_list

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.domain.model.RMCharacter
import com.example.domain.model.CharacterFilter
import com.example.domain.repository.CharacterRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.async
import javax.inject.Inject

@HiltViewModel
class CharactersViewModel @Inject constructor(
    private val characterRepository: CharacterRepository,
    private val imageLoader: coil.ImageLoader,
    @ApplicationContext private val context: Context
) : ViewModel() {

    // MutableStateFlow для хранения текущего фильтра
    private val _characterFilter = MutableStateFlow(CharacterFilter())
    val characterFilter: StateFlow<CharacterFilter> = _characterFilter.asStateFlow()

    // Поток PagingData, который автоматически обновляется при изменении фильтра
    val characters: Flow<PagingData<RMCharacter>> = _characterFilter
        .flatMapLatest { filter ->
            // При каждом изменении фильтра, Paging Library автоматически
            // перезапускает этот Flow и получает новые данные из репозитория
            characterRepository.getCharacters(filter)
        }
        .cachedIn(viewModelScope)

    /**
     * Этот метод вызывается из UI для применения новых фильтров.
     */
    fun onFilterApplied(newFilter: CharacterFilter) {
        // Обновляем StateFlow с фильтрами. Это автоматически
        // запустит новый запрос на получение данных через flatMapLatest.
        _characterFilter.value = newFilter
    }

    /**
     * Запускает предзагрузку изображений для указанного списка персонажей.
     * Эту функцию следует вызывать, когда первая порция данных Paging доступна.
     */
    fun preloadImagesForCharacters(charactersToPreload: List<RMCharacter>) {
        // Добавляем проверку, чтобы не запускать предзагрузку дважды
        if (charactersToPreload.isEmpty()) {
            return
        }

        viewModelScope.launch {
            val preloadJobs = charactersToPreload.mapNotNull { character ->
                character.imageUrl?.let { url ->
                    if (url.isBlank()) {
                        return@let null
                    }
                    async {
                        try {
                            val request = coil.request.ImageRequest.Builder(context)
                                .data(url)
                                .build()
                            imageLoader.execute(request)
                        } catch (e: CancellationException) {
                            throw e
                        } catch (e: Exception) {
                            // Логирование ошибки предзагрузки
                        }
                    }
                }
            }
            preloadJobs.awaitAllSafely()
        }
    }

    // Вспомогательная функция для awaitAll, которая не падает из-за одного исключения
    private suspend fun <T> List<Deferred<T>>.awaitAllSafely(): List<Result<T>> {
        val results = mutableListOf<Result<T>>()
        forEach { deferred ->
            try {
                results.add(Result.success(deferred.await()))
            } catch (e: Exception) {
                if (e is CancellationException) throw e
                results.add(Result.failure(e))
            }
        }
        return results
    }
}
