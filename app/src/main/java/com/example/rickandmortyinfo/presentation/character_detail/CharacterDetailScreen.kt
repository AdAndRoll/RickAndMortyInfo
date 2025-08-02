package com.example.presentation.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.rickandmortyinfo.presentation.character_detail.CharacterDetailState
import com.example.rickandmortyinfo.presentation.character_detail.CharacterDetailViewModel
import com.example.rickandmortyinfo.presentation.character_detail.components.DetailText


/**
 * Компонуемая функция для экрана с детальной информацией о персонаже.
 *
 * @param characterId ID персонажа, который нужно отобразить.
 * @param viewModel ViewModel для управления состоянием экрана, предоставляемый Hilt.
 */
@Composable
fun CharacterDetailScreen(
    characterId: Int,
    viewModel: CharacterDetailViewModel = hiltViewModel()
) {
    // Запускаем загрузку данных, как только экран становится видимым.
    LaunchedEffect(key1 = characterId) {
        viewModel.loadCharacterDetails(characterId)
    }

    // Собираем состояние из ViewModel.
    val state by viewModel.characterDetailState.collectAsState()

    // Отображаем UI в зависимости от текущего состояния.
    when (val currentState = state) {
        is CharacterDetailState.Loading -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
        is CharacterDetailState.Success -> {
            // Исправлено: теперь мы обращаемся к вложенным объектам
            val characterDetails = currentState.character
            val character = characterDetails.character
            val origin = characterDetails.origin
            val location = characterDetails.location

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {
                AsyncImage(
                    model = character.imageUrl, // Исправлено: свойство imageUrl
                    contentDescription = "Изображение ${character.name}",
                    modifier = Modifier
                        .size(200.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
                Text(
                    text = character.name,
                    style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 16.dp)
                )
                DetailText(label = "Статус", value = character.status)
                DetailText(label = "Вид", value = character.species)
                DetailText(label = "Пол", value = character.gender)
                DetailText(label = "Происхождение", value = origin.name) // Исправлено: origin.name
                DetailText(label = "Последняя локация", value = location.name) // Исправлено: location.name
            }
        }
        is CharacterDetailState.Error -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = currentState.message,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.error,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
    }
}


