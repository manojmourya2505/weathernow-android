package com.manojmourya.weathernow.domain.usecase

import com.manojmourya.weathernow.domain.model.City
import com.manojmourya.weathernow.domain.repository.CityRepository
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Test

class SelectCityUseCaseTest {

    private val repository = mockk<CityRepository>(relaxed = true)
    private val useCase = SelectCityUseCase(repository)

    @Test
    fun `invoke delegates to repository selectCity with the given city`() = runTest {
        val city = City(name = "Berlin", country = "Germany", admin1 = null, latitude = 52.52, longitude = 13.40)

        useCase(city)

        coVerify(exactly = 1) { repository.selectCity(city) }
    }
}
