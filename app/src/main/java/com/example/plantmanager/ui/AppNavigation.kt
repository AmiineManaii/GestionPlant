package com.example.plantmanager.ui

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.example.plantmanager.viewmodels.PlantViewModel
import com.example.plantmanager.viewmodels.WeatherViewModel
import com.example.plantmanager.ui.screens.PlantListScreen
import com.example.plantmanager.ui.screens.AddPlantScreen
import com.example.plantmanager.ui.screens.PlantDetailScreen
import com.example.plantmanager.ui.screens.EditPlantScreen
import com.example.plantmanager.ui.screens.WeatherDetailScreen
import com.example.plantmanager.ui.screens.WateringHistoryScreen
import com.example.plantmanager.ui.screens.WateringCalendarScreen
import com.example.plantmanager.ui.screens.AdviceScreen
import com.example.plantmanager.ui.screens.StatisticsScreen

@Composable
fun AppNavigation(
    plantViewModel: PlantViewModel,
    weatherViewModel: WeatherViewModel
) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.PlantList.route
    ) {
        composable(Screen.PlantList.route) {
            PlantListScreen(
                plantViewModel = plantViewModel,
                weatherViewModel = weatherViewModel,
                onPlantClick = { plant ->
                    navController.navigate(Screen.PlantDetail.createRoute(plant))
                },
                onAddPlantClick = {
                    navController.navigate(Screen.AddPlant.route)
                },
                onWeatherClick = {
                    navController.navigate(Screen.WeatherDetail.route)
                },
                onCalendarClick = {
                    navController.navigate(Screen.WateringCalendar.route)
                },
                onAdviceClick = {
                    navController.navigate(Screen.Advice.route)
                },
                onStatsClick = {
                    navController.navigate(Screen.Statistics.route)
                }
            )
        }

        composable(Screen.AddPlant.route) {
            AddPlantScreen(
                plantViewModel = plantViewModel,
                onBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.PlantDetail.route,
            arguments = listOf(navArgument("plantId") { type = NavType.IntType })
        ) { backStackEntry ->
            val plantId = backStackEntry.arguments?.getInt("plantId") ?: return@composable
            PlantDetailScreen(
                plantId = plantId,
                plantViewModel = plantViewModel,
                onBack = { navController.popBackStack() },
                onEdit = { id -> navController.navigate(Screen.EditPlant.createRoute(id)) },
                onViewHistory = { id -> navController.navigate(Screen.WateringHistory.createRoute(id)) },
                weatherViewModel = weatherViewModel
            )
        }

        composable(
            route = Screen.EditPlant.route,
            arguments = listOf(navArgument("plantId") { type = NavType.IntType })
        ) { backStackEntry ->
            val plantId = backStackEntry.arguments?.getInt("plantId") ?: return@composable
            EditPlantScreen(
                plantId = plantId,
                plantViewModel = plantViewModel,
                onBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.WateringHistory.route,
            arguments = listOf(navArgument("plantId") { type = NavType.IntType })
        ) { backStackEntry ->
            val plantId = backStackEntry.arguments?.getInt("plantId") ?: return@composable
            WateringHistoryScreen(
                plantId = plantId,
                plantViewModel = plantViewModel,
                onBack = { navController.popBackStack() }
            )
        }
        
        composable(Screen.WeatherDetail.route) {
            WeatherDetailScreen(
                weatherViewModel = weatherViewModel,
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.WateringCalendar.route) {
            WateringCalendarScreen(
                plantViewModel = plantViewModel,
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Advice.route) {
            AdviceScreen(
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Statistics.route) {
            StatisticsScreen(
                plantViewModel = plantViewModel,
                onBack = { navController.popBackStack() }
            )
        }
    }
}

sealed class Screen(val route: String) {
    object PlantList : Screen("plant_list")
    object PlantDetail : Screen("plant_detail/{plantId}") {
        fun createRoute(plantId: Int) = "plant_detail/$plantId"
    }
    object AddPlant : Screen("add_plant")
    object EditPlant : Screen("plant_edit/{plantId}") {
        fun createRoute(plantId: Int) = "plant_edit/$plantId"
    }
    object WateringHistory : Screen("watering_history/{plantId}") {
        fun createRoute(plantId: Int) = "watering_history/$plantId"
    }
    object WeatherDetail : Screen("weather_detail")
    object WateringCalendar : Screen("watering_calendar")
    object Advice : Screen("advice")
    object Statistics : Screen("statistics")
}
