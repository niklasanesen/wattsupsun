package no.uio.ifi.in2000.team_43.ui.info

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import no.uio.ifi.in2000.team_43.model.ctx.Ctx

@Composable
fun InfoScreen(
    ctx: Ctx,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier.fillMaxSize()) {
        Column {
            Text("coords: ${ctx.coords?.coordinates()}")
        }
    }
}
