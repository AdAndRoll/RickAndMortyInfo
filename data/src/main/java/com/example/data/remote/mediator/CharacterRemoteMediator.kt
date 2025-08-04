package com.example.data.remote.mediator

import android.util.Log
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.example.data.local.database.CharacterDatabase
import com.example.data.local.datasources.CharacterLocalDataSource
import com.example.data.local.entity.CharacterEntity
import com.example.data.local.entity.RemoteKeyEntity
import com.example.data.mappers.toCharacterEntity
import com.example.data.remote.datasources.CharacterRemoteDataSource
import com.example.data.utils.NetworkResult
import com.example.domain.model.CharacterFilter
import retrofit2.HttpException
import java.io.IOException
import java.util.concurrent.TimeUnit

@OptIn(ExperimentalPagingApi::class)
class CharacterRemoteMediator(
    private val characterRemoteDataSource: CharacterRemoteDataSource,
    private val characterLocalDataSource: CharacterLocalDataSource,
    private val characterDatabase: CharacterDatabase,
    private val filter: CharacterFilter
) : RemoteMediator<Int, CharacterEntity>() {

    private val CACHE_TIMEOUT = TimeUnit.MINUTES.toMillis(30)
    private val TAG = "CharacterRemoteMediator"

    override suspend fun initialize(): InitializeAction {
        val remoteKey = characterLocalDataSource.getRemoteKey()
        val lastUpdateTime = remoteKey?.createdAt ?: 0L
        val characterCount = characterLocalDataSource.getAllCharactersCount()
        val now = System.currentTimeMillis()
        val isCacheOutdated = now - lastUpdateTime >= CACHE_TIMEOUT
        val isCacheInconsistent = lastUpdateTime > 0 && characterCount == 0

        val isFilterChanged = remoteKey?.let {
            it.filterName != filter.name ||
                    it.filterStatus != filter.status ||
                    it.filterSpecies != filter.species ||
                    it.filterType != filter.type ||
                    it.filterGender != filter.gender
        } ?: false

        Log.d(
            TAG,
            "Checking cache integrity. Last update: $lastUpdateTime, character count: $characterCount"
        )
        Log.d(
            TAG,
            "Is cache outdated: $isCacheOutdated, Is cache inconsistent: $isCacheInconsistent"
        )
        Log.d(TAG, "Is filter changed: $isFilterChanged")


        return if (isCacheOutdated || isCacheInconsistent || isFilterChanged) {
            Log.d(
                TAG,
                "Cache is outdated, inconsistent, or filter has changed. Launching initial REFRESH."
            )
            InitializeAction.LAUNCH_INITIAL_REFRESH
        } else {
            Log.d(TAG, "Cache is fresh and consistent. Skipping initial REFRESH.")
            InitializeAction.SKIP_INITIAL_REFRESH
        }
    }

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, CharacterEntity>
    ): MediatorResult {
        return try {
            val currentPage: Int

            when (loadType) {
                LoadType.REFRESH -> {
                    Log.d(TAG, "LoadType: REFRESH. Starting from page 1.")
                    currentPage = 1
                }

                LoadType.PREPEND -> {
                    Log.d(TAG, "LoadType: PREPEND. End of pagination reached.")
                    return MediatorResult.Success(endOfPaginationReached = true)
                }

                LoadType.APPEND -> {
                    val remoteKey = characterLocalDataSource.getRemoteKey()
                    val nextKey = remoteKey?.nextKey

                    Log.d(TAG, "LoadType: APPEND. Next key from DB: $nextKey")

                    if (nextKey == null) {
                        Log.d(TAG, "nextKey is null. End of pagination reached.")
                        return MediatorResult.Success(endOfPaginationReached = true)
                    } else {
                        currentPage = nextKey
                    }
                }
            }

            Log.d(TAG, "Requesting characters for page: $currentPage")
            val apiResult = characterRemoteDataSource.getCharacters(
                page = currentPage,
                name = filter.name,
                status = filter.status,
                species = filter.species,
                type = filter.type,
                gender = filter.gender
            )
            Log.d(TAG, "API request for page $currentPage finished.")

            val endOfPaginationReached: Boolean
            when (apiResult) {
                is NetworkResult.Success -> {
                    val characters = apiResult.data.results
                    endOfPaginationReached = apiResult.data.info.next == null

                    characterDatabase.withTransaction {
                        if (loadType == LoadType.REFRESH) {
                            characterLocalDataSource.clearAllCharacters()
                            characterLocalDataSource.clearAllRemoteKeys()
                            Log.d(TAG, "DB cleared for REFRESH due to filter change.")
                        }

                        val newRemoteKey = RemoteKeyEntity(
                            id = 0,
                            prevKey = if (currentPage == 1) null else currentPage - 1,
                            nextKey = if (endOfPaginationReached) null else currentPage + 1,
                            createdAt = System.currentTimeMillis(),
                            filterName = filter.name,
                            filterStatus = filter.status,
                            filterSpecies = filter.species,
                            filterType = filter.type,
                            filterGender = filter.gender
                        )
                        characterLocalDataSource.insertRemoteKey(newRemoteKey)
                        characterLocalDataSource.insertCharacters(characters.map { it.toCharacterEntity() })
                        Log.d(TAG, "${characters.size} characters inserted into DB.")
                    }
                    Log.d(TAG, "Returning success. End of pagination: $endOfPaginationReached")
                }

                is NetworkResult.Error -> {
                    Log.e(
                        TAG,
                        "API request failed with error: ${apiResult.exception.localizedMessage}"
                    )
                    return MediatorResult.Error(apiResult.exception)
                }
            }
            MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)

        } catch (e: IOException) {
            Log.e(TAG, "Network or I/O error occurred: ${e.localizedMessage}")
            MediatorResult.Error(e)
        } catch (e: HttpException) {
            Log.e(TAG, "HTTP error occurred: ${e.code()} - ${e.localizedMessage}")
            MediatorResult.Error(e)
        } catch (e: Exception) {
            Log.e(TAG, "An unexpected error occurred: ${e.localizedMessage}", e)
            MediatorResult.Error(e)
        }
    }
}