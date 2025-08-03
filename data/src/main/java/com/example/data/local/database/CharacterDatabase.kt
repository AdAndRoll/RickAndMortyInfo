package com.example.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.data.local.converters.CharacterTypeConverters
import com.example.data.local.dao.CharacterDao
import com.example.data.local.dao.CharacterDetailsDao
import com.example.data.local.dao.RemoteKeyDao
import com.example.data.local.entity.CharacterDetailsEntity
import com.example.data.local.entity.CharacterEntity
import com.example.data.local.entity.RemoteKeyEntity

/**
 * Абстрактный класс базы данных Room для приложения "Рик и Морти".
 *
 * @property entities Массив классов сущностей, которые будут таблицами в этой базе данных.
 * Включает [CharacterEntity] для списка персонажей, [RemoteKeyEntity] для ключей пагинации
 * и [CharacterDetailsEntity] для детальной информации о персонаже.
 * @property version Номер версии базы данных. Увеличивайте при изменении схемы.
 * @property exportSchema Если true, Room будет экспортировать схему базы данных в файл JSON.
 */
@Database(
    entities = [CharacterEntity::class, RemoteKeyEntity::class, CharacterDetailsEntity::class],
    version = 7,
    exportSchema = false
)
@TypeConverters(CharacterTypeConverters::class)
abstract class CharacterDatabase : RoomDatabase() {

    /**
     * Предоставляет DAO для доступа к данным списка персонажей.
     * @return Экземпляр [CharacterDao].
     */
    abstract fun characterDao(): CharacterDao

    /**
     * Предоставляет DAO для доступа к данным удаленных ключей пагинации.
     * @return Экземпляр [RemoteKeyDao].
     */
    abstract fun remoteKeysDao(): RemoteKeyDao

    /**
     * Предоставляет DAO для доступа к деталям персонажей.
     * @return Экземпляр [CharacterDetailsDao].
     */
    abstract fun characterDetailsDao(): CharacterDetailsDao
}
