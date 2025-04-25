package no.uio.ifi.in2000.team_43.ui.pages

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import no.uio.ifi.in2000.team_43.R
import no.uio.ifi.in2000.team_43.ui.theme.Purple40

@Composable
fun ProfilePage(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.cat),
            contentDescription = "profile",
            contentScale = ContentScale.FillBounds,
            modifier = Modifier
                .width(250.dp)
                .height(250.dp)
                .clip(CircleShape)
        )
        Spacer(modifier = Modifier.padding(16.dp))
        Text(
            text = "Fornavn Etternavn",
            fontSize = 28.sp
        )
        Spacer(modifier = Modifier.padding(16.dp))
        Button(
            onClick = {},
            enabled = true,
            colors = ButtonDefaults.buttonColors(
                containerColor = Purple40
            ),
            modifier = Modifier
                .fillMaxWidth(0.7f)
                .height(48.dp)
        ) {
            Text("Instillinger")
        }
        Spacer(modifier = Modifier.padding(8.dp))
        Button(
            onClick = {},
            enabled = true,
            colors = ButtonDefaults.buttonColors(
                containerColor = Purple40
            ),
            modifier = Modifier
                .fillMaxWidth(0.7f)
                .height(48.dp)
        ) {
            Text("Personvernerklæring")
        }
    }
}