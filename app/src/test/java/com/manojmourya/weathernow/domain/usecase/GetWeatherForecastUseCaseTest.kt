package com.manojmourya.weathernow.domain.usecase

import app.cash.turbine.test
import com.manojmourya.weathernow.domain.model.CurrentWeather
import com.manojmourya.weathernow.domain.model.WeatherForecast
import com.manojmourya.weathernow.domain.repository.WeatherRepository
import com.manojmourya.weathernow.domain.util.Resource
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class GetWeatherForecastUseCaseTest {

    private val repository = mockk<WeatherRepository>()
    private val useCase = GetWeatherForecastUseCase(repository)

    private val forecast = WeatherForecast(
        timezone = "Europe/London",
        current = CurrentWeather(
            temperature = 18.0,
            feelsLike = 17.0,
            humidity = 60,
            windSpeed = 10.0,
            weatherCode = 1,
            time = "2026-09-30T12:00",
        ),
        hourly = emptyList(),
        daily = emptyList(),
    )

    @Test
    fun `invoke delegates to repository getWeatherForecast with the given coordinates`() = runTest {
        every { repository.getWeatherForecast(51.5, -0.1) } returns flowOf(Resource.Success(forecast))

        useCase(51.5, -0.1).test {
            val result = awaitItem() as Resource.Success
            assertEquals(forecast, result.data)
            awaitComplete()
        }

        verify(exactly = 1) { repository.getWeatherForecast(51.5, -0.1) }
    }

    @Test
    fun `invoke propagates errors from the repository`() = runTest {
        every { repository.getWeatherForecast(0.0, 0.0) } returns flowOf(Resource.Error("No internet connection."))

        useCase(0.0, 0.0).test {
            val result = awaitItem() as Resource.Error
            assertEquals("No internet connection.", result.message)
            awaitComplete()
        }
    }
}
