package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey


/**
 * Сущность (Entity) для хранения информации об эпизоде в локальной базе данных Room.
 */
@Entity(tableName = "episodes")
data class RMEpisodeEntity(
    @PrimaryKey
    val id: Int,
    val name: String,
    val airDate: String?,
    val episodeCode: String,
    val characterUrls: List<String>,
    val url: String,
    val created: String
)