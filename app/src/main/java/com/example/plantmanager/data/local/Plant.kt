package com.example.plantmanager.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "plants")
data class Plant(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val species: String = "",
    val wateringFrequency: Int = 7,
    val lastWateringDate: Long = System.currentTimeMillis(),
    val creationDate: Long = System.currentTimeMillis(),
    val imageUrl: String? = null,
    val notes: String = "",

    val minTemperature: Int? = null,
    val maxTemperature: Int? = null,

    val locationType: String = "Indoor" // Indoor, Outdoor, Balcony
)