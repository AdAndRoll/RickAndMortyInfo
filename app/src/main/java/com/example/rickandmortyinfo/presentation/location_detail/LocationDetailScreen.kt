package com.example.rickandmortyinfo.presentation.location_detail

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
 * @param onBackClick Функция, которая будет вызвана при нажатии кнопки "назад".
 * @param onCharacterClick Функция, которая будет вызвана при нажатии на жителя.
 * @param viewModel ViewModel для управления состоянием экрана, предоставляемый Hilt.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocationDetailScreen(
    locationId: Int,
    onBackClick: () -> Unit,
    onCharacterClick: (Int) -> Unit, // Новый параметр для навигации по персонажам
    viewModel: LocationDetailViewModel = hiltViewModel()
) {
    // Запускаем загрузку данных, когда компонент впервые появляется на экране.
    LaunchedEffect(key1 = locationId) {
        viewModel.loadLocationDetails(locationId)
    }

    // Собираем состояние из ViewModel.
    val state by viewModel.locationDetailState.collectAsState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Детали локации",
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

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
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

                    // Теперь используем `residentNames` вместо `residents`
                    if (location.residentNames.isNotEmpty()) {
                        item {
                            Text(
                                text = "Жители",
                                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 16.dp, bottom = 8.dp)
                            )
                        }

                        // Отображаем имена жителей из нового списка
                        items(location.residentNames) { residentName ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                                // Убрана логика клика, так как у нас нет ID для навигации.
                                // Вы можете добавить ее, если измените LocationDetail,
                                // чтобы он включал ID.
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = residentName,
                                        style = MaterialTheme.typography.bodyMedium,
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            }
                        }
                    }
                    // Добавляем отступ в конце списка.
                    item { Box(modifier = Modifier.padding(bottom = 16.dp)) }
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
