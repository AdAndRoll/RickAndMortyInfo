package com.example.data.mappers

import com.example.data.local.entity.CharacterEntity
import com.example.data.remote.dto.CharacterDto
import com.example.domain.model.RMCharacter // Импорт доменной модели

/**
 * Маппер для преобразования DTO и Entity моделей в доменные модели и обратно.
 * Использует функции расширения для удобства.
 */

/**
 * Преобразует [CharacterDto] (модель из сети) в [CharacterEntity] (модель для Room).
 * Используется для сохранения сетевых данных в локальной базе.
 */
fun CharacterDto.toCharacterEntity(): CharacterEntity {
    return CharacterEntity(
        id = this.id,
        name = this.name,
        species = this.species,
        status = this.status,
        gender = this.gender,
        imageUrl = this.image
    )
}

/**
 * Преобразует [CharacterEntity] (модель из Room) в [RMCharacter] (доменная модель).
 * Используется для предоставления данных из локального кэша доменному слою.
 */
fun CharacterEntity.toCharacter(): RMCharacter {
    return RMCharacter(
        id = this.id,
        name = this.name,
        species = this.species,
        status = this.status,
        gender = this.gender,
        imageUrl = this.imageUrl
    )
}

/**
 * Преобразует [CharacterDto] (модель из сети) в [RMCharacter] (доменная модель).
 * Используется, если данные напрямую из сети нужно передать в доменный слой
 * (например, если нет необходимости их кэшировать или для детальной информации,
 * которая не хранится в сокращенном CharacterEntity).
 *
 * Хотя для списка мы всегда будем работать через Room, эта функция может быть полезна
 * для других сценариев или для прямого использования в репозитории для некоторых операций.
 */
fun CharacterDto.toCharacterDomain(): RMCharacter {
    return RMCharacter(
        id = this.id,
        name = this.name,
        species = this.species,
        status = this.status,
        gender = this.gender,
        imageUrl = this.image
    )
}