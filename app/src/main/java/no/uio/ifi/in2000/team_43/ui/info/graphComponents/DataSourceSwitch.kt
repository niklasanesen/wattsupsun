package no.uio.ifi.in2000.team_43.ui.info.graphComponents

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun DataSourceSwitch(
    isMetSelected: Boolean,
    onToggle: (Boolean) -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "MET",
            style = MaterialTheme.typography.bodyMedium,
            color = if (isMetSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
        )

        Switch(
            checked = !isMetSelected,
            onCheckedChange = { onToggle(!it) },
            colors = SwitchDefaults.colors(
                checkedThumbColor = MaterialTheme.colorScheme.primary,
                checkedTrackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                uncheckedThumbColor = MaterialTheme.colorScheme.secondary,
                uncheckedTrackColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.4f)
            ),
            modifier = Modifier.padding(horizontal = 12.dp)
        )

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "PVGIS",
                style = MaterialTheme.typography.bodyMedium,
                color = if (!isMetSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
            )

            IconButton(onClick = { showDialog = true }) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = "Informasjon om PVGIS",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = {
                Text("Met VS PVGIS")
            },
            text = {
                Text(
                            "Met dataen tar hensyn til skydekke og snø.\n" +
                            "PVGIS tar ikke hensys til skydekke og snø"
                )
            },
            confirmButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("Lukk")
                }
            }
        )
    }
}
