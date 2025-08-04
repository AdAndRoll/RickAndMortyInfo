// Файл: com/example/rickandmortyinfo/app/di/DatabaseModule.kt

package com.example.rickandmortyinfo.app.di

import android.content.Context
import androidx.room.Room
import com.example.data.db.dao.LocationDao
import com.example.data.local.converters.CharacterTypeConverters
import com.example.data.local.dao.CharacterDao
import com.example.data.local.dao.CharacterDetailsDao
import com.example.data.local.dao.RMEpisodeDao
import com.example.data.local.dao.RemoteKeyDao
import com.example.data.local.database.CharacterDatabase
import com.squareup.moshi.Moshi
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
     * Предоставляет синглтон-экземпляр базы данных Room.
     * Hilt автоматически внедряет все зависимости из этого модуля.
     *
     * @param context Контекст приложения, предоставляемый Hilt.
     * @param characterTypeConverters Конвертер типов для персонажей, предоставляемый Hilt.
     * @param locationConverters Конвертер типов для локаций, предоставляемый Hilt.
     * @return Экземпляр [CharacterDatabase].
     */
    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context,
        characterTypeConverters: CharacterTypeConverters,
    ): CharacterDatabase {
        return Room.databaseBuilder(
            context,
            CharacterDatabase::class.java,
            "rick_and_morty_db"
        )
            .addTypeConverter(characterTypeConverters)
            .fallbackToDestructiveMigration(true)
            .build()
    }

    /**
     * Предоставляет синглтон-экземпляр CharacterTypeConverters.
     * Hilt создает этот конвертер, используя экземпляр Moshi.
     *
     * @param moshi Экземпляр Moshi для конвертеров типов.
     * @return Экземпляр [CharacterTypeConverters].
     */
    @Provides
    @Singleton
    fun provideCharacterTypeConverters(moshi: Moshi): CharacterTypeConverters {
        return CharacterTypeConverters(moshi)
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

    /**
     * Предоставляет DAO для доступа к данным о локациях.
     */
    @Provides
    @Singleton
    fun provideLocationDao(database: CharacterDatabase): LocationDao {
        return database.locationDao()
    }

    /**
     * Предоставляет DAO для доступа к данным об эпизодах.
     */
    @Provides
    @Singleton
    fun provideEpisodeDao(database: CharacterDatabase): RMEpisodeDao {
        return database.episodeDao()
    }
}
