// package com.example.data.local.datasources
package com.example.data.local.datasources

import androidx.paging.PagingSource
import com.example.data.local.dao.CharacterDao
import com.example.data.local.dao.RemoteKeyDao // Новый RemoteKeyDao
import com.example.data.local.entity.CharacterEntity
import com.example.data.local.entity.RemoteKeyEntity // Новая RemoteKeyEntity
import javax.inject.Inject

class CharacterLocalDataSource @Inject constructor(
    private val characterDao: CharacterDao,
    private val remoteKeysDao: RemoteKeyDao // Теперь это новый RemoteKeyDao
) {
    suspend fun insertCharacters(characters: List<CharacterEntity>) {
        characterDao.insertCharacters(characters)
    }

    fun getAllCharacters(): PagingSource<Int, CharacterEntity> {
        return characterDao.getAllCharacters()
    }

    suspend fun clearAllCharacters() {
        characterDao.clearAllCharacters()
    }

    // --- Измененные методы для RemoteKey ---
    suspend fun insertRemoteKey(remoteKey: RemoteKeyEntity) {
        remoteKeysDao.insertOrReplace(remoteKey) // Используем insertOrReplace для одной записи
    }

    suspend fun getRemoteKey(): RemoteKeyEntity? {
        return remoteKeysDao.getRemoteKey() // Получаем единственную запись
    }

    suspend fun clearAllRemoteKeys() {
        remoteKeysDao.clearAllRemoteKeys()
    }

    // Этот метод больше не нужен, так как RemoteKeyEntity не привязана к characterId
    // suspend fun getRemoteKeyByCharacterId(characterId: Int): RemoteKeyEntity? {
    //     return remoteKeysDao.getRemoteKeyByCharacterId(characterId) // Этот метод удален из RemoteKeyDao
    // }
}