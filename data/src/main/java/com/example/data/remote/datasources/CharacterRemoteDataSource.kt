package com.example.data.remote.datasources

import android.util.Log
import com.example.data.remote.api.RickAndMortyApi
import com.example.data.remote.dto.CharacterDto
import com.example.data.remote.dto.CharactersResponse
import com.example.data.utils.NetworkResult
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
        type: String?,    // <--- Должен быть этот параметр
        gender: String? = null
    ): NetworkResult<CharactersResponse> {
        return try {
            Log.d(TAG, "Making API call for page $page with filters: name=$name, status=$status, species=$species, gender=$gender")

            // Выполняем запрос к API. Если он успешен, возвращаем NetworkResult.Success.
            // Если возникнет ошибка HTTP или сети, Retrofit бросит исключение,
            // которое будет поймано в соответствующих блоках catch.
            val response = api.getCharacters( // Ваш Retrofit сервис
                page = page,
                name = name,
                status = status,
                species = species,
                type = type,    // <--- Передаем в @Query("type")
                gender = gender // <--- Передаем в @Query("gender")
            )
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

    /**
     * Получает полную информацию об одном персонаже по его ID.
     *
     * @param characterId ID персонажа, информацию о котором нужно получить.
     * @return [NetworkResult] с [CharacterDto] в случае успеха, или [Throwable] в случае ошибки.
     */
    suspend fun getCharacterById(characterId: Int): NetworkResult<CharacterDto> {
        return try {
            Log.d(TAG, "Making API call for character details with ID: $characterId")
            val response = api.getCharacterById(characterId)
            Log.d(TAG, "API call successful for character ID: $characterId.")
            NetworkResult.Success(response)
        } catch (e: IOException) {
            Log.e(TAG, "Network or I/O error occurred fetching details for ID $characterId: ${e.localizedMessage}")
            NetworkResult.Error(e)
        } catch (e: HttpException) {
            Log.e(TAG, "HTTP error occurred fetching details for ID $characterId: ${e.code()} - ${e.localizedMessage}")
            NetworkResult.Error(e)
        } catch (e: Exception) {
            Log.e(TAG, "An unexpected error occurred fetching details for ID $characterId: ${e.localizedMessage}", e)
            NetworkResult.Error(e)
        }
    }

    /**
     * Получает полную информацию о нескольких персонажах по списку их ID.
     * Этот метод объединяет ID в строку и делает один сетевой запрос.
     *
     * @param characterIds Список ID персонажей.
     * @return [NetworkResult] со списком [CharacterDto] в случае успеха, или [Throwable] в случае ошибки.
     */
    suspend fun getCharactersByIds(characterIds: List<Int>): NetworkResult<List<CharacterDto>> {
        return try {
            val idsString = characterIds.joinToString(separator = ",")
            Log.d(TAG, "Making API call for characters with IDs: $idsString")

            // Retrofit и Moshi возвращают List<CharacterDto> даже для одного ID.
            val response = api.getCharactersByIds(idsString)

            Log.d(TAG, "API call successful for IDs: $idsString. Found ${response.size} characters.")
            NetworkResult.Success(response)

        } catch (e: IOException) {
            Log.e(TAG, "Network or I/O error occurred fetching multiple characters: ${e.localizedMessage}")
            NetworkResult.Error(e)
        } catch (e: HttpException) {
            Log.e(TAG, "HTTP error occurred fetching multiple characters: ${e.code()} - ${e.localizedMessage}")
            NetworkResult.Error(e)
        } catch (e: Exception) {
            Log.e(TAG, "An unexpected error occurred fetching multiple characters: ${e.localizedMessage}", e)
            NetworkResult.Error(e)
        }
    }
}
