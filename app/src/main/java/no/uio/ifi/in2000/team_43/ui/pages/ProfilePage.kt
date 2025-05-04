package no.uio.ifi.in2000.team_43.ui.pages

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import no.uio.ifi.in2000.team_43.R
import no.uio.ifi.in2000.team_43.ui.theme.Purple40
import androidx.compose.foundation.layout.size
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.TextButton
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import no.uio.ifi.in2000.team_43.model.dataStore.DataStoreManager

@Composable
fun ProfilePage(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val dataStoreManager = remember { DataStoreManager(context) }

    val firstname by dataStoreManager.firstnameFlow.collectAsState(initial = "")
    val lastname by dataStoreManager.lastnameFlow.collectAsState(initial = "")
    var tempFirstname by remember { mutableStateOf(firstname) }
    var tempLastname by remember { mutableStateOf(lastname) }
    var showDialog by remember { mutableStateOf(false) }
    var showPrivacyDialog by remember { mutableStateOf(false) }

    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.cat),
            contentDescription = "profile",
            contentScale = ContentScale.FillBounds,
            modifier = Modifier
                .width(250.dp)
                .height(250.dp)
                .clip(CircleShape)
        )
        Spacer(modifier = Modifier.padding(16.dp))
        Box(modifier = Modifier.clickable { showDialog = true }) {
            Text(
                text = if (firstname.isBlank() && lastname.isBlank()) "Fornavn Etternavn" else "$firstname $lastname",
                fontSize = 28.sp,
                modifier = Modifier.padding(end = 20.dp)
            )
            Icon(
                imageVector = Icons.Default.Edit,
                contentDescription = "edit name",
                tint = Purple40,
                modifier = Modifier
                    .size(16.dp)
                    .align(Alignment.TopEnd)
            )
        }
        Spacer(modifier = Modifier.padding(16.dp))
        Button(
            onClick = {},
            enabled = true,
            colors = ButtonDefaults.buttonColors(
                containerColor = Purple40
            ),
            modifier = Modifier
                .fillMaxWidth(0.7f)
                .height(48.dp)
        ) {
            Text("Instillinger")
        }

        Spacer(modifier = Modifier.padding(8.dp))

        // Personvernerklæringsknapp
        Button(
            onClick = {
                // Når knappen trykkes, vis personvernerklæringen i en popup
                showPrivacyDialog = true
            },
            enabled = true,
            colors = ButtonDefaults.buttonColors(
                containerColor = Purple40
            ),
            modifier = Modifier
                .fillMaxWidth(0.7f)
                .height(48.dp)
        ) {
            Text("Personvernerklæring")
        }

        // Popup for personvernerklæring
        if (showPrivacyDialog) {
            AlertDialog(
                onDismissRequest = { showPrivacyDialog = false },
                title = {
                    Text("Personvernerklæring")
                },
                text = {
                    Text(
                        "Vi samler ikke inn noen informasjon om deg.\n\n" +
                                "All data du legger inn lagres kun lokalt på mobilen din og deles aldri med noen.",
                        lineHeight = 20.sp
                    )
                },
                confirmButton = {
                    TextButton(onClick = { showPrivacyDialog = false }) {
                        Text("Lukk")
                    }
                }
            )
        }

        // Dialog for å redigere navn
        if (showDialog) {
            AlertDialog(
                title = {
                    Text(text = "Rediger navn")
                },
                text = {
                    Column {
                        OutlinedTextField(
                            value = tempFirstname,
                            onValueChange = { tempFirstname = it },
                            label = { Text("Fornavn") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = tempLastname,
                            onValueChange = { tempLastname = it },
                            label = { Text("Etternavn") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                },
                onDismissRequest = {
                    showDialog = false
                    tempFirstname = ""
                    tempLastname = ""
                },
                confirmButton = {
                    Button(
                        onClick = {
                            CoroutineScope(Dispatchers.IO).launch {
                                dataStoreManager.saveFirstname(tempFirstname)
                                dataStoreManager.saveLastname(tempLastname)
                            }
                            showDialog = false
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Purple40)
                    ) {
                        Text("Lagre")
                    }
                },
                dismissButton = {
                    Button(
                        onClick = {
                            showDialog = false
                            tempFirstname = ""
                            tempLastname = ""
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.LightGray
                        )
                    ) {
                        Text("Avbryt")
                    }
                }
            )
        }
    }
}
