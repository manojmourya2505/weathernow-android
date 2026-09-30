package com.manojmourya.weathernow.domain.usecase

import com.manojmourya.weathernow.domain.model.City
import com.manojmourya.weathernow.domain.repository.CityRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/** Observes the city currently selected to drive the Home screen. */
class GetSelectedCityUseCase @Inject constructor(
    private val repository: CityRepository,
) {
    operator fun invoke(): Flow<City?> = repository.getSelectedCity()
}
