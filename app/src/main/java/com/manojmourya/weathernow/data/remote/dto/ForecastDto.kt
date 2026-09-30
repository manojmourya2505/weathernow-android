package com.manojmourya.weathernow.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ForecastResponseDto(
    val latitude: Double,
    val longitude: Double,
    val timezone: String,
    val current: CurrentDto,
    val hourly: HourlyDto,
    val daily: DailyDto,
)

@Serializable
data class CurrentDto(
    val time: String,
    @SerialName("temperature_2m") val temperature2m: Double,
    @SerialName("relative_humidity_2m") val relativeHumidity2m: Int,
    @SerialName("weather_code") val weatherCode: Int,
    @SerialName("wind_speed_10m") val windSpeed10m: Double,
    @SerialName("apparent_temperature") val apparentTemperature: Double,
)

@Serializable
data class HourlyDto(
    val time: List<String>,
    @SerialName("temperature_2m") val temperature2m: List<Double>,
    @SerialName("weather_code") val weatherCode: List<Int>,
)

@Serializable
data class DailyDto(
    val time: List<String>,
    @SerialName("weather_code") val weatherCode: List<Int>,
    @SerialName("temperature_2m_max") val temperature2mMax: List<Double>,
    @SerialName("temperature_2m_min") val temperature2mMin: List<Double>,
    @SerialName("precipitation_probability_max") val precipitationProbabilityMax: List<Int>,
)
