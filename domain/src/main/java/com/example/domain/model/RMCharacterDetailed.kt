package com.example.domain.model

/**
 * Чистая модель данных для детальной информации о персонаже.
 *
 * @property character Объект RMCharacter.
 * @property origin Объект RMLocation.
 * @property location Объект RMLocation.
 * @property episodes Список объектов RMCharacterEpisodeSummary, в которых появился персонаж.
 */

data class RMCharacterDetailed(
    val character: RMCharacter,
    val origin: RMLocation,
    val location: RMLocation,
    val episodes: List<RMCharacterEpisodeSummary>
)