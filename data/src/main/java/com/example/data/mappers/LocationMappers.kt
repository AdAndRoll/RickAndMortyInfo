package com.example.data.mappers

import com.example.data.local.entity.CharacterDetailsEntity
import com.example.data.local.entity.LocationDetailEntity
import com.example.data.remote.dto.CharacterDto
import com.example.data.remote.dto.LocationRemoteResponse
import com.example.domain.model.LocationDetail

/**
 * Расширение для преобразования сетевой модели [LocationRemoteResponse]
 * в сущность базы данных [LocationDetailEntity].
 *
 * @return Объект [LocationDetailEntity], готовый к сохранению в базе данных Room.
 */
fun LocationRemoteResponse.toEntity(): LocationDetailEntity {
    return LocationDetailEntity(
        id = this.id,
        name = this.name,
        type = this.type,
        dimension = this.dimension,
        residents = this.residents, // Сохраняем список URL-адресов резидентов
        url = this.url,
        created = this.created
    )
}

/**
 * Расширение для преобразования сущности базы данных [LocationDetailEntity]
 * в доменную модель [LocationDetail].
 *
 * @param residentNames Список имён резидентов.
 * @return Объект [LocationDetail] для использования в UI.
 */
fun LocationDetailEntity.toDomainModel(residentNames: List<String>): LocationDetail {
    return LocationDetail(
        id = this.id,
        name = this.name,
        type = this.type,
        dimension = this.dimension,
        residentNames = residentNames // Передаем список имен
    )
}

/**
 * Расширение для прямого преобразования сетевой модели [LocationRemoteResponse]
 * в доменную модель [LocationDetail].
 *
 * @param residentNames Список имён резидентов.
 * @return Объект [LocationDetail] для использования в слоях UI и бизнес-логики.
 */
fun LocationRemoteResponse.toDomainModel(residentNames: List<String>): LocationDetail {
    return LocationDetail(
        id = this.id,
        name = this.name,
        type = this.type,
        dimension = this.dimension,
        residentNames = residentNames
    )
}


