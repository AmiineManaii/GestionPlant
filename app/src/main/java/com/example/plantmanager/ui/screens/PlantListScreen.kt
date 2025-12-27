package com.example.plantmanager.ui.screens

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.plantmanager.ui.components.WeatherCard
import com.example.plantmanager.ui.components.PlantCard
import com.example.plantmanager.ui.components.LocationPermissionDialog
import com.example.plantmanager.viewmodels.PlantViewModel
import com.example.plantmanager.viewmodels.WeatherViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlantListScreen(
    plantViewModel: PlantViewModel = viewModel(),
    weatherViewModel: WeatherViewModel = viewModel(),
    onPlantClick: (Int) -> Unit,
    onAddPlantClick: () -> Unit,
    onWeatherClick: () -> Unit
) {
    val plants by plantViewModel.allPlants.collectAsState(initial = emptyList())
    val weatherState by weatherViewModel.weatherState.collectAsState()
    val location by weatherViewModel.currentLocation.collectAsState()

    var filter by remember { mutableStateOf(FilterType.ALL) }
    val context = LocalContext.current


    var showPermissionDialog by remember { mutableStateOf(false) }
    var hasLocationPermission by remember { mutableStateOf(false) }


    LaunchedEffect(Unit) {
        val fineLocationPermission = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == android.content.pm.PackageManager.PERMISSION_GRANTED

        val coarseLocationPermission = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == android.content.pm.PackageManager.PERMISSION_GRANTED

        hasLocationPermission = fineLocationPermission || coarseLocationPermission

        if (hasLocationPermission) {
            weatherViewModel.fetchWeather()
        } else {
            showPermissionDialog = true
        }
    }


    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val fineLocationGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false
        val coarseLocationGranted = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false

        hasLocationPermission = fineLocationGranted || coarseLocationGranted

        if (hasLocationPermission) {
            weatherViewModel.fetchWeather()
        } else {
            showPermissionDialog = true
        }
    }


    fun requestLocationPermission() {
        locationPermissionLauncher.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
    }


    LaunchedEffect(showPermissionDialog) {
        if (showPermissionDialog && !hasLocationPermission) {
            requestLocationPermission()
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "🌿 PlantManager",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddPlantClick,
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(imageVector = androidx.compose.material.icons.Icons.Default.Add, contentDescription = "Ajouter une plante")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            WeatherCard(
                weatherState = weatherState,
                location = location,
                hasLocationPermission = hasLocationPermission,
                onRefresh = { weatherViewModel.fetchWeather() },
                onRequestPermission = { requestLocationPermission() },
                onClick = onWeatherClick
            )


            if (showPermissionDialog && !hasLocationPermission) {
                LocationPermissionDialog(
                    onDismiss = { showPermissionDialog = false },
                    onAllow = {
                        showPermissionDialog = false
                        requestLocationPermission()
                    },
                    onUseDefault = {
                        showPermissionDialog = false
                        weatherViewModel.fetchWeather()
                    }
                )
            }


            FilterRow(
                currentFilter = filter,
                onFilterChange = { filter = it },
                plantCount = plants.size
            )


            if (plants.isEmpty()) {
                EmptyState(onAddClick = onAddPlantClick)
            } else {
                val filteredPlants = when (filter) {
                    FilterType.ALL -> plants
                    FilterType.NEEDS_WATER -> {

                        plants.filter { plant ->
                            val nextWatering = plant.lastWateringDate +
                                    (plant.wateringFrequency * 24 * 60 * 60 * 1000)
                            System.currentTimeMillis() > nextWatering
                        }
                    }
                }

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(filteredPlants) { plant ->
                        PlantCard(
                            plant = plant,
                            onClick = { onPlantClick(plant.id) },
                            onWaterClick = { plantViewModel.waterPlant(plant.id) }
                        )
                    }
                }
            }
        }
    }

    
}



@Composable
fun FilterRow(
    currentFilter: FilterType,
    onFilterChange: (FilterType) -> Unit,
    plantCount: Int
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Filtres
        FilterChip(
            selected = currentFilter == FilterType.ALL,
            onClick = { onFilterChange(FilterType.ALL) },
            label = { Text("Toutes ($plantCount)") }
        )

        FilterChip(
            selected = currentFilter == FilterType.NEEDS_WATER,
            onClick = { onFilterChange(FilterType.NEEDS_WATER) },
            label = { Text("À arroser") }
        )
    }
}

@Composable
fun EmptyState(onAddClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "🌵",
            fontSize = 64.sp
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Aucune plante",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = "Commencez par ajouter votre première plante",
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = onAddClick) {
            Text("Ajouter une plante")
        }
    }
}

enum class FilterType {
    ALL, NEEDS_WATER
}


