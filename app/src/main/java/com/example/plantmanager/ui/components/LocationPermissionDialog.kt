package com.example.plantmanager.ui.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun LocationPermissionDialog(
    onDismiss: () -> Unit,
    onAllow: () -> Unit,
    onUseDefault: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Permission de localisation") },
        text = {
            Text("PlantManager a besoin de votre localisation pour afficher la météo précise de votre position. Si vous refusez, l'application utilisera Tunis comme position par défaut.")
        },
        confirmButton = {
            Button(onClick = onAllow) { Text("Autoriser") }
        },
        dismissButton = {
            Button(onClick = onUseDefault) { Text("Utiliser Tunis") }
        }
    )
}
