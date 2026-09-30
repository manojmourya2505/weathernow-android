package com.manojmourya.weathernow.domain.usecase

import com.manojmourya.weathernow.domain.model.City
import com.manojmourya.weathernow.domain.repository.CityRepository
import com.manojmourya.weathernow.domain.util.Resource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/** Searches for cities by name using the geocoding API. */
class SearchCitiesUseCase @Inject constructor(
    private val repository: CityRepository,
) {
    operator fun invoke(query: String): Flow<Resource<List<City>>> = repository.searchCities(query)
}
