package com.example.rickandmortyinfo.app.di

import com.example.domain.repository.CharacterRepository
import com.example.domain.usecases.GetCharactersUseCase
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
}