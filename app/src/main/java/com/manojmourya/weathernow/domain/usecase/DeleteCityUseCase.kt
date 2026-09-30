package com.manojmourya.weathernow.domain.usecase

import com.manojmourya.weathernow.domain.model.City
import com.manojmourya.weathernow.domain.repository.CityRepository
import javax.inject.Inject

/** Removes a city from favorites. */
class DeleteCityUseCase @Inject constructor(
    private val repository: CityRepository,
) {
    suspend operator fun invoke(city: City) = repository.deleteCity(city)
}
