package no.uio.ifi.in2000.team_43.ui.sharedScreenComponents

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import no.uio.ifi.in2000.team_43.ui.theme.Purple40

@Composable
fun WelcomeButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    FloatingActionButton(
        onClick = onClick,
        containerColor = Purple40,
        contentColor = Color.White,
        modifier = modifier.size(56.dp)
    ) {
        Icon(
            imageVector = Icons.Default.Home,
            contentDescription = "Gå til velkomstskjerm"
        )
    }
}