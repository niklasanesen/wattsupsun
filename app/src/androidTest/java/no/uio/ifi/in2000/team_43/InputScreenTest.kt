package no.uio.ifi.in2000.team_43

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import junit.framework.TestCase
import no.uio.ifi.in2000.team_43.model.ctx.Ctx
import no.uio.ifi.in2000.team_43.model.input.Input
import no.uio.ifi.in2000.team_43.model.panel.SunPanel
import no.uio.ifi.in2000.team_43.ui.input.InputScreen
import org.junit.Rule
import org.junit.Test


@ExperimentalMaterial3Api
class InputScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun testInputScreen() {

        var receivedInput: Input? = null

        val fakeCtx =
            Ctx(coordinates = null, area = null, address = null, input = null, electricityDistrict = null)

        val fakeSelectedPanel = SunPanel("Sunwind 405W", price = 3000, effect = 405, pictureResId = R.drawable.sunwind405w_panel, len = 1.72, width = 1.12)

        composeTestRule.setContent {
            InputScreen(
                ctx = fakeCtx,
                onNextBtnClicked = { input -> receivedInput = input },
            )

        }

        // Enter valid data
        composeTestRule.onNodeWithText("Lengde i m").performTextInput("12")
        composeTestRule.onNodeWithText("Bredde i m").performTextInput("8")
        composeTestRule.onNodeWithText("Takvinkel i °").performTextInput("30")
        composeTestRule.onNodeWithText("Sunwind 405W").performClick()

        // Click "Neste"
        composeTestRule.onNodeWithText("Neste").performClick()

        // Assert the callback was triggered with correct input
        composeTestRule.runOnIdle {
            TestCase.assertEquals(Input(12, 8, 30, 0, fakeSelectedPanel), receivedInput)
        }
    }
}