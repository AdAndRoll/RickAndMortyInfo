package com.example.domain.model

/**
 * Сырая модель данных для детальной информации о персонаже.
 * Используется в Domain-слое для передачи всех необходимых данных
 * от репозитория к Use Case.
 *
 * @property character Объект RMCharacter.
 * @property origin Объект RMLocation.
 * @property location Объект RMLocation.
 * @property episodeUrls Список URL-адресов эпизодов, в которых появился персонаж.
 */
data class RMCharacterDetailsRaw(
    val character: RMCharacter,
    val origin: RMLocation,
    val location: RMLocation,
    val episodeUrls: List<String>
)
