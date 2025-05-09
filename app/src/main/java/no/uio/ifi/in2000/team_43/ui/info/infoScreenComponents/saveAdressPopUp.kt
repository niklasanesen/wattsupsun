package no.uio.ifi.in2000.team_43.ui.info.infoScreenComponents

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun AddressNamePopup(
    showDialog: Boolean,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var input by remember { mutableStateOf("") }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { onDismiss() },
            title = { Text("Navngi adresse") },
            text = {
                Column {
                    Text("Skriv inn et navn for denne adressen")
                    Spacer(modifier = Modifier.height(8.dp))
                    TextField(
                        value = input,
                        onValueChange = { input = it },
                        placeholder = { Text("F.eks. Hjem, Hytta, Jobb...") },
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    if (input.isNotBlank()) {
                        onConfirm(input.trim())
                        onDismiss()
                    }
                }) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = onDismiss) {
                    Text("Avbryt")
                }
            }
        )
    }
}


