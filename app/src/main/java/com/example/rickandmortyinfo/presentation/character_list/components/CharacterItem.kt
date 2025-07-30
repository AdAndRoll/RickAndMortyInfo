package com.example.rickandmortyinfo.presentation.character_list.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage

@Composable
fun CharacterItem(
    name: String,
    species: String,
    status: String,
    gender: String,
    imageUrl: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .padding(8.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .fillMaxWidth()
    ) {
        AsyncImage(
            model = imageUrl,
            contentDescription = name,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp)
                .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp))
        )
        Column(modifier = Modifier.padding(8.dp)) {
            Text(text = name, style = MaterialTheme.typography.titleMedium)
            Text(text = "$species • $status", style = MaterialTheme.typography.bodyMedium)
            Text(text = gender, style = MaterialTheme.typography.bodySmall)
        }
    }
}
