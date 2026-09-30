package com.manojmourya.weathernow.presentation.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.manojmourya.weathernow.domain.usecase.SearchCitiesUseCase
import com.manojmourya.weathernow.domain.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
@HiltViewModel
class SearchViewModel @Inject constructor(
    private val searchCitiesUseCase: SearchCitiesUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    /** Drives the debounced network search; kept separate from [_uiState] so the text field
     * always reflects what the user typed immediately, without waiting on the debounce. */
    private val queryFlow = MutableStateFlow("")

    init {
        queryFlow
            .debounce(SEARCH_DEBOUNCE_MILLIS)
            .distinctUntilChanged()
            .flatMapLatest { query ->
                if (query.isBlank()) flowOf(Resource.Success(emptyList())) else searchCitiesUseCase(query)
            }
            .onEach { resource ->
                _uiState.update { current ->
                    when (resource) {
                        is Resource.Loading -> current.copy(isLoading = true, errorMessage = null)
                        is Resource.Success -> current.copy(isLoading = false, results = resource.data, errorMessage = null)
                        is Resource.Error -> current.copy(isLoading = false, errorMessage = resource.message)
                    }
                }
            }
            .launchIn(viewModelScope)
    }

    fun onQueryChanged(newQuery: String) {
        _uiState.update { it.copy(query = newQuery) }
        queryFlow.value = newQuery
    }

    private companion object {
        const val SEARCH_DEBOUNCE_MILLIS = 300L
    }
}
