package com.example.rickandmortyinfo.app.di

import com.example.domain.repository.CharacterRepository
import com.example.domain.repository.LocationRepository
import com.example.domain.usecases.GetCharacterDetailsUseCase
import com.example.domain.usecases.GetCharactersUseCase
import com.example.domain.usecases.GetLocationDetailsUseCase
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
     * Предоставляет [GetCharacterDetailsUseCase].
     * Hilt автоматически инжектирует CharacterRepository (или другие зависимости UseCase),
     * который будет предоставлен из DataModule (или другого соответствующего модуля).
     */
    @Provides
    @Singleton // Или @ViewModelScoped, если установлен в ViewModelComponent
    fun provideGetCharacterDetailsUseCase(
        repository: CharacterRepository // Предполагается, что он также зависит от CharacterRepository
        // Если у него другие зависимости, укажите их здесь
    ): GetCharacterDetailsUseCase {
        return GetCharacterDetailsUseCase(repository) // Создаем экземпляр
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