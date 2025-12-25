package com.example.plantmanager.ui




import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.plantmanager.ui.screens.PlantListScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.PlantList.route
    ) {
        composable(Screen.PlantList.route) {
            PlantListScreen(
                onPlantClick = { plant ->
                    // Naviguer vers le détail
                    // navController.navigate("plant_detail/${plant.id}")
                },
                onAddPlantClick = {
                    // Naviguer vers l'ajout
                    // navController.navigate(Screen.AddPlant.route)
                }
            )
        }

        // Ajouter d'autres écrans ici
    }
}

sealed class Screen(val route: String) {
    object PlantList : Screen("plant_list")
    object PlantDetail : Screen("plant_detail/{plantId}") {
        fun createRoute(plantId: Int) = "plant_detail/$plantId"
    }
    object AddPlant : Screen("add_plant")
    object Settings : Screen("settings")
}