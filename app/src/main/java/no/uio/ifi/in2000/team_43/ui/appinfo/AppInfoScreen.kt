package no.uio.ifi.in2000.team_43.ui.appinfo

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.paddingFromBaseline
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import no.uio.ifi.in2000.team_43.R

@Composable
fun AppInfoScreen(
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .padding(28.dp)
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        item {
            Image(
                painter = painterResource(R.drawable.logo),
                contentDescription = "App Icon",
                modifier = Modifier
                    .padding(horizontal = 40.dp)
            )

            Spacer(Modifier.height(12.dp))

            Text(
                text = "Velkommen til WattsUpSun!",
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(vertical = 12.dp)
                // modifier = Modifier.paddingFromBaseline(8.dp)
            )
            Text(
                text = "Denne appen gir deg relevant og nyttig informasjon når det kommer til å installere solcellepaneler på eiendommen din. Målet med appen er at du enklere skal kunne vurdere nytten ved solcelleplaneler og om dette kan være noe for deg:) \n\nDu vil også kunne bruke appen videre til å få en oversikt over strømproduksjon og annet nyttig!",
                modifier = Modifier.padding(vertical = 8.dp)
            )

            Spacer(Modifier.height(32.dp))

            /*
            AsyncImage(
                model = "https://images.unsplash.com/photo-1563761141-38f17aad6d44?q=80&w=3134&auto=format&fit=crop&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D",
                contentDescription = "Sun",
                modifier = Modifier.clip(RoundedCornerShape(16.dp))
            )
            */
        }
    }
}