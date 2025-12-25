package com.example.plantmanager.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface PlantDao {

    @Query("SELECT * FROM plants ORDER BY name ASC")
    fun getAllPlants(): Flow<List<Plant>>

    @Query("SELECT * FROM plants WHERE id = :plantId")
    fun getPlantById(plantId: Int): Flow<Plant?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlant(plant: Plant)

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
}