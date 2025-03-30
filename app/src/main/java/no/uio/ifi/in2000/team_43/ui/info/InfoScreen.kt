package no.uio.ifi.in2000.team_43.ui.info

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import no.uio.ifi.in2000.team_43.model.ctx.Ctx

@Composable
fun InfoScreen(
    ctx: Ctx,
    modifier: Modifier = Modifier,
) {
    /*
    Box(modifier = modifier
        .fillMaxSize()
        .padding(20.dp)
    ) {
        Column {
            Text("coords: ${ctx.coords?.coordinates()}")
            Spacer(modifier = Modifier.height(16.dp))
            Text("Inputverdier av takflate: \nLengde: ${ctx.input?.length} \nBredde: ${ctx.input?.width} \nVinkel: ${ctx.input?.angle}° \nRetning: ${ctx.input?.direction}°")
        }
    }

     */
    LazyColumn(
        modifier = modifier
            .padding(20.dp)
            .wrapContentSize(Alignment.TopStart)
            .fillMaxWidth(),
        verticalArrangement = Arrangement.Top
        // horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            Text("coords: ${ctx.coords?.coordinates()}")
            Spacer(modifier = Modifier.height(16.dp))
            Text("Inputverdier av takflate: \nLengde: ${ctx.input?.length} \nBredde: ${ctx.input?.width} \nVinkel: ${ctx.input?.angle}° \nRetning: ${ctx.input?.direction}°")


            // Bare for å teste om det går å scrolle
            // Text("H\nH\nH\nH\nH\nH\nH\nH\nH\nH\nH\nH\nH\nH\nH\nH\nH\nH\nH\nH\nH\nH\nH\nH\nH\nH\nH\nH\nH\nH\nH\nH\nH\nH\nH\nH\nH\nH\nH\nH\nH\nH\nH\nH\nH\nH\nH\nH\nH\nH\nH\nH\nH\nH\nH\nH\nH\nH\nH\n")
        }
    }
}
