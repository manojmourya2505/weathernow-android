package com.manojmourya.weathernow.domain.usecase

import com.manojmourya.weathernow.domain.model.City
import com.manojmourya.weathernow.domain.repository.CityRepository
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Test

class DeleteCityUseCaseTest {

    private val repository = mockk<CityRepository>(relaxed = true)
    private val useCase = DeleteCityUseCase(repository)

    @Test
    fun `invoke delegates to repository deleteCity with the given city`() = runTest {
        val city = City(id = 7, name = "Oslo", country = "Norway", admin1 = null, latitude = 59.91, longitude = 10.75)

        useCase(city)

        coVerify(exactly = 1) { repository.deleteCity(city) }
    }
}
