package no.uio.ifi.in2000.team_43

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import no.uio.ifi.in2000.team_43.ui.pages.ProfilePage
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33])
class ProfileScreenTest {
    @Rule
    @JvmField
    val composeTestRule = createComposeRule()

    @Test
    fun basicRenderTest() {
        // setup composable
        composeTestRule.setContent {
            ProfilePage()
        }

        // simply check if the correct text is rendered
        composeTestRule.onNodeWithText("Fornavn Etternavn", substring = true)
            .assertIsDisplayed()
    }
}
