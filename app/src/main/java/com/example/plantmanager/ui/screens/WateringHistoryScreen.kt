package com.example.plantmanager.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.plantmanager.viewmodels.PlantViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WateringHistoryScreen(
    plantId: Int,
    plantViewModel: PlantViewModel,
    onBack: () -> Unit
) {
    val historyFlow = plantViewModel.getAllWateringEvents(plantId)
    val history by historyFlow.collectAsState(initial = emptyList())
    val dateFormatter = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
    val timeFormatter = SimpleDateFormat("HH:mm", Locale.getDefault())
    val keyFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val eventKeys = remember(history) { history.map { keyFormatter.format(Date(it.date)) }.toSet() }
    var selectedTab by remember { mutableStateOf(0) }
    var currentYear by remember { mutableStateOf(Calendar.getInstance().get(Calendar.YEAR)) }
    var currentMonth by remember { mutableStateOf(Calendar.getInstance().get(Calendar.MONTH)) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Historique des arrosages", fontWeight = FontWeight.SemiBold) },
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
        ) {
            TabRow(selectedTabIndex = selectedTab) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("Liste") },
                    icon = { Icon(Icons.Default.WaterDrop, contentDescription = null) }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("Calendrier") },
                    icon = { Icon(Icons.Default.CalendarToday, contentDescription = null) }
                )
            }
            when (selectedTab) {
                0 -> {
                    if (history.isEmpty()) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text("Aucun arrosage enregistré")
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(history) { ev ->
                                ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                                    Row(
                                        modifier = Modifier.padding(16.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        val d = dateFormatter.format(Date(ev.date))
                                        val t = timeFormatter.format(Date(ev.date))
                                        Text("$d • $t", style = MaterialTheme.typography.titleMedium)
                                    }
                                }
                            }
                        }
                    }
                }
                1 -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            IconButton(onClick = {
                                if (currentMonth == 0) {
                                    currentMonth = 11
                                    currentYear -= 1
                                } else {
                                    currentMonth -= 1
                                }
                            }) { Icon(Icons.Default.ArrowBack, contentDescription = "Mois précédent") }
                            val cal = Calendar.getInstance().apply {
                                set(Calendar.YEAR, currentYear)
                                set(Calendar.MONTH, currentMonth)
                                set(Calendar.DAY_OF_MONTH, 1)
                            }
                            val monthTitle = SimpleDateFormat("MMMM yyyy", Locale.getDefault()).format(cal.time)
                            Text(monthTitle.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)
                            IconButton(onClick = {
                                if (currentMonth == 11) {
                                    currentMonth = 0
                                    currentYear += 1
                                } else {
                                    currentMonth += 1
                                }
                            }) { Icon(Icons.Default.ArrowForward, contentDescription = "Mois suivant") }
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            listOf("Lun","Mar","Mer","Jeu","Ven","Sam","Dim").forEach {
                                Text(it, modifier = Modifier.weight(1f), textAlign = androidx.compose.ui.text.style.TextAlign.Center, style = MaterialTheme.typography.labelLarge)
                            }
                        }
                        val days = run {
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
                                        val hasEvent = eventKeys.contains(key)
                                        ElevatedCard(
                                            modifier = Modifier
                                                .weight(1f)
                                                .height(56.dp),
                                            colors = CardDefaults.elevatedCardColors(
                                                containerColor = if (hasEvent) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
                                            )
                                        ) {
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxSize()
                                                    .padding(horizontal = 8.dp),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                                            ) {
                                                Text("$d", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                                                if (hasEvent) {
                                                    Icon(Icons.Default.WaterDrop, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
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
    }
}
