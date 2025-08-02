package com.example.rickandmortyinfo.presentation.character_list

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.rickandmortyinfo.presentation.character_filter.CharacterFilterScreen
import com.example.rickandmortyinfo.presentation.character_list.components.CharacterItem
import com.example.rickandmortyinfo.presentation.character_list.components.CharacterListToolbar
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CharacterListScreen(
    // Добавляем параметр для обработки клика на элемент списка
    onCharacterClick: (Int) -> Unit,
    viewModel: CharactersViewModel = hiltViewModel()
) {
    val characters = viewModel.characters.collectAsLazyPagingItems()
    var showFilterSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            CharacterListToolbar(
                title = "Rick and Morty Characters",
                onFilterClick = {
                    showFilterSheet = true
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (characters.loadState.refresh is LoadState.Loading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
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
                                imageUrl = character.imageUrl ?: "",
                                // Добавляем модификатор .clickable для навигации
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        onCharacterClick(character.id)
                                    }
                            )
                        }
                    }

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

                if (characters.loadState.refresh is LoadState.NotLoading && characters.itemCount == 0) {
                    Text(
                        text = "Персонажи не найдены.",
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }
        }
    }

    if (showFilterSheet) {
        ModalBottomSheet(
            onDismissRequest = {
                showFilterSheet = false
            },
            sheetState = sheetState
        ) {
            CharacterFilterScreen(
                onApplyFilter = { newFilter ->
                    viewModel.onFilterApplied(newFilter)
                    coroutineScope.launch {
                        characters.refresh()
                        sheetState.hide()
                        showFilterSheet = false
                    }
                },
                onDismiss = {
                    coroutineScope.launch {
                        sheetState.hide()
                        showFilterSheet = false
                    }
                }
            )
        }
    }
}
