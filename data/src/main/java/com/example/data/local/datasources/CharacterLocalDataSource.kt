package com.example.data.local.datasources

import androidx.paging.PagingSource
import com.example.data.local.dao.CharacterDao
import com.example.data.local.dao.RemoteKeyDao
import com.example.data.local.entity.CharacterEntity
import com.example.data.local.entity.RemoteKeyEntity
import com.example.domain.model.CharacterFilter

import javax.inject.Inject

class CharacterLocalDataSource @Inject constructor(
    private val characterDao: CharacterDao,
    private val remoteKeysDao: RemoteKeyDao
) {
    suspend fun insertCharacters(characters: List<CharacterEntity>) {
        characterDao.insertCharacters(characters)
    }

    fun getCharactersPagingSource(filter: CharacterFilter): PagingSource<Int, CharacterEntity> {
        return characterDao.getCharactersPagingSource(
            name = filter.name.takeIf { it?.isNotBlank() == true },
            status = filter.status.takeIf { it?.isNotBlank() == true },
            species = filter.species.takeIf { it?.isNotBlank() == true },
            gender = filter.gender.takeIf { it?.isNotBlank() == true },
            type = filter.type.takeIf { it?.isNotBlank() ==true }
        )
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
