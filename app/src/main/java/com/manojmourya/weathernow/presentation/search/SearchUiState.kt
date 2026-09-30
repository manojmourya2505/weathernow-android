package com.manojmourya.weathernow.presentation.search

import com.manojmourya.weathernow.domain.model.City

data class SearchUiState(
    val query: String = "",
    val isLoading: Boolean = false,
    val results: List<City> = emptyList(),
    val errorMessage: String? = null,
) {
    val showEmptyPrompt: Boolean get() = query.isBlank() && !isLoading && errorMessage == null
    val showNoResults: Boolean get() = query.isNotBlank() && !isLoading && errorMessage == null && results.isEmpty()
}
