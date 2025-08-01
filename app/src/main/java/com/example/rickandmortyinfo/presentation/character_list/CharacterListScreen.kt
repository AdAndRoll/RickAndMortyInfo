package com.example.rickandmortyinfo.presentation.character_list

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.paging.compose.itemKey
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
                items(
                    count = characters.itemCount,
                    key = characters.itemKey { it.id }
                ) { index ->
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

                characters.apply {
                    when {
                        loadState.refresh is LoadState.Loading -> {
                            item(span = { GridItemSpan(maxLineSpan) }) {
                                Box(
                                    modifier = Modifier.fillMaxWidth(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator()
                                }
                            }
                        }
                        loadState.append is LoadState.Loading -> {
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
                        loadState.refresh is LoadState.Error -> {
                            val error = loadState.refresh as LoadState.Error
                            item(span = { GridItemSpan(maxLineSpan) }) {
                                Column(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalArrangement = Arrangement.Center,
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = "Error: ${error.error.localizedMessage ?: "Unknown error"}",
                                        modifier = Modifier.padding(16.dp)
                                    )
                                }
                            }
                        }
                        loadState.append is LoadState.Error -> {
                            val error = loadState.append as LoadState.Error
                            item(span = { GridItemSpan(maxLineSpan) }) {
                                Text(
                                    text = "Error loading more: ${error.error.localizedMessage ?: "Unknown error"}",
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp)
                                )
                            }
                        }
                    }
                }
            }

            if (characters.loadState.refresh is LoadState.NotLoading && characters.itemCount == 0) {
                Text(
                    text = "No characters found.",
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
    }
}
