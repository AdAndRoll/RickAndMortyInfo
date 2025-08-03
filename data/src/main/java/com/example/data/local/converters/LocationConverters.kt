package com.example.data.local.converters

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

/**
 * Преобразователи типов для Room, чтобы сохранять сложные объекты.
 * Теперь корректно работает со списком URL-адресов резидентов как со строками.
 */
class LocationConverters {
    private val gson = Gson()

    @TypeConverter
    fun fromResidentsList(list: List<String>): String {
        return gson.toJson(list)
    }

    @TypeConverter
    fun toResidentsList(json: String): List<String> {
        val type = object : TypeToken<List<String>>() {}.type
        return gson.fromJson(json, type)
    }
}
