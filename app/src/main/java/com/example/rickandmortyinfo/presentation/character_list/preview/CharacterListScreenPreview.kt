package com.example.rickandmortyinfo.presentation.character_list.preview

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.rickandmortyinfo.presentation.character_list.CharacterListScreen
import com.example.rickandmortyinfo.presentation.ui.theme.RickAndMortyInfoTheme


class CharacterListScreenPreview {

    @Preview(showBackground = true)
    @Composable
    fun CharacterListScreenDefaultPreview() {
        RickAndMortyInfoTheme {
            // В превью hiltViewModel() не может инжектировать ViewModel.
            // Поэтому, для превью самого экрана CharactersListScreen,
            // мы не можем просто передать ViewModel по умолчанию.
            // Вместо этого, мы можем создать упрощенную версию экрана
            // или сосредоточиться на превью отдельных компонентов.

            // Если вы хотите увидеть просто структуру Scaffold с TopAppBar:
            CharacterListScreen(
                // Не передаем ViewModel, так как hiltViewModel() не работает в превью.
                // В реальном приложении Hilt предоставит ее.
                // Для полноценного превью с данными PagingData,
                // обычно создают отдельный Composable, который принимает PagingData
                // напрямую, а не ViewModel.
                onCharacterClick = { characterId ->
                    // Обрабатываем клик для превью, например, выводим в лог
                    // Предполагается, что onCharacterClick принимает ID персонажа
                    Log.d("PreviewClick", "Персонаж нажат в превью: ID $characterId")
                }
            )
        }
    }
}