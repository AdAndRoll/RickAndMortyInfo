package com.example.data.local.episodes.datasources

import com.example.data.local.dao.RMEpisodeDao
import com.example.data.local.entity.RMEpisodeEntity
import javax.inject.Inject

/**
 * Источник данных для локального хранилища (базы данных Room).
 * Инкапсулирует логику операций с базой данных для сущностей эпизодов.
 *
 * @param dao Объект доступа к данным для эпизодов.
 */
class EpisodeLocalDataSource @Inject constructor(
    private val dao: RMEpisodeDao
) {
    /**
     * Получает эпизод из локальной базы данных по его ID.
     * @param episodeId ID эпизода.
     * @return Объект RMEpisodeEntity или null, если эпизод не найден.
     */
    suspend fun getEpisode(episodeId: Int): RMEpisodeEntity? {
        return dao.getEpisode(episodeId)
    }

    /**
     * Получает список эпизодов из локальной базы данных по списку их ID.
     * @param episodeIds Список ID эпизодов.
     * @return Список объектов RMEpisodeEntity.
     */
    suspend fun getEpisodes(episodeIds: List<Int>): List<RMEpisodeEntity> {
        return dao.getEpisodes(episodeIds)
    }

    /**
     * Вставляет один эпизод в локальную базу данных.
     * @param episode Сущность эпизода.
     */
    suspend fun insertEpisode(episode: RMEpisodeEntity) {
        dao.insertEpisode(episode)
    }

    /**
     * Вставляет список эпизодов в локальную базу данных.
     * Это полезно, когда мы получаем сразу несколько эпизодов из сети.
     * @param episodes Список сущностей эпизодов.
     */
    suspend fun insertEpisodes(episodes: List<RMEpisodeEntity>) {
        dao.insertAll(episodes)
    }
}