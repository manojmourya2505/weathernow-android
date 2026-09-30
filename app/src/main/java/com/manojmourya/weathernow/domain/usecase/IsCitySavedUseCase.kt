package com.manojmourya.weathernow.domain.usecase

import com.manojmourya.weathernow.domain.repository.CityRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/** Checks whether a city at the given coordinates is already saved as a favorite. */
class IsCitySavedUseCase @Inject constructor(
    private val repository: CityRepository,
) {
    operator fun invoke(latitude: Double, longitude: Double): Flow<Boolean> =
        repository.isCitySaved(latitude, longitude)
}
