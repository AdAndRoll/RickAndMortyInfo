package com.example.rickandmortyinfo.app.di

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.add

import coil.ImageLoader
import coil.decode.ImageDecoderDecoder
import coil.disk.DiskCache
import coil.memory.MemoryCache
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


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


    @Provides
    @Singleton
    fun provideImageLoader(
        @ApplicationContext context: Context
    ): ImageLoader {
        return ImageLoader.Builder(context)
            .memoryCache {
                MemoryCache.Builder(context)
                    .maxSizePercent(0.25) // Использовать до 25% доступной памяти приложения
                    .build()
            }
            .diskCache {
                DiskCache.Builder()
                    .directory(context.cacheDir.resolve("image_cache")) // Указать директорию для дискового кэша
                    .maxSizeBytes(250 * 1024 * 1024) // 250 MB, например
                    .build()
            }
            .respectCacheHeaders(false) // Может помочь, если серверные заголовки мешают кэшированию
            .crossfade(true) // Плавное появление изображения
            .build()
    }
}
