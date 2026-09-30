package com.manojmourya.weathernow.domain.usecase

import com.manojmourya.weathernow.domain.model.City
import com.manojmourya.weathernow.domain.repository.CityRepository
import javax.inject.Inject

/** Switches the Home screen to show [city], saving it first if it isn't already a favorite. */
class SelectCityUseCase @Inject constructor(
    private val repository: CityRepository,
) {
    suspend operator fun invoke(city: City) = repository.selectCity(city)
}
