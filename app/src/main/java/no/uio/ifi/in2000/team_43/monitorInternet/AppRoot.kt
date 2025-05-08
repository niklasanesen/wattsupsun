package no.uio.ifi.in2000.team_43.monitorInternet

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box


import androidx.compose.foundation.layout.Column

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.AlertDialog
import androidx.compose.material.SnackbarDuration
import androidx.compose.material.SnackbarHostState
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview


import androidx.compose.ui.unit.dp
import no.uio.ifi.in2000.team_43.R

@Composable
fun AppRoot(
    networkStatus: NetworkStatus,
    content: @Composable () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        // Innholdet vises bak varselet
        content()

        // Vis varsel øverst dersom det ikke er internett
        if (networkStatus != NetworkStatus.Available) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.Red)
                    .padding(35.dp)
                    .align(Alignment.TopCenter) // Nå er dette inni en BoxScope
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        painter = painterResource(id = R.drawable.wifi_off),
                        contentDescription = "Ingen internett",
                        tint = Color.White,
                        modifier = Modifier.size(60.dp).padding(end = 15.dp)

                    )
                    Text(
                        text = "Ingen internettforbindelse. Denne appen krever internett for å fungere optimalt.",
                        color = Color.White,
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }
        }
    }
}


// SJEKKE UI
@Composable
fun NoInternetBannerPreviewWrapper() {
    Box(modifier = Modifier.fillMaxSize()) {
        // Legg inn herifra
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.Red)
                .padding(35.dp)
                .align(Alignment.TopCenter) // Nå er dette inni en BoxScope
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    painter = painterResource(id = R.drawable.wifi_off),
                    contentDescription = "Ingen internett",
                    tint = Color.White,
                    modifier = Modifier.size(60.dp).padding(end = 15.dp)

                )
                Text(
                    text = "Ingen internettforbindelse. Denne appen krever internett for å fungere optimalt.",
                    color = Color.White,
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun NoInternetBannerPreview() {
    NoInternetBannerPreviewWrapper()
}



