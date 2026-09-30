package com.manojmourya.weathernow.presentation.detail

import com.manojmourya.weathernow.domain.model.City
import com.manojmourya.weathernow.domain.model.WeatherForecast

data class CityDetailUiState(
    val city: City,
    val isLoading: Boolean = true,
    val forecast: WeatherForecast? = null,
    val errorMessage: String? = null,
    val isSaved: Boolean = false,
    val isCurrentHomeCity: Boolean = false,
)
