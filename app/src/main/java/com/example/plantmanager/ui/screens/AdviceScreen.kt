package com.example.plantmanager.ui.screens

import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AcUnit
import androidx.compose.material.icons.filled.Balance
import androidx.compose.material.icons.filled.Forest
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.plantmanager.ui.components.SectionCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdviceScreen(
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Conseils", fontWeight = FontWeight.Bold) },
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
            SectionCard(icon = Icons.Default.WaterDrop, title = "Arrosage") {
                Text("Vérifier l’humidité du sol avant d’arroser.")
                Text("Arroser le matin pour limiter l’évaporation.")
                Text("Adapter la fréquence selon la saison et l’espèce.")
                Text("Éviter l’eau stagnante dans les pots.")
            }

            SectionCard(icon = Icons.Default.Thermostat, title = "Température") {
                Text("Protéger des courants d’air froid en hiver.")
                Text("Éviter les fortes chaleurs directes près des fenêtres.")
                Text("Respecter les minimums et maximums de chaque plante.")
            }

            SectionCard(icon = Icons.Default.Lightbulb, title = "Lumière") {
                Text("Privilégier une lumière indirecte pour la plupart des plantes.")
                Text("Tourner le pot régulièrement pour une croissance homogène.")
                Text("Surveiller les brûlures de feuilles exposées au soleil direct.")
            }

            SectionCard(icon = Icons.Default.Lightbulb, title = "Entretien") {
                Text("Tailler les feuilles abîmées pour stimuler la croissance.")
                Text("Rempoter une fois par an si les racines sont serrées.")
                Text("Utiliser un substrat adapté à l’espèce.")
            }

            SectionCard(icon = Icons.Default.AcUnit, title = "Humidité et saison") {
                Text("Augmenter l’humidité ambiante pour les plantes tropicales.")
                Text("Réduire les arrosages en hiver, reprendre progressivement au printemps.")
                Text("Nettoyer les feuilles pour favoriser la photosynthèse.")
            }

            SectionCard(icon = Icons.Default.Balance, title = "Bonnes pratiques") {
                Text("Observer régulièrement l’état des feuilles et du sol.")
                Text("Adapter les soins au lieu: intérieur, balcon ou extérieur.")
                Text("Noter les arrosages pour suivre l’évolution.")
            }
        }
    }
}
