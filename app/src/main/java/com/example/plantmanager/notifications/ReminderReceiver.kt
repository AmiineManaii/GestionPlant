package com.example.plantmanager.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class ReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val plantName = intent.getStringExtra("plantName") ?: "Plante"
        val notificationId = intent.getIntExtra("notificationId", System.currentTimeMillis().toInt())
        NotificationHelper.createChannel(context)
        NotificationHelper.notify(
            context,
            notificationId,
            "Rappel d’arrosage",
            "Il est bientôt l’heure d’arroser: $plantName"
        )
    }
}

