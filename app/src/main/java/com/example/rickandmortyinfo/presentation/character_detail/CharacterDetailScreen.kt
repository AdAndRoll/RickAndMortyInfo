package com.example.presentation.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
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
import com.example.domain.model.RMCharacterDetailed
import com.example.domain.model.RMCharacter
import com.example.rickandmortyinfo.presentation.character_detail.components.DetailText


/**
 * Компонуемая функция для экрана с детальной информацией о персонаже.
 *
 * @param characterId ID персонажа, который нужно отобразить.
 * @param onBackClick Функция, которая будет вызвана при нажатии кнопки "назад".
 * @param onLocationClick Функция, которая будет вызвана при нажатии на локацию.
 * @param viewModel ViewModel для управления состоянием экрана, предоставляемый Hilt.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CharacterDetailScreen(
    characterId: Int,
    onBackClick: () -> Unit,
    onLocationClick: (Int) -> Unit, // Новый параметр для навигации по локациям
    viewModel: CharacterDetailViewModel = hiltViewModel()
) {
    LaunchedEffect(key1 = characterId) {
        viewModel.loadCharacterDetails(characterId)
    }

    val state by viewModel.characterDetailState.collectAsState()

    // Используем Scaffold для создания базовой структуры экрана с TopAppBar.
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Детали персонажа",
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Кнопка назад"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        // Основной контент экрана помещается в LazyColumn, который использует отступы от Scaffold.
        when (val currentState = state) {
            is CharacterDetailState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            is CharacterDetailState.Success -> {
                val characterDetails: RMCharacterDetailed = currentState.character
                val character: RMCharacter = characterDetails.character
                val origin = characterDetails.origin
                val location = characterDetails.location

                // Используем LazyColumn как корневой элемент для всего прокручиваемого контента
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues) // Применяем отступы из Scaffold
                        .padding(horizontal = 16.dp), // Добавляем горизонтальные отступы
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp) // Пространство между элементами списка
                ) {
                    // Первый элемент: Изображение и имя
                    item {
                        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                            AsyncImage(
                                model = character.imageUrl,
                                contentDescription = "Изображение ${character.name}",
                                modifier = Modifier
                                    .padding(top = 16.dp) // Отступ сверху для первого элемента
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
                        }
                    }

                    // Элементы для детальной информации
                    item { DetailText(label = "Статус", value = character.status) }
                    item { DetailText(label = "Вид", value = character.species) }

                    character.type?.let { type ->
                        if (type.isNotBlank()) {
                            item { DetailText(label = "Тип", value = type) }
                        }
                    }

                    item { DetailText(label = "Пол", value = character.gender) }

                    // Детали происхождения и последней локации теперь кликабельны
                    item {
                        DetailText(
                            label = "Происхождение",
                            value = origin.name,
                            onClick = {
                                // Извлекаем ID из URL и передаем его в onLocationClick
                                if (origin.url.isNotBlank()) {
                                    val locationId = origin.url.substringAfterLast("/").toIntOrNull()
                                    if (locationId != null) onLocationClick(locationId)
                                }
                            }
                        )
                    }
                    item {
                        DetailText(
                            label = "Последняя локация",
                            value = location.name,
                            onClick = {
                                // Извлекаем ID из URL и передаем его в onLocationClick
                                if (location.url.isNotBlank()) {
                                    val locationId = location.url.substringAfterLast("/").toIntOrNull()
                                    if (locationId != null) onLocationClick(locationId)
                                }
                            }
                        )
                    }

                    // Заголовок для списка эпизодов
                    if (characterDetails.episode.isNotEmpty()) {
                        item {
                            Text(
                                text = "Эпизоды",
                                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                                modifier = Modifier
                                    .fillMaxWidth() // Растянуть по ширине для выравнивания
                                    .padding(top = 16.dp, bottom = 8.dp)
                                    .padding(start = 0.dp) // Убираем лишний горизонтальный отступ
                            )
                        }

                        // Список эпизодов
                        items(characterDetails.episode, key = { it }) { episodeUrl ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                            ) {
                                val episodeNumber = episodeUrl.substringAfterLast("/")
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Эпизод $episodeNumber",
                                        style = MaterialTheme.typography.bodyMedium,
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            }
                        }
                    }
                    // Можно добавить отступ снизу, если нужно
                    item { Box(modifier = Modifier.padding(bottom = 16.dp)) }
                }
            }
            is CharacterDetailState.Error -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
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
}
