package com.example.rickandmortyinfo.presentation.character_list.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults // Для cardColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage // ИМПОРТ ДЛЯ COIL
import com.example.rickandmortyinfo.R // Убедитесь, что у вас есть ресурс для плейсхолдера

@Composable
fun CharacterItem(
    name: String,
    species: String,
    status: String,
    gender: String,
    imageUrl: String,
    modifier: Modifier = Modifier // Добавим модификатор для гибкости
) {
    Card(
        modifier = modifier.fillMaxWidth(), // Убедитесь, что карточка заполняет доступную ширину
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier.padding(8.dp)
        ) {
            // Использование AsyncImage из Coil для загрузки изображения
            AsyncImage(
                model = imageUrl, // URL изображения
                contentDescription = "Image of $name", // Описание для доступности
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f) // Сохраняет соотношение сторон 1:1 (квадрат)
                    .clip(RoundedCornerShape(4.dp)), // Скругление углов для изображения
                contentScale = ContentScale.Crop, // Обрезает изображение, чтобы оно заполняло ImageView
                placeholder = coil.compose.rememberAsyncImagePainter(R.drawable.ic_placeholder), // Плейсхолдер при загрузке
                error = coil.compose.rememberAsyncImagePainter(R.drawable.ic_error) // Изображение ошибки
            )

            Text(
                text = name,
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
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