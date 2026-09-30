package com.manojmourya.weathernow.presentation.detail

import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.manojmourya.weathernow.domain.model.City
import com.manojmourya.weathernow.domain.usecase.DeleteCityUseCase
import com.manojmourya.weathernow.domain.usecase.GetSavedCitiesUseCase
import com.manojmourya.weathernow.domain.usecase.GetSelectedCityUseCase
import com.manojmourya.weathernow.domain.usecase.GetWeatherForecastUseCase
import com.manojmourya.weathernow.domain.usecase.SaveCityUseCase
import com.manojmourya.weathernow.domain.usecase.SelectCityUseCase
import com.manojmourya.weathernow.domain.util.Resource
import com.manojmourya.weathernow.presentation.navigation.Screen
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CityDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getWeatherForecastUseCase: GetWeatherForecastUseCase,
    private val getSavedCitiesUseCase: GetSavedCitiesUseCase,
    private val getSelectedCityUseCase: GetSelectedCityUseCase,
    private val saveCityUseCase: SaveCityUseCase,
    private val deleteCityUseCase: DeleteCityUseCase,
    private val selectCityUseCase: SelectCityUseCase,
) : ViewModel() {

    private val baseCity: City = City(
        id = null,
        name = Uri.decode(savedStateHandle.get<String>(Screen.Detail.ARG_NAME).orEmpty()),
        country = Uri.decode(savedStateHandle.get<String>(Screen.Detail.ARG_COUNTRY).orEmpty()).ifBlank { null },
        admin1 = Uri.decode(savedStateHandle.get<String>(Screen.Detail.ARG_ADMIN1).orEmpty()).ifBlank { null },
        latitude = savedStateHandle.get<String>(Screen.Detail.ARG_LATITUDE)?.toDoubleOrNull() ?: 0.0,
        longitude = savedStateHandle.get<String>(Screen.Detail.ARG_LONGITUDE)?.toDoubleOrNull() ?: 0.0,
    )

    val uiState: StateFlow<CityDetailUiState> = combine(
        getWeatherForecastUseCase(baseCity.latitude, baseCity.longitude),
        getSavedCitiesUseCase(),
        getSelectedCityUseCase(),
    ) { forecastResource, savedCities, selectedCity ->
        val savedMatch = savedCities.find {
            it.latitude == baseCity.latitude && it.longitude == baseCity.longitude
        }
        val effectiveCity = savedMatch ?: baseCity
        val isCurrentHome = selectedCity != null &&
            selectedCity.latitude == baseCity.latitude &&
            selectedCity.longitude == baseCity.longitude

        val common = CityDetailUiState(
            city = effectiveCity,
            isSaved = savedMatch != null,
            isCurrentHomeCity = isCurrentHome,
        )
        when (forecastResource) {
            is Resource.Loading -> common.copy(isLoading = true)
            is Resource.Success -> common.copy(isLoading = false, forecast = forecastResource.data)
            is Resource.Error -> common.copy(isLoading = false, errorMessage = forecastResource.message)
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = CityDetailUiState(city = baseCity),
    )

    fun toggleFavorite() {
        viewModelScope.launch {
            val current = uiState.value
            if (current.isSaved) {
                deleteCityUseCase(current.city)
            } else {
                saveCityUseCase(current.city)
            }
        }
    }

    fun setAsHomeCity() {
        viewModelScope.launch { selectCityUseCase(uiState.value.city) }
    }
}
