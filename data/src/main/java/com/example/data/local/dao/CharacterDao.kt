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
    // В CharacterDao
    @Query("SELECT * FROM characters WHERE " +
            "(:name IS NULL OR LOWER(name) LIKE '%' || LOWER(:name) || '%') AND " + // Сравнение имени без учета регистра
            "(:status IS NULL OR LOWER(status) = LOWER(:status)) AND " +            // Сравнение статуса без учета регистра
            "(:species IS NULL OR LOWER(species) LIKE '%' || LOWER(:species) || '%') AND " + // Сравнение вида без учета регистра
            "(:type IS NULL OR LOWER(type) = LOWER(:type)) AND " +                  // Сравнение типа без учета регистра
            "(:gender IS NULL OR LOWER(gender) = LOWER(:gender)) " +                // Сравнение пола без учета регистра
            "ORDER BY id ASC")
    fun getCharactersPagingSource(
        name: String?, status: String?,
        species: String?,
        type: String?,
        gender: String?
    ): PagingSource<Int, CharacterEntity>
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

    /**
     * Возвращает общее количество персонажей в базе данных.
     * Этот метод необходим для проверки, пуста ли база данных.
     *
     * @return Количество записей в таблице.
     */
    @Query("SELECT COUNT(*) FROM characters")
    suspend fun getAllCharactersCount(): Int
}
