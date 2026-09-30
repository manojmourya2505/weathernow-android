package com.manojmourya.weathernow.domain.util

/**
 * Pure mapping from Open-Meteo's WMO weather codes to human-readable
 * descriptions. Kept dependency-free (no Android/Compose imports) so it is
 * trivially unit-testable; icon selection lives in the presentation layer.
 *
 * Reference: https://open-meteo.com/en/docs (WMO Weather interpretation codes)
 */
object WeatherCodeMapper {

    fun description(code: Int): String = when (code) {
        0 -> "Clear sky"
        1 -> "Mainly clear"
        2 -> "Partly cloudy"
        3 -> "Overcast"
        45 -> "Fog"
        48 -> "Depositing rime fog"
        51 -> "Light drizzle"
        53 -> "Moderate drizzle"
        55 -> "Dense drizzle"
        56 -> "Light freezing drizzle"
        57 -> "Dense freezing drizzle"
        61 -> "Slight rain"
        63 -> "Moderate rain"
        65 -> "Heavy rain"
        66 -> "Light freezing rain"
        67 -> "Heavy freezing rain"
        71 -> "Slight snow fall"
        73 -> "Moderate snow fall"
        75 -> "Heavy snow fall"
        77 -> "Snow grains"
        80 -> "Slight rain showers"
        81 -> "Moderate rain showers"
        82 -> "Violent rain showers"
        85 -> "Slight snow showers"
        86 -> "Heavy snow showers"
        95 -> "Thunderstorm"
        96 -> "Thunderstorm with slight hail"
        99 -> "Thunderstorm with heavy hail"
        else -> "Unknown"
    }

    enum class Category { CLEAR, PARTLY_CLOUDY, CLOUDY, FOG, DRIZZLE, RAIN, SNOW, SHOWERS, THUNDERSTORM }

    fun category(code: Int): Category = when (code) {
        0 -> Category.CLEAR
        1, 2 -> Category.PARTLY_CLOUDY
        3 -> Category.CLOUDY
        45, 48 -> Category.FOG
        51, 53, 55, 56, 57 -> Category.DRIZZLE
        61, 63, 65, 66, 67 -> Category.RAIN
        71, 73, 75, 77 -> Category.SNOW
        80, 81, 82, 85, 86 -> Category.SHOWERS
        95, 96, 99 -> Category.THUNDERSTORM
        else -> Category.CLOUDY
    }
}
