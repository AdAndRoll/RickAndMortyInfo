package com.example.data.remote.api

import com.example.data.remote.dto.CharactersResponse
import com.example.data.remote.dto.CharacterDto
import retrofit2.http.GET
import retrofit2.http.Query

interface RickAndMortyApi {

    /**
     * Получает список персонажей из API.
     * Поддерживает пагинацию и различные параметры фильтрации.
     *
     * @param page Номер страницы для пагинации.
     * @param name Фильтр по имени персонажа.
     * @param status Фильтр по статусу персонажа (e.g., "alive", "dead", "unknown").
     * @param species Фильтр по виду персонажа (e.g., "Human", "Alien").
     * @param gender Фильтр по полу персонажа (e.g., "Male", "Female", "Genderless", "unknown").
     * @return Объект [CharactersResponse], содержащий информацию о пагинации и список [CharacterDto].
     */
    @GET("character")
    suspend fun getCharacters(
        @Query("page") page: Int,
        @Query("name") name: String? = null,
        @Query("status") status: String? = null,
        @Query("species") species: String? = null,
        @Query("type") type: String? = null,       // Этот параметр для "типа", а не "пола"
        @Query("gender") gender: String? = null    // <--- Вот этот параметр для "пола"
    ): CharactersResponse // Замените на вашу модель ответа
}