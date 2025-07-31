package com.example.data.local.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.local.entity.CharacterEntity

/**
 * Data Access Object (DAO) для работы с сущностями персонажей [CharacterEntity].
 * Определяет методы для вставки, получения и очистки данных о персонажах в локальной базе данных.
 */
@Dao
interface CharacterDao {

    /**
     * Вставляет список персонажей в базу данных.
     * Если персонаж с таким же ID уже существует, он будет заменен (OnConflictStrategy.REPLACE).
     *
     * @param characters Список [CharacterEntity] для вставки.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCharacters(characters: List<CharacterEntity>)

    /**
     * Получает всех персонажей из базы данных в виде [PagingSource].
     * Используется Paging 3 для загрузки данных постранично.
     *
     * @return [PagingSource] с [CharacterEntity].
     */
    @Query("SELECT * FROM characters")
    fun getAllCharacters(): PagingSource<Int, CharacterEntity>

    /**
     * Очищает (удаляет) все записи из таблицы персонажей.
     */
    @Query("DELETE FROM characters")
    suspend fun clearAllCharacters()
}