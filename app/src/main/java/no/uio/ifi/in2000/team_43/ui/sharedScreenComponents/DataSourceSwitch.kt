package no.uio.ifi.in2000.team_43.ui.sharedScreenComponents

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp


@Composable
fun DataSourceSwitch(
    isMetSelected: Boolean,
    onToggle: (Boolean) -> Unit
) {
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
                uncheckedThumbColor = MaterialTheme.colorScheme.secondary
            ),
            modifier = Modifier.padding(horizontal = 8.dp)
        )

        Text(
            text = "PVGIS",
            style = MaterialTheme.typography.bodyMedium,
            color = if (!isMetSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
