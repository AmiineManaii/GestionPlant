package com.example.plantmanager.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "session")
data class Session(
    @PrimaryKey val id: Int = 1,
    val currentUserId: Int?
)

