package com.example.rickandmortyinfo.app

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * Главный класс приложения, аннотированный [HiltAndroidApp].
 * Это отправная точка для Hilt, которая инициирует генерацию кода
 * для внедрения зависимостей по всему приложению.
 */
@HiltAndroidApp
class RickAndMortyApp : Application() {
    // В этом классе пока ничего дополнительного не нужно.
    // Hilt автоматически позаботится о необходимых инициализациях.
}