package com.example.data.mappers


import com.example.data.local.entity.RMEpisodeEntity
import com.example.data.remote.dto.RMEpisodeDto
import com.example.domain.model.RMEpisode
import com.example.domain.model.RMCharacterEpisodeSummary

/**
 * Преобразует [RMEpisodeDto] (модель из сети) в [RMEpisodeEntity] (модель для Room).
 * Используется для сохранения данных об эпизоде в локальном кэше.
 */
fun RMEpisodeDto.toEpisodeEntity(): RMEpisodeEntity {
    return RMEpisodeEntity(
        id = this.id,
        name = this.name,
        airDate = this.airDate,
        episodeCode = this.episodeCode,
        characterUrls = this.characterUrls,
        url = this.url,
        created = this.created
    )
}

/**
 * Преобразует [RMEpisodeEntity] (модель из Room) в [RMEpisode] (доменная модель).
 * Используется для предоставления данных из локального кэша доменному слою.
 */
fun RMEpisodeEntity.toEpisode(): RMEpisode {
    return RMEpisode(
        id = this.id,
        name = this.name,
        airDate = this.airDate,
        episodeCode = this.episodeCode,
        characterUrls = this.characterUrls,
        url = this.url,
        created = this.created
    )
}

/**
 * Преобразует [RMEpisodeDto] (модель из сети) в [RMEpisode] (доменная модель).
 * Используется, если данные напрямую из сети нужно передать в доменный слой.
 */
fun RMEpisodeDto.toEpisode(): RMEpisode {
    return RMEpisode(
        id = this.id,
        name = this.name,
        airDate = this.airDate,
        episodeCode = this.episodeCode,
        characterUrls = this.characterUrls,
        url = this.url,
        created = this.created
    )
}

/**
 * Преобразует [RMEpisodeDto] (модель из сети) в [RMCharacterEpisodeSummary] (сокращенная доменная модель).
 * Он извлекает только id, имя и url, что соответствует нашей новой оптимизированной архитектуре.
 */
fun RMEpisodeDto.toEpisodeSummary(): RMCharacterEpisodeSummary {
    return RMCharacterEpisodeSummary(
        id = this.id,
        name = this.name,
        url = this.url
    )
}
