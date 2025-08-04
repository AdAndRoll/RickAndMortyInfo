package com.example.domain.usecases

import com.example.domain.model.RMCharacterDetailed
import com.example.domain.repository.CharacterRepository
import com.example.domain.utils.Result

/**
 * Use case для получения детальной информации об одном персонаже.
 * Инкапсулирует логику получения данных, делая ViewModel более чистым.
 * @param characterRepository Репозиторий, предоставляющий доступ к данным.
 */
class GetCharacterDetailsUseCase constructor(
    private val characterRepository: CharacterRepository
) {
    /**
     * Выполняет бизнес-логику для получения деталей персонажа.
     *
     * @param characterId ID персонажа, информацию о котором нужно получить.
     * @return [Result] с [RMCharacterDetailed] в случае успеха, или [Throwable] в случае ошибки.
     */
    suspend fun execute(characterId: Int): Result<RMCharacterDetailed> {
        return characterRepository.getCharacterDetails(characterId)
    }
}
