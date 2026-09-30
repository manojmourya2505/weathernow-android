package com.manojmourya.weathernow.data.repository

import app.cash.turbine.test
import com.manojmourya.weathernow.data.remote.dto.CurrentDto
import com.manojmourya.weathernow.data.remote.dto.DailyDto
import com.manojmourya.weathernow.data.remote.dto.ForecastResponseDto
import com.manojmourya.weathernow.data.remote.dto.HourlyDto
import com.manojmourya.weathernow.data.remote.service.ForecastApiService
import com.manojmourya.weathernow.domain.util.Resource
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException

class WeatherRepositoryImplTest {

    private val forecastApiService = mockk<ForecastApiService>()
    private val repository = WeatherRepositoryImpl(forecastApiService)

    private fun sampleDto() = ForecastResponseDto(
        latitude = 51.5,
        longitude = -0.1,
        timezone = "Europe/London",
        current = CurrentDto(
            time = "2026-09-30T12:00",
            temperature2m = 18.5,
            relativeHumidity2m = 60,
            weatherCode = 2,
            windSpeed10m = 12.0,
            apparentTemperature = 17.0,
        ),
        hourly = HourlyDto(
            time = listOf("2026-09-30T12:00", "2026-09-30T13:00"),
            temperature2m = listOf(18.5, 19.0),
            weatherCode = listOf(2, 2),
        ),
        daily = DailyDto(
            time = listOf("2026-09-30"),
            weatherCode = listOf(2),
            temperature2mMax = listOf(21.0),
            temperature2mMin = listOf(14.0),
            precipitationProbabilityMax = listOf(10),
        ),
    )

    @Test
    fun `getWeatherForecast emits loading then success with the mapped forecast`() = runTest {
        coEvery { forecastApiService.getForecast(latitude = 51.5, longitude = -0.1) } returns sampleDto()

        repository.getWeatherForecast(51.5, -0.1).test {
            assertEquals(Resource.Loading, awaitItem())
            val success = awaitItem() as Resource.Success
            assertEquals("Europe/London", success.data.timezone)
            assertEquals(18.5, success.data.current.temperature, 0.0)
            assertEquals(17.0, success.data.current.feelsLike, 0.0)
            assertEquals(1, success.data.daily.size)
            assertEquals(2, success.data.hourly.size)
            awaitComplete()
        }
    }

    @Test
    fun `getWeatherForecast emits a network error message on IOException`() = runTest {
        coEvery { forecastApiService.getForecast(latitude = any(), longitude = any()) } throws IOException("no network")

        repository.getWeatherForecast(1.0, 2.0).test {
            assertEquals(Resource.Loading, awaitItem())
            val error = awaitItem() as Resource.Error
            assertTrue(error.message.contains("internet", ignoreCase = true))
            awaitComplete()
        }
    }

    @Test
    fun `getWeatherForecast emits a server error message with the http status code on HttpException`() = runTest {
        coEvery { forecastApiService.getForecast(latitude = any(), longitude = any()) } throws
            HttpException(Response.error<Any>(404, "".toResponseBody(null)))

        repository.getWeatherForecast(1.0, 2.0).test {
            assertEquals(Resource.Loading, awaitItem())
            val error = awaitItem() as Resource.Error
            assertTrue(error.message.contains("404"))
            awaitComplete()
        }
    }

    @Test
    fun `getWeatherForecast emits an error message for a generic exception`() = runTest {
        coEvery { forecastApiService.getForecast(latitude = any(), longitude = any()) } throws
            RuntimeException("weird failure")

        repository.getWeatherForecast(1.0, 2.0).test {
            assertEquals(Resource.Loading, awaitItem())
            val error = awaitItem() as Resource.Error
            assertTrue(error.message.isNotBlank())
            awaitComplete()
        }
    }
}
