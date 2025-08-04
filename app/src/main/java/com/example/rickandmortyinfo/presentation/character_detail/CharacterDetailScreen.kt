package com.example.rickandmortyinfo.presentation.character_detail

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.domain.model.RMCharacter
import com.example.domain.model.RMCharacterDetailed
import com.example.domain.model.RMCharacterEpisodeSummary
import com.example.rickandmortyinfo.presentation.character_detail.components.DetailText


/**
 * Компонуемая функция для экрана с детальной информацией о персонаже.
 *
 * @param characterId ID персонажа, который нужно отобразить.
 * @param onBackClick Функция, которая будет вызвана при нажатии кнопки "назад" (стрелка).
 * @param onCloseClick Функция, которая будет вызвана при нажатии кнопки "закрыть" (крестик).
 * @param onLocationClick Функция, которая будет вызвана при нажатии на локацию.
 * @param onFilterClick Функция для навигации с фильтром.
 * @param onEpisodeClick Функция, которая будет вызвана при нажатии на эпизод.
 * @param viewModel ViewModel для управления состоянием экрана, предоставляемый Hilt.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CharacterDetailScreen(
    characterId: Int,
    onBackClick: () -> Unit,
    onCloseClick: () -> Unit,
    onLocationClick: (Int) -> Unit,
    onFilterClick: (String, String) -> Unit,
    onEpisodeClick: (Int) -> Unit, // Новый параметр для навигации к деталям эпизода
    viewModel: CharacterDetailViewModel = hiltViewModel()
) {
    LaunchedEffect(key1 = characterId) {
        viewModel.loadCharacterDetails(characterId)
    }

    val state by viewModel.characterDetailState.collectAsState()

    Scaffold(
        containerColor = Color.Transparent,
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
                },
                actions = {
                    IconButton(onClick = onCloseClick) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Кнопка закрыть"
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f)
                )
            )
        }
    ) { paddingValues ->
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

                Card(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        item {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                AsyncImage(
                                    model = character.imageUrl,
                                    contentDescription = "Изображение ${character.name}",
                                    modifier = Modifier
                                        .padding(top = 16.dp)
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

                        item {
                            DetailText(
                                label = "Статус",
                                value = character.status,
                                onClick = { onFilterClick("status", character.status) }
                            )
                        }
                        item {
                            DetailText(
                                label = "Вид",
                                value = character.species,
                                onClick = { onFilterClick("species", character.species) }
                            )
                        }

                        character.type?.let { type ->
                            if (type.isNotBlank()) {
                                item {
                                    DetailText(
                                        label = "Тип",
                                        value = type,
                                        onClick = { onFilterClick("type", type) }
                                    )
                                }
                            }
                        }

                        item {
                            DetailText(
                                label = "Пол",
                                value = character.gender,
                                onClick = { onFilterClick("gender", character.gender) }
                            )
                        }

                        item {
                            DetailText(
                                label = "Происхождение",
                                value = origin.name,
                                onClick = {
                                    if (origin.url.isNotBlank()) {
                                        val locationId =
                                            origin.url.substringAfterLast("/").toIntOrNull()
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
                                    if (location.url.isNotBlank()) {
                                        val locationId =
                                            location.url.substringAfterLast("/").toIntOrNull()
                                        if (locationId != null) onLocationClick(locationId)
                                    }
                                }
                            )
                        }

                        if (characterDetails.episodes.isNotEmpty()) {
                            item {
                                Text(
                                    text = "Эпизоды",
                                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 16.dp, bottom = 8.dp)
                                        .padding(start = 0.dp)
                                )
                            }

                            // Обновленный блок для отображения списка эпизодов
                            items(characterDetails.episodes, key = { it.id }) { episodeSummary ->
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp)
                                        .clickable { onEpisodeClick(episodeSummary.id) }
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(16.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = episodeSummary.name,
                                            style = MaterialTheme.typography.bodyMedium,
                                            modifier = Modifier.weight(1f)
                                        )
                                    }
                                }
                            }
                        }
                        item { Box(modifier = Modifier.padding(bottom = 16.dp)) }
                    }
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
