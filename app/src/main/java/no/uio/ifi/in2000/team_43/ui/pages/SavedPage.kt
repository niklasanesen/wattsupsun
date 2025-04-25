package no.uio.ifi.in2000.team_43.ui.pages

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.paddingFromBaseline
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import no.uio.ifi.in2000.team_43.R


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SavedPage(
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBarSaved()
        },
    ) { innerPadding ->

        var addresses = listOf("Hjem", "Hytte", "Feriehus")

        LazyColumn(
            modifier = modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 20.dp)
                .padding(innerPadding),
            verticalArrangement = Arrangement.spacedBy(20.dp, Alignment.CenterVertically),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            items(addresses.size) { address ->
                if (address == 0) {
                    Spacer(modifier = Modifier.height(16.dp))
                }

                AddressCard(address = addresses[address])

                if (address == addresses.size - 1) {
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }

        }
    }
}

@Composable
fun AddressCard(
    address: String
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(112.dp),
        onClick = { /*TODO*/ },
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Text(
            text = address,
            modifier = Modifier
                .paddingFromBaseline(top = 40.dp)
                .padding(horizontal = 20.dp),
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = "Eksempelveien 12b, 0310 Oslo",
            modifier = Modifier.padding(horizontal = 20.dp)
        )

        Text(
            text = "3 solcellepaneler",
            modifier = Modifier.padding(horizontal = 20.dp)
        )

        /*
        Image(
            painter = painterResource( /*id = R.drawable.Media*/),
            contentDescription = "image description",
            contentScale = ContentScale.FillBounds
        )
         */
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
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            titleContentColor = MaterialTheme.colorScheme.primary,
        ),
        /*
        actions = {
            if (currentScreen != HomeScreen.InfoApp) {
                IconButton(onClick = { navController.navigate(HomeScreen.InfoApp.name) }) {
                    Icon(
                        imageVector = Icons.Filled.Info,
                        contentDescription = "Info"
                    )
                }
            }
        },

         */
    )
}