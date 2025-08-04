package com.example.data.mappers

import com.example.data.local.entity.LocationDetailEntity
import com.example.data.remote.dto.LocationRemoteResponse
import com.example.domain.model.LocationDetail
import com.example.domain.model.Resident // Импортируем новый класс Resident

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
        residents = this.residents,
        url = this.url,
        created = this.created
    )
}

/**
 * Расширение для преобразования сущности базы данных [LocationDetailEntity]
 * в доменную модель [LocationDetail].
 *
 * @param residents Список объектов [Resident], содержащих ID и имя.
 * @return Объект [LocationDetail] для использования в UI.
 */
fun LocationDetailEntity.toDomainModel(residents: List<Resident>): LocationDetail {
    return LocationDetail(
        id = this.id,
        name = this.name,
        type = this.type,
        dimension = this.dimension,
        residents = residents
    )
}

/**
 * Расширение для прямого преобразования сетевой модели [LocationRemoteResponse]
 * в доменную модель [LocationDetail].
 *
 * @param residents Список объектов [Resident], содержащих ID и имя.
 * @return Объект [LocationDetail] для использования в слоях UI и бизнес-логики.
 */
fun LocationRemoteResponse.toDomainModel(residents: List<Resident>): LocationDetail {
    return LocationDetail(
        id = this.id,
        name = this.name,
        type = this.type,
        dimension = this.dimension,
        residents = residents
    )
}
