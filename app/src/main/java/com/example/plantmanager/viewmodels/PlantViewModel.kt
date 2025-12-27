package com.example.plantmanager.viewmodels



import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.plantmanager.data.local.Plant
import com.example.plantmanager.data.local.PlantDao
import com.example.plantmanager.data.local.WateringEvent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class PlantViewModel(private val plantDao: PlantDao) : ViewModel() {

    val allPlants: Flow<List<Plant>> = plantDao.getAllPlants()
    fun getPlantFlowById(plantId: Int): Flow<Plant?> = plantDao.getPlantById(plantId)

    private val _selectedPlant = MutableStateFlow<Plant?>(null)
    val selectedPlant: StateFlow<Plant?> = _selectedPlant

    val plantsNeedingWater: Flow<List<Plant>> = plantDao.getPlantsNeedingWater()

    fun insertPlant(plant: Plant) = viewModelScope.launch {
        val insertedId = plantDao.insertPlant(plant).toInt()
        plantDao.insertWateringEvent(WateringEvent(plantId = insertedId, date = plant.lastWateringDate, source = "initial"))
    }

    fun updatePlant(plant: Plant) = viewModelScope.launch {
        plantDao.updatePlant(plant)
    }

    fun deletePlant(plant: Plant) = viewModelScope.launch {
        plantDao.deletePlant(plant)
    }

    fun selectPlant(plant: Plant) {
        _selectedPlant.value = plant
    }

    fun clearSelectedPlant() {
        _selectedPlant.value = null
    }

    fun waterPlant(plantId: Int) = viewModelScope.launch {
        val plant = plantDao.getPlantNow(plantId)
        if (plant != null) {
            val elapsed = System.currentTimeMillis() - plant.lastWateringDate
            val halfPeriodMillis = (plant.wateringFrequency * 24L * 60L * 60L * 1000L) / 2L
            if (elapsed >= halfPeriodMillis) {
                val now = System.currentTimeMillis()
                plantDao.waterPlant(plantId, now)
                plantDao.insertWateringEvent(WateringEvent(plantId = plantId, date = now, source = "manual"))
            }
        }
    }

    fun getLastWateringEvents(plantId: Int): Flow<List<com.example.plantmanager.data.local.WateringEvent>> =
        plantDao.getLastWateringEvents(plantId)

    fun getAllWateringEvents(plantId: Int): Flow<List<com.example.plantmanager.data.local.WateringEvent>> =
        plantDao.getAllWateringEvents(plantId)

    fun getEventsInRange(startMillis: Long, endMillis: Long): Flow<List<com.example.plantmanager.data.local.WateringEvent>> =
        plantDao.getEventsInRange(startMillis, endMillis)


    suspend fun needsWatering(plant: Plant): Boolean {
        val nextWatering = plant.lastWateringDate +
                (plant.wateringFrequency * 24 * 60 * 60 * 1000)
        return System.currentTimeMillis() > nextWatering
    }


    fun getTimeUntilWatering(plant: Plant): Long {
        val nextWatering = plant.lastWateringDate +
                (plant.wateringFrequency * 24 * 60 * 60 * 1000)
        return nextWatering - System.currentTimeMillis()
    }

    fun getTimeUntilAllowedWatering(plant: Plant): Long {
        val allowedAt = plant.lastWateringDate + (plant.wateringFrequency * 24L * 60L * 60L * 1000L) / 2L
        return allowedAt - System.currentTimeMillis()
    }
}
