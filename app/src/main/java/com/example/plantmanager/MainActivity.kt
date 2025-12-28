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
import com.example.plantmanager.ui.AppNavigation
import com.example.plantmanager.ui.theme.PlantManagerTheme
import com.example.plantmanager.viewmodels.PlantViewModel
import com.example.plantmanager.viewmodels.WeatherViewModel
import com.example.plantmanager.viewmodels.AuthViewModel
 

class MainActivity : ComponentActivity() {

override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)


    val database = PlantDatabase.getDatabase(this)
    val plantDao = database.plantDao()
    val userDao = database.userDao()
    val sessionDao = database.sessionDao()
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
    val authViewModelFactory = object : ViewModelProvider.Factory {
        override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
            return AuthViewModel(userDao, sessionDao) as T
        }
    }

    val plantViewModel: PlantViewModel by viewModels { plantViewModelFactory }
    val weatherViewModel: WeatherViewModel by viewModels { weatherViewModelFactory }
    val authViewModel: AuthViewModel by viewModels { authViewModelFactory }

    setContent {
        PlantManagerTheme {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background
            ) {
                val startRoute = if (kotlin.run {
                        val s = kotlinx.coroutines.runBlocking { sessionDao.getSessionNow() }
                        s?.currentUserId != null
                    }) "plant_list" else "sign_in"
                AppNavigation(
                    plantViewModel = plantViewModel,
                    weatherViewModel = weatherViewModel,
                    authViewModel = authViewModel,
                    startDestination = startRoute
                )
            }
        }
    }
}
}
