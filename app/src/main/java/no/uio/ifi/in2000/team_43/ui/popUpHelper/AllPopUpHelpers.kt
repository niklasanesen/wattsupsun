package no.uio.ifi.in2000.team_43.ui.popUpHelper

import androidx.compose.material.AlertDialog
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable

@Composable
fun HelpOnMapScreen(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Hjelp med kart") },
        text = { Text("IDK. 🤷‍♂️") },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Lukk")
            }
        }
    )
}


@Composable
fun HelpOnInputScreen(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Hjelp med input") },
        text = { Text("IDK. 🤷‍♂️") },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Lukk")
            }
        }
    )
}


@Composable
fun HelpOnInfoScreen(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Hjelp") },
        text = { Text("IDK. 🤷‍♂️") },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Lukk")
            }
        }
    )
}


@Composable
fun HelpOnSateliteScreen(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Hjelp") },
        text = { Text("IDK. 🤷‍♂️") },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Lukk")
            }
        }
    )
}

