package com.example.rickandmortyinfo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
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
                // Используем Box для наложения слоев: сначала фон, потом контент
                Box(modifier = Modifier.fillMaxSize()) {
                    // Фоновое изображение, загруженное из ресурсов приложения.
                    // Убедитесь, что у вас есть файл rick_and_morty_background.png
                    // или .jpg в папке res/drawable.
                    Image(
                        painter = painterResource(id = R.drawable.rick_and_morty_background),
                        contentDescription = "Фоновое изображение с Риком и Морти",
                        modifier = Modifier.fillMaxSize(),
                        // Обрезаем изображение, чтобы оно заполнило всю доступную область
                        contentScale = ContentScale.Crop
                    )

                    // Контейнер, который теперь имеет прозрачный цвет фона,
                    // чтобы фоновое изображение было видно.
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        // Ключевое изменение: задаем прозрачный цвет.
                        color = Color.Transparent
                    ) {
                        // Вызываем наш NavHost, который управляет навигацией.
                        // Он будет отображаться поверх прозрачного Surface
                        // и фонового изображения.
                        RickAndMortyNavHost()
                    }
                }
            }
        }
    }
}
