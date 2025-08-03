package com.example.rickandmortyinfo.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.presentation.screens.CharacterDetailScreen
import com.example.rickandmortyinfo.presentation.character_list.CharacterListScreen
import com.example.rickandmortyinfo.presentation.location_detail.LocationDetailScreen // Импортируем новый экран

@Composable
fun RickAndMortyNavHost() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "character_list"
    ) {
        // Экран со списком персонажей
        composable("character_list") {
            CharacterListScreen(
                onCharacterClick = { characterId ->
                    navController.navigate("character_detail/$characterId")
                }
            )
        }

        // Экран с деталями персонажа
        composable(
            route = "character_detail/{characterId}",
            arguments = listOf(navArgument("characterId") { type = NavType.IntType })
        ) { backStackEntry ->
            val characterId = backStackEntry.arguments?.getInt("characterId") ?: -1
            CharacterDetailScreen(
                characterId = characterId,
                onBackClick = {
                    navController.popBackStack()
                },
                onLocationClick = { locationId ->
                    // Навигация на экран деталей локации при клике на локацию
                    navController.navigate("location_detail/$locationId")
                }
            )
        }

        // Экран с деталями локации
        composable(
            route = "location_detail/{locationId}",
            arguments = listOf(navArgument("locationId") { type = NavType.IntType })
        ) { backStackEntry ->
            val locationId = backStackEntry.arguments?.getInt("locationId") ?: -1
            LocationDetailScreen(
                locationId = locationId,
                onBackClick = {
                    navController.popBackStack()
                },
                onCharacterClick = { characterId ->
                    // Навигация обратно на экран деталей персонажа
                    navController.navigate("character_detail/$characterId")
                }
            )
        }
    }
}
