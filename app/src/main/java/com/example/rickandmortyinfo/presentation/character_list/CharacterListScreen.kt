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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.rickandmortyinfo.presentation.character_filter.CharacterFilterScreen
import com.example.rickandmortyinfo.presentation.character_list.components.CharacterItem
import com.example.rickandmortyinfo.presentation.character_list.components.CharacterListToolbar
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CharacterListScreen(
    onCharacterClick: (Int) -> Unit,
    viewModel: CharactersViewModel = hiltViewModel()
) {
    val characters = viewModel.characters.collectAsLazyPagingItems()
    var showFilterSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val coroutineScope = rememberCoroutineScope()

    val loadState = characters.loadState
    val swipeRefreshState = rememberSwipeRefreshState(
        isRefreshing = loadState.refresh is LoadState.Loading
    )

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
        SwipeRefresh(
            state = swipeRefreshState,
            onRefresh = { characters.refresh() },
            modifier = Modifier.padding(paddingValues)
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                // 1. Отображаем список, если он уже содержит элементы
                // Это должно перекрыть случай, когда refresh еще Loading, но данные уже начали поступать
                if (characters.itemCount > 0) {
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
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            onCharacterClick(character.id)
                                        }
                                )
                            }
                        }

                        // Состояние загрузки следующих страниц (append)
                        when (loadState.append) {
                            is LoadState.Loading -> {
                                item(span = { GridItemSpan(maxLineSpan) }) {
                                    Box(
                                        modifier = Modifier.fillMaxWidth().padding(16.dp),
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
                                        modifier = Modifier.fillMaxWidth().padding(16.dp)
                                    )
                                    // Button(onClick = { characters.retry() }) { Text("Повторить append") }
                                }
                            }
                            else -> {} // NotLoading или нет ошибок
                        }
                    }
                }
                // 2. Если список пуст, анализируем состояния загрузки
                else {
                    when {
                        // Пока идет первоначальная загрузка ИЛИ обновление через swipe-to-refresh
                        loadState.refresh is LoadState.Loading -> {
                            CircularProgressIndicator()
                        }
                        // Ошибка при первоначальной загрузке ИЛИ обновлении
                        loadState.refresh is LoadState.Error -> {
                            val error = loadState.refresh as LoadState.Error
                            Text(
                                text = "Ошибка: ${error.error.localizedMessage ?: "Неизвестная ошибка"}",
                                modifier = Modifier.padding(16.dp)
                            )
                            // Button(onClick = { characters.refresh() }) { Text("Повторить refresh") }
                        }
                        // Первоначальная загрузка завершена, ошибок нет, но список пуст
                        loadState.refresh is LoadState.NotLoading &&
                                loadState.append.endOfPaginationReached && // Убеждаемся, что и первая, и последующие загрузки завершены
                                characters.itemCount == 0 -> {
                            Text(
                                text = "Персонажи не найдены.",
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                        // Если refresh завершен, но append все еще может что-то загрузить (маловероятно для itemCount == 0, но для полноты)
                        // или если просто еще не определились окончательно (например, начальное состояние перед первой загрузкой)
                        // В этом случае тоже можно показывать индикатор, чтобы избежать "Персонажи не найдены" преждевременно
                        else -> {
                            // Это состояние может быть очень кратковременным.
                            // Если предыдущие условия не покрывают его, и "мерцание" остается,
                            // здесь может быть место для очень короткой задержки или специфической проверки.
                            // Но в большинстве случаев, предыдущие условия должны его покрыть.
                            // Если мерцание всё ещё тут, можно попробовать оставить CircularProgressIndicator()
                            // как "fallback", пока Compose не "устаканит" состояния.
                            // CircularProgressIndicator() // <-- Раскомментируйте, если проблема не уходит
                        }
                    }
                }
            }
        }
    }

    if (showFilterSheet) {
        ModalBottomSheet(
            onDismissRequest = { showFilterSheet = false },
            sheetState = sheetState
        ) {
            CharacterFilterScreen(
                onApplyFilter = { newFilter ->
                    viewModel.onFilterApplied(newFilter)
                    coroutineScope.launch {
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


