package com.example.plantmanager.viewmodels



import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.plantmanager.data.local.Plant
import com.example.plantmanager.data.local.PlantDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class PlantViewModel(private val plantDao: PlantDao) : ViewModel() {

    // Toutes les plantes
    val allPlants: Flow<List<Plant>> = plantDao.getAllPlants()

    // État pour une plante spécifique (pour l'écran de détail)
    private val _selectedPlant = MutableStateFlow<Plant?>(null)
    val selectedPlant: StateFlow<Plant?> = _selectedPlant

    // État pour les plantes nécessitant arrosage
    val plantsNeedingWater: Flow<List<Plant>> = plantDao.getPlantsNeedingWater()

    // Opérations CRUD
    fun insertPlant(plant: Plant) = viewModelScope.launch {
        plantDao.insertPlant(plant)
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

    // Arroser une plante
    fun waterPlant(plantId: Int) = viewModelScope.launch {
        plantDao.waterPlant(plantId)
    }

    // Vérifier si une plante a besoin d'eau
    suspend fun needsWatering(plant: Plant): Boolean {
        val nextWatering = plant.lastWateringDate +
                (plant.wateringFrequency * 24 * 60 * 60 * 1000)
        return System.currentTimeMillis() > nextWatering
    }

    // Calculer le temps restant avant arrosage
    fun getTimeUntilWatering(plant: Plant): Long {
        val nextWatering = plant.lastWateringDate +
                (plant.wateringFrequency * 24 * 60 * 60 * 1000)
        return nextWatering - System.currentTimeMillis()
    }
}