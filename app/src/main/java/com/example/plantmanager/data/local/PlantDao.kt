package com.example.plantmanager.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import com.example.plantmanager.data.local.WateringEvent

@Dao
interface PlantDao {

    @Query("SELECT * FROM plants ORDER BY wateringFrequency ASC")
    fun getAllPlants(): Flow<List<Plant>>

    @Query("SELECT * FROM plants WHERE id = :plantId")
    fun getPlantById(plantId: Int): Flow<Plant?>
    
    @Query("SELECT * FROM plants WHERE id = :plantId LIMIT 1")
    suspend fun getPlantNow(plantId: Int): Plant?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlant(plant: Plant): Long

    @Update
    suspend fun updatePlant(plant: Plant)

    @Delete
    suspend fun deletePlant(plant: Plant)

    @Query("DELETE FROM plants")
    suspend fun deleteAllPlants()

    @Query("SELECT * FROM plants WHERE lastWateringDate + (wateringFrequency * 24 * 60 * 60 * 1000) < :currentTime")
    fun getPlantsNeedingWater(currentTime: Long = System.currentTimeMillis()): Flow<List<Plant>>

    @Query("UPDATE plants SET lastWateringDate = :wateringDate WHERE id = :plantId")
    suspend fun waterPlant(plantId: Int, wateringDate: Long = System.currentTimeMillis())

    @Insert
    suspend fun insertWateringEvent(event: WateringEvent)

    @Query("SELECT * FROM watering_events WHERE plantId = :plantId ORDER BY date DESC LIMIT 5")
    fun getLastWateringEvents(plantId: Int): Flow<List<WateringEvent>>

    @Query("SELECT * FROM watering_events WHERE plantId = :plantId ORDER BY date DESC")
    fun getAllWateringEvents(plantId: Int): Flow<List<WateringEvent>>

    @Query("SELECT * FROM watering_events WHERE date BETWEEN :startMillis AND :endMillis ORDER BY date ASC")
    fun getEventsInRange(startMillis: Long, endMillis: Long): Flow<List<WateringEvent>>
}
