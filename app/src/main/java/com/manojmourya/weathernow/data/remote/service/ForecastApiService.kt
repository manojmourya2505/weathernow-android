package com.manojmourya.weathernow.data.remote.service

import com.manojmourya.weathernow.data.remote.dto.ForecastResponseDto
import retrofit2.http.GET
import retrofit2.http.Query

/** https://api.open-meteo.com/v1/forecast */
interface ForecastApiService {
    @GET("v1/forecast")
    suspend fun getForecast(
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,
        @Query("current") current: String =
            "temperature_2m,relative_humidity_2m,weather_code,wind_speed_10m,apparent_temperature",
        @Query("daily") daily: String =
            "weather_code,temperature_2m_max,temperature_2m_min,precipitation_probability_max",
        @Query("hourly") hourly: String = "temperature_2m,weather_code",
        @Query("timezone") timezone: String = "auto",
    ): ForecastResponseDto
}
