package com.example.rickandmortyinfo.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.rickandmortyinfo.presentation.character_list.CharacterListScreen // Импортируем ваш экран
import com.example.rickandmortyinfo.presentation.ui.theme.RickAndMortyInfoTheme

import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint // Аннотация для Hilt, чтобы он мог инжектировать зависимости в Activity
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge() // Если хотите, чтобы контент распространялся на System Bars
        setContent {
            RickAndMortyInfoTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    CharacterListScreen() // Вызываем ваш экран списка персонажей
                }
            }
        }
    }
}