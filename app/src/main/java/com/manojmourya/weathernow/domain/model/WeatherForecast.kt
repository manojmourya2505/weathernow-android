package com.manojmourya.weathernow.domain.model

/** Current observed conditions for a location. */
data class CurrentWeather(
    val temperature: Double,
    val feelsLike: Double,
    val humidity: Int,
    val windSpeed: Double,
    val weatherCode: Int,
    val time: String,
)

/** A single hour's forecast entry. */
data class HourlyForecastItem(
    val time: String,
    val temperature: Double,
    val weatherCode: Int,
)

/** A single day's forecast entry. */
data class DailyForecastItem(
    val date: String,
    val weatherCode: Int,
    val maxTemperature: Double,
    val minTemperature: Double,
    val precipitationProbability: Int,
)

/** Aggregate forecast: current conditions plus hourly/daily outlook. */
data class WeatherForecast(
    val timezone: String,
    val current: CurrentWeather,
    val hourly: List<HourlyForecastItem>,
    val daily: List<DailyForecastItem>,
)
