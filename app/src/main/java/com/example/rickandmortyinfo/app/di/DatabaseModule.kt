package com.example.rickandmortyinfo.app.di

import android.content.Context
import androidx.room.Room
import com.example.data.local.database.CharacterDatabase
import com.example.data.local.dao.CharacterDao
import com.example.data.local.dao.RemoteKeyDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt-модуль для предоставления зависимостей, связанных с базой данных Room.
 * Устанавливается в [SingletonComponent].
 */
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context
    ): CharacterDatabase {
        return Room.databaseBuilder(
            context,
            CharacterDatabase::class.java,
            "rick_and_morty_db"
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    @Singleton
    fun provideCharacterDao(database: CharacterDatabase): CharacterDao {
        return database.characterDao()
    }

    @Provides
    @Singleton
    fun provideRemoteKeysDao(database: CharacterDatabase): RemoteKeyDao {
        return database.remoteKeysDao()
    }
}