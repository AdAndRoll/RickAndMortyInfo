// app/src/main/java/com/example/data/local/dao/RemoteKeyDao.kt
package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.local.entity.RemoteKeyEntity // Убедитесь, что это новая RemoteKeyEntity

@Dao
interface RemoteKeyDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrReplace(remoteKey: RemoteKeyEntity) // Вставляем или заменяем ОДНУ запись

    @Query("SELECT * FROM remote_keys WHERE id = 0") // Получаем единственную запись с ID=0
    suspend fun getRemoteKey(): RemoteKeyEntity?

    @Query("DELETE FROM remote_keys")
    suspend fun clearAllRemoteKeys()

    @Query("SELECT createdAt FROM remote_keys WHERE id = 0") // Получаем время создания для initialize()
    suspend fun getCreationTime(): Long?
}