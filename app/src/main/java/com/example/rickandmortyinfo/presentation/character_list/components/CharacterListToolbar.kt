package com.example.rickandmortyinfo.presentation.character_list.components


import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList

import androidx.compose.material3.Icon
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.vector.ImageVector

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CharacterListToolbar(
    title: String,
    onFilterClick: () -> Unit,
    modifier: Modifier = Modifier,
    filterIcon: ImageVector = Icons.Default.FilterList,
) {
    TopAppBar(
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        },
        actions = {
            IconButton(onClick = onFilterClick) {
                Icon(
                    imageVector = filterIcon,
                    contentDescription = "Filter"
                )
            }
        },
        modifier = modifier,
    )
}
