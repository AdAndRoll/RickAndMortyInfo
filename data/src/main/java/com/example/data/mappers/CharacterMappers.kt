package com.example.data.mappers


import com.example.data.local.entity.CharacterDetailsEntity
import com.example.data.local.entity.CharacterDetailsLocation
import com.example.data.local.entity.CharacterEntity
import com.example.data.remote.dto.CharacterDto
import com.example.domain.model.RMCharacter
import com.example.domain.model.RMCharacterDetailsRaw
import com.example.domain.model.RMLocation


/**
 * Преобразует [CharacterDto] (модель из сети) в [CharacterEntity] (модель для Room).
 * Используется для сохранения данных о персонаже в локальном кэше.
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
 * Используется для получения данных напрямую из сети, когда кэширование не требуется.
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
 * Преобразует [CharacterDto] (модель из сети) в [RMCharacterDetailsRaw] (доменная "сырая" модель).
 * Это ключевой маппер для нашего нового подхода. Он извлекает все необходимые данные
 * из DTO, включая список URL-адресов эпизодов, и передаёт их в доменный слой.
 */
fun CharacterDto.toCharacterDetailsRaw(): RMCharacterDetailsRaw {
    return RMCharacterDetailsRaw(
        character = this.toCharacter(),
        origin = RMLocation(origin.name, origin.url),
        location = RMLocation(location.name, location.url),
        episodeUrls = this.episode // Маппим список URL-адресов
    )
}


/**
 * Преобразует [CharacterDto] (модель из сети) в [CharacterDetailsEntity] (модель для Room).
 * Используется для сохранения полной информации о персонаже в локальный кэш.
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
        location = CharacterDetailsLocation(location.name, location.url),
        episodeUrls = this.episode
    )
}

/**
 * Преобразует [CharacterDetailsEntity] (модель из Room) в [RMCharacterDetailsRaw] (доменная "сырая" модель).
 * Этот маппер используется для извлечения полной информации о персонаже из кэша.
 * Он предоставляет Use Case все необходимые данные, включая список URL-адресов.
 */
fun CharacterDetailsEntity.toCharacterDetailsRaw(): RMCharacterDetailsRaw {
    val basicCharacter = RMCharacter(
        id = this.id,
        name = this.name,
        species = this.species,
        type = this.type,
        status = this.status,
        gender = this.gender,
        imageUrl = this.imageUrl
    )
    return RMCharacterDetailsRaw(
        character = basicCharacter,
        origin = RMLocation(origin.name, origin.url),
        location = RMLocation(location.name, location.url),
        episodeUrls = this.episodeUrls
    )
}
