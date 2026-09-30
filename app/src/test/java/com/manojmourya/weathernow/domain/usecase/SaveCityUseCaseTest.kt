package com.manojmourya.weathernow.domain.usecase

import com.manojmourya.weathernow.domain.model.City
import com.manojmourya.weathernow.domain.repository.CityRepository
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Test

class SaveCityUseCaseTest {

    private val repository = mockk<CityRepository>(relaxed = true)
    private val useCase = SaveCityUseCase(repository)

    @Test
    fun `invoke delegates to repository saveCity with the given city`() = runTest {
        val city = City(name = "Tokyo", country = "Japan", admin1 = null, latitude = 35.68, longitude = 139.69)

        useCase(city)

        coVerify(exactly = 1) { repository.saveCity(city) }
    }
}
