package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.local.entity.CharacterDetailsEntity

/**
 * Интерфейс Data Access Object (DAO) для работы с сущностью CharacterDetailsEntity.
 * Предоставляет методы для взаимодействия с таблицей "character_details".
 */
@Dao
interface CharacterDetailsDao {

    /**
     * Возвращает детали персонажа по его уникальному идентификатору.
     * @param characterId Уникальный идентификатор персонажа.
     * @return Объект CharacterDetailsEntity или null, если персонаж не найден.
     */
    @Query("SELECT * FROM character_details WHERE id = :characterId")
    suspend fun getCharacterDetails(characterId: Int): CharacterDetailsEntity?

    /**
     * Вставляет или обновляет детали персонажа.
     * Если персонаж с таким id уже существует, он будет заменен.
     * @param characterDetails Объект CharacterDetailsEntity для вставки/обновления.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCharacterDetails(characterDetails: CharacterDetailsEntity)

    /**
     * Удаляет детали персонажа по его уникальному идентификатору.
     * @param characterId Уникальный идентификатор персонажа.
     * @return Количество удаленных строк.
     */
    @Query("DELETE FROM character_details WHERE id = :characterId")
    suspend fun deleteCharacterDetails(characterId: Int): Int

    /**
     * Получает детали персонажа по его ID.
     * Возвращаемый тип теперь `CharacterDetailsEntity?`, что позволяет вернуть null,
     * если персонаж не найден в базе данных.
     *
     * @param characterId ID персонажа.
     * @return [CharacterDetailsEntity] или null, если персонаж не найден.
     */
    @Query("SELECT * FROM character_details WHERE id = :characterId")
    suspend fun getCharacterDetailsById(characterId: Int): CharacterDetailsEntity?
}
