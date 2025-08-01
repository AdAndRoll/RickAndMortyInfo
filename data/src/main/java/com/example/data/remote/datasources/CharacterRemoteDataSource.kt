package com.example.data.remote.datasources

import android.util.Log
import com.example.data.remote.api.RickAndMortyApi
import com.example.data.remote.dto.CharactersResponse
import com.example.data.utils.NetworkResult
import javax.inject.Inject
import java.io.IOException
import retrofit2.HttpException
import retrofit2.Response

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
    private val TAG = "CharacterRemoteDataSource"

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
    ): NetworkResult<CharactersResponse> {
        return try {
            Log.d(TAG, "Making API call for page $page with filters: name=$name, status=$status, species=$species, gender=$gender")

            // Выполняем запрос к API. Если он успешен, возвращаем NetworkResult.Success.
            // Если возникнет ошибка HTTP или сети, Retrofit бросит исключение,
            // которое будет поймано в соответствующих блоках catch.
            val response = api.getCharacters(page, name, status, species, gender)
            Log.d(TAG, "API call successful for page $page.")
            NetworkResult.Success(response)

        } catch (e: IOException) {
            // Ошибки сети: нет интернета, таймаут и т.д.
            Log.e(TAG, "Network or I/O error occurred: ${e.localizedMessage}")
            NetworkResult.Error(e)
        } catch (e: HttpException) {
            // Ошибки HTTP: 4xx, 5xx ответы от сервера (например, 404 Not Found)
            Log.e(TAG, "HTTP error occurred: ${e.code()} - ${e.localizedMessage}")
            NetworkResult.Error(e)
        } catch (e: Exception) {
            // Любые другие неожиданные исключения (например, ошибка парсинга JSON)
            Log.e(TAG, "An unexpected error occurred: ${e.localizedMessage}", e)
            NetworkResult.Error(e)
        }
    }
}
