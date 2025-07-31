package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.local.entity.RemoteKeyEntity

/**
 * Data Access Object (DAO) для работы с сущностями удаленных ключей пагинации [RemoteKeyEntity].
 * Используется [androidx.paging.RemoteMediator] для управления ключами пагинации.
 */
@Dao
interface RemoteKeyDao {

    /**
     * Вставляет список удаленных ключей в базу данных.
     * Если ключ с таким же ID персонажа уже существует, он будет заменен.
     *
     * @param remoteKeys Список [RemoteKeyEntity] для вставки.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllRemoteKeys(remoteKeys: List<RemoteKeyEntity>)

    /**
     * Получает удаленный ключ для конкретного персонажа по его ID.
     *
     * @param characterId ID персонажа, для которого нужно получить ключ.
     * @return [RemoteKeyEntity] или null, если ключ не найден.
     */
    @Query("SELECT * FROM remote_keys WHERE characterId = :characterId")
    suspend fun getRemoteKeyByCharacterId(characterId: Int): RemoteKeyEntity?

    /**
     * Очищает (удаляет) все записи из таблицы удаленных ключей.
     */
    @Query("DELETE FROM remote_keys")
    suspend fun clearAllRemoteKeys()
}