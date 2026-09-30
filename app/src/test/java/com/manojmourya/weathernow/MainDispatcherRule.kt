package com.manojmourya.weathernow

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.rules.TestWatcher
import org.junit.runner.Description

/**
 * JUnit4 rule that installs a [TestDispatcher] as [Dispatchers.Main] for the duration of a test,
 * so ViewModel code that launches on `viewModelScope` (which defaults to `Dispatchers.Main.immediate`)
 * runs on a controllable, deterministic dispatcher.
 *
 * Pass [testDispatcher] into `runTest(...)` in each test so the coroutine scheduler driving
 * `Dispatchers.Main` is the same one `runTest` advances - otherwise `delay`-based code (e.g.
 * debounce) will never be advanced by `advanceUntilIdle()`/`advanceTimeBy()`.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class MainDispatcherRule(
    val testDispatcher: TestDispatcher = UnconfinedTestDispatcher(),
) : TestWatcher() {

    override fun starting(description: Description) {
        Dispatchers.setMain(testDispatcher)
    }

    override fun finished(description: Description) {
        Dispatchers.resetMain()
    }
}
