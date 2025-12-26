package com.example.plantmanager.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.plantmanager.viewmodels.PlantViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditPlantScreen(
    plantId: Int,
    plantViewModel: PlantViewModel,
    onBack: () -> Unit
) {
    val plantState by plantViewModel.getPlantFlowById(plantId).collectAsState(initial = null)

    var species by remember(plantState) { mutableStateOf(plantState?.species.orEmpty()) }
    var imageUrl by remember(plantState) { mutableStateOf(plantState?.imageUrl.orEmpty()) }
    var notes by remember(plantState) { mutableStateOf(plantState?.notes.orEmpty()) }
    var minTemperatureText by remember(plantState) { mutableStateOf(plantState?.minTemperature?.toString() ?: "") }
    var maxTemperatureText by remember(plantState) { mutableStateOf(plantState?.maxTemperature?.toString() ?: "") }
    var locationType by remember(plantState) { mutableStateOf(plantState?.locationType ?: "Indoor") }

    val isValid = true
    var dropdownExpanded by remember { mutableStateOf(false) }
    val locationOptions = listOf("Indoor", "Outdoor", "Balcony")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Modifier la plante") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Retour")
                    }
                },
                actions = {
                    Button(
                        onClick = {
                            plantState?.let { current ->
                                val updated = current.copy(
                                    species = species.trim(),
                                    imageUrl = imageUrl.trim().ifEmpty { null },
                                    notes = notes.trim(),
                                    minTemperature = minTemperatureText.toIntOrNull(),
                                    maxTemperature = maxTemperatureText.toIntOrNull(),
                                    locationType = locationType
                                )
                                plantViewModel.updatePlant(updated)
                                onBack()
                            }
                        },
                        enabled = isValid && plantState != null
                    ) { Text("Enregistrer") }
                }
            )
        }
    ) { padding ->
        if (plantState == null) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = androidx.compose.ui.Alignment.Center) {
                Text("Plante introuvable")
            }
            return@Scaffold
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = plantState!!.name,
                onValueChange = {},
                label = { Text("Nom") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                enabled = false
            )
            OutlinedTextField(
                value = species,
                onValueChange = { species = it },
                label = { Text("Espèce") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = plantState!!.wateringFrequency.toString(),
                onValueChange = {},
                label = { Text("Fréquence d'arrosage (jours)") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                enabled = false
            )
            ExposedDropdownMenuBox(
                expanded = dropdownExpanded,
                onExpandedChange = { dropdownExpanded = !dropdownExpanded }
            ) {
                OutlinedTextField(
                    value = locationType,
                    onValueChange = {},
                    label = { Text("Emplacement") },
                    readOnly = true,
                    modifier = Modifier.menuAnchor().fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = dropdownExpanded,
                    onDismissRequest = { dropdownExpanded = false }
                ) {
                    locationOptions.forEach { option ->
                        DropdownMenuItem(
                            text = { Text(option) },
                            onClick = {
                                locationType = option
                                dropdownExpanded = false
                            }
                        )
                    }
                }
            }
            OutlinedTextField(
                value = imageUrl,
                onValueChange = { imageUrl = it },
                label = { Text("Image URL") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                label = { Text("Notes") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = minTemperatureText,
                    onValueChange = { minTemperatureText = it.filter { ch -> ch.isDigit() || ch == '-' }.take(3) },
                    label = { Text("Min (°C)") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = maxTemperatureText,
                    onValueChange = { maxTemperatureText = it.filter { ch -> ch.isDigit() || ch == '-' }.take(3) },
                    label = { Text("Max (°C)") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}
