package com.example.rickandmortyinfo.app.di

import com.example.data.local.datasources.CharacterLocalDataSource
import com.example.data.remote.datasources.CharacterRemoteDataSource
import com.example.data.repository.CharacterRepositoryImpl
import com.example.domain.repository.CharacterRepository
import com.example.data.local.database.CharacterDatabase
import com.example.data.local.dao.CharacterDao
import com.example.data.local.dao.RemoteKeyDao
import com.example.data.local.datasources.CharacterDetailsLocalDataSource
import com.example.data.remote.api.RickAndMortyApi
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
        characterDetailsLocalDataSource: CharacterDetailsLocalDataSource, // <--- ДОБАВЛЕН ЭТОТ ПАРАМЕТР
        characterDatabase: CharacterDatabase                             // <--- CharacterDatabase теперь четвертый
    ): CharacterRepository {
        return CharacterRepositoryImpl(
            characterRemoteDataSource,
            characterLocalDataSource,
            characterDetailsLocalDataSource, // <--- ПЕРЕДАЕМ ЕГО ТРЕТЬИМ
            characterDatabase                // <--- CharacterDatabase ПЕРЕДАЕМ ЧЕТВЕРТЫМ
        )
    }
}