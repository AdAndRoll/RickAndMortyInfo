    package com.example.rickandmortyinfo.presentation.character_filter

    import androidx.compose.foundation.layout.*
    import androidx.compose.foundation.rememberScrollState
    import androidx.compose.foundation.verticalScroll
    import androidx.compose.material.icons.Icons
    import androidx.compose.material.icons.filled.ArrowDropDown
    import androidx.compose.material.icons.filled.Close
    import androidx.compose.material3.*
    import androidx.compose.runtime.*
    import androidx.compose.ui.Alignment
    import androidx.compose.ui.Modifier
    import androidx.compose.ui.unit.dp
    import com.example.domain.model.CharacterFilter


    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun CharacterFilterScreen(
        onApplyFilter: (CharacterFilter) -> Unit,
        onDismiss: () -> Unit
    ) {
        // Состояние для каждого фильтра
        var name by remember { mutableStateOf("") }
        var status by remember { mutableStateOf<String?>(null) }
        var species by remember { mutableStateOf("") }
        var type by remember { mutableStateOf("") }
        var gender by remember { mutableStateOf<String?>(null) }

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Фильтры") },
                    navigationIcon = {
                        IconButton(onClick = onDismiss) {
                            Icon(Icons.Default.Close, contentDescription = "Закрыть")
                        }
                    }
                )
            },
            bottomBar = {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        onClick = {
                            onApplyFilter(
                                CharacterFilter(
                                    name = name.ifBlank { null },
                                    status = status,
                                    species = species.ifBlank { null },
                                    type = type.ifBlank { null },
                                    gender = gender
                                )
                            )
                            onDismiss()
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Применить")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    OutlinedButton(
                        onClick = {
                            // Сброс всех фильтров
                            name = ""
                            status = null
                            species = ""
                            type = ""
                            gender = null
                            onApplyFilter(CharacterFilter())
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Сбросить")
                    }
                }
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Фильтр по имени
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Имя") },
                    modifier = Modifier.fillMaxWidth()
                )

                // Фильтр по статусу
                FilterDropdown(
                    label = "Статус",
                    options = listOf("alive", "dead", "unknown"),
                    selectedOption = status,
                    onOptionSelected = { status = it }
                )

                // Фильтр по виду
                OutlinedTextField(
                    value = species,
                    onValueChange = { species = it },
                    label = { Text("Вид") },
                    modifier = Modifier.fillMaxWidth()
                )

                // Фильтр по типу
                OutlinedTextField(
                    value = type,
                    onValueChange = { type = it },
                    label = { Text("Тип") },
                    modifier = Modifier.fillMaxWidth()
                )

                // Фильтр по полу
                FilterDropdown(
                    label = "Пол",
                    options = listOf("female", "male", "genderless", "unknown"),
                    selectedOption = gender,
                    onOptionSelected = { gender = it }
                )
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun FilterDropdown(
        label: String,
        options: List<String>,
        selectedOption: String?,
        onOptionSelected: (String?) -> Unit
    ) {
        var expanded by remember { mutableStateOf(false) }

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded },
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedTextField(
                value = selectedOption ?: "Не выбрано",
                onValueChange = {},
                readOnly = true,
                label = { Text(label) },
                trailingIcon = {
                    Icon(
                        Icons.Default.ArrowDropDown,
                        contentDescription = "Развернуть меню"
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor()
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                // Опция для сброса выбора
                DropdownMenuItem(
                    text = { Text("Не выбрано") },
                    onClick = {
                        onOptionSelected(null)
                        expanded = false
                    }
                )
                // Список всех опций
                options.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option.replaceFirstChar { it.uppercase() }) },
                        onClick = {
                            onOptionSelected(option)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
