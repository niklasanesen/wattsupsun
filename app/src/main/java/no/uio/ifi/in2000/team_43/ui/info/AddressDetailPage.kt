package no.uio.ifi.in2000.team_43.ui.info

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import no.uio.ifi.in2000.team_43.model.input.LagreAdresse
import no.uio.ifi.in2000.team_43.ui.pages.TopAppBarSaved


@Composable
fun AddressDetailPage(address: LagreAdresse) {
    Scaffold(
        topBar = { TopAppBarSaved() }
    ) { padding ->
        Column(modifier = Modifier
            .padding(padding)
            .padding(16.dp)) {
            Text("Detaljer for valgt adresse:")
            Text("Breddegrad: ${address.latitude}")
            Text("Lengdegrad: ${address.longitude}")
            Text("Høyde: ${address.height}")
            Text("Bredde: ${address.width}")
            Text("Vinkel: ${address.angle}")
            Text("Retning: ${address.direction}")
        }
    }
}
