package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.local.entity.RMEpisodeEntity

/**
 * Объект доступа к данным (DAO) для работы с сущностью RMEpisodeEntity.
 */
@Dao
interface RMEpisodeDao {
    /**
     * Вставляет эпизод в базу данных или заменяет его, если он уже существует.
     * @param episode Объект RMEpisodeEntity.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEpisode(episode: RMEpisodeEntity)

    /**
     * Вставляет список эпизодов в базу данных или заменяет их, если они уже существуют.
     * @param episodes Список объектов RMEpisodeEntity.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(episodes: List<RMEpisodeEntity>)
    /**
     * Получает эпизод по его ID.
     * @param episodeId ID эпизода.
     * @return Объект RMEpisodeEntity.
     */
    @Query("SELECT * FROM episodes WHERE id = :episodeId")
    suspend fun getEpisode(episodeId: Int): RMEpisodeEntity?

    /**
     * Получает список эпизодов по списку их ID.
     * @param episodeIds Список ID эпизодов.
     * @return Список объектов RMEpisodeEntity.
     */
    @Query("SELECT * FROM episodes WHERE id IN (:episodeIds)")
    suspend fun getEpisodes(episodeIds: List<Int>): List<RMEpisodeEntity>
}
