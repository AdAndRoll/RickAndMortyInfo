// app/src/main/java/com/example/data/local/entity/RemoteKeyEntity.kt
package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "remote_keys")
data class RemoteKeyEntity(
    // Мы будем использовать фиксированный ID (например, 0), чтобы всегда работать с одной и той же записью.
    @PrimaryKey(autoGenerate = false)
    val id: Int = 0, // Это сделает ее единственной записью в таблице remote_keys

    val prevKey: Int?, // Предыдущая страница (если нужна прокрутка вверх)
    val nextKey: Int?, // Следующая страница для загрузки
    val createdAt: Long,// Метка времени для определения "свежести" кэша
    // !!! НОВЫЕ ПОЛЯ: Добавлены поля для сохранения параметров фильтра
    val filterName: String? = null,
    val filterStatus: String? = null,
    val filterSpecies: String? = null,
    val filterType: String? = null,
    val filterGender: String? = null
)