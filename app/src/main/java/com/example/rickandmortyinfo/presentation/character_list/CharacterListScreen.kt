package com.example.rickandmortyinfo.presentation.character_list

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
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
import com.example.rickandmortyinfo.presentation.character_list.components.CharacterItem
import com.example.rickandmortyinfo.presentation.character_list.components.CharacterListToolbar

@Composable
fun CharacterListScreen(
    viewModel: CharactersViewModel = hiltViewModel()
) {
    val characters = viewModel.characters.collectAsLazyPagingItems()

    Scaffold(
        topBar = {
            CharacterListToolbar(
                title = "Rick and Morty Characters",
                onFilterClick = {
                    // TODO: Реализовать логику открытия окна фильтрации
                }
            )
        }
    ) { paddingValues ->
        // Отображаем полноэкранный индикатор загрузки только при первой загрузке
        if (characters.loadState.refresh is LoadState.Loading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            // Как только первая порция данных загружена, показываем список
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(characters.itemCount) { index ->
                        val character = characters[index]
                        if (character != null) {
                            CharacterItem(
                                name = character.name ?: "Unknown",
                                species = character.species ?: "Unknown",
                                status = character.status ?: "Unknown",
                                gender = character.gender ?: "Unknown",
                                imageUrl = character.imageUrl ?: ""
                            )
                        }
                    }

                    // Обработка состояния загрузки дополнительных страниц (пагинация)
                    characters.apply {
                        when (loadState.append) {
                            is LoadState.Loading -> {
                                item(span = { GridItemSpan(maxLineSpan) }) {
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
                            is LoadState.Error -> {
                                val error = loadState.append as LoadState.Error
                                item(span = { GridItemSpan(maxLineSpan) }) {
                                    Text(
                                        text = "Ошибка загрузки: ${error.error.localizedMessage ?: "Неизвестная ошибка"}",
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(16.dp)
                                    )
                                }
                            }
                            else -> {}
                        }
                    }
                }

                // Сообщение, если персонажей не найдено
                if (characters.loadState.refresh is LoadState.NotLoading && characters.itemCount == 0) {
                    Text(
                        text = "Персонажи не найдены.",
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }
        }
    }
}
