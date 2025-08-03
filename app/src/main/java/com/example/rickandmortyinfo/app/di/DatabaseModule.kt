package com.example.rickandmortyinfo.app.di

import android.content.Context
import androidx.room.Room
import com.example.data.local.converters.CharacterTypeConverters
import com.example.data.local.dao.CharacterDao
import com.example.data.local.dao.CharacterDetailsDao
import com.example.data.local.dao.RemoteKeyDao
import com.example.data.local.database.CharacterDatabase
import com.google.gson.Gson

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

    /**
     * Предоставляет синглтон-экземпляр Gson.
     */
    @Provides
    @Singleton
    fun provideGson(): Gson {
        return Gson()
    }

    /**
     * Предоставляет синглтон-экземпляр базы данных Room.
     * @param context Контекст приложения, предоставляемый Hilt.
     * @param gson Экземпляр Gson для конвертеров типов.
     * @return Экземпляр [CharacterDatabase].
     */
    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context,
        gson: Gson
    ): CharacterDatabase {
        return Room.databaseBuilder(
            context,
            CharacterDatabase::class.java,
            "rick_and_morty_db"
        )
            .addTypeConverter(CharacterTypeConverters(gson))
            .fallbackToDestructiveMigration()
            .build()
    }

    /**
     * Предоставляет DAO для доступа к данным списка персонажей.
     */
    @Provides
    @Singleton
    fun provideCharacterDao(database: CharacterDatabase): CharacterDao {
        return database.characterDao()
    }

    /**
     * Предоставляет DAO для доступа к данным удаленных ключей пагинации.
     */
    @Provides
    @Singleton
    fun provideRemoteKeysDao(database: CharacterDatabase): RemoteKeyDao {
        return database.remoteKeysDao()
    }

    /**
     * Предоставляет DAO для доступа к деталям персонажей.
     */
    @Provides
    @Singleton
    fun provideCharacterDetailsDao(database: CharacterDatabase): CharacterDetailsDao {
        return database.characterDetailsDao()
    }
}
