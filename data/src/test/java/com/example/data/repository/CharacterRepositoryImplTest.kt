package com.example.data.repository

import android.util.Log
import androidx.paging.ExperimentalPagingApi
import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.paging.testing.asSnapshot
import com.example.data.local.database.CharacterDatabase
import com.example.data.local.datasources.CharacterDetailsLocalDataSource
import com.example.data.local.datasources.CharacterLocalDataSource
import com.example.data.local.entity.CharacterDetailsEntity
import com.example.data.local.entity.CharacterDetailsLocation
import com.example.data.local.entity.CharacterEntity
import com.example.data.local.entity.RemoteKeyEntity
import com.example.data.mappers.toCharacter
import com.example.data.mappers.toCharacterDetailed
import com.example.data.remote.datasources.CharacterRemoteDataSource
import com.example.data.remote.dto.CharacterDto
import com.example.data.remote.dto.LocationDto
import com.example.data.utils.NetworkResult
import com.example.domain.model.CharacterFilter
import com.example.domain.model.RMCharacter
import com.example.domain.utils.Result
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@OptIn(ExperimentalPagingApi::class)
class CharacterRepositoryImplTest {

    private lateinit var repository: CharacterRepositoryImpl
    private lateinit var remoteDataSource: CharacterRemoteDataSource
    private lateinit var localDataSource: CharacterLocalDataSource
    private lateinit var detailsLocalDataSource: CharacterDetailsLocalDataSource
    private lateinit var database: CharacterDatabase

    @BeforeEach
    fun setUp() {
        mockkStatic(Log::class)
        every { Log.d(any(), any()) } returns 0
        every { Log.e(any(), any()) } returns 0
        every { Log.e(any(), any(), any()) } returns 0

        remoteDataSource = mockk(relaxed = true)
        localDataSource = mockk(relaxed = true)
        detailsLocalDataSource = mockk(relaxed = true)
        database = mockk(relaxed = true)
        repository = CharacterRepositoryImpl(
            remoteDataSource,
            localDataSource,
            detailsLocalDataSource,
            database
        )
    }


    @Test
    fun `getCharacters returns mapped PagingData from local source`() = runTest {
        // Arrange
        val filter = CharacterFilter(name = "Rick")
        val characterEntity = CharacterEntity(
            id = 1, name = "Rick Sanchez", species = "Human",
            type = "", status = "Alive", gender = "Male", imageUrl = "url"
        )
        val expectedCharacter = characterEntity.toCharacter()


        val testPagingSource = object : PagingSource<Int, CharacterEntity>() {
            override suspend fun load(params: LoadParams<Int>): LoadResult<Int, CharacterEntity> {

                return LoadResult.Page(
                    data = listOf(characterEntity),
                    prevKey = null,
                    nextKey = null
                )
            }

            override fun getRefreshKey(state: PagingState<Int, CharacterEntity>): Int? {

                return state.anchorPosition?.let { anchorPosition ->
                    state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                        ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
                }
            }
        }


        every { localDataSource.getCharactersPagingSource(filter) } returns testPagingSource


        val freshRemoteKey = RemoteKeyEntity(
            id = 0,
            prevKey = null,
            nextKey = null,
            createdAt = System.currentTimeMillis(),
            filterName = filter.name,
            filterStatus = filter.status,
            filterSpecies = filter.species,
            filterType = filter.type,
            filterGender = filter.gender
        )
        coEvery { localDataSource.getRemoteKey() } returns freshRemoteKey
        coEvery { localDataSource.getAllCharactersCount() } returns 1


        val flow = repository.getCharacters(filter)

        val result: List<RMCharacter> = flow.asSnapshot()


        assertEquals(listOf(expectedCharacter), result)
    }


    @Test
    fun `refreshCharacters clears local cache`() = runTest {

        coEvery { localDataSource.clearAllCharacters() } returns Unit
        coEvery { localDataSource.clearAllRemoteKeys() } returns Unit


        repository.refreshCharacters()


        coVerify { localDataSource.clearAllCharacters() }
        coVerify { localDataSource.clearAllRemoteKeys() }
    }

    @Test
    fun `getCharacterDetails returns cached data when available`() = runTest {

        val characterId = 1
        val characterDetailsEntity = CharacterDetailsEntity(
            id = 1,
            name = "Rick Sanchez",
            species = "Human",
            type = "",
            status = "Alive",
            gender = "Male",
            imageUrl = "url",
            origin = CharacterDetailsLocation("Earth", "url"),
            location = CharacterDetailsLocation("Earth", "url"),
            episodeUrls = listOf("ep1", "ep2")
        )
        coEvery { detailsLocalDataSource.getCharacterDetails(characterId) } returns characterDetailsEntity
        val expected = Result.Success(characterDetailsEntity.toCharacterDetailed())

        // Act
        val result = repository.getCharacterDetails(characterId)

        // Assert
        assertEquals(expected, result)
    }

    @Test
    fun `getCharacterDetails fetches from remote when cache is empty`() = runTest {

        val characterId = 1
        val characterDto = CharacterDto(
            id = 1,
            name = "Rick Sanchez",
            species = "Human",
            type = "",
            status = "Alive",
            gender = "Male",
            image = "url",
            origin = LocationDto("Earth", "url"),
            location = LocationDto("Earth", "url"),
            episode = listOf("ep1", "ep2"),
            url = "url",
            created = "date"
        )
        coEvery { detailsLocalDataSource.getCharacterDetails(characterId) } returns null
        coEvery { remoteDataSource.getCharacterById(characterId) } returns NetworkResult.Success(
            characterDto
        )
        coEvery { detailsLocalDataSource.insertCharacterDetails(any()) } returns Unit
        val expected = Result.Success(characterDto.toCharacterDetailed())


        val result = repository.getCharacterDetails(characterId)


        assertEquals(expected, result)
    }

    @Test
    fun `getCharacterDetails returns error on remote failure`() = runTest {

        val characterId = 1
        val exception = RuntimeException("Network error")
        coEvery { detailsLocalDataSource.getCharacterDetails(characterId) } returns null
        coEvery { remoteDataSource.getCharacterById(characterId) } returns NetworkResult.Error(
            exception
        )


        val result = repository.getCharacterDetails(characterId)


        assertTrue(result is Result.Error)
        assertEquals(exception, (result as Result.Error).exception)
    }
}