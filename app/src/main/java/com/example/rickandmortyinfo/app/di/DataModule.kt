package com.example.rickandmortyinfo.app.di


import com.example.data.local.dao.CharacterDao
import com.example.data.local.dao.CharacterDetailsDao
import com.example.data.local.dao.RMEpisodeDao
import com.example.data.local.dao.RemoteKeyDao
import com.example.data.repository.EpisodeRepositoryImpl
import com.example.domain.repository.EpisodeRepository

import com.example.data.local.database.CharacterDatabase
import com.example.data.local.datasources.CharacterDetailsLocalDataSource
import com.example.data.local.datasources.CharacterLocalDataSource

import com.example.data.local.datasources.LocationLocalDataSource
import com.example.data.local.episodes.datasources.EpisodeLocalDataSource

import com.example.data.remote.api.RickAndMortyApi
import com.example.data.remote.datasources.CharacterRemoteDataSource
import com.example.data.remote.datasources.EpisodeRemoteDataSource
import com.example.data.remote.datasources.LocationRemoteDataSource
import com.example.data.remote.episodes.api.RMEpisodeApi
import com.example.data.repository.CharacterRepositoryImpl
import com.example.data.repository.LocationRepositoryImpl
import com.example.domain.repository.CharacterRepository
import com.example.domain.repository.LocationRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt-модуль для предоставления зависимостей, принадлежащих слою данных (Data Sources, Repository Implementation).
 * Устанавливается в [SingletonComponent].
 */
@Module
@InstallIn(SingletonComponent::class)
object DataModule {

    @Provides
    @Singleton
    fun provideCharacterLocalDataSource(
        characterDao: CharacterDao,
        remoteKeysDao: RemoteKeyDao
    ): CharacterLocalDataSource {
        return CharacterLocalDataSource(characterDao, remoteKeysDao)
    }

    @Provides
    @Singleton
    fun provideCharacterRemoteDataSource(
        api: RickAndMortyApi
    ): CharacterRemoteDataSource {
        return CharacterRemoteDataSource(api)
    }

    /**
     * Предоставляет реализацию интерфейса [CharacterRepository].
     * Возвращаем интерфейс [CharacterRepository], чтобы доменный слой зависел от абстракции.
     */

    @Provides
    @Singleton
    fun provideCharacterRepository(
        characterRemoteDataSource: CharacterRemoteDataSource,
        characterLocalDataSource: CharacterLocalDataSource,
        characterDetailsLocalDataSource: CharacterDetailsLocalDataSource,
        characterDatabase: CharacterDatabase
    ): CharacterRepository {
        return CharacterRepositoryImpl(
            characterRemoteDataSource,
            characterLocalDataSource,
            characterDetailsLocalDataSource,
            characterDatabase
        )
    }

    /**
     * Предоставляет источник данных для сетевых запросов по эпизодам.
     */
    @Provides
    @Singleton
    fun provideEpisodeRemoteDataSource(api: RMEpisodeApi): EpisodeRemoteDataSource {
        return EpisodeRemoteDataSource(api)
    }

    /**
     * Предоставляет источник данных для локального кэша эпизодов.
     */
    @Provides
    @Singleton
    fun provideEpisodeLocalDataSource(dao: RMEpisodeDao): EpisodeLocalDataSource {
        return EpisodeLocalDataSource(dao)
    }

    /**
     * Предоставляет реализацию интерфейса [EpisodeRepository].
     */
    @Provides
    @Singleton
    fun provideEpisodeRepository(
        remoteDataSource: EpisodeRemoteDataSource,
        localDataSource: EpisodeLocalDataSource,
        characterRemoteDataSource: CharacterRemoteDataSource,
        characterDetailsDao: CharacterDetailsDao
    ): EpisodeRepository {
        return EpisodeRepositoryImpl(
            remoteDataSource,
            localDataSource,
            characterRemoteDataSource,
            characterDetailsDao
        )
    }


    /**
     * Предоставляет реализацию интерфейса [LocationRepository].
     */
    @Provides
    @Singleton
    fun provideLocationRepository(
        locationRemoteDataSource: LocationRemoteDataSource,
        locationLocalDataSource: LocationLocalDataSource,
        characterDetailsDao: CharacterDetailsDao,
        characterRemoteDataSource: CharacterRemoteDataSource
    ): LocationRepository {
        return LocationRepositoryImpl(
            locationRemoteDataSource,
            locationLocalDataSource,
            characterRemoteDataSource,
            characterDetailsDao
        )
    }
}
