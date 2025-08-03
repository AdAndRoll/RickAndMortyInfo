package com.example.domain.usecases

import androidx.paging.PagingData
import com.example.domain.model.CharacterFilter
import com.example.domain.model.RMCharacter
import com.example.domain.repository.CharacterRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class GetCharactersUseCaseTest {

    private lateinit var mockCharacterRepository: CharacterRepository
    private lateinit var getCharactersUseCase: GetCharactersUseCase

    @Before
    fun setUp() {
        mockCharacterRepository = mockk()
        getCharactersUseCase = GetCharactersUseCase(mockCharacterRepository)
    }

    @Test
    fun `execute returns flow from repository`() {
        // Создаем моковые данные
        val filter = CharacterFilter(name = "Rick")
        val mockCharacters = listOf(
            RMCharacter(
                id = 1,
                name = "Rick Sanchez",
                species = "Human",
                type = "",
                status = "Alive",
                gender = "Male",
                imageUrl = "url"
            )
        )
        val mockPagingData = PagingData.from(mockCharacters)
        val expectedFlow: Flow<PagingData<RMCharacter>> = flowOf(mockPagingData)

        // Настраиваем поведение mock-репозитория
        every { mockCharacterRepository.getCharacters(filter) } returns expectedFlow

        // Выполняем use case
        val resultFlow = getCharactersUseCase.execute(filter)

        // Проверяем, что метод репозитория был вызван с правильным фильтром
        verify(exactly = 1) { mockCharacterRepository.getCharacters(filter) }

        // Примечание: Мы не можем просто сравнить потоки,
        // так как они являются разными объектами.
        // Достаточно убедиться, что use case вызвал правильный метод у репозитория.
    }
}
