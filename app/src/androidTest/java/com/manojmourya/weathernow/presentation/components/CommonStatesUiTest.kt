package com.manojmourya.weathernow.presentation.components

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.manojmourya.weathernow.presentation.theme.WeatherNowTheme
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

/**
 * Compose UI tests for the shared empty/error state composables used by the Home and
 * Search screens (see [com.manojmourya.weathernow.presentation.home.HomeScreen] and
 * [com.manojmourya.weathernow.presentation.search.SearchScreen]). These are exercised directly,
 * with fake in-test state and no Hilt/ViewModel wiring, to keep the test simple and reliable.
 */
class CommonStatesUiTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun emptyState_showsTitleAndMessage() {
        composeTestRule.setContent {
            WeatherNowTheme {
                EmptyState(
                    title = "No saved cities yet",
                    message = "Cities you add from search will show up here.",
                )
            }
        }

        composeTestRule.onNodeWithText("No saved cities yet").assertIsDisplayed()
        composeTestRule.onNodeWithText("Cities you add from search will show up here.").assertIsDisplayed()
    }

    @Test
    fun errorState_showsMessageAndInvokesRetryCallbackOnClick() {
        var retried = false

        composeTestRule.setContent {
            WeatherNowTheme {
                ErrorState(
                    message = "Something went wrong. Please try again.",
                    onRetry = { retried = true },
                )
            }
        }

        composeTestRule.onNodeWithText("Something went wrong. Please try again.").assertIsDisplayed()
        composeTestRule.onNodeWithText("Retry").assertIsDisplayed()

        composeTestRule.onNodeWithText("Retry").performClick()

        assertTrue(retried)
    }
}
