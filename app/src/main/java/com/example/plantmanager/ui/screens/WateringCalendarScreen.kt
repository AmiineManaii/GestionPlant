package com.example.plantmanager.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.ui.graphics.Color
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.plantmanager.data.local.Plant
import com.example.plantmanager.viewmodels.PlantViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WateringCalendarScreen(
    plantViewModel: PlantViewModel,
    onBack: () -> Unit
) {
    val plants by plantViewModel.allPlants.collectAsState(initial = emptyList())
    var currentYear by remember { mutableStateOf(Calendar.getInstance().get(Calendar.YEAR)) }
    var currentMonth by remember { mutableStateOf(Calendar.getInstance().get(Calendar.MONTH)) }
    var selectedDay by remember { mutableStateOf<Int?>(null) }

    val keyFormatter = remember { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()) }
    val monthTitleFormatter = remember { SimpleDateFormat("MMMM yyyy", Locale.getDefault()) }

    val monthStart = remember(currentYear, currentMonth) {
        Calendar.getInstance().apply {
            set(Calendar.YEAR, currentYear)
            set(Calendar.MONTH, currentMonth)
            set(Calendar.DAY_OF_MONTH, 1)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
    }
    val monthEnd = remember(currentYear, currentMonth) {
        Calendar.getInstance().apply {
            set(Calendar.YEAR, currentYear)
            set(Calendar.MONTH, currentMonth)
            set(Calendar.DAY_OF_MONTH, getActualMaximum(Calendar.DAY_OF_MONTH))
            set(Calendar.HOUR_OF_DAY, 23)
            set(Calendar.MINUTE, 59)
            set(Calendar.SECOND, 59)
            set(Calendar.MILLISECOND, 999)
        }
    }
    val monthEvents by plantViewModel.getEventsInRange(monthStart.timeInMillis, monthEnd.timeInMillis).collectAsState(initial = emptyList())
    val actualEventsByDay = remember(monthEvents) {
        val map = mutableMapOf<String, MutableList<Int>>()
        monthEvents.filter { it.source == "manual" }.forEach { ev ->
            val key = keyFormatter.format(Date(ev.date))
            val list = map.getOrPut(key) { mutableListOf() }
            list.add(ev.plantId)
        }
        map
    }
    val eventsByDay = remember(plants, currentYear, currentMonth) {
        val map = mutableMapOf<String, MutableList<Plant>>()
        val startMillis = monthStart.timeInMillis
        val endMillis = monthEnd.timeInMillis
        plants.forEach { plant ->
            val freqMs = plant.wateringFrequency * 24L * 60L * 60L * 1000L
            var next = plant.lastWateringDate
            while (next < startMillis) next += freqMs
            while (next <= endMillis) {
                val key = keyFormatter.format(Date(next))
                val list = map.getOrPut(key) { mutableListOf() }
                list.add(plant)
                next += freqMs
            }
        }
        map
    }
    val monthTitle = remember(currentYear, currentMonth) {
        monthTitleFormatter.format(monthStart.time).replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
    }
    val days = remember(currentYear, currentMonth) {
        val c = Calendar.getInstance().apply {
            set(Calendar.YEAR, currentYear)
            set(Calendar.MONTH, currentMonth)
            set(Calendar.DAY_OF_MONTH, 1)
        }
        val maxDay = c.getActualMaximum(Calendar.DAY_OF_MONTH)
        val startDow = c.get(Calendar.DAY_OF_WEEK)
        val startIndex = (startDow + 5) % 7
        val result = mutableListOf<Int?>()
        repeat(startIndex) { result.add(null) }
        for (d in 1..maxDay) result.add(d)
        while (result.size % 7 != 0) result.add(null)
        result
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Calendrier d’arrosage", fontWeight = FontWeight.Bold) },
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
            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                IconButton(onClick = {
                    if (currentMonth == 0) {
                        currentMonth = 11
                        currentYear -= 1
                    } else {
                        currentMonth -= 1
                    }
                    selectedDay = null
                }) { Icon(Icons.Default.ArrowBack, contentDescription = "Mois précédent") }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.CalendarToday, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Spacer(Modifier.width(8.dp))
                    Text(monthTitle, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)
                }
                IconButton(onClick = {
                    if (currentMonth == 11) {
                        currentMonth = 0
                        currentYear += 1
                    } else {
                        currentMonth += 1
                    }
                    selectedDay = null
                }) { Icon(Icons.Default.ArrowForward, contentDescription = "Mois suivant") }
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                listOf("Lun","Mar","Mer","Jeu","Ven","Sam","Dim").forEach {
                    Text(it, modifier = Modifier.weight(1f), textAlign = TextAlign.Center, style = MaterialTheme.typography.labelLarge)
                }
            }
            for (row in days.chunked(7)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    row.forEach { d ->
                        if (d == null) {
                            Box(modifier = Modifier.weight(1f).height(56.dp))
                        } else {
                            val c = Calendar.getInstance().apply {
                                set(Calendar.YEAR, currentYear)
                                set(Calendar.MONTH, currentMonth)
                                set(Calendar.DAY_OF_MONTH, d)
                                set(Calendar.HOUR_OF_DAY, 0)
                                set(Calendar.MINUTE, 0)
                                set(Calendar.SECOND, 0)
                                set(Calendar.MILLISECOND, 0)
                            }
                            val key = keyFormatter.format(c.time)
                            val upcoming = eventsByDay[key].orEmpty()
                            val actualIds = actualEventsByDay[key].orEmpty()
                            ElevatedCard(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(56.dp),
                                colors = CardDefaults.elevatedCardColors(
                                    containerColor = when {
                                        upcoming.isNotEmpty() && actualIds.isNotEmpty() -> MaterialTheme.colorScheme.secondaryContainer
                                        upcoming.isNotEmpty() -> MaterialTheme.colorScheme.primaryContainer
                                        actualIds.isNotEmpty() -> MaterialTheme.colorScheme.tertiaryContainer
                                        else -> MaterialTheme.colorScheme.surface
                                    }
                                ),
                                onClick = { selectedDay = d }
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(6.dp)
                                ) {
                                    Text(
                                        "$d",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.SemiBold,
                                        modifier = Modifier.align(Alignment.BottomStart)
                                    )
                                    val upCount = upcoming.size
                                    val doneCount = actualIds.toSet().size
                                    Row(
                                        modifier = Modifier.align(Alignment.TopEnd),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        if (upCount > 0) {
                                            Badge(containerColor = MaterialTheme.colorScheme.primary) {
                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                    Icon(
                                                        Icons.Default.WaterDrop,
                                                        contentDescription = null,
                                                        tint = Color.White,
                                                        modifier = Modifier.size(14.dp)
                                                    )
                                                    if (upCount > 1) {
                                                        Spacer(Modifier.width(4.dp))
                                                        Text("$upCount", style = MaterialTheme.typography.labelSmall, color = Color.White)
                                                    }
                                                }
                                            }
                                        }
                                        if (doneCount > 0) {
                                            Spacer(Modifier.width(6.dp))
                                            Badge(containerColor = MaterialTheme.colorScheme.tertiary) {
                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                    Icon(
                                                        Icons.Default.CheckCircle,
                                                        contentDescription = null,
                                                        tint = Color.White,
                                                        modifier = Modifier.size(14.dp)
                                                    )
                                                    if (doneCount > 1) {
                                                        Spacer(Modifier.width(4.dp))
                                                        Text("$doneCount", style = MaterialTheme.typography.labelSmall, color = Color.White)
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            selectedDay?.let { d ->
                val c = Calendar.getInstance().apply {
                    set(Calendar.YEAR, currentYear)
                    set(Calendar.MONTH, currentMonth)
                    set(Calendar.DAY_OF_MONTH, d)
                }
                val key = keyFormatter.format(c.time)
                val upcoming = eventsByDay[key].orEmpty()
                val actualIds = actualEventsByDay[key].orEmpty().toSet()
                val alreadyWatered = upcoming.filter { actualIds.contains(it.id) }
                val toWater = upcoming.filter { !actualIds.contains(it.id) }
                if (upcoming.isEmpty() && actualIds.isEmpty()) {
                    Text("Aucun arrosage ce jour", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                } else {
                    if (toWater.isNotEmpty()) {
                        Text("À arroser le $d", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                        toWater.forEach { p ->
                            ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                                Row(modifier = Modifier.padding(12.dp)) {
                                    Text(p.name, style = MaterialTheme.typography.titleSmall)
                                }
                            }
                        }
                    }
                    if (alreadyWatered.isNotEmpty() || actualIds.isNotEmpty()) {
                        Text("Déjà arrosées le $d", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                        val mapById = plants.associateBy { it.id }
                        val wateredPlants = if (alreadyWatered.isNotEmpty()) alreadyWatered else actualIds.mapNotNull { mapById[it] }
                        wateredPlants.forEach { p ->
                            ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                                Row(modifier = Modifier.padding(12.dp)) {
                                    Text(p.name, style = MaterialTheme.typography.titleSmall)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
