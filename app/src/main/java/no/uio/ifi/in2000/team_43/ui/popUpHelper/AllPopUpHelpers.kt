package no.uio.ifi.in2000.team_43.ui.popUpHelper

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.AlertDialog
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
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
        text = { Text("IDK. 🤷‍♂️") },
        confirmButton = {
            TextButton(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(backgroundColor = Color.Transparent, contentColor = Cerulean)
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
        text = { Text("IDK. 🤷‍♂️") },
        confirmButton = {
            TextButton(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(backgroundColor = Color.Transparent, contentColor = Cerulean)
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
        text = { Text("IDK. 🤷‍♂️") },
        confirmButton = {
            TextButton(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(backgroundColor = Color.Transparent, contentColor = Cerulean)
            ) {
                Text("Lukk")
            }
        }
    )
}


@Composable
fun HelpOnSateliteScreen(onDismiss: () -> Unit) {
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
        text = { Text("IDK. 🤷‍♂️") },
        confirmButton = {
            TextButton(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(backgroundColor = Color.Transparent, contentColor = Cerulean)
            ) {
                Text("Lukk")
            }
        }
    )
}

