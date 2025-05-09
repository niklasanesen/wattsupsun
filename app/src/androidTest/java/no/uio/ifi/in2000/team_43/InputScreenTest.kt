package no.uio.ifi.in2000.team_43

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.ui.test.filter
import androidx.compose.ui.test.filterToOne
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onFirst
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import junit.framework.TestCase
import no.uio.ifi.in2000.team_43.model.ctx.Ctx
import no.uio.ifi.in2000.team_43.model.input.Input
import no.uio.ifi.in2000.team_43.model.panel.SunPanel
import no.uio.ifi.in2000.team_43.ui.input.InputScreen
import no.uio.ifi.in2000.team_43.ui.met.MetViewModel
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
            Ctx(coords = null, areal = null, address = null, input = null, stromdistrikt = null)

        val fakeSelectedPanel = SunPanel("Sunwind 405W", pris = 3000, effekt = 405, bildeResId = R.drawable.sunwind405w_panel)

        composeTestRule.setContent {
            InputScreen(
                ctx = fakeCtx,
                metViewModel = null,
                onNextBtnClicked = { input -> receivedInput = input },
            )

        }

        // Enter valid data
        composeTestRule.onNodeWithText("Lengde").performTextInput("12")
        composeTestRule.onNodeWithText("Bredde").performTextInput("8")
        composeTestRule.onNodeWithText("Takvinkel i °").performTextInput("30")
        composeTestRule.onNodeWithText("Sunwind 405W").performClick()

        // Click "Neste"
        composeTestRule.onNodeWithText("Neste").performClick()

        // Assert the callback was triggered with correct input
        composeTestRule.runOnIdle {
            TestCase.assertEquals(Input(12, 8, 30, 0, fakeSelectedPanel), receivedInput) // assuming 0° is "Nord"
        }
    }
}