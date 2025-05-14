package no.uio.ifi.in2000.team_43.ui.popUpHelper

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import no.uio.ifi.in2000.team_43.ui.theme.Cerulean
import no.uio.ifi.in2000.team_43.ui.theme.DarkSlate

@Composable
fun HelpOnMapScreen(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(16.dp),
        title = { Text(
            text = "Om denne siden",
            style = MaterialTheme.typography.titleLarge.copy(
                color = DarkSlate,
                fontSize = 18.sp
            ),
            modifier = Modifier.padding(bottom = 16.dp)
        ) },
        text = { Text("Trykk på søkefeltet nederst på kart-skjermen og søk opp din adresse.\n" +
        "Deretter trykk på bekreft adresse hvis det er den korrekte, ellers kan du avbryte og søke på nytt.") },
        confirmButton = {
            TextButton(
                onClick = onDismiss,
                colors = ButtonDefaults.textButtonColors(
                    containerColor = Color.Transparent,
                    contentColor = Cerulean
                )
            ) {
                Text("Lukk")
            }
        }
    )
}


@Composable
fun HelpOnInputScreen(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(16.dp),
        title = { Text(
            text = "Om denne siden",
            style = MaterialTheme.typography.titleLarge.copy(
                color = DarkSlate,
                fontSize = 18.sp
            ),
            modifier = Modifier.padding(bottom = 16.dp)
        ) },
        text = { Text("Her fyller du inn takets lengde, bredde, vinkel og retning.\n" +
                "Velg deretter ønsket type solcellepanel og klikk «Neste» ") },
        confirmButton = {
            TextButton(
                onClick = onDismiss,
                colors = ButtonDefaults.textButtonColors(
                    containerColor = Color.Transparent,
                    contentColor = Cerulean
                )
            ) {
                Text("Lukk")
            }
        }
    )
}


@Composable
fun HelpOnInfoScreen(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(16.dp),
        title = { Text(
            text = "Om denne siden",
            style = MaterialTheme.typography.titleLarge.copy(
                color = DarkSlate,
                fontSize = 18.sp
            ),
            modifier = Modifier.padding(bottom = 16.dp)
        ) },
        text = { Text("Her får du oversikt over taket ditt og beregnet solenergiproduksjon, basert på takdata, vær- og strømforhold." +
                "\n\nSlik bruker du grafen nederst:" +
                "\nBruk bryteren for å bytte mellom PVGIS- og MET-data" +
                "\nKlikk på grafen for å se energiproduksjonen for en valgt måned" +
                "\n\nHelt nederst finner du en knapp for å lagre adressen.") },
        confirmButton = {
            TextButton(
                onClick = onDismiss,
                colors = ButtonDefaults.textButtonColors(
                    containerColor = Color.Transparent,
                    contentColor = Cerulean
                )
            ) {
                Text("Lukk")
            }
        }
    )
}


@Composable
fun HelpOnSatelliteScreen(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(16.dp),
        title = { Text(
            text = "Om denne siden",
            style = MaterialTheme.typography.titleLarge.copy(
                color = DarkSlate,
                fontSize = 18.sp
            ),
            modifier = Modifier.padding(bottom = 16.dp)
        ) },
        text = { Text("Trinnvis forklaring: \n" +
                "NB: knapper vil dukke opp etter hvert som ulike operasjoner blir gjort\n" +
                "1. Trykk på kartet for å legge til punkter\n" +
                "2. Hvis du setter et punkt feil, benytt 'Angre punkt' knappen\n" +
                "3. Etter 4 punkter er lagt til, vil en takflate opprettes\n" +
                "4. Hvis takflaten er feil kan du trykke på 'Slett takflate' for å fjerne takflate\n" +
                "5. Hvis alt stemmer, trykk på 'Neste for å gå videre'")},
        confirmButton = {
            TextButton(
                onClick = onDismiss,
                colors = ButtonDefaults.textButtonColors(
                    containerColor = Color.Transparent,
                    contentColor = Cerulean
                )
            ) {
                Text("Lukk")
            }
        }
    )
}