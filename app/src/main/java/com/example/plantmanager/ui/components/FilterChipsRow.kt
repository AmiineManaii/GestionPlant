package com.example.plantmanager.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun FilterChipsRow(
    selected: String,
    onSelectedChange: (String) -> Unit,
    allCount: Int
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .horizontalScroll(rememberScrollState())
    ) {
        FilterChip(
            selected = selected == "ALL",
            onClick = { onSelectedChange("ALL") },
            label = { Text("Toutes ($allCount)") }
        )
        FilterChip(
            selected = selected == "NEEDS_WATER",
            onClick = { onSelectedChange("NEEDS_WATER") },
            label = { Text("À arroser") }
        )
        FilterChip(
            selected = selected == "INDOOR",
            onClick = { onSelectedChange("INDOOR") },
            label = { Text("Intérieur") }
        )
        FilterChip(
            selected = selected == "OUTDOOR",
            onClick = { onSelectedChange("OUTDOOR") },
            label = { Text("Extérieur") }
        )
        FilterChip(
            selected = selected == "BALCONY",
            onClick = { onSelectedChange("BALCONY") },
            label = { Text("Balcon") }
        )
    }
}
