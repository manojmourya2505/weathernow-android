package com.manojmourya.weathernow.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.manojmourya.weathernow.domain.usecase.GetSelectedCityUseCase
import com.manojmourya.weathernow.domain.usecase.GetWeatherForecastUseCase
import com.manojmourya.weathernow.domain.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getSelectedCityUseCase: GetSelectedCityUseCase,
    private val getWeatherForecastUseCase: GetWeatherForecastUseCase,
) : ViewModel() {

    /** Replays the latest value so late subscribers immediately get the current state, and
     * emitting a new value forces [uiState] to re-fetch the forecast for a retry. */
    private val retryTrigger = MutableSharedFlow<Unit>(replay = 1).apply { tryEmit(Unit) }

    val uiState: StateFlow<HomeUiState> = combine(
        getSelectedCityUseCase(),
        retryTrigger,
    ) { city, _ -> city }
        .flatMapLatest { city ->
            if (city == null) {
                flowOf(HomeUiState(isLoading = false, city = null))
            } else {
                getWeatherForecastUseCase(city.latitude, city.longitude).map { resource ->
                    when (resource) {
                        is Resource.Loading -> HomeUiState(isLoading = true, city = city)
                        is Resource.Success -> HomeUiState(isLoading = false, city = city, forecast = resource.data)
                        is Resource.Error -> HomeUiState(isLoading = false, city = city, errorMessage = resource.message)
                    }
                }
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = HomeUiState(),
        )

    fun retry() {
        viewModelScope.launch { retryTrigger.emit(Unit) }
    }
}
