package com.manojmourya.weathernow.domain.repository

import com.manojmourya.weathernow.domain.model.WeatherForecast
import com.manojmourya.weathernow.domain.util.Resource
import kotlinx.coroutines.flow.Flow

interface WeatherRepository {
    /**
     * Fetches the current + hourly + daily forecast for the given coordinates.
     * Emits [Resource.Loading] immediately, then [Resource.Success] or
     * [Resource.Error].
     */
    fun getWeatherForecast(latitude: Double, longitude: Double): Flow<Resource<WeatherForecast>>
}
