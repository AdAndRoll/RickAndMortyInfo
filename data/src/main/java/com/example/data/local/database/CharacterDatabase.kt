// data/local/database/CharacterDatabase.kt
package com.example.data.local.database // Обновленный пакет!

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.data.local.dao.CharacterDao
import com.example.data.local.dao.RemoteKeyDao

import com.example.data.local.entity.CharacterEntity
import com.example.data.local.entity.RemoteKeyEntity

/**
 * Абстрактный класс базы данных Room для приложения "Рик и Морти".
 * Определяет сущности (таблицы) и DAO (методы доступа к данным).
 *
 * @property entities Массив классов сущностей, которые будут таблицами в этой базе данных.
 * Включает [CharacterEntity] для персонажей и [RemoteKeyEntity] для ключей пагинации.
 * @property version Номер версии базы данных. Увеличивайте при изменении схемы.
 * @property exportSchema Если true, Room будет экспортировать схему базы данных в файл JSON
 * для контроля версий и проверки. В производственной среде обычно false.
 */
@Database(
    entities = [CharacterEntity::class, RemoteKeyEntity::class],
    version = 6,
    exportSchema = false
)
abstract class CharacterDatabase : RoomDatabase() {

    /**
     * Предоставляет DAO для доступа к данным персонажей.
     * @return Экземпляр [CharacterDao].
     */
    abstract fun characterDao(): CharacterDao

    /**
     * Предоставляет DAO для доступа к данным удаленных ключей пагинации.
     * @return Экземпляр [RemoteKeyDao].
     */
    abstract fun remoteKeysDao(): RemoteKeyDao
}