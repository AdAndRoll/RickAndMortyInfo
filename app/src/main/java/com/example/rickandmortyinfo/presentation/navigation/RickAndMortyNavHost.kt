package com.example.rickandmortyinfo.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.presentation.screens.CharacterDetailScreen

import com.example.rickandmortyinfo.presentation.character_list.CharacterListScreen

@Composable
fun RickAndMortyNavHost() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "character_list"
    ) {
        composable("character_list") {
            // Передаем CharacterListScreen функцию-callback, которая будет вызвана при клике.
            // Она использует navController для перехода на другой экран.
            CharacterListScreen(
                onCharacterClick = { characterId ->
                    navController.navigate("character_detail/$characterId")
                }
            )
        }

        composable(
            route = "character_detail/{characterId}",
            arguments = listOf(navArgument("characterId") { type = NavType.IntType })
        ) { backStackEntry ->
            // !!! КЛЮЧЕВОЕ ИСПРАВЛЕНИЕ:
            // Извлекаем characterId из backStackEntry и передаем его в CharacterDetailScreen.
            val characterId = backStackEntry.arguments?.getInt("characterId") ?: -1
            CharacterDetailScreen(
                characterId = characterId,
                onBackClick = {
                    navController.popBackStack() // Navigate back
                },
            )
        }
    }
}
