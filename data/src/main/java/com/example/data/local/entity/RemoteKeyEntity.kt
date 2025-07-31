package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Сущность Room для хранения удаленных ключей пагинации.
 * Используется [androidx.paging.RemoteMediator] для отслеживания
 * следующей/предыдущей страницы, которую нужно загрузить из API.
 *
 * @param characterId ID персонажа, к которому относится данный ключ. Является первичным ключом.
 * @param prevKey Номер предыдущей страницы, или null, если это первая страница.
 * @param nextKey Номер следующей страницы, или null, если это последняя страница.
 */
@Entity(tableName = "remote_keys")
data class RemoteKeyEntity(
    @PrimaryKey val characterId: Int, // Здесь id персонажа, а не страницы.
    // Это позволяет связать remote key с конкретным элементом данных.
    val prevKey: Int?,
    val nextKey: Int?
)