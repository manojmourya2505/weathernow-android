package com.manojmourya.weathernow.presentation.saved

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.manojmourya.weathernow.domain.model.City
import com.manojmourya.weathernow.domain.usecase.DeleteCityUseCase
import com.manojmourya.weathernow.domain.usecase.GetSavedCitiesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SavedCitiesViewModel @Inject constructor(
    getSavedCitiesUseCase: GetSavedCitiesUseCase,
    private val deleteCityUseCase: DeleteCityUseCase,
) : ViewModel() {

    val uiState: StateFlow<SavedCitiesUiState> = getSavedCitiesUseCase()
        .map { cities -> SavedCitiesUiState(isLoading = false, cities = cities) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = SavedCitiesUiState(),
        )

    fun deleteCity(city: City) {
        viewModelScope.launch { deleteCityUseCase(city) }
    }
}
