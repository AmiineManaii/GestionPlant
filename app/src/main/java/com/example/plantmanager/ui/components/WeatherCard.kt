package com.example.plantmanager.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.plantmanager.data.remote.WeatherCode
import com.example.plantmanager.viewmodels.WeatherViewModel

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
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            CircularProgressIndicator(modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Chargement météo...")
                        }
                    }
                    is WeatherViewModel.WeatherState.Success -> {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "${weatherState.data.current_weather.temperature}°C",
                                    fontSize = 28.sp,
                                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = WeatherCode.getIcon(weatherState.data.current_weather.weathercode),
                                    fontSize = 24.sp
                                )
                            }
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
                            TextButton(onClick = onRefresh, modifier = Modifier.padding(0.dp)) {
                                Text("Réessayer")
                            }
                        }
                    }
                }

                IconButton(onClick = onRefresh) {
                    Icon(Icons.Default.Refresh, "Actualiser")
                }
            }

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
