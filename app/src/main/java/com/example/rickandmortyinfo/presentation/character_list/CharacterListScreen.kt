// app/src/main/java/com/example/rickandmortyinfo/presentation/character_list/CharacterListScreen.kt
package com.example.rickandmortyinfo.presentation.character_list

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.example.rickandmortyinfo.presentation.character_list.components.CharacterItem
import com.example.rickandmortyinfo.presentation.character_list.components.CharacterListToolbar

@Composable
fun CharacterListScreen(
    // ViewModel автоматически инжектируется благодаря @HiltViewModel и hiltViewModel()
    viewModel: CharactersViewModel = hiltViewModel()
) {
    // Собираем Flow<PagingData<RMCharacter>> из ViewModel в LazyPagingItems
    val characters = viewModel.characters.collectAsLazyPagingItems()

    Scaffold(
        topBar = {
            CharacterListToolbar(
                title = "Rick and Morty Characters",
                onFilterClick = {
                    // TODO: Реализовать логику открытия окна фильтрации
                    // Например, показ BottomSheetDialog с опциями фильтрации
                }
            )
        }
    ) { paddingValues ->
        // Box используется для центрирования индикатора загрузки или текста ошибки
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Отображение списка персонажей
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(
                    count = characters.itemCount,
                    key = characters.itemKey { it.id } // Используем ID персонажа как уникальный ключ
                ) { index ->
                    val character = characters[index]
                    if (character != null) {
                        CharacterItem(
                            name = character.name,
                            species = character.species,
                            status = character.status,
                            gender = character.gender,
                            imageUrl = character.imageUrl
                        )
                    }
                }

                // Обработка состояний загрузки (Loading, Error, NotLoading)
                characters.apply {
                    when {
                        // Начальная загрузка или обновление
                        loadState.refresh is LoadState.Loading -> {
                            item {
                                Box(
                                    modifier = Modifier.fillParentMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator()
                                }
                            }
                        }
                        // Загрузка дополнительных элементов (скролл вниз)
                        loadState.append is LoadState.Loading -> {
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator()
                                }
                            }
                        }
                        // Ошибка при начальной загрузке
                        loadState.refresh is LoadState.Error -> {
                            val error = loadState.refresh as LoadState.Error
                            item {
                                Column(
                                    modifier = Modifier.fillParentMaxSize(),
                                    verticalArrangement = Arrangement.Center,
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = "Error: ${error.error.localizedMessage ?: "Unknown error"}",
                                        modifier = Modifier.padding(16.dp)
                                    )
                                    // Можно добавить кнопку для повторной попытки загрузки
                                }
                            }
                        }
                        // Ошибка при загрузке дополнительных элементов
                        loadState.append is LoadState.Error -> {
                            val error = loadState.append as LoadState.Error
                            item {
                                Text(
                                    text = "Error loading more: ${error.error.localizedMessage ?: "Unknown error"}",
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp)
                                )
                                // Можно добавить кнопку для повторной попытки загрузки
                            }
                        }
                    }
                }
            }
        }
    }
}