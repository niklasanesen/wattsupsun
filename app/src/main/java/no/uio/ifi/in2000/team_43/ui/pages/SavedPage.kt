package no.uio.ifi.in2000.team_43.ui.pages

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import no.uio.ifi.in2000.team_43.model.input.LagreAdresse
import androidx.compose.foundation.clickable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.google.gson.Gson
import no.uio.ifi.in2000.team_43.model.dataStore.DataStoreManager
import no.uio.ifi.in2000.team_43.ui.info.AddressDetailPage
import androidx.compose.ui.Alignment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight


@Composable
fun SavedPage(
    modifier: Modifier = Modifier
) {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "saved") {
        composable("saved") {
            SavedPageScreen(
                modifier = modifier,
                onCardClick = { address ->
                    val json = Gson().toJson(address)
                    navController.navigate("detail/$json")
                }
            )
        }
        composable(
            "detail/{addressJson}",
            arguments = listOf(navArgument("addressJson") { type = NavType.StringType })
        ) { backStackEntry ->
            val json = backStackEntry.arguments?.getString("addressJson")
            val address = Gson().fromJson(json, LagreAdresse::class.java)
            AddressDetailPage(
                address = address,
                navController = navController,
                modifier = Modifier
            )
        }
    }
}




@Composable
fun SavedPageScreen(
    modifier: Modifier = Modifier,
    onCardClick: (LagreAdresse) -> Unit = {}
) {
    val context = LocalContext.current
    val dataStoreManager = remember { DataStoreManager(context) }
    val addresses by dataStoreManager.addresses.collectAsState(initial = emptyList())

    Scaffold(
        modifier = modifier,
        topBar = { TopAppBarSaved() },
    ) { innerPadding ->
        Column (
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (addresses.isEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(28.dp),
                    verticalArrangement = Arrangement.Top
                ) {
                    Card (
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xffe8ca89),
                        ),
                        modifier = Modifier
                    ) {
                        Text(
                            text = "Ingen adresser lagret enda.",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xff9c2d17),
                            modifier = Modifier
                                .paddingFromBaseline(top = 40.dp)
                                .padding(horizontal = 20.dp),
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Gå til kart, velg adresse og registrer takflater for å lagre en adresse!",
                            modifier = Modifier
                                .paddingFromBaseline(bottom = 24.dp)
                                .padding(horizontal = 20.dp),
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 20.dp)
                        .padding(innerPadding),
                    verticalArrangement = Arrangement.spacedBy(20.dp, Alignment.CenterVertically),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    items(addresses) { address ->
                        // For å legge til padding over (så skyggene blir riktig)
                        if (address == addresses[0]) {
                            Spacer(modifier = Modifier.height(16.dp))
                        }

                        AddressCard(address = address, onClick = { onCardClick(address) })

                        // For å legge til padding under (så skyggene blir riktig)
                        if (address == addresses[addresses.size -1]) {
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                    }
                }

                // Knapp for å slette alle adresser
                Button(
                    onClick = {
                        CoroutineScope(Dispatchers.IO).launch {
                            dataStoreManager.deleteAllAddresses()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp)
                        .height(72.dp)
                        .paddingFromBaseline(bottom = innerPadding.calculateBottomPadding()),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Slett alle adresser", color = MaterialTheme.colorScheme.onError)
                }
            }


        }
    }
}


@Composable
fun AddressCard(address: LagreAdresse, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = address.lagretNavn,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = address.addressString,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
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
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color(0xff88aabd),
            titleContentColor = Color.White
        )
    )
}
