package com.example.plantmanager.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import com.example.plantmanager.viewmodels.PlantViewModel
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.WaterDrop
import coil.compose.AsyncImage
import androidx.compose.ui.layout.ContentScale
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material.icons.filled.Warning
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlantDetailScreen(
    plantId: Int,
    plantViewModel: PlantViewModel,
    onBack: () -> Unit,
    onEdit: (Int) -> Unit,
    weatherViewModel: com.example.plantmanager.viewmodels.WeatherViewModel
) {
    val plantState by plantViewModel.getPlantFlowById(plantId).collectAsState(initial = null)
    var showDeleteConfirm by remember { mutableStateOf(false) }

    val weatherState by weatherViewModel.weatherState.collectAsState()
    val currentTemp = when (weatherState) {
        is com.example.plantmanager.viewmodels.WeatherViewModel.WeatherState.Success ->
            (weatherState as com.example.plantmanager.viewmodels.WeatherViewModel.WeatherState.Success)
                .data.current_weather.temperature
        else -> null
    }

    val isTempSafe = remember(plantState, currentTemp) {
        val minOk = plantState?.minTemperature?.let { t -> currentTemp?.let { it >= t } ?: true } ?: true
        val maxOk = plantState?.maxTemperature?.let { t -> currentTemp?.let { it <= t } ?: true } ?: true
        minOk && maxOk
    }

    // Couleurs dynamiques selon la sécurité température
    val accentColor = if (isTempSafe) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
    val overlayGradientColors = if (isTempSafe)
        listOf(Color.Transparent, Color(0xFF2E7D32))
    else
        listOf(Color.Transparent, Color(0xFFB71C1C)) // Overlay rouge sombre quand danger

    val placeholderGradient = if (isTempSafe)
        listOf(Color(0xFF2E7D32), Color(0xFF1B5E20))
    else
        listOf(Color(0xFFB71C1C), Color(0xFF8F0000))

    if (plantState == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Plante introuvable", style = MaterialTheme.typography.headlineMedium)
        }
        return
    }

    val plant = plantState!!

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            CenterAlignedTopAppBar(
                title = { },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Retour",
                            tint = Color.White
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { showDeleteConfirm = true }) {
                        Icon(Icons.Default.Delete, contentDescription = "Supprimer", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.Transparent)
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { onEdit(plantId) },
                containerColor = accentColor,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(Icons.Default.Edit, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Modifier la plante")
            }
        }
    ) { paddingValues ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            verticalArrangement = Arrangement.spacedBy(20.dp),
            contentPadding = PaddingValues(bottom = 100.dp, start = 16.dp, end = 16.dp, top = 0.dp)
        ) {
            // Hero Image Section
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(360.dp)
                        .clip(RoundedCornerShape(24.dp))
                ) {
                    if (!plant.imageUrl.isNullOrEmpty()) {
                        AsyncImage(
                            model = plant.imageUrl,
                            contentDescription = plant.name,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    brush = Brush.verticalGradient(placeholderGradient),
                                    shape = RoundedCornerShape(24.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("🌿", style = MaterialTheme.typography.displayLarge, color = Color.White)
                        }
                    }

                    // Overlay gradient dynamique
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Brush.verticalGradient(overlayGradientColors))
                    )

                    // Infos plante
                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(24.dp)
                    ) {
                        Text(
                            text = plant.name,
                            style = MaterialTheme.typography.headlineLarge,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                        if (plant.species.isNotEmpty()) {
                            Text(
                                text = plant.species,
                                style = MaterialTheme.typography.titleMedium,
                                color = Color.White.copy(alpha = 0.9f)
                            )
                        }
                        Spacer(Modifier.height(12.dp))
                        currentTemp?.let { temp ->
                            val safetyText = if (isTempSafe) "Température idéale" else "Danger température !"
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = if (isTempSafe) Icons.Default.CheckCircle else Icons.Default.Warning,
                                    contentDescription = null,
                                    tint = if (isTempSafe) Color(0xFF4CAF50) else Color(0xFFFF5722),
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(Modifier.width(10.dp))
                                Text(
                                    text = "$safetyText • $temp°C",
                                    color = Color.White,
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }
            }

            // Carte arrosage avec progression + dates exactes
            item {
                val daysSinceLast = ((System.currentTimeMillis() - plant.lastWateringDate) / (24 * 60 * 60 * 1000)).toLong().coerceAtLeast(0).toInt()
                val progress = (daysSinceLast.toFloat() / plant.wateringFrequency).coerceIn(0f, 1f)
                val needsWaterNow = progress >= 1f
                val remainingDays = (plant.wateringFrequency - daysSinceLast).coerceAtLeast(0)
                val canWaterNow = remember(plant) {
                    val elapsed = System.currentTimeMillis() - plant.lastWateringDate
                    val halfPeriodMillis = (plant.wateringFrequency * 24L * 60L * 60L * 1000L) / 2L
                    elapsed >= halfPeriodMillis
                }

                // Calcul du prochain arrosage
                val nextWateringMillis = plant.lastWateringDate + (plant.wateringFrequency.toLong() * 24 * 60 * 60 * 1000)

                // Formatage des dates
                val dateFormatter = remember { SimpleDateFormat("dd MMM yyyy", Locale.getDefault()) }
                val timeFormatter = remember { SimpleDateFormat("HH:mm", Locale.getDefault()) }

                val lastWateringDate = dateFormatter.format(Date(plant.lastWateringDate))
                val lastWateringTime = timeFormatter.format(Date(plant.lastWateringDate))
                val nextWateringDate = dateFormatter.format(Date(nextWateringMillis))
                val nextWateringTime = timeFormatter.format(Date(nextWateringMillis))
                val timeUntilAllowedMillis = remember(plant) {
                    val allowedAt = plant.lastWateringDate + (plant.wateringFrequency * 24L * 60L * 60L * 1000L) / 2L
                    (allowedAt - System.currentTimeMillis()).coerceAtLeast(0L)
                }
                val timeUntilAllowedText = remember(timeUntilAllowedMillis) {
                    val hours = (timeUntilAllowedMillis / (60L * 60L * 1000L)).toInt()
                    val days = hours / 24
                    when {
                        timeUntilAllowedMillis <= 0L -> "Arrosage autorisé"
                        days >= 1 -> "Arrosage autorisé dans $days jour(s)"
                        else -> "Arrosage autorisé dans $hours h"
                    }
                }

                ElevatedCard(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow)
                ) {
                    Column(
                        modifier = Modifier
                            .padding(24.dp)
                            .fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Text(
                            text = "Prochain arrosage",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.SemiBold,
                            textAlign = TextAlign.Center
                        )

                        Spacer(Modifier.height(20.dp))

                        Box(
                            modifier = Modifier.size(180.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(
                                progress = { progress },
                                modifier = Modifier.fillMaxSize(),
                                color = accentColor,
                                strokeWidth = 12.dp,
                                strokeCap = StrokeCap.Round
                            )
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    Icons.Default.WaterDrop,
                                    contentDescription = null,
                                    tint = accentColor,
                                    modifier = Modifier.size(56.dp)
                                )
                                Spacer(Modifier.height(8.dp))
                                Text(
                                    text = if (needsWaterNow) "À arroser maintenant !" else "$remainingDays jours",
                                    style = MaterialTheme.typography.headlineMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = accentColor,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }

                        Spacer(Modifier.height(24.dp))

                        // Dates exactes
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "Dernier arrosage",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "$lastWateringDate à $lastWateringTime",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Medium
                            )

                            Text(
                                text = "Prochain arrosage prévu",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "$nextWateringDate à $nextWateringTime",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Medium,
                                color = if (needsWaterNow) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                            )
                        }

                        Spacer(Modifier.height(28.dp))

                        Button(
                            onClick = { plantViewModel.waterPlant(plantId) },
                            modifier = Modifier
                                .fillMaxWidth(0.75f)
                                .height(52.dp),
                            enabled = canWaterNow,
                            colors = ButtonDefaults.buttonColors(containerColor = accentColor),
                            shape = RoundedCornerShape(26.dp)
                        ) {
                            Icon(Icons.Default.WaterDrop, contentDescription = null, tint = Color.White)
                            Spacer(Modifier.width(12.dp))
                            Text("Arroser maintenant", fontWeight = FontWeight.Medium)
                        }
                        if (!canWaterNow) {
                            Spacer(Modifier.height(12.dp))
                            Text(
                                text = timeUntilAllowedText,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }

            // Cartes Emplacement & Température
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    ElevatedCard(modifier = Modifier.weight(1f)) {
                        Column(
                            modifier = Modifier.padding(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                Icons.Default.Home,
                                contentDescription = null,
                                tint = accentColor,
                                modifier = Modifier.size(32.dp)
                            )
                            Spacer(Modifier.height(12.dp))
                            Text("Emplacement", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Spacer(Modifier.height(4.dp))
                            Text(
                                plant.locationType,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold,
                                textAlign = TextAlign.Center
                            )
                        }
                    }

                    ElevatedCard(modifier = Modifier.weight(1f)) {
                        Column(
                            modifier = Modifier.padding(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                Icons.Default.Thermostat,
                                contentDescription = null,
                                tint = accentColor,
                                modifier = Modifier.size(32.dp)
                            )
                            Spacer(Modifier.height(12.dp))
                            Text("Température idéale", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Spacer(Modifier.height(4.dp))
                            Text(
                                "${plant.minTemperature ?: "-"}° – ${plant.maxTemperature ?: "-"}°C",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }

            // Notes
            if (!plant.notes.isNullOrEmpty()) {
                item {
                    ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(24.dp)) {
                            Text(
                                "Notes personnelles",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                            Spacer(Modifier.height(12.dp))
                            Text(plant.notes, style = MaterialTheme.typography.bodyLarge)
                        }
                    }
                }
            }
        }
    }

    // Dialog suppression
    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text("Supprimer la plante ?") },
            text = { Text("Cette action est irréversible.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        plantViewModel.deletePlant(plant)
                        showDeleteConfirm = false
                        onBack()
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) { Text("Supprimer") }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = false }) { Text("Annuler") }
            }
        )
    }
}
