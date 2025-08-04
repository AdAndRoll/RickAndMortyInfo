package com.example.domain.usecases

import com.example.domain.model.LocationDetail
import com.example.domain.model.Resident
import com.example.domain.repository.LocationRepository
import com.example.domain.utils.Result
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.io.IOException

@ExperimentalCoroutinesApi
class GetLocationDetailsUseCaseTest {

    private lateinit var mockLocationRepository: LocationRepository
    private lateinit var getLocationDetailsUseCase: GetLocationDetailsUseCase

    @Before
    fun setUp() {
        mockLocationRepository = mockk()
        getLocationDetailsUseCase = GetLocationDetailsUseCase(mockLocationRepository)
    }

    @Test
    fun `when repository returns success, use case should return flow with success`() = runTest {
        // Создаем моковые данные для успешного сценария
        val locationId = 1
        val mockLocationDetail = LocationDetail(
            id = 1,
            name = "Earth (C-137)",
            type = "Planet",
            dimension = "Dimension C-137",
            residents = listOf(Resident(id = 1, name = "Rick Sanchez"))
        )
        val expectedFlow = flowOf(Result.Success(mockLocationDetail))

        // Настраиваем поведение mock-репозитория
        coEvery { mockLocationRepository.getLocationDetails(locationId) } returns expectedFlow

        // Выполняем use case
        val resultFlow = getLocationDetailsUseCase.execute(locationId)

        // Собираем первый элемент из потока и проверяем его
        val result = resultFlow.first()
        assertEquals(Result.Success(mockLocationDetail), result)

        // Проверяем, что метод репозитория был вызван
        coVerify(exactly = 1) { mockLocationRepository.getLocationDetails(locationId) }
    }

    @Test
    fun `when repository returns error, use case should return flow with error`() = runTest {
        // Создаем моковые данные для сценария с ошибкой
        val locationId = 1
        val expectedError = IOException("Network error")
        val expectedFlow = flowOf(Result.Error(expectedError))

        // Настраиваем поведение mock-репозитория
        coEvery { mockLocationRepository.getLocationDetails(locationId) } returns expectedFlow

        // Выполняем use case
        val resultFlow = getLocationDetailsUseCase.execute(locationId)

        // Собираем первый элемент из потока и проверяем его
        val result = resultFlow.first()
        val expectedResult = Result.Error(expectedError)
        assertEquals(expectedResult.javaClass, result.javaClass)

        // ИСПРАВЛЕНО: Заменили 'throwable' на 'exception', чтобы соответствовать вашему классу Result.Error.
        assertEquals(
            (expectedResult as Result.Error).exception.message,
            (result as Result.Error).exception.message
        )

        // Проверяем, что метод репозитория был вызван
        coVerify(exactly = 1) { mockLocationRepository.getLocationDetails(locationId) }
    }
}
