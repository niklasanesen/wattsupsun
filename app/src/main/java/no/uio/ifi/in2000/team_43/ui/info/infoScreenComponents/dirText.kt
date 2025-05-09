package no.uio.ifi.in2000.team_43.ui.info.infoScreenComponents

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Card for tips basert på retning:
@Composable
fun DirectionCard(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = Color(0xFFE3F2FD), // Light blue color
    textcolor: Color = Color(0xFF1976D2) // Darker blue text
) {
    Card(
        modifier = modifier
            .padding(horizontal = 32.dp)
            .fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        backgroundColor = color,
        // colors = CardDefaults.cardColors(containerColor = color)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = text,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = textcolor
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DirectionCardPreview() {
    MaterialTheme {
        DirectionCard("Østvendte paneler gir mest strømproduksjon tidlig på dagen.")
    }
}

@Composable
fun NorthDirectionCard(text: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        backgroundColor = Color(0xFFFFCDD2) // Lys rød bakgrunn
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = text,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = Color(0xFFB71C1C) // Mørkere rød tekst
                )
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun NorthDirectionCardPreview() {
    MaterialTheme {
        NorthDirectionCard("Nord: Basert på retningen du har valgt lønner det seg ikke å ha solcellepaneler.")
    }
}



