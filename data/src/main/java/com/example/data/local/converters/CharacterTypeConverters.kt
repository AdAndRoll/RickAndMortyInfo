package com.example.data.local.converters

import androidx.room.ProvidedTypeConverter
import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

/**
 * TypeConverter для преобразования списка строк в JSON-строку и обратно.
 * Используется Room для сохранения `List<String>` в базе данных.
 */
@ProvidedTypeConverter
class CharacterTypeConverters(private val gson: Gson) {

    /**
     * Преобразует JSON-строку обратно в List<String>.
     *
     * @param value JSON-строка.
     * @return List<String>.
     */
    @TypeConverter
    fun fromStringList(value: String): List<String> {
        // Определяем тип для Gson
        val listType = object : TypeToken<List<String>>() {}.type
        // Преобразуем JSON-строку обратно в список
        return gson.fromJson(value, listType)
    }

    /**
     * Преобразует List<String> в JSON-строку для сохранения в базе данных.
     *
     * @param list Список строк.
     * @return JSON-строка.
     */
    @TypeConverter
    fun toStringList(list: List<String>): String {
        // Преобразуем список в JSON-строку
        return gson.toJson(list)
    }
}
