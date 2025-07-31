package com.example.rickandmortyinfo.app.di

import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

/**
 * Hilt-модуль для предоставления зависимостей уровня приложения.
 * Включает в себя общие зависимости или зависимости, которые не подходят для других модулей.
 */
@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    // Здесь могут быть другие зависимости, специфичные для уровня приложения,
    // например, SharedPreferences, Managers и т.д.
    // Пока что, этот модуль может быть пустым, если все остальные зависимости
    // уже покрыты Network, Database, Data и Domain модулями.

    // Пример:
    // @Provides
    // @Singleton
    // fun provideSomethingElse(): SomeDependency {
    //     return SomeDependency()
    // }
}