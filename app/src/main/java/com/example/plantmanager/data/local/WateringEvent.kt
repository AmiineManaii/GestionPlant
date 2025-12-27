package com.example.plantmanager.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "watering_events")
data class WateringEvent(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val plantId: Int,
    val date: Long = System.currentTimeMillis(),
    val source: String = "manual" // "manual" or "initial"
)
