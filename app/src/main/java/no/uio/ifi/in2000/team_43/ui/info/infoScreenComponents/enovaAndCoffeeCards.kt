package no.uio.ifi.in2000.team_43.ui.info.infoScreenComponents

import android.content.Intent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import no.uio.ifi.in2000.team_43.R
import java.text.NumberFormat
import java.util.Locale

// For testing av card til enova support:
@Composable
fun EnovaCard(supportAmount: Double) {
    val isZeroSupport = supportAmount <= 0
    val backgroundColor = if (isZeroSupport) Color(0xFFFFCDD2) else Color(0xFF304745)
    val textColor = if (isZeroSupport) Color(0xFFD32F2F) else Color(0xFFFFFFFF)
    val context = LocalContext.current
    val enovaInfoLink = "https://www.enova.no/privat/alle-energitiltak/solenergi/solcelleanlegg/?gad_source=1&gad_campaignid=16624425572&gbraid=0AAAAADLywwiacDJN5I7llRB-oOuYbvBmW&gclid=Cj0KCQjw5ubABhDIARIsAHMighb65GEWXVeJy3jCq8CYd0rcsR4L1n7G4GHf9wBGVB-ngIYtISqzJIIaAp3fEALw_wcB"

    // for better readability of the number
    val readableSupportAmount = NumberFormat.getInstance(Locale("no", "NO")).format(supportAmount.toInt())
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Text(
                    text = if (isZeroSupport) "Ingen beregnet Enova-støtte" else "Estimert Enova-støtte",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = textColor
                    ),
                    modifier = Modifier.padding(end = 8.dp)
                )
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = "Les mer om Enova-støtte",
                    tint = textColor,
                    modifier = Modifier
                        .clickable {
                            val intent = Intent(
                                Intent.ACTION_VIEW,
                                enovaInfoLink.toUri()
                            )
                            context.startActivity(intent)
                        }
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            if (isZeroSupport) {
                Text(
                    text = "Du mottar dessverre ingen støtte fra Enova",
                    style = MaterialTheme.typography.bodyMedium.copy(color = textColor)
                )
            } else {
                Text(
                    text = "$readableSupportAmount kr",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = textColor
                    )
                )
            }
        }
    }
}



// preview of the test card for enova:
@Preview(showBackground = true)
@Composable
fun EnovaSupportCardPreview_Positive() {
    MaterialTheme {
        EnovaCard(supportAmount = 15000.0)
    }
}

@Preview(showBackground = true)
@Composable
fun EnovaSupportCardPreview_Zero() {
    MaterialTheme {
        EnovaCard(supportAmount = 0.0)
    }
}


@Composable
fun CoffeeCard(production: Int) {
    val isZeroProduction = production <= 0
    val backgroundColor = Color(0xFFFFE0B2)  // Light orange
    val textColor = Color(0xFFBF360C)  // Dark red

    // for better readability of the number
    val readableProduction = NumberFormat.getInstance(Locale("no", "NO")).format(production)

    Card(
        modifier = Modifier
            .fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Using the VectorDrawable (XML) coffee cup icon
                Icon(
                    painter = painterResource(id = R.drawable.coffee_cup), // Using the .xml VectorDrawable
                    contentDescription = "Kaffekopp",
                    modifier = Modifier.size(70.dp).padding(end = 8.dp),
                    tint = textColor
                )
                // The text that describes production
                Text(
                    text = if (isZeroProduction) "Koker opp kaffe, et øyeblikk..." else "Solcellepanel hos deg kan koke opp $readableProduction kopper kaffe årlig!",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = textColor
                    )
                )
            }
        }
    }
}




@Preview(showBackground = true)
@Composable
fun NoProdPreviewCoffeeCard() {
    CoffeeCard(production = 0)
}

@Preview(showBackground = true)
@Composable
fun PreviewCoffeeCard() {
    CoffeeCard(production = 500)
}
