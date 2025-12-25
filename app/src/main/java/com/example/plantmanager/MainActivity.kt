package com.example.plantmanager

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelProvider
import com.example.plantmanager.data.LocationManager
import com.example.plantmanager.data.local.PlantDatabase
import com.example.plantmanager.ui.screens.PlantListScreen
import com.example.plantmanager.ui.theme.PlantManagerTheme
import com.example.plantmanager.viewmodels.PlantViewModel
import com.example.plantmanager.viewmodels.WeatherViewModel

class MainActivity : ComponentActivity() {

override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)


    val database = PlantDatabase.getDatabase(this)
    val plantDao = database.plantDao()
    val locationManager = LocationManager(this)

    val plantViewModelFactory = object : ViewModelProvider.Factory {
        override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
            return PlantViewModel(plantDao) as T
        }
    }

    val weatherViewModelFactory = object : ViewModelProvider.Factory {
        override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
            return WeatherViewModel(locationManager) as T
        }
    }

    val plantViewModel: PlantViewModel by viewModels { plantViewModelFactory }
    val weatherViewModel: WeatherViewModel by viewModels { weatherViewModelFactory }

    setContent {
        PlantManagerTheme {
            // A surface container using the 'background' color from the theme
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background
            ) {
                PlantListScreen(
                    plantViewModel = plantViewModel,
                    weatherViewModel = weatherViewModel,
                    onPlantClick = { plantId ->
                        // TODO: Naviguer vers détail
                    },
                    onAddPlantClick = {
                        // TODO: Naviguer vers ajout
                    }
                )
            }
        }
    }
}
}