package com.example.rickandmortyinfo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.rickandmortyinfo.presentation.navigation.RickAndMortyNavHost
import com.example.rickandmortyinfo.presentation.ui.theme.RickAndMortyInfoTheme
import dagger.hilt.android.AndroidEntryPoint

/**
 * Главная активность приложения.
 * Аннотация @AndroidEntryPoint нужна Hilt для внедрения зависимостей
 * в Activity, Fragment, View и другие Android-классы.
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            RickAndMortyInfoTheme {
                // Контейнер, который использует 'background' цвет из темы
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // Вызываем наш NavHost, который управляет навигацией
                    // между CharacterListScreen и CharacterDetailScreen.
                    RickAndMortyNavHost()
                }
            }
        }
    }
}
