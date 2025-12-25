package com.example.plantmanager.ui.screens

import android.Manifest
import android.content.Context
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.plantmanager.data.remote.WeatherCode
import com.example.plantmanager.viewmodels.PlantViewModel
import com.example.plantmanager.viewmodels.WeatherViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlantListScreen(
    plantViewModel: PlantViewModel = viewModel(),
    weatherViewModel: WeatherViewModel = viewModel(),
    onPlantClick: (Int) -> Unit,
    onAddPlantClick: () -> Unit
) {
    // États
    val plants by plantViewModel.allPlants.collectAsState(initial = emptyList())
    val weatherState by weatherViewModel.weatherState.collectAsState()
    val location by weatherViewModel.currentLocation.collectAsState()

    var filter by remember { mutableStateOf(FilterType.ALL) }
    val context = LocalContext.current

    // État pour gérer la permission de localisation
    var showPermissionDialog by remember { mutableStateOf(false) }
    var hasLocationPermission by remember { mutableStateOf(false) }

    // Vérifier la permission au démarrage
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

    // Lanceur pour demander la permission
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

    // Fonction pour demander la permission
    fun requestLocationPermission() {
        locationPermissionLauncher.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
    }

    // Au premier démarrage, demander la permission
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
                Icon(imageVector = Icons.Default.Add, contentDescription = "Ajouter une plante")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Carte météo avec gestion des permissions
            WeatherCard(
                weatherState = weatherState,
                location = location,
                hasLocationPermission = hasLocationPermission,
                onRefresh = { weatherViewModel.fetchWeather() },
                onRequestPermission = { requestLocationPermission() }
            )

            // Dialog d'information sur la permission
            if (showPermissionDialog && !hasLocationPermission) {
                AlertDialog(
                    onDismissRequest = { showPermissionDialog = false },
                    title = { Text("Permission de localisation") },
                    text = {
                        Text("PlantManager a besoin de votre localisation pour afficher " +
                                "la météo précise de votre position. Si vous refusez, " +
                                "l'application utilisera Tunis comme position par défaut.")
                    },
                    confirmButton = {
                        Button(
                            onClick = {
                                showPermissionDialog = false
                                requestLocationPermission()
                            }
                        ) {
                            Text("Autoriser")
                        }
                    },
                    dismissButton = {
                        Button(
                            onClick = {
                                showPermissionDialog = false
                                // Utiliser la localisation par défaut (Tunis)
                                weatherViewModel.fetchWeather()
                            }
                        ) {
                            Text("Utiliser Tunis")
                        }
                    }
                )
            }

            // Filtres
            FilterRow(
                currentFilter = filter,
                onFilterChange = { filter = it },
                plantCount = plants.size
            )

            // Liste des plantes
            if (plants.isEmpty()) {
                EmptyState(onAddClick = onAddPlantClick)
            } else {
                val filteredPlants = when (filter) {
                    FilterType.ALL -> plants
                    FilterType.NEEDS_WATER -> {
                        // Filtrer les plantes qui ont besoin d'eau
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
fun WeatherCard(
    weatherState: WeatherViewModel.WeatherState,
    location: Pair<Double, Double>?,
    hasLocationPermission: Boolean,
    onRefresh: () -> Unit,
    onRequestPermission: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Afficher un avertissement si pas de permission
            if (!hasLocationPermission) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Warning,
                        contentDescription = "Avertissement",
                        tint = Color.hsl(0.09f, 0.5f, 0.5f),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Localisation par défaut (Tunis)",
                        fontSize = 12.sp,
                        color = Color.hsl(0.09f, 0.5f, 0.5f)
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    IconButton(
                        onClick = onRequestPermission,
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            Icons.Default.LocationOn,
                            contentDescription = "Activer la localisation",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                when (weatherState) {
                    is WeatherViewModel.WeatherState.Loading -> {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            CircularProgressIndicator(modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Chargement météo...")
                        }
                    }

                    is WeatherViewModel.WeatherState.Success -> {
                        Column {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "${weatherState.data.current_weather.temperature}°C",
                                    fontSize = 28.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                // Utiliser WeatherCode.getIcon directement
                                Text(
                                    text = WeatherCode.getIcon(weatherState.data.current_weather.weathercode),
                                    fontSize = 24.sp
                                )
                            }
                            // Utiliser WeatherCode.getDescription directement
                            Text(
                                text = WeatherCode.getDescription(weatherState.data.current_weather.weathercode),
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    is WeatherViewModel.WeatherState.Error -> {
                        Column {
                            Text("❌ Erreur météo", color = MaterialTheme.colorScheme.error)
                            TextButton(
                                onClick = onRefresh,
                                modifier = Modifier.padding(0.dp)
                            ) {
                                Text("Réessayer")
                            }
                        }
                    }
                }

                IconButton(onClick = onRefresh) {
                    Icon(Icons.Default.Refresh, "Actualiser")
                }
            }

            // Localisation
            location?.let { (lat, lon) ->
                Text(
                    text = "📍 ${"%.4f".format(lat)}, ${"%.4f".format(lon)}",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.outline,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }
}

@Composable
fun PlantCard(
    plant: com.example.plantmanager.data.local.Plant,
    onClick: () -> Unit,
    onWaterClick: () -> Unit
) {
    // Calculer si la plante a besoin d'eau
    val needsWatering = remember(plant) {
        val nextWatering = plant.lastWateringDate +
                (plant.wateringFrequency * 24 * 60 * 60 * 1000)
        System.currentTimeMillis() > nextWatering
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = if (needsWatering) {
                MaterialTheme.colorScheme.errorContainer
            } else {
                MaterialTheme.colorScheme.surface
            }
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Image (placeholder pour l'instant)
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .background(
                        color = MaterialTheme.colorScheme.primaryContainer,
                        shape = MaterialTheme.shapes.medium
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text("🌱", fontSize = 24.sp)
            }

            // Infos
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 16.dp)
            ) {
                Text(
                    text = plant.name,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium
                )

                if (plant.species.isNotEmpty()) {
                    Text(
                        text = plant.species,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Prochain arrosage
                val daysUntilWatering = remember(plant) {
                    val remaining = plant.lastWateringDate +
                            (plant.wateringFrequency * 24 * 60 * 60 * 1000) -
                            System.currentTimeMillis()
                    val days = (remaining / (24 * 60 * 60 * 1000)).toInt()
                    days
                }

                Text(
                    text = if (needsWatering) {
                        "⚠️ Besoin d'eau maintenant!"
                    } else if (daysUntilWatering <= 1) {
                        "💧 À arroser demain"
                    } else {
                        "💧 Prochain arrosage dans $daysUntilWatering jours"
                    },
                    fontSize = 14.sp,
                    color = if (needsWatering) {
                        MaterialTheme.colorScheme.error
                    } else {
                        MaterialTheme.colorScheme.primary
                    }
                )
            }

            // Bouton arrosage rapide
            IconButton(
                onClick = onWaterClick,
                modifier = Modifier.size(48.dp)
            ) {
                Icon(
                    Icons.Default.WaterDrop,
                    contentDescription = "Arroser",
                    tint = if (needsWatering) MaterialTheme.colorScheme.primary else Color.Gray
                )
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