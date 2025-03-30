package no.uio.ifi.in2000.team_43.ui.input

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import no.uio.ifi.in2000.team_43.model.ctx.Ctx

@Composable
fun InputScreen(
    ctx: Ctx,
    onNextBtnClicked: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier.fillMaxSize()) {
        Column {
            Text("coords: ${ctx.coords?.coordinates()}")
            Button(
                onClick = { onNextBtnClicked() },
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowForward,
                    contentDescription = "Navigate to Info"
                )
            }
        }
    }
}
