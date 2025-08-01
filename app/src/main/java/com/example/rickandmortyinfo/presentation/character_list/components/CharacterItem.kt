package com.example.rickandmortyinfo.presentation.character_list.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.rickandmortyinfo.R

@Composable
fun CharacterItem(
    name: String,
    species: String,
    status: String,
    gender: String,
    imageUrl: String,
    modifier: Modifier = Modifier
) {
    Card(
        // Используем fillMaxWidth, чтобы макеты в LazyVerticalGrid
        // всегда были одинаковой ширины.
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier.padding(8.dp)
        ) {
            AsyncImage(
                model = imageUrl,
                contentDescription = "Изображение персонажа $name",
                modifier = Modifier
                    .fillMaxWidth()
                    // !!! Ключевое изменение: задаем фиксированное соотношение сторон
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(4.dp)),
                // !!! Ключевое изменение: используем ContentScale.Crop, чтобы
                // изображение всегда заполняло доступное пространство
                contentScale = ContentScale.Crop,
                placeholder = painterResource(id = R.drawable.ic_placeholder),
                error = painterResource(id = R.drawable.ic_error)
            )
            Text(
                text = name,
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .padding(top = 8.dp)
                    .fillMaxWidth()
            )
            Text(
                text = "$species - $status ($gender)",
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
