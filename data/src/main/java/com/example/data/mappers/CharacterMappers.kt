package com.example.data.mappers

import com.example.data.local.entity.CharacterDetailsEntity
import com.example.data.local.entity.CharacterDetailsLocation
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
 * Используется для сохранения сетевых данных в локальной базе для пагинации.
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
 * Используется, если данные напрямую из сети нужно передать в доменный слой.
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
 * Используется для получения полной информации о персонаже с детального экрана из сети.
 */
fun CharacterDto.toCharacterDetailed(): RMCharacterDetailed {
    return RMCharacterDetailed(
        character = this.toCharacter(),
        origin = LocationRM(origin.name, origin.url),
        location = LocationRM(location.name, location.url),
        episode = episode
    )
}

// --- Новые мапперы для кэширования деталей персонажа ---

/**
 * Преобразует [CharacterDto] (модель из сети) в [CharacterDetailsEntity] (модель для Room).
 * Этот маппер используется для сохранения полной информации о персонаже в локальный кэш.
 * Теперь он напрямую использует List<String> благодаря TypeConverter.
 */
fun CharacterDto.toCharacterDetailsEntity(): CharacterDetailsEntity {
    return CharacterDetailsEntity(
        id = this.id,
        name = this.name,
        species = this.species,
        type = this.type,
        status = this.status,
        gender = this.gender,
        imageUrl = this.image,
        origin = CharacterDetailsLocation(origin.name, origin.url),
        location = CharacterDetailsLocation (location.name, location.url),
        episodeUrls = this.episode // Теперь передаем список напрямую
    )
}

/**
 * Преобразует [CharacterDetailsEntity] (модель из Room) в [RMCharacterDetailed] (доменная модель).
 * Этот маппер используется для извлечения полной информации о персонаже из кэша.
 * Теперь он напрямую получает List<String> из сущности.
 */
fun CharacterDetailsEntity.toCharacterDetailed(): RMCharacterDetailed {
    val basicCharacter = RMCharacter(
        id = this.id,
        name = this.name,
        species = this.species,
        type = this.type,
        status = this.status,
        gender = this.gender,
        imageUrl = this.imageUrl
    )
    return RMCharacterDetailed(
        character = basicCharacter,
        origin = LocationRM(origin.name, origin.url),
        location = LocationRM(location.name, location.url),
        episode = this.episodeUrls // Теперь получаем список напрямую
    )
}
