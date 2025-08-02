package com.example.data.mappers

import com.example.data.local.entity.CharacterEntity
import com.example.data.remote.dto.CharacterDto
import com.example.domain.model.RMCharacter
import com.example.domain.model.RMCharacterDetailed
import com.example.domain.model.LocationRM

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
        type = this.type,
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
        type = this.type,
        status = this.status,
        gender = this.gender,
        imageUrl = this.imageUrl
    )
}

/**
 * Преобразует [CharacterDto] (модель из сети) в [RMCharacter] (доменная модель).
 * Используется, если данные напрямую из сети нужно передать в доменный слой
 * (например, для детальной информации, которая не кэшируется).
 *
 * Я переименовал эту функцию в `toCharacter()`, чтобы сделать ее более
 * единообразной с маппером для `CharacterEntity`.
 */
fun CharacterDto.toCharacter(): RMCharacter {
    return RMCharacter(
        id = this.id,
        name = this.name,
        species = this.species,
        type = this.type,
        status = this.status,
        gender = this.gender,
        imageUrl = this.image
    )
}

/**
 * Расширяющая функция для преобразования CharacterDto в RMCharacterDetailed.
 * Используется для получения полной информации о персонаже с детального экрана.
 */
fun CharacterDto.toCharacterDetailed(): RMCharacterDetailed {
    // Используем уже существующий маппер для создания базового объекта
    val basicCharacter = this.toCharacter()
    return RMCharacterDetailed(
        character = basicCharacter,
        origin = LocationRM(origin.name, origin.url),
        location = LocationRM(location.name, location.url),
        episode = episode
    )
}
