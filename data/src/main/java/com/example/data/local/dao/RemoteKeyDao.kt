package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.local.entity.RemoteKeyEntity // Убедитесь, что это новая RemoteKeyEntity

@Dao
interface RemoteKeyDao {

    /**
     * Вставляет или заменяет существующую запись RemoteKeyEntity.
     * Эта функция будет автоматически работать с новой сущностью,
     * содержащей поля для фильтров.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrReplace(remoteKey: RemoteKeyEntity)

    /**
     * Получает единственную запись RemoteKeyEntity из таблицы,
     * включая параметры фильтра, которые были использованы для её создания.
     */
    @Query("SELECT * FROM remote_keys WHERE id = 0")
    suspend fun getRemoteKey(): RemoteKeyEntity?

    /**
     * Очищает всю таблицу с ключами пагинации.
     * Используется при обновлении данных.
     */
    @Query("DELETE FROM remote_keys")
    suspend fun clearAllRemoteKeys()

    // !!! ИЗМЕНЕНИЕ: Убран метод getCreationTime(), так как
    // время создания теперь доступно через getRemoteKey().
}
