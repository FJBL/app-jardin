package com.mx.plantas.ui

import android.app.Application
import android.graphics.Bitmap
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.mx.plantas.data.Plant
import com.mx.plantas.data.PlantDatabase
import com.mx.plantas.data.PlantRepository
import com.mx.plantas.scanner.PlantClassifierTFLite
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class PlantViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = PlantRepository(
        PlantDatabase.getDatabase(application).plantDao(),
        application
    )
    private val imageClassifier = PlantClassifierTFLite()

    private val searchQuery = MutableStateFlow("")
    private val _selectedPlant = MutableStateFlow<Plant?>(null)
    val selectedPlant: StateFlow<Plant?> = _selectedPlant

    val plants = repository.allPlants.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    val filteredPlants = combine(searchQuery, plants) { query, list ->
        if (query.isBlank()) {
            list
        } else {
            list.filter { plant ->
                plant.name.contains(query, ignoreCase = true) ||
                    plant.scientificName.contains(query, ignoreCase = true) ||
                    plant.family.contains(query, ignoreCase = true)
            }
        }
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    init {
        viewModelScope.launch {
            repository.seedDatabase()
        }
    }

    fun onSearchChanged(query: String) {
        searchQuery.value = query
    }

    fun loadPlant(plantId: Long) {
        viewModelScope.launch {
            _selectedPlant.value = repository.getPlantById(plantId)
        }
    }

    suspend fun loadPlantForEdit(plantId: Long): Plant? {
        return repository.getPlantById(plantId)
    }

    suspend fun findPlantByImageAsync(bitmap: Bitmap): Pair<Plant?, String> {
        try {
            // Ejecutar clasificación con TensorFlow Lite
            val predictions = imageClassifier.classifyImage(bitmap)
            
            if (predictions.isEmpty()) {
                return null to imageClassifier.debugInfo
            }
            
            // Obtener todas las plantas disponibles
            val allPlants = repository.getAllPlants()
            val plantNames = allPlants.map { it.name }
            
            // Buscar la mejor coincidencia
            val bestPrediction = predictions.firstOrNull()?.plantName
            val matchedPlant = allPlants.find { plant ->
                plant.name.contains(bestPrediction ?: "", ignoreCase = true) ||
                bestPrediction?.contains(plant.name, ignoreCase = true) ?: false
            }
            
            return matchedPlant to imageClassifier.debugInfo
        } catch (e: Exception) {
            return null to "Error: ${e.message}"
        }
    }

    fun findPlantByImage(bitmap: Bitmap): Plant? {
        try {
            val predictions = imageClassifier.classifyImage(bitmap)
            if (predictions.isEmpty()) return null
            
            val bestPrediction = predictions.firstOrNull()?.plantName
            val currentPlants = plants.value
            
            return currentPlants.find { plant ->
                plant.name.contains(bestPrediction ?: "", ignoreCase = true) ||
                bestPrediction?.contains(plant.name, ignoreCase = true) ?: false
            }
        } catch (e: Exception) {
            return null
        }
    }

    fun getDebugInfo(): String = imageClassifier.debugInfo

    suspend fun addPlant(plant: Plant) {
        repository.addPlant(plant)
    }

    suspend fun updatePlant(plant: Plant) {
        repository.updatePlant(plant)
    }

    override fun onCleared() {
        super.onCleared()
        imageClassifier.close()
    }
}

