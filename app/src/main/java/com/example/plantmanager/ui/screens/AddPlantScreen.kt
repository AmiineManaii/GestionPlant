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
import com.example.plantmanager.data.local.Plant
import coil.compose.AsyncImage
import androidx.compose.ui.layout.ContentScale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddPlantScreen(
    plantViewModel: PlantViewModel,
    onBack: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var species by remember { mutableStateOf("") }
    var frequencyText by remember { mutableStateOf("7") }
    var locationType by remember { mutableStateOf("Indoor") }
    var imageUrl by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    var minTemperatureText by remember { mutableStateOf("") }
    var maxTemperatureText by remember { mutableStateOf("") }
    var dropdownExpanded by remember { mutableStateOf(false) }
    val locationOptions = listOf("Indoor", "Outdoor", "Balcony")
    val isValid = name.isNotBlank() && frequencyText.toIntOrNull()?.let { it in 1..60 } == true

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Ajouter une plante") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Retour")
                    }
                },
                actions = {
                    Button(
                        onClick = {
                            val freq = frequencyText.toIntOrNull() ?: 7
                            val plant = Plant(
                                name = name.trim(),
                                species = species.trim(),
                                wateringFrequency = freq,
                                lastWateringDate = System.currentTimeMillis(),
                                locationType = locationType,
                                imageUrl = imageUrl.trim().ifEmpty { null },
                                notes = notes.trim(),
                                minTemperature = minTemperatureText.toIntOrNull(),
                                maxTemperature = maxTemperatureText.toIntOrNull()
                            )
                            plantViewModel.insertPlant(plant)
                            onBack()
                        },
                        enabled = isValid
                    ) {
                        Text("Enregistrer")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Nom") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = species,
                onValueChange = { species = it },
                label = { Text("Espèce (optionnel)") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = frequencyText,
                onValueChange = { frequencyText = it.filter { ch -> ch.isDigit() }.take(2) },
                label = { Text("Fréquence d'arrosage (jours)") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )
            ExposedDropdownMenuBox(
                expanded = dropdownExpanded,
                onExpandedChange = { dropdownExpanded = !dropdownExpanded }
            ) {
                OutlinedTextField(
                    value = locationType,
                    onValueChange = {},
                    label = { Text("Emplacement") },
                    singleLine = true,
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
                label = { Text("Image URL (optionnel)") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            if (imageUrl.isNotBlank()) {
                Card {
                    AsyncImage(
                        model = imageUrl,
                        contentDescription = "Prévisualisation",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(160.dp)
                    )
                }
            }
            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                label = { Text("Notes (optionnel)") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = minTemperatureText,
                    onValueChange = { minTemperatureText = it.filter { ch -> ch.isDigit() || ch == '-' }.take(3) },
                    label = { Text("Min (°C) optionnel") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = maxTemperatureText,
                    onValueChange = { maxTemperatureText = it.filter { ch -> ch.isDigit() || ch == '-' }.take(3) },
                    label = { Text("Max (°C) optionnel") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}
