// Файл: com/example/data/local/converters/CharacterTypeConverters.kt

package com.example.data.local.converters

import androidx.room.ProvidedTypeConverter
import androidx.room.TypeConverter
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import javax.inject.Inject

/**
 * Единый класс преобразователей типов для Room.
 * Он содержит все необходимые конвертеры для работы с сущностями.
 *
 * @param moshi Экземпляр Moshi, предоставленный через DI.
 */
@ProvidedTypeConverter
class CharacterTypeConverters @Inject constructor(private val moshi: Moshi) {

    // Создаем адаптер Moshi для преобразования списка строк.
    private val stringListAdapter: JsonAdapter<List<String>> = moshi.adapter(
        Types.newParameterizedType(List::class.java, String::class.java)
    )

    /**
     * Преобразует List<String> в JSON-строку с помощью Moshi для сохранения в базе данных.
     * @param list Список строк.
     * @return JSON-строка.
     */
    @TypeConverter
    fun fromStringList(list: List<String>?): String? {
        if (list == null) return null
        return stringListAdapter.toJson(list)
    }

    /**
     * Преобразует JSON-строку обратно в List<String> с помощью Moshi.
     * @param json JSON-строка.
     * @return Список строк.
     */
    @TypeConverter
    fun toStringList(json: String?): List<String>? {
        if (json == null) return null
        return stringListAdapter.fromJson(json)
    }
}
