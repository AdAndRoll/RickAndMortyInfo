package com.example.rickandmortyinfo.app.di

import com.example.domain.repository.CharacterRepository
import com.example.domain.repository.EpisodeRepository // Импортируем новый репозиторий
import com.example.domain.repository.LocationRepository
import com.example.domain.usecases.GetCharacterDetailsUseCase
import com.example.domain.usecases.GetCharacterEpisodesUseCase
import com.example.domain.usecases.GetCharactersUseCase
import com.example.domain.usecases.GetLocationDetailsUseCase
import com.example.domain.usecases.GetSingleEpisodeUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt-модуль для предоставления зависимостей доменного слоя (Use Cases).
 * Устанавливается в [SingletonComponent].
 */
@Module
@InstallIn(SingletonComponent::class)
object DomainModule {

    /**
     * Предоставляет [GetCharactersUseCase].
     * Hilt автоматически инжектирует CharacterRepository, который будет предоставлен из DataModule.
     */
    @Provides
    @Singleton
    fun provideGetCharactersUseCase(
        repository: CharacterRepository
    ): GetCharactersUseCase {
        return GetCharactersUseCase(repository)
    }

    /**
     * Предоставляет [GetCharacterEpisodesUseCase].
     * Зависимость от EpisodeRepository предоставляется из DataModule.
     */
    @Provides
    @Singleton
    fun provideGetCharacterEpisodesUseCase(
        repository: EpisodeRepository
    ): GetCharacterEpisodesUseCase {
        return GetCharacterEpisodesUseCase(repository)
    }

    /**
     * Предоставляет [GetSingleEpisodeUseCase].
     * Зависимость от EpisodeRepository предоставляется из DataModule.
     */
    @Provides
    @Singleton
    fun provideGetSingleEpisodeUseCase(
        repository: EpisodeRepository
    ): GetSingleEpisodeUseCase {
        return GetSingleEpisodeUseCase(repository)
    }

    /**
     * Предоставляет [GetCharacterDetailsUseCase].
     * Hilt автоматически инжектирует CharacterRepository и GetCharacterEpisodesUseCase.
     */
    @Provides
    @Singleton
    fun provideGetCharacterDetailsUseCase(
        characterRepository: CharacterRepository,
        getCharacterEpisodesUseCase: GetCharacterEpisodesUseCase
    ): GetCharacterDetailsUseCase {
        return GetCharacterDetailsUseCase(characterRepository, getCharacterEpisodesUseCase)
    }

    /**
     * Предоставляет [GetLocationDetailsUseCase].
     */
    @Provides
    @Singleton
    fun provideGetLocationDetailsUseCase(
        repository: LocationRepository
    ): GetLocationDetailsUseCase {
        return GetLocationDetailsUseCase(repository)
    }
}
