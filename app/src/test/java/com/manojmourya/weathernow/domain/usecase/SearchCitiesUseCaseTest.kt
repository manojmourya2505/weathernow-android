package com.manojmourya.weathernow.domain.usecase

import app.cash.turbine.test
import com.manojmourya.weathernow.domain.model.City
import com.manojmourya.weathernow.domain.repository.CityRepository
import com.manojmourya.weathernow.domain.util.Resource
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class SearchCitiesUseCaseTest {

    private val repository = mockk<CityRepository>()
    private val useCase = SearchCitiesUseCase(repository)

    @Test
    fun `invoke delegates to repository searchCities and returns its flow unchanged`() = runTest {
        val city = City(name = "Paris", country = "France", admin1 = null, latitude = 48.85, longitude = 2.35)
        every { repository.searchCities("Paris") } returns flowOf(Resource.Success(listOf(city)))

        useCase("Paris").test {
            val result = awaitItem() as Resource.Success
            assertEquals(listOf(city), result.data)
            awaitComplete()
        }

        verify(exactly = 1) { repository.searchCities("Paris") }
    }

    @Test
    fun `invoke propagates errors from the repository`() = runTest {
        every { repository.searchCities("??") } returns flowOf(Resource.Error("Something went wrong."))

        useCase("??").test {
            val result = awaitItem() as Resource.Error
            assertEquals("Something went wrong.", result.message)
            awaitComplete()
        }
    }
}
