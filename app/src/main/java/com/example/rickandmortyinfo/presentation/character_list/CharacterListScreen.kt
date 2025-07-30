package com.example.rickandmortyinfo.presentation.character_list

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.rickandmortyinfo.presentation.character_list.components.CharacterItem
import com.example.rickandmortyinfo.presentation.character_list.components.CharacterListToolbar

//@Composable
//fun CharacterListScreen(
//    characters: List<Character>,
//    onFilterClick: () -> Unit,
//    modifier: Modifier = Modifier
//) {
//    Column(modifier = modifier.fillMaxSize()) {
//        CharacterListToolbar(
//            title = "Персонажи",
//            onFilterClick = onFilterClick
//        )
//        LazyColumn(
//            modifier = Modifier.fillMaxSize(),
//            contentPadding = PaddingValues(8.dp),
//            verticalArrangement = Arrangement.spacedBy(8.dp)
//        ) {
//            items(characters) { character ->
//                CharacterItem(
//                    name = character.name,
//                    species = character.species,
//                    status = character.status,
//                    gender = character.gender,
//                    imageUrl = character.imageUrl // предполагаем, что это поле уже есть
//                )
//            }
//        }
//    }
//}