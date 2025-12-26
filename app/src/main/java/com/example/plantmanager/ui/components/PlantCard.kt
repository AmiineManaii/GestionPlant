package com.example.plantmanager.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.Image
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.plantmanager.data.local.Plant
import coil.compose.AsyncImage

@Composable
fun PlantCard(
    plant: Plant,
    onClick: () -> Unit,
    onWaterClick: () -> Unit
) {
    val needsWatering = remember(plant) {
        val nextWatering = plant.lastWateringDate + (plant.wateringFrequency * 24 * 60 * 60 * 1000)
        System.currentTimeMillis() > nextWatering
    }
    val canWaterNow = remember(plant) {
        val elapsed = System.currentTimeMillis() - plant.lastWateringDate
        val halfPeriodMillis = (plant.wateringFrequency * 24L * 60L * 60L * 1000L) / 2L
        elapsed >= halfPeriodMillis
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = if (needsWatering) MaterialTheme.colorScheme.errorContainer else MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (!plant.imageUrl.isNullOrEmpty()) {
                AsyncImage(
                    model = plant.imageUrl,
                    contentDescription = plant.name,
                    modifier = Modifier
                        .size(60.dp)
                        .clip(MaterialTheme.shapes.medium),
                    contentScale = ContentScale.Crop
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .background(color = MaterialTheme.colorScheme.primaryContainer, shape = MaterialTheme.shapes.medium),
                    contentAlignment = Alignment.Center
                ) {
                    Text("🌱", fontSize = 24.sp)
                }
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 16.dp)
            ) {
                Text(text = plant.name, fontSize = 18.sp, fontWeight = androidx.compose.ui.text.font.FontWeight.Medium)

                if (plant.species.isNotEmpty()) {
                    Text(text = plant.species, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }

                val daysUntilWatering = remember(plant) {
                    val remaining = plant.lastWateringDate + (plant.wateringFrequency * 24 * 60 * 60 * 1000) - System.currentTimeMillis()
                    (remaining / (24 * 60 * 60 * 1000)).toInt()
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
                    color = if (needsWatering) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                )
            }

            IconButton(onClick = onWaterClick, modifier = Modifier.size(48.dp), enabled = canWaterNow) {
                Icon(Icons.Default.WaterDrop, contentDescription = "Arroser", tint = if (needsWatering) MaterialTheme.colorScheme.primary else Color.Gray)
            }
        }
    }
}
