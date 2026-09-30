package com.manojmourya.weathernow.domain.usecase

import com.manojmourya.weathernow.domain.model.City
import com.manojmourya.weathernow.domain.repository.CityRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/** Observes the list of locally saved favorite cities. */
class GetSavedCitiesUseCase @Inject constructor(
    private val repository: CityRepository,
) {
    operator fun invoke(): Flow<List<City>> = repository.getSavedCities()
}
