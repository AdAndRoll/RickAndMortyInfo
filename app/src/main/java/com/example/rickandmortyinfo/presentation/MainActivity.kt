package com.example.rickandmortyinfo.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.example.rickandmortyinfo.R
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
                Box(modifier = Modifier.fillMaxSize()) {
                    Image(
                        painter = painterResource(id = R.drawable.rick_and_morty_background),
                        contentDescription = "Фоновое изображение с Риком и Морти",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )

                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = Color.Transparent
                    ) {
                        RickAndMortyNavHost()
                    }
                }
            }
        }
    }
}
