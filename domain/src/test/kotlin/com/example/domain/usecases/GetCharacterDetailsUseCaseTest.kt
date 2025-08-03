package com.example.domain.usecases

import com.example.domain.model.LocationRM
import com.example.domain.model.RMCharacter
import com.example.domain.model.RMCharacterDetailed
import com.example.domain.repository.CharacterRepository
import com.example.domain.utils.Result
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.io.IOException

@ExperimentalCoroutinesApi
class GetCharacterDetailsUseCaseTest {

    // Объявляем mock-объект для CharacterRepository
    private lateinit var mockCharacterRepository: CharacterRepository
    private lateinit var getCharacterDetailsUseCase: GetCharacterDetailsUseCase

    @Before
    fun setUp() {
        // Инициализируем mock-объект перед каждым тестом
        mockCharacterRepository = mockk()
        getCharacterDetailsUseCase = GetCharacterDetailsUseCase(mockCharacterRepository)
    }

    @Test
    fun `when repository returns success, use case should return success with character details`() = runTest {
        // Создаем моковые данные для успешного сценария
        val characterId = 1
        val mockCharacter = RMCharacter(
            id = 1,
            name = "Rick Sanchez",
            species = "Human",
            type = "",
            status = "Alive",
            gender = "Male",
            imageUrl = "url"
        )
        val mockOrigin = LocationRM(name = "Earth (C-137)", url = "url")
        val mockLocation = LocationRM(name = "Citadel of Ricks", url = "url")
        val mockCharacterDetailed = RMCharacterDetailed(
            character = mockCharacter,
            origin = mockOrigin,
            location = mockLocation,
            episode = listOf("url1", "url2")
        )

        // Настраиваем поведение mock-репозитория
        coEvery { mockCharacterRepository.getCharacterDetails(characterId) } returns Result.Success(mockCharacterDetailed)

        // Выполняем use case
        val result = getCharacterDetailsUseCase.execute(characterId)

        // Проверяем, что результат — это Success и содержит правильные данные
        assertEquals(Result.Success(mockCharacterDetailed), result)

        // Проверяем, что метод репозитория был вызван ровно один раз
        coVerify(exactly = 1) { mockCharacterRepository.getCharacterDetails(characterId) }
    }

    @Test
    fun `when repository returns error, use case should return error`() = runTest {
        // Создаем моковые данные для сценария с ошибкой
        val characterId = 1
        val expectedError = IOException("Network error")

        // Настраиваем поведение mock-репозитория для возврата ошибки
        coEvery { mockCharacterRepository.getCharacterDetails(characterId) } returns Result.Error(expectedError)

        // Выполняем use case
        val result = getCharacterDetailsUseCase.execute(characterId)

        // Проверяем, что результат — это Error и содержит правильное исключение
        val expectedResult = Result.Error(expectedError)
        assertEquals(expectedResult.javaClass, result.javaClass)
        assertEquals(
            (expectedResult as Result.Error).exception.message,
            (result as Result.Error).exception.message
        )

        // Проверяем, что метод репозитория был вызван
        coVerify(exactly = 1) { mockCharacterRepository.getCharacterDetails(characterId) }
    }
}
