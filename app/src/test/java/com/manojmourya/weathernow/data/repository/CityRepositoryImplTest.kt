package com.manojmourya.weathernow.data.repository

import app.cash.turbine.test
import com.manojmourya.weathernow.data.local.SavedCityDao
import com.manojmourya.weathernow.data.local.WeatherNowDatabase
import com.manojmourya.weathernow.data.remote.dto.GeocodingResponseDto
import com.manojmourya.weathernow.data.remote.dto.GeocodingResultDto
import com.manojmourya.weathernow.data.remote.service.GeocodingApiService
import com.manojmourya.weathernow.domain.util.Resource
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException

class CityRepositoryImplTest {

    private val geocodingApiService = mockk<GeocodingApiService>()
    private val savedCityDao = mockk<SavedCityDao>(relaxed = true)
    private val database = mockk<WeatherNowDatabase>(relaxed = true)

    private lateinit var repository: CityRepositoryImpl

    @Before
    fun setUp() {
        repository = CityRepositoryImpl(geocodingApiService, savedCityDao, database)
    }

    @Test
    fun `searchCities emits loading then success with mapped cities`() = runTest {
        val dto = GeocodingResponseDto(
            results = listOf(
                GeocodingResultDto(
                    id = 1,
                    name = "London",
                    latitude = 51.5074,
                    longitude = -0.1278,
                    country = "United Kingdom",
                    admin1 = "England",
                ),
            ),
        )
        coEvery { geocodingApiService.searchCities(name = "London") } returns dto

        repository.searchCities("London").test {
            assertEquals(Resource.Loading, awaitItem())
            val success = awaitItem() as Resource.Success
            assertEquals(1, success.data.size)
            assertEquals("London", success.data[0].name)
            assertEquals("England, United Kingdom", success.data[0].subtitle)
            // Geocoding results are transient search hits, never a saved/local id.
            assertEquals(null, success.data[0].id)
            awaitComplete()
        }
    }

    @Test
    fun `searchCities trims the query before calling the api`() = runTest {
        coEvery { geocodingApiService.searchCities(name = "Paris") } returns GeocodingResponseDto(results = null)

        repository.searchCities("  Paris  ").test {
            assertEquals(Resource.Loading, awaitItem())
            val success = awaitItem() as Resource.Success
            assertTrue(success.data.isEmpty())
            awaitComplete()
        }
    }

    @Test
    fun `searchCities maps a null results list to an empty success list`() = runTest {
        coEvery { geocodingApiService.searchCities(name = "Nowhereville") } returns GeocodingResponseDto(results = null)

        repository.searchCities("Nowhereville").test {
            assertEquals(Resource.Loading, awaitItem())
            val success = awaitItem() as Resource.Success
            assertTrue(success.data.isEmpty())
            awaitComplete()
        }
    }

    @Test
    fun `searchCities emits a network error message on IOException`() = runTest {
        coEvery { geocodingApiService.searchCities(name = "X") } throws IOException("boom")

        repository.searchCities("X").test {
            assertEquals(Resource.Loading, awaitItem())
            val error = awaitItem() as Resource.Error
            assertTrue(error.message.contains("internet", ignoreCase = true))
            awaitComplete()
        }
    }

    @Test
    fun `searchCities emits a server error message with the http status code on HttpException`() = runTest {
        val response = Response.error<Any>(500, "".toResponseBody(null))
        coEvery { geocodingApiService.searchCities(name = "X") } throws HttpException(response)

        repository.searchCities("X").test {
            assertEquals(Resource.Loading, awaitItem())
            val error = awaitItem() as Resource.Error
            assertTrue(error.message.contains("500"))
            awaitComplete()
        }
    }

    @Test
    fun `searchCities emits an error message for a generic exception`() = runTest {
        coEvery { geocodingApiService.searchCities(name = "X") } throws RuntimeException("weird failure")

        repository.searchCities("X").test {
            assertEquals(Resource.Loading, awaitItem())
            val error = awaitItem() as Resource.Error
            assertTrue(error.message.isNotBlank())
            awaitComplete()
        }
    }
}
