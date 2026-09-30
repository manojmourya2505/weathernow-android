package com.manojmourya.weathernow.presentation.search

import app.cash.turbine.test
import com.manojmourya.weathernow.MainDispatcherRule
import com.manojmourya.weathernow.domain.model.City
import com.manojmourya.weathernow.domain.usecase.SearchCitiesUseCase
import com.manojmourya.weathernow.domain.util.Resource
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SearchViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val searchCitiesUseCase = mockk<SearchCitiesUseCase>()

    private val city = City(name = "Paris", country = "France", admin1 = null, latitude = 48.85, longitude = 2.35)

    private fun viewModel() = SearchViewModel(searchCitiesUseCase)

    @Test
    fun `onQueryChanged updates query immediately without waiting for debounce`() =
        runTest(mainDispatcherRule.testDispatcher) {
            every { searchCitiesUseCase("Par") } returns flowOf(Resource.Loading)

            val vm = viewModel()
            vm.onQueryChanged("Par")

            assertEquals("Par", vm.uiState.value.query)
        }

    @Test
    fun `search success populates results after debounce settles`() = runTest(mainDispatcherRule.testDispatcher) {
        every { searchCitiesUseCase("Paris") } returns flowOf(Resource.Loading, Resource.Success(listOf(city)))

        val vm = viewModel()
        vm.onQueryChanged("Paris")
        advanceUntilIdle()

        assertEquals(listOf(city), vm.uiState.value.results)
        assertTrue(!vm.uiState.value.isLoading)
        assertEquals(null, vm.uiState.value.errorMessage)
    }

    @Test
    fun `blank query resets to an empty result set without calling the use case`() =
        runTest(mainDispatcherRule.testDispatcher) {
            val vm = viewModel()

            vm.onQueryChanged("")
            advanceUntilIdle()

            assertTrue(vm.uiState.value.results.isEmpty())
            assertTrue(vm.uiState.value.showEmptyPrompt)
        }

    @Test
    fun `search failure surfaces the error message and clears loading`() =
        runTest(mainDispatcherRule.testDispatcher) {
            every { searchCitiesUseCase("Nowhere") } returns flowOf(Resource.Error("Server error (500): failed"))

            val vm = viewModel()
            vm.onQueryChanged("Nowhere")
            advanceUntilIdle()

            assertEquals("Server error (500): failed", vm.uiState.value.errorMessage)
            assertTrue(!vm.uiState.value.isLoading)
        }

    @Test
    fun `uiState flow observed via turbine reaches a success state`() = runTest(mainDispatcherRule.testDispatcher) {
        every { searchCitiesUseCase("Paris") } returns flowOf(Resource.Loading, Resource.Success(listOf(city)))

        val vm = viewModel()

        vm.uiState.test {
            assertEquals(SearchUiState(), awaitItem())

            vm.onQueryChanged("Paris")
            var item = awaitItem()
            while (item.results.isEmpty() && item.errorMessage == null) {
                item = awaitItem()
            }
            assertEquals(listOf(city), item.results)
            cancelAndIgnoreRemainingEvents()
        }
    }
}
