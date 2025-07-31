package com.example.data.local.datasources // Новый пакет!

import androidx.paging.PagingSource
import com.example.data.local.dao.CharacterDao
import com.example.data.local.dao.RemoteKeyDao
import com.example.data.local.entity.CharacterEntity
import com.example.data.local.entity.RemoteKeyEntity
import javax.inject.Inject

/**
 * Локальный источник данных для персонажей.
 * Отвечает за взаимодействие с базой данных Room для кэширования и получения данных.
 *
 * @param characterDao DAO для доступа к данным персонажей.
 * @param remoteKeysDao DAO для доступа к данным удаленных ключей пагинации.
 */
class CharacterLocalDataSource @Inject constructor(
    private val characterDao: CharacterDao,
    private val remoteKeysDao: RemoteKeyDao
) {
    /**
     * Вставляет список персонажей в локальную базу данных.
     *
     * @param characters Список [CharacterEntity] для вставки.
     */
    suspend fun insertCharacters(characters: List<CharacterEntity>) {
        characterDao.insertCharacters(characters)
    }

    /**
     * Получает всех персонажей из локальной базы данных в виде [PagingSource].
     * Этот метод используется Paging 3 для загрузки кэшированных данных.
     *
     * @return [PagingSource] с [CharacterEntity].
     */
    fun getAllCharacters(): PagingSource<Int, CharacterEntity> {
        return characterDao.getAllCharacters()
    }

    /**
     * Очищает все записи о персонажах в локальной базе данных.
     */
    suspend fun clearAllCharacters() {
        characterDao.clearAllCharacters()
    }

    /**
     * Вставляет список удаленных ключей пагинации в локальную базу данных.
     *
     * @param remoteKeys Список [RemoteKeyEntity] для вставки.
     */
    suspend fun insertAllRemoteKeys(remoteKeys: List<RemoteKeyEntity>) {
        remoteKeysDao.insertAllRemoteKeys(remoteKeys)
    }

    /**
     * Получает удаленный ключ для конкретного персонажа из локальной базы данных.
     *
     * @param characterId ID персонажа, для которого нужно получить ключ.
     * @return [RemoteKeyEntity] или null, если ключ не найден.
     */
    suspend fun getRemoteKeyByCharacterId(characterId: Int): RemoteKeyEntity? {
        return remoteKeysDao.getRemoteKeyByCharacterId(characterId)
    }

    /**
     * Очищает все записи об удаленных ключах пагинации в локальной базе данных.
     */
    suspend fun clearAllRemoteKeys() {
        remoteKeysDao.clearAllRemoteKeys()
    }
}