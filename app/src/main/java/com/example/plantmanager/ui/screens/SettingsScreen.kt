package com.example.plantmanager.ui.screens

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.plantmanager.notifications.NotificationHelper
import com.example.plantmanager.notifications.ReminderScheduler
import com.example.plantmanager.prefs.PreferencesManager
import com.example.plantmanager.ui.components.SectionCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    onOpenProfile: () -> Unit
) {
    val context = LocalContext.current
    val prefs = remember { PreferencesManager(context) }
    var leadHours by remember { mutableStateOf(prefs.getReminderLeadHours()) }
    val permissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Réglages", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Retour") }
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
            SectionCard(icon = Icons.Default.Settings, title = "Rappel d’arrosage") {
                Text("Délai avant arrosage (heures)")
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf(1,2,3,4).forEach { h ->
                        FilterChip(
                            selected = leadHours == h,
                            onClick = {
                                leadHours = h
                                prefs.setReminderLeadHours(h)
                            },
                            label = { Text("$h h") }
                        )
                    }
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(onClick = {
                        NotificationHelper.createChannel(context)
                        if (Build.VERSION.SDK_INT >= 33) {
                            permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                        }
                        ReminderScheduler.scheduleTest(context)
                    }) {
                        Icon(Icons.Default.Notifications, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text("Tester la notification")
                    }
                    Text("Reçoit après 10 s")
                }
            }
            SectionCard(icon = Icons.Default.Person, title = "Compte") {
                Text("Gérer les informations de votre profil")
                Spacer(Modifier.height(8.dp))
                Button(onClick = onOpenProfile) { Text("Ouvrir le profil") }
            }
        }
    }
}
