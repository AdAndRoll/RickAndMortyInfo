package com.example.rickandmortyinfo.presentation.character_list.preview

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.rickandmortyinfo.presentation.character_list.components.CharacterItem

@Preview(showBackground = true)
@Composable
fun CharacterItemPreview() {
    MaterialTheme {
        CharacterItem(
            name = "Rick Sanchez",
            species = "Human",
            status = "Alive",
            gender = "Male",
            imageUrl = "https://rickandmortyapi.com/api/character/avatar/1.jpeg"
        )
    }
}
