package com.example.rickandmortyinfo.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.presentation.screens.CharacterDetailScreen
import com.example.rickandmortyinfo.presentation.character_list.CharacterListScreen
import com.example.rickandmortyinfo.presentation.location_detail.LocationDetailScreen

@Composable
fun RickAndMortyNavHost() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "character_list"
    ) {
        // Экран со списком персонажей теперь принимает необязательные аргументы для фильтрации.
        composable(
            route = "character_list?status={status}&gender={gender}&type={type}",
            arguments = listOf(
                navArgument("status") {
                    type = NavType.StringType
                    defaultValue = ""
                    nullable = true
                },
                navArgument("gender") {
                    type = NavType.StringType
                    defaultValue = ""
                    nullable = true
                },
                navArgument("type") {
                    type = NavType.StringType
                    defaultValue = ""
                    nullable = true
                }
            )
        ) { backStackEntry ->
            // Получаем значения фильтров из аргументов навигации
            val statusFilter = backStackEntry.arguments?.getString("status")
            val genderFilter = backStackEntry.arguments?.getString("gender")
            val typeFilter = backStackEntry.arguments?.getString("type")

            CharacterListScreen(
                onCharacterClick = { characterId ->
                    navController.navigate("character_detail/$characterId")
                },
                // Передаем полученные фильтры на экран списка персонажей.
                initialStatusFilter = statusFilter,
                initialGenderFilter = genderFilter,
                initialTypeFilter = typeFilter
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
                onCloseClick = {
                    navController.popBackStack("character_list", false)
                },
                onLocationClick = { locationId ->
                    navController.navigate("location_detail/$locationId")
                },
                // Новый колбэк для навигации с фильтром
                onFilterClick = { filterType, filterValue ->
                    // Формируем маршрут на основе типа фильтра и значения
                    val route = when (filterType) {
                        "status" -> "character_list?status=$filterValue"
                        "gender" -> "character_list?gender=$filterValue"
                        "type" -> "character_list?type=$filterValue"
                        else -> "character_list"
                    }
                    navController.navigate(route)
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
                onCloseClick = {
                    navController.popBackStack("character_list", false)
                },
                onBackClick = {
                    navController.popBackStack()
                },
                onCharacterClick = { characterId ->
                    navController.navigate("character_detail/$characterId")
                }
            )
        }
    }
}
