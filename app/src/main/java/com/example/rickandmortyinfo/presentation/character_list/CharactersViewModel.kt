package com.example.rickandmortyinfo.presentation.character_list

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import coil.ImageLoader
import coil.request.ImageRequest
import coil.request.SuccessResult // Добавьте этот импорт, если будете проверять результат
import com.example.domain.model.RMCharacter
import com.example.domain.model.CharacterFilter
import com.example.domain.repository.CharacterRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow // Лучше использовать StateFlow для public API
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll

import javax.inject.Inject

@HiltViewModel
class CharactersViewModel @Inject constructor(
    private val characterRepository: CharacterRepository,
    private val imageLoader: ImageLoader, // ImageLoader инжектируется, это отлично!
    @ApplicationContext private val context: Context // ApplicationContext здесь безопасен
) : ViewModel() {

    private val _isFirstPageReady = MutableStateFlow(false)
    val isFirstPageReady: StateFlow<Boolean> = _isFirstPageReady.asStateFlow() // Используем StateFlow

    // TODO: Рассмотрите возможность передавать CharacterFilter динамически, если фильтры будут меняться
    // Например, через функцию, которая будет обновлять _characters Flow.
    // Пока что CharacterFilter() создает пустой/дефолтный фильтр.
    private val _charactersFlow: Flow<PagingData<RMCharacter>> = characterRepository.getCharacters(CharacterFilter())
        .cachedIn(viewModelScope) // cachedIn очень важен для Paging

    val characters: Flow<PagingData<RMCharacter>> = _charactersFlow

    /**
     * Запускает предзагрузку изображений для указанного списка персонажей.
     * Эту функцию следует вызывать, когда первая порция данных Paging доступна.
     */
    fun preloadImagesForCharacters(charactersToPreload: List<RMCharacter>) {
        // Проверяем, не была ли уже выполнена предзагрузка для начальной страницы
        // или если список для предзагрузки пуст.
        if (_isFirstPageReady.value || charactersToPreload.isEmpty()) {
            return
        }

        //Timber.d("Начало предзагрузки для ${charactersToPreload.size} изображений.")

        viewModelScope.launch {
            val preloadJobs = charactersToPreload.mapNotNull { character ->
                character.imageUrl?.let { url ->
                    if (url.isBlank()) { // Пропускаем пустые URL
                        return@let null
                    }
                    async { // Запускаем каждую загрузку параллельно
                        try {
                            val request = ImageRequest.Builder(context)
                                .data(url)
                                // .size(coil.size.Size.ORIGINAL) // Можно указать, если нужно загрузить в оригинальном размере для кэша
                                // Важно: не указывайте .target() здесь,
                                // так как нам нужно только загрузить в кэш, а не отображать.
                                // Coil по умолчанию будет использовать конфигурацию из вашего глобального ImageLoader
                                // (включая дисковый и memory кэш, настроенные в AppModule).
                                .build()
                            // imageLoader.execute(request) вернет ImageResult
                            // Нам просто нужно выполнить запрос. execute() подходит для этого.
                            val result = imageLoader.execute(request)
                            if (result is SuccessResult) {
                                //Timber.d("Предзагрузка успешна: $url")
                            } else {
                                //Timber.w("Предзагрузка не удалась (не ошибка, но и не успех) для $url: ${result}")
                            }
                            // Если нужно, можно обработать результат (например, логировать ошибки)
                        } catch (e: CancellationException) {
                            //Timber.i("Предзагрузка отменена для $url")
                            throw e // Важно пробрасывать CancellationException
                        } catch (e: Exception) {
                            //Timber.e(e, "Ошибка предзагрузки изображения: $url")
                            // Здесь можно решить, считать ли это критичным.
                            // Для предзагрузки обычно достаточно залогировать.
                        }
                    }
                }
            }

            // Ожидаем завершения всех задач предзагрузки
            // Игнорируем ошибки отдельных загрузок, чтобы не прерывать весь процесс,
            // если только одна картинка не загрузилась.
            preloadJobs.awaitAllSafely() // Самописная функция или обработка результатов awaitAll()

            _isFirstPageReady.value = true
           // Timber.d("Предзагрузка изображений завершена.")
        }
    }

    // Вспомогательная функция для awaitAll, которая не падает из-за одного исключения
    // (Kotlin не предоставляет такую из коробки, но ее легко написать)
    private suspend fun <T> List<Deferred<T>>.awaitAllSafely(): List<Result<T>> {
        val results = mutableListOf<Result<T>>()
        forEach { deferred ->
            try {
                results.add(Result.success(deferred.await()))
            } catch (e: Exception) {
                if (e is CancellationException) throw e // Пробрасываем CancellationException
                results.add(Result.failure(e))
                //Timber.w(e, "Одно из заданий предзагрузки завершилось с ошибкой.")
            }
        }
        return results
    }

    // TODO: Добавить логику для сброса _isFirstPageReady, если данные обновляются (например, pull-to-refresh)
    // чтобы предзагрузка могла запуститься снова для новых данных.
    // Например, если у вас есть функция refreshCharacters():
    // fun refreshCharacters() {
    // _isFirstPageReady.value = false
    // // Логика обновления characterRepository.getCharacters(...)
    // }
}
