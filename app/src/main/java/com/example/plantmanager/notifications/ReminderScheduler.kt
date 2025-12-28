package com.example.plantmanager.notifications

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.example.plantmanager.data.local.Plant
import kotlin.math.max

object ReminderScheduler {
    fun scheduleForPlant(context: Context, plant: Plant, leadHours: Int) {
        val next = plant.lastWateringDate + plant.wateringFrequency * 24L * 60L * 60L * 1000L
        val triggerAt = max(0L, next - leadHours * 60L * 60L * 1000L)
        scheduleAt(context, triggerAt, plant.name)
    }

    fun scheduleTest(context: Context) {
        val triggerAt = System.currentTimeMillis() + 10_000L
        scheduleAt(context, triggerAt, "Test")
    }

    private fun scheduleAt(context: Context, triggerAtMillis: Long, plantName: String) {
        val intent = Intent(context, ReminderReceiver::class.java).apply {
            putExtra("plantName", plantName)
            putExtra("notificationId", (plantName.hashCode() xor triggerAtMillis.toInt()))
        }
        val pending = PendingIntent.getBroadcast(
            context,
            plantName.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val am = context.getSystemService(AlarmManager::class.java)
        am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAtMillis, pending)
    }
}

