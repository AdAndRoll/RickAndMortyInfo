// data/remote/datasources/CharacterRemoteDataSource.kt
package com.example.data.remote.datasources

import com.example.data.remote.api.RickAndMortyApi
import com.example.data.remote.dto.CharactersResponse
import com.example.data.utils.NetworkResult // Импортируем наш новый sealed class
import javax.inject.Inject
import java.io.IOException
import retrofit2.HttpException

/**
 * Удаленный источник данных для персонажей.
 * Отвечает за выполнение сетевых запросов к API "Рик и Морти"
 * с использованием Retrofit и обработку базовых сетевых ошибок.
 *
 * @param api Экземпляр [RickAndMortyApi], предоставляемый Hilt для выполнения запросов.
 */
class CharacterRemoteDataSource @Inject constructor(
    private val api: RickAndMortyApi
) {
    /**
     * Получает список персонажей из удаленного API.
     * Оборачивает результат в [NetworkResult] для явной обработки успеха или ошибки.
     *
     * @param page Номер страницы для запроса.
     * @param name Фильтр по имени.
     * @param status Фильтр по статусу.
     * @param species Фильтр по виду.
     * @param gender Фильтр по полу.
     * @return [NetworkResult] с [CharactersResponse] в случае успеха, или [Throwable] в случае ошибки.
     */
    suspend fun getCharacters(
        page: Int,
        name: String? = null,
        status: String? = null,
        species: String? = null,
        gender: String? = null
    ): NetworkResult<CharactersResponse> { // Возвращает NetworkResult
        return try {
            val response = api.getCharacters(page, name, status, species, gender)
            NetworkResult.Success(response)
        } catch (e: IOException) {
            // Ошибки сети: нет интернета, таймаут и т.д.
            NetworkResult.Error(e)
        } catch (e: HttpException) {
            // Ошибки HTTP: 4xx, 5xx ответы от сервера (например, 404 Not Found)
            NetworkResult.Error(e)
        } catch (e: Exception) {
            // Любые другие неожиданные исключения
            NetworkResult.Error(e)
        }
    }
}