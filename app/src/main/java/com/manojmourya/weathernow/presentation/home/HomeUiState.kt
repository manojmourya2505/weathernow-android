package com.manojmourya.weathernow.presentation.home

import com.manojmourya.weathernow.domain.model.City
import com.manojmourya.weathernow.domain.model.WeatherForecast

data class HomeUiState(
    val isLoading: Boolean = true,
    val city: City? = null,
    val forecast: WeatherForecast? = null,
    val errorMessage: String? = null,
) {
    val hasNoSelectedCity: Boolean get() = !isLoading && city == null && errorMessage == null
}
