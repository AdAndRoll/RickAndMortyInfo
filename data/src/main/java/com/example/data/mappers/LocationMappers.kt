package com.example.data.mappers

import com.example.data.local.entity.LocationDetailEntity
import com.example.data.remote.dto.LocationRemoteResponse
import com.example.domain.model.LocationDetail
import com.example.domain.model.Resident // Импортируем новый класс Resident

/**
 * Расширение для преобразования сетевой модели [LocationRemoteResponse]
 * в сущность базы данных [LocationDetailEntity].
 *
 * Эта функция остается неизменной, так как сущность БД по-прежнему
 * хранит только URL-адреса, а не детали жителей.
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
 * Теперь в качестве параметра принимается список готовых объектов [Resident],
 * а не просто список имен. Это позволяет передать ID вместе с именем.
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
        residents = residents // Передаем список объектов Resident
    )
}

/**
 * Расширение для прямого преобразования сетевой модели [LocationRemoteResponse]
 * в доменную модель [LocationDetail].
 *
 * Как и в случае с сущностью БД, теперь в качестве параметра принимается
 * список готовых объектов [Resident].
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
