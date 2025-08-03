package com.example.rickandmortyinfo.presentation.location_detail

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.domain.model.LocationDetail
import com.example.rickandmortyinfo.presentation.location_detail.components.DetailTextLocation

/**
 * Компонуемая функция для экрана с детальной информацией о локации.
 *
 * @param locationId ID локации, которую нужно отобразить.
 * @param onBackClick Функция, которая будет вызвана при нажатии кнопки "закрыть" (крестик)
 * для возврата к списку персонажей.
 * @param onArrowBackClick Функция, которая будет вызвана при нажатии кнопки "назад" (стрелка)
 * для возврата на предыдущий экран в стеке.
 * @param onCharacterClick Функция, которая будет вызвана при нажатии на жителя.
 * @param viewModel ViewModel для управления состоянием экрана, предоставляемый Hilt.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocationDetailScreen(
    locationId: Int,
    onBackClick: () -> Unit,
    onCloseClick: () -> Unit,
    onCharacterClick: (Int) -> Unit,
    viewModel: LocationDetailViewModel = hiltViewModel()
) {
    LaunchedEffect(key1 = locationId) {
        viewModel.loadLocationDetails(locationId)
    }

    val state by viewModel.locationDetailState.collectAsState()

    Scaffold(
        // Делаем фон Scaffold прозрачным, чтобы видеть фоновое изображение
        containerColor = Color.Transparent,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Детали локации",
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                // Кнопка "назад" слева
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Кнопка назад"
                        )
                    }
                },
                // Кнопка "закрыть" справа
                actions = {
                    IconButton(onClick = onCloseClick) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Кнопка закрыть"
                        )
                    }
                },
                // Делаем TopAppBar менее прозрачным
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f)
                )
            )
        }
    ) { paddingValues ->
        when (val currentState = state) {
            is LocationDetailState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            is LocationDetailState.Success -> {
                val location: LocationDetail = currentState.location

                // Оборачиваем весь контент в полностью непрозрачную Card для лучшей читаемости
                Card(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(16.dp),
                    // Используем полностью непрозрачный белый цвет для фона карточки
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
                            Text(
                                text = location.name,
                                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(top = 16.dp)
                            )
                        }

                        item { DetailTextLocation(label = "Тип", value = location.type) }
                        item { DetailTextLocation(label = "Измерение", value = location.dimension) }

                        if (location.residents.isNotEmpty()) {
                            item {
                                Text(
                                    text = "Жители",
                                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 16.dp, bottom = 8.dp)
                                )
                            }

                            items(location.residents) { resident ->
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp)
                                        .clickable {
                                            onCharacterClick(resident.id)
                                        }
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(16.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = resident.name,
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
            is LocationDetailState.Error -> {
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
