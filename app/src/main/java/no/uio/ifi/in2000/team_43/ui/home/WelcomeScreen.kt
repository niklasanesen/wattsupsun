package no.uio.ifi.in2000.team_43.ui.home

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import no.uio.ifi.in2000.team_43.R
import no.uio.ifi.in2000.team_43.model.dataStore.DataStoreManager
import no.uio.ifi.in2000.team_43.ui.theme.Purple40

@Composable
fun WelcomeScreen(
    onGetStartedClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val dataStoreManager = remember { DataStoreManager(context) }
    val firstname by dataStoreManager.firstnameFlow.collectAsState(initial = "")
    val lastname by dataStoreManager.lastnameFlow.collectAsState(initial = "")

    // State to track if this is the first app launch and if tutorial should show
    var showTutorialDialog by remember { mutableStateOf(false) }
    var showAppInfoDialog by remember { mutableStateOf(false) }

    // Check if this is the first launch
    val isFirstLaunch = remember {
        context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
            .getBoolean("is_first_launch", true)
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
    ) {
        // Info button in top right corner
        IconButton(
            onClick = { showAppInfoDialog = true },
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp)
                .size(48.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Info,
                contentDescription = "App informasjon",
                modifier = Modifier.size(28.dp),
                tint = Color(0xFF3B7D9C)
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // App Logo
            Image(
                painter = painterResource(id = R.drawable.logo_circle),
                contentDescription = "WattsUpSun Logo",
                modifier = Modifier
                    .size(160.dp)
                    .padding(16.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Welcome Text
            Text(
                text = "Velkommen til",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontSize = 28.sp,
                    color = Color(0xFF3B7D9C)
                )
            )

            Text(
                text = "WattsUpSun",
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 40.sp,
                    color = Color(0xFF3B7D9C)
                )
            )

            // User name display - only show if name exists
            if (firstname.isNotBlank() || lastname.isNotBlank()) {
                Card(
                    modifier = Modifier
                        .padding(top = 16.dp, bottom = 8.dp)
                        .fillMaxWidth(0.7f),
                    shape = RoundedCornerShape(32.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFF83C1EB).copy(alpha = 0.2f)
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Text(
                        text = "$firstname $lastname",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontSize = 22.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF3B7D9C)
                        ),
                        modifier = Modifier
                            .padding(vertical = 12.dp, horizontal = 24.dp)
                            .align(Alignment.CenterHorizontally),
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(modifier = Modifier.height(40.dp))

            // App Description Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Din guide til solenergi",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 22.sp,
                            color = Color(0xFF3B7D9C)
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "WattsUpSun hjelper deg å beregne solcellepotensiale for din takflate. " +
                                "Finn ut hvor mye strøm du kan produsere og hvordan takretningen påvirker effektiviteten.",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontSize = 16.sp,
                            lineHeight = 24.sp,
                            textAlign = TextAlign.Center
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(48.dp))

            // Get Started Button
            Button(
                onClick = {
                    // Check if this is first launch to show tutorial
                    if (isFirstLaunch) {
                        showTutorialDialog = true
                    } else {
                        // Just continue if not first launch
                        onGetStartedClick()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF3B7D9C)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "Kom i gang",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontSize = 18.sp
                    )
                )
            }
        }

        // Tutorial Dialog
        if (showTutorialDialog) {
            TutorialDialog(
                onDismiss = {
                    // Set first launch to false and proceed to map screen
                    context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
                        .edit()
                        .putBoolean("is_first_launch", false)
                        .apply()

                    // Hide dialog and go to the map screen
                    showTutorialDialog = false
                    onGetStartedClick()
                }
            )
        }

        // App Info Dialog
        if (showAppInfoDialog) {
            AppInfoDialog(
                onDismiss = { showAppInfoDialog = false }
            )
        }
    }
}

@Composable
fun TutorialDialog(
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true
        )
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Slik bruker du WattsUpSun",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF3B7D9C),
                        fontSize = 24.sp
                    ),
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                Text(
                    text = "1. Skriv inn din adresse i søkefeltet",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.Medium,
                        fontSize = 16.sp
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                )

                Text(
                    text = "2. Velg adressen fra forslagene som dukker opp",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.Medium,
                        fontSize = 16.sp
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                )

                Text(
                    text = "3. Trykk på 'Bekreft adresse'",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.Medium,
                        fontSize = 16.sp
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                )

                Text(
                    text = "4. Oppgi informasjon om taket ditt eller tegn takflaten",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.Medium,
                        fontSize = 16.sp
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = onDismiss,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Purple40
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "Forstått",
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }
        }
    }
}

@Composable
fun AppInfoDialog(
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true
        )
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // App Info Content directly embedded
                Image(
                    painter = painterResource(R.drawable.logo),
                    contentDescription = "App Icon",
                    modifier = Modifier.padding(horizontal = 40.dp)
                )

                Spacer(Modifier.height(12.dp))

                Text(
                    text = "Velkommen til WattsUpSun!",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(vertical = 12.dp)
                )

                Text(
                    text = "Denne appen gir deg relevant og nyttig informasjon når det kommer til å installere solcellepaneler på eiendommen din. Målet med appen er at du enklere skal kunne vurdere nytten ved solcelleplaneler og om dette kan være noe for deg:) \n\nDu vil også kunne bruke appen videre til å få en oversikt over strømproduksjon og annet nyttig!",
                    modifier = Modifier.padding(vertical = 8.dp)
                )

                Spacer(Modifier.height(32.dp))

                Button(
                    onClick = onDismiss,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Purple40
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "Lukk",
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }
        }
    }
}