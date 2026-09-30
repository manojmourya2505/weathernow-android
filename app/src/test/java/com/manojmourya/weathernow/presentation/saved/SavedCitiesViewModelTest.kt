package com.manojmourya.weathernow.presentation.saved

import app.cash.turbine.test
import com.manojmourya.weathernow.MainDispatcherRule
import com.manojmourya.weathernow.domain.model.City
import com.manojmourya.weathernow.domain.usecase.DeleteCityUseCase
import com.manojmourya.weathernow.domain.usecase.GetSavedCitiesUseCase
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class SavedCitiesViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val getSavedCitiesUseCase = mockk<GetSavedCitiesUseCase>()
    private val deleteCityUseCase = mockk<DeleteCityUseCase>(relaxed = true)

    private val city = City(id = 1, name = "Rome", country = "Italy", admin1 = null, latitude = 41.9, longitude = 12.5)

    private fun viewModel() = SavedCitiesViewModel(getSavedCitiesUseCase, deleteCityUseCase)

    @Test
    fun `uiState reflects the saved cities emitted by the use case`() = runTest(mainDispatcherRule.testDispatcher) {
        every { getSavedCitiesUseCase() } returns flowOf(listOf(city))

        viewModel().uiState.test {
            var item = awaitItem()
            while (item.isLoading) {
                item = awaitItem()
            }
            assertEquals(listOf(city), item.cities)
            assertTrue(!item.isEmpty)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `uiState reports isEmpty once loaded with no cities`() = runTest(mainDispatcherRule.testDispatcher) {
        every { getSavedCitiesUseCase() } returns flowOf(emptyList())

        viewModel().uiState.test {
            var item = awaitItem()
            while (item.isLoading) {
                item = awaitItem()
            }
            assertTrue(item.cities.isEmpty())
            assertTrue(item.isEmpty)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `deleteCity delegates to the delete use case`() = runTest(mainDispatcherRule.testDispatcher) {
        every { getSavedCitiesUseCase() } returns flowOf(emptyList())

        viewModel().deleteCity(city)

        coVerify(exactly = 1) { deleteCityUseCase(city) }
    }
}
