package no.uio.ifi.in2000.team_43.ui.info.infoScreenComponents

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun AddressNamePopup(
    showDialog: Boolean,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    if (showDialog) {
        var input by remember { mutableStateOf("") }
        val maxLength = 21
        val showLimitWarning = input.length >= maxLength

        AlertDialog(
            onDismissRequest = { onDismiss() },
            title = { Text("Navngi adresse") },
            text = {
                Column {
                    Text("Skriv inn et navn for denne adressen")
                    Spacer(modifier = Modifier.height(8.dp))
                    TextField(
                        value = input,
                        onValueChange = { newValue ->
                            // Filter out non-alphanumeric + space and limit to maxLength
                            val filtered = newValue.filter { it.isLetterOrDigit() || it.isWhitespace() }
                            if (filtered.length <= maxLength) {
                                input = filtered
                            }
                        },
                        placeholder = { Text("F.eks. Hjem, Hytta, Jobb...") },
                        singleLine = true
                    )
                    if (showLimitWarning) {
                        Text(
                            text = "Maksimalt 21 tegn tillatt",
                            color = Color.Red,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
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


