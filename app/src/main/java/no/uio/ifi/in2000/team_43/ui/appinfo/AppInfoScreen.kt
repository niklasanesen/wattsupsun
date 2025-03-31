package no.uio.ifi.in2000.team_43.ui.appinfo

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.paddingFromBaseline
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage

@Composable
fun AppInfoScreen(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .padding(28.dp)
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        // Spacer(Modifier.height(12.dp))
        Text(
            text = "Velkommen til WattsUpSun!",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(20.dp)
            // modifier = Modifier.paddingFromBaseline(8.dp)
        )
        Text(
            text = "Denne appen gir deg all relevant og nyttig informasjon når det kommer til å installere solcellepaneler på eiendommen din. Målet er at du enklere skal kunne vurdere nytten ved solcelleplaneler og om dette -og eventuelt hvilke som- er noe for deg:) "
        )

        Spacer(Modifier.height(32.dp))

        AsyncImage(
            model = "https://images.unsplash.com/photo-1563761141-38f17aad6d44?q=80&w=3134&auto=format&fit=crop&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D",
            contentDescription = "Sun",
            modifier = Modifier.clip(RoundedCornerShape(16.dp))
        )
    }
}