package com.example.rickandmortyinfo.presentation.character_list.preview

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.rickandmortyinfo.presentation.character_list.components.CharacterListToolbar

@Preview(showBackground = true)
@Composable
fun CharacterListToolbarPreview() {
    MaterialTheme {
        CharacterListToolbar(
            title = "Characters",
            onFilterClick = {}
        )
    }
}