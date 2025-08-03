package com.example.data.local.datasources

import com.example.data.local.dao.CharacterDetailsDao
import com.example.data.local.entity.CharacterDetailsEntity
import javax.inject.Inject

/**
 * Локальный источник данных для получения детальной информации о персонаже.
 *
 * Этот класс отвечает за взаимодействие с базой данных Room
 * для получения одного конкретного персонажа по его ID, используя [CharacterDetailsDao].
 *
 * @property characterDetailsDao Объект для доступа к данным деталей персонажей в БД.
 */
class CharacterDetailsLocalDataSource @Inject constructor(
    private val characterDetailsDao: CharacterDetailsDao
) {
    /**
     * Получает детали персонажа из локальной базы данных по его ID.
     * Эта suspend-функция подходит для однократного получения данных.
     *
     * @param characterId Уникальный идентификатор персонажа.
     * @return Объект [CharacterDetailsEntity] или null, если персонаж не найден.
     */
    suspend fun getCharacterDetails(characterId: Int): CharacterDetailsEntity? {
        return characterDetailsDao.getCharacterDetails(characterId)
    }

    /**
     * Сохраняет детали персонажа в локальной базе данных.
     *
     * @param characterDetails Объект [CharacterDetailsEntity] для вставки или обновления.
     */
    suspend fun insertCharacterDetails(characterDetails: CharacterDetailsEntity) {
        characterDetailsDao.insertCharacterDetails(characterDetails)
    }
}
