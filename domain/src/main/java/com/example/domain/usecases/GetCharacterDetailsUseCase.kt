package com.example.domain.usecases

import com.example.domain.model.RMCharacterDetailed
import com.example.domain.model.RMCharacterEpisodeSummary
import com.example.domain.model.RMCharacterDetailsRaw
import com.example.domain.repository.CharacterRepository
import com.example.domain.utils.Result


/**
 * Use Case для получения полной детальной информации о персонаже, включая эпизоды.
 * Этот Use Case выступает в роли оркестратора, координируя запросы к разным репозиториям
 * для сборки полной модели RMCharacterDetailed.
 *
 * @property characterRepository Репозиторий для получения данных о персонаже.
 * @property getCharacterEpisodesUseCase Use Case для получения данных об эпизодов.
 */
class GetCharacterDetailsUseCase(
    private val characterRepository: CharacterRepository,
    private val getCharacterEpisodesUseCase: GetCharacterEpisodesUseCase
) {
    /**
     * Выполняет бизнес-логику для получения деталей персонажа.
     *
     * @param characterId ID персонажа, информацию о котором нужно получить.
     * @return [Result] с [RMCharacterDetailed] в случае успеха, или [Throwable] в случае ошибки.
     */
    suspend fun execute(characterId: Int): Result<RMCharacterDetailed> {

        return when (val characterResult = characterRepository.getCharacterDetails(characterId)) {
            is Result.Success -> {
                val rawData = characterResult.data

                when (val episodesResult = getCharacterEpisodesUseCase.execute(rawData.episodeUrls)) {
                    is Result.Success -> {

                        val updatedDetailedCharacter = RMCharacterDetailed(
                            character = rawData.character,
                            origin = rawData.origin,
                            location = rawData.location,
                            episodes = episodesResult.data
                        )
                        Result.Success(updatedDetailedCharacter)
                    }
                    is Result.Error -> Result.Error(episodesResult.exception)
                }
            }
            is Result.Error -> Result.Error(characterResult.exception)
        }
    }
}
