// Файл: com/example/rickandmortyinfo/app/di/NetworkModule.kt

package com.example.rickandmortyinfo.app.di

import com.example.data.remote.api.LocationApiService
import com.example.data.remote.api.RickAndMortyApi
import com.example.data.remote.episodes.api.RMEpisodeApi
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

/**
 * Hilt-модуль для предоставления сетевых зависимостей.
 * Устанавливается в [SingletonComponent], что делает все предоставляемые зависимости синглтонами на уровне приложения.
 */
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    private const val BASE_URL = "https://rickandmortyapi.com/api/"

    @Provides
    @Singleton
    fun provideHttpLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(
        loggingInterceptor: HttpLoggingInterceptor
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    /**
     * Предоставляет синглтон-экземпляр Moshi.
     * Мы добавляем KotlinJsonAdapterFactory для корректной работы с классами данных Kotlin.
     * Этот Moshi будет использоваться как для сетевых, так и для локальных операций с JSON.
     */
    @Provides
    @Singleton
    fun provideMoshi(): Moshi {
        return Moshi.Builder()
            .addLast(KotlinJsonAdapterFactory())
            .build()
    }

    @Provides
    @Singleton
    fun provideMoshiConverterFactory(moshi: Moshi): MoshiConverterFactory {
        return MoshiConverterFactory.create(moshi)
    }

    @Provides
    @Singleton
    fun provideRetrofit(
        okHttpClient: OkHttpClient,
        moshiConverterFactory: MoshiConverterFactory
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(moshiConverterFactory)
            .build()
    }

    @Provides
    @Singleton
    fun provideRickAndMortyApi(retrofit: Retrofit): RickAndMortyApi {
        return retrofit.create(RickAndMortyApi::class.java)
    }

    /**
     * Предоставляет сервис для работы с локациями.
     */
    @Provides
    @Singleton
    fun provideLocationApiService(retrofit: Retrofit): LocationApiService {
        return retrofit.create(LocationApiService::class.java)
    }

    /**
     * Предоставляет сервис для работы с эпизодами.
     */
    @Provides
    @Singleton
    fun provideRMEpisodeApi(retrofit: Retrofit): RMEpisodeApi {
        return retrofit.create(RMEpisodeApi::class.java)
    }
}
