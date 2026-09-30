package com.manojmourya.weathernow.domain.usecase

import com.manojmourya.weathernow.domain.model.WeatherForecast
import com.manojmourya.weathernow.domain.repository.WeatherRepository
import com.manojmourya.weathernow.domain.util.Resource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/** Fetches the current + hourly + daily forecast for a location. */
class GetWeatherForecastUseCase @Inject constructor(
    private val repository: WeatherRepository,
) {
    operator fun invoke(latitude: Double, longitude: Double): Flow<Resource<WeatherForecast>> =
        repository.getWeatherForecast(latitude, longitude)
}
