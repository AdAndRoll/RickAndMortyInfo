package com.example.data.local.datasources

import androidx.paging.PagingSource
import com.example.data.local.dao.CharacterDao
import com.example.data.local.dao.RemoteKeyDao
import com.example.data.local.entity.CharacterEntity
import com.example.data.local.entity.RemoteKeyEntity
import javax.inject.Inject

class CharacterLocalDataSource @Inject constructor(
    private val characterDao: CharacterDao,
    private val remoteKeysDao: RemoteKeyDao
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
        remoteKeysDao.insertOrReplace(remoteKey)
    }

    suspend fun getRemoteKey(): RemoteKeyEntity? {
        return remoteKeysDao.getRemoteKey()
    }

    suspend fun clearAllRemoteKeys() {
        remoteKeysDao.clearAllRemoteKeys()
    }

    // --- ДОБАВЛЕН НОВЫЙ МЕТОД ---
    suspend fun getAllCharactersCount(): Int {
        return characterDao.getAllCharactersCount()
    }
}
