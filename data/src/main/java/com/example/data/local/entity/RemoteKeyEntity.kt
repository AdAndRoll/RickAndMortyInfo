package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "remote_keys")
data class RemoteKeyEntity(

    @PrimaryKey(autoGenerate = false)
    val id: Int = 0,

    val prevKey: Int?,
    val nextKey: Int?,
    val createdAt: Long,

    val filterName: String? = null,
    val filterStatus: String? = null,
    val filterSpecies: String? = null,
    val filterType: String? = null,
    val filterGender: String? = null
)