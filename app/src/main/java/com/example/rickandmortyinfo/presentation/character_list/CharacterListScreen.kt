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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.domain.model.CharacterFilter
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
    initialStatusFilter: String?,
    initialGenderFilter: String?,
    initialTypeFilter: String?,
    viewModel: CharactersViewModel = hiltViewModel()
) {
    LaunchedEffect(
        key1 = initialStatusFilter,
        key2 = initialGenderFilter,
        key3 = initialTypeFilter
    ) {
        if (!initialStatusFilter.isNullOrEmpty() ||
            !initialGenderFilter.isNullOrEmpty() ||
            !initialTypeFilter.isNullOrEmpty()
        ) {
            val newFilter = CharacterFilter(
                status = initialStatusFilter,
                gender = initialGenderFilter,
                type = initialTypeFilter
            )
            viewModel.onFilterApplied(newFilter)
        }
    }

    val characters = viewModel.characters.collectAsLazyPagingItems()
    var showFilterSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val coroutineScope = rememberCoroutineScope()

    val loadState = characters.loadState
    val swipeRefreshState = rememberSwipeRefreshState(
        isRefreshing = loadState.refresh is LoadState.Loading
    )

    Scaffold(
        containerColor = Color.Transparent,
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
                } else {
                    when {
                        loadState.refresh is LoadState.Loading -> {
                            CircularProgressIndicator()
                        }

                        loadState.refresh is LoadState.Error -> {
                            val error = loadState.refresh as LoadState.Error
                            Text(
                                text = "Ошибка: ${error.error.localizedMessage ?: "Неизвестная ошибка"}",
                                modifier = Modifier.padding(16.dp)
                            )
                        }

                        loadState.refresh is LoadState.NotLoading &&
                                loadState.append.endOfPaginationReached &&
                                characters.itemCount == 0 -> {
                            Text(
                                text = "Персонажи не найдены.",
                                modifier = Modifier.padding(16.dp)
                            )
                        }

                        else -> {}
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
