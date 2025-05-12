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
import androidx.compose.foundation.layout.paddingFromBaseline
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
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
import no.uio.ifi.in2000.team_43.ui.theme.Cerulean
import no.uio.ifi.in2000.team_43.ui.theme.Purple40

@Composable
fun WelcomeScreen(
    onGetStartedClick: () -> Unit,
    modifier: Modifier = Modifier,
    startup: Boolean = false
) {
    val context = LocalContext.current
    val dataStoreManager = remember { DataStoreManager(context) }
    val firstname by dataStoreManager.firstnameFlow.collectAsState(initial = "")
    val lastname by dataStoreManager.lastnameFlow.collectAsState(initial = "")

    // State to track if this is the first app launch and if tutorial should show
    var showTutorialDialog by remember { mutableStateOf(false) }

    // Check if this is the first launch
    val isFirstLaunch = remember {
        context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
            .getBoolean("is_first_launch", true)
    }

    var buttonText = "Kom i gang"
    if (!startup) {
        buttonText = "Lukk"
    }


    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
    ) {
        IconButton(
            onClick = { showTutorialDialog = true },
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(4.dp)
                .size(64.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Info,
                contentDescription = "Info",
                modifier = Modifier.size(28.dp),
                tint = Cerulean
            )
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .padding(top = 40.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            item {
                // App Logo
                Image(
                    painter = painterResource(id = R.drawable.logo_circle),
                    contentDescription = "WattsUpSun Logo",
                    modifier = Modifier
                        .size(160.dp)
                        .padding(8.dp)
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Welcome Text
                Text(
                    text = "Velkommen til",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontSize = 28.sp,
                        color = Cerulean
                    )
                )

                Text(
                    text = "WattsUpSun",
                    style = MaterialTheme.typography.headlineLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 40.sp,
                        color = Cerulean
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
                                fontSize = 18.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Cerulean
                            ),
                            modifier = Modifier
                                .padding(vertical = 8.dp, horizontal = 16.dp)
                                .align(Alignment.CenterHorizontally),
                            textAlign = TextAlign.Center
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

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
                                color = Cerulean
                            )
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        Text(
                            text = "WattsUpSun gir deg relevant og nyttig informasjon når det kommer til å installere solcellepaneler på din eiendom. Hovedsaklig vil den hjelpe deg med å vurdere lønnsomheten av solcellepaneler! \n" +
                                    "\n" +
                                    "Du vil også kunne bruke appen videre til å få en oversikt over strømproduksjon og annet nyttig!",
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontSize = 16.sp,
                                lineHeight = 24.sp,
                                // textAlign = TextAlign.Center
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(28.dp))

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
                        containerColor = Cerulean
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = buttonText,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontSize = 18.sp
                        )
                    )
                }
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
    }
}

@Composable
fun TutorialDialog(
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(),
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
                    text =
                    "1. Gå til kart-skjermen nede i venstre hjørne\n" +
                            "2. Skriv inn din adresse i søkefeltet\n" +
                            "3. Velg adressen fra forslagene som dukker opp\n" +
                            "4. Trykk på 'Bekreft adresse'\n" +
                            "5. Oppgi informasjon om taket ditt eller tegn takflaten\n" +
                            "6. Lagre adressen",
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = onDismiss,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Cerulean
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