package com.example.plantmanager.ui.screens

import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Equalizer
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.plantmanager.viewmodels.PlantViewModel
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatisticsScreen(
    plantViewModel: PlantViewModel,
    onBack: () -> Unit
) {
    val plants by plantViewModel.allPlants.collectAsState(initial = emptyList())
    val needsWater by plantViewModel.plantsNeedingWater.collectAsState(initial = emptyList())

    val now = System.currentTimeMillis()
    val cal = remember { Calendar.getInstance() }
    val start30 = remember(now) {
        Calendar.getInstance().apply { timeInMillis = now; add(Calendar.DAY_OF_YEAR, -30) }.timeInMillis
    }
    val end30 = now
    val events30 by plantViewModel.getEventsInRange(start30, end30).collectAsState(initial = emptyList())
    val manualEvents30 = events30.filter { it.source == "manual" }

    val next7End = remember(now) {
        Calendar.getInstance().apply { timeInMillis = now; add(Calendar.DAY_OF_YEAR, 7) }.timeInMillis
    }
    val upcomingCount = plants.count { plant ->
        val freqMs = plant.wateringFrequency * 24L * 60L * 60L * 1000L
        var next = plant.lastWateringDate + freqMs
        next in now..next7End
    }
    val overdueCount = plants.count { plant ->
        val freqMs = plant.wateringFrequency * 24L * 60L * 60L * 1000L
        val next = plant.lastWateringDate + freqMs
        next < now
    }

    val avgFrequency = if (plants.isNotEmpty()) plants.map { it.wateringFrequency }.average() else 0.0
    val indoorCount = plants.count { it.locationType.equals("Indoor", ignoreCase = true) }
    val outdoorCount = plants.count { it.locationType.equals("Outdoor", ignoreCase = true) }
    val balconyCount = plants.count { it.locationType.equals("Balcony", ignoreCase = true) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Statistiques", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Retour")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            ElevatedCard {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Equalizer, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Spacer(Modifier.width(8.dp))
                        Text("Vue d’ensemble", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                    }
                    Text("Plantes: ${plants.size}")
                    Text("À arroser: ${needsWater.size}")
                    Text("Arrosages manuels (30 jours): ${manualEvents30.size}")
                }
            }

            ElevatedCard {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.WaterDrop, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Spacer(Modifier.width(8.dp))
                        Text("Planning d’arrosage", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                    }
                    Text("À venir (7 jours): $upcomingCount")
                    Text("En retard: $overdueCount")
                    Text("Fréquence moyenne: ${String.format("%.1f", avgFrequency)} jours")
                }
            }

            ElevatedCard {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Equalizer, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Spacer(Modifier.width(8.dp))
                        Text("Répartition par lieu", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                    }
                    Text("Intérieur: $indoorCount")
                    Text("Extérieur: $outdoorCount")
                    Text("Balcon: $balconyCount")
                }
            }

            ElevatedCard {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Warning, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Spacer(Modifier.width(8.dp))
                        Text("Suggestions rapides", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                    }
                    val tip = when {
                        overdueCount > 0 -> "Plusieurs arrosages en retard, prioriser aujourd’hui."
                        upcomingCount > 0 -> "Préparer les arrosages pour la semaine."
                        else -> "Tout est à jour."
                    }
                    Text(tip)
                }
            }
        }
    }
}
