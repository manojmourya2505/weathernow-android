package com.manojmourya.weathernow.presentation.home

import app.cash.turbine.test
import com.manojmourya.weathernow.MainDispatcherRule
import com.manojmourya.weathernow.domain.model.City
import com.manojmourya.weathernow.domain.model.CurrentWeather
import com.manojmourya.weathernow.domain.model.WeatherForecast
import com.manojmourya.weathernow.domain.usecase.GetSelectedCityUseCase
import com.manojmourya.weathernow.domain.usecase.GetWeatherForecastUseCase
import com.manojmourya.weathernow.domain.util.Resource
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class HomeViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val getSelectedCityUseCase = mockk<GetSelectedCityUseCase>()
    private val getWeatherForecastUseCase = mockk<GetWeatherForecastUseCase>()

    private val city = City(
        id = 1,
        name = "London",
        country = "United Kingdom",
        admin1 = "England",
        latitude = 51.5,
        longitude = -0.1,
    )

    private fun sampleForecast() = WeatherForecast(
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

    private fun viewModel() = HomeViewModel(getSelectedCityUseCase, getWeatherForecastUseCase)

    @Test
    fun `uiState reports no selected city when none is stored`() = runTest(mainDispatcherRule.testDispatcher) {
        every { getSelectedCityUseCase() } returns flowOf(null)

        viewModel().uiState.test {
            var item = awaitItem()
            while (item.isLoading) {
                item = awaitItem()
            }
            assertTrue(item.hasNoSelectedCity)
            assertNull(item.city)
            assertNull(item.forecast)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `uiState transitions to success once the forecast loads`() = runTest(mainDispatcherRule.testDispatcher) {
        val forecast = sampleForecast()
        every { getSelectedCityUseCase() } returns flowOf(city)
        every { getWeatherForecastUseCase(city.latitude, city.longitude) } returns
            flowOf(Resource.Loading, Resource.Success(forecast))

        viewModel().uiState.test {
            var item = awaitItem()
            while (item.forecast == null && item.errorMessage == null) {
                item = awaitItem()
            }
            assertEquals(city, item.city)
            assertEquals(forecast, item.forecast)
            assertTrue(!item.isLoading)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `uiState surfaces an error message when the forecast fails`() = runTest(mainDispatcherRule.testDispatcher) {
        every { getSelectedCityUseCase() } returns flowOf(city)
        every { getWeatherForecastUseCase(city.latitude, city.longitude) } returns
            flowOf(Resource.Loading, Resource.Error("No internet connection."))

        viewModel().uiState.test {
            var item = awaitItem()
            while (item.errorMessage == null && item.forecast == null) {
                item = awaitItem()
            }
            assertEquals("No internet connection.", item.errorMessage)
            assertTrue(!item.isLoading)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `retry re-invokes the forecast use case for the current city`() = runTest(mainDispatcherRule.testDispatcher) {
        val forecast = sampleForecast()
        every { getSelectedCityUseCase() } returns flowOf(city)
        every { getWeatherForecastUseCase(city.latitude, city.longitude) } returns
            flowOf(Resource.Success(forecast))

        val vm = viewModel()

        vm.uiState.test {
            var item = awaitItem()
            while (item.forecast == null) {
                item = awaitItem()
            }
            assertEquals(forecast, item.forecast)

            // Retry re-triggers the fetch. The resulting state is identical (same forecast data),
            // so StateFlow - which only notifies collectors on a distinct new value - won't emit a
            // further item; what matters is that the use case itself was invoked again.
            vm.retry()

            cancelAndIgnoreRemainingEvents()
        }

        verify(atLeast = 2) { getWeatherForecastUseCase(city.latitude, city.longitude) }
    }
}
