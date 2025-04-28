package no.uio.ifi.in2000.team_43.ui.pages

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import no.uio.ifi.in2000.team_43.data.DataStoreManager
import no.uio.ifi.in2000.team_43.model.input.LagreAdresse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SavedPage(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val dataStoreManager = remember { DataStoreManager(context) }

    // Hente listen som State
    val addresses by dataStoreManager.addresses.collectAsState(initial = emptyList())

    Scaffold(
        modifier = modifier,
        topBar = { TopAppBarSaved() },
        bottomBar = {
            // Legg til en knapp nederst som sletter alle adresser
            Button(
                onClick = {
                    CoroutineScope(Dispatchers.IO).launch {
                        dataStoreManager.deleteAllAddresses()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
            ) {
                Text("Slett alle adresser", color = MaterialTheme.colorScheme.onError)
            }
        }
    ) { innerPadding ->

        if (addresses.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "Ingen adresser lagret enda.")
            }
        } else {
            LazyColumn(
                modifier = modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 20.dp)
                    .padding(innerPadding),
                verticalArrangement = Arrangement.spacedBy(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                items(addresses) { address ->
                    AddressCard(address = address)
                }
            }
        }
    }
}

@Composable
fun AddressCard(address: LagreAdresse) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(150.dp),

        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "Breddegrad: ${address.latitude}")
            Text(text = "Lengdegrad: ${address.longitude}")
            Text(text = "Høyde: ${address.height} m")
            Text(text = "Bredde: ${address.width} m")
            Text(text = "Vinkel: ${address.angle}°")
            Text(text = "Retning: ${address.direction}°")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopAppBarSaved(
    modifier: Modifier = Modifier,
) {
    CenterAlignedTopAppBar(
        title = { Text("Lagrede adresser") },
        colors = TopAppBarDefaults.topAppBarColors()
    )
}
