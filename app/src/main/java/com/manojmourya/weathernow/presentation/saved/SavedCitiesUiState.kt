package com.manojmourya.weathernow.presentation.saved

import com.manojmourya.weathernow.domain.model.City

data class SavedCitiesUiState(
    val isLoading: Boolean = true,
    val cities: List<City> = emptyList(),
) {
    val isEmpty: Boolean get() = !isLoading && cities.isEmpty()
}
