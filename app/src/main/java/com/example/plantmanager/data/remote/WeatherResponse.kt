package com.example.plantmanager.data.remote


data class WeatherResponse(
    val latitude: Double,
    val longitude: Double,
    val generationtime_ms: Double,
    val utc_offset_seconds: Int,
    val timezone: String,
    val timezone_abbreviation: String,
    val elevation: Double,
    val current_weather: CurrentWeather
)

data class CurrentWeather(
    val temperature: Double,
    val windspeed: Double,
    val winddirection: Double,
    val weathercode: Int,
    val time: String
)


object WeatherCode {
    fun getDescription(code: Int): String {
        return when (code) {
            0 -> "Ciel dégagé"
            1 -> "Principalement clair"
            2 -> "Partiellement nuageux"
            3 -> "Couvert"
            45, 48 -> "Brouillard"
            51, 53, 55 -> "Bruine"
            56, 57 -> "Bruine verglaçante"
            61, 63, 65 -> "Pluie"
            66, 67 -> "Pluie verglaçante"
            71, 73, 75 -> "Neige"
            77 -> "Grains de neige"
            80, 81, 82 -> "Averses"
            85, 86 -> "Averses de neige"
            95 -> "Orage"
            96, 99 -> "Orage avec grêle"
            else -> "Inconnu"
        }
    }

    fun getIcon(code: Int): String {
        return when (code) {
            0 -> "☀️"
            1, 2 -> "⛅"
            3 -> "☁️"
            in 45..48 -> "🌫️"
            in 51..67 -> "🌧️"
            in 71..77 -> "❄️"
            in 80..82 -> "🌦️"
            in 85..86 -> "🌨️"
            in 95..99 -> "⛈️"
            else -> "🌈"
        }
    }
}