package com.example.plantmanager.viewmodels



import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.plantmanager.data.LocationManager
import com.example.plantmanager.data.remote.RetrofitInstance
import com.example.plantmanager.data.remote.WeatherResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class WeatherViewModel(
    private val locationManager: LocationManager
) : ViewModel() {

    private val _weatherState = MutableStateFlow<WeatherState>(WeatherState.Loading)
    val weatherState: StateFlow<WeatherState> = _weatherState

    private val _currentLocation = MutableStateFlow<Pair<Double, Double>?>(null)
    val currentLocation: StateFlow<Pair<Double, Double>?> = _currentLocation

    init {
        fetchWeather()
    }

    fun fetchWeather() {
        _weatherState.value = WeatherState.Loading

        viewModelScope.launch {
            try {

                val location = locationManager.getCurrentLocation()
                _currentLocation.value = location

                location?.let { (latitude, longitude) ->

                    val response = RetrofitInstance.weatherApi.getCurrentWeather(
                        latitude = latitude,
                        longitude = longitude
                    )

                    _weatherState.value = WeatherState.Success(response)
                } ?: run {
                    _weatherState.value = WeatherState.Error("Localisation non disponible")
                }
            } catch (e: Exception) {
                _weatherState.value = WeatherState.Error(e.message ?: "Erreur inconnue")
            }
        }
    }


    sealed class WeatherState {
        object Loading : WeatherState()
        data class Success(val data: WeatherResponse) : WeatherState()
        data class Error(val message: String) : WeatherState()
    }
}