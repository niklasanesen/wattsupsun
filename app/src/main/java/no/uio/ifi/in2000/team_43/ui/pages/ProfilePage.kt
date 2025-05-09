package no.uio.ifi.in2000.team_43.ui.pages

import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.FileProvider
import coil3.compose.rememberAsyncImagePainter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import no.uio.ifi.in2000.team_43.model.dataStore.DataStoreManager
import java.io.File

@Composable
fun ProfilePage(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val dataStoreManager = remember { DataStoreManager(context) }

    val firstname by dataStoreManager.firstnameFlow.collectAsState(initial = "")
    val lastname by dataStoreManager.lastnameFlow.collectAsState(initial = "")
    val profilePhotoUri by dataStoreManager.profilePhotoUriFlow.collectAsState(initial = "")
    var tempFirstname by remember { mutableStateOf(firstname) }
    var tempLastname by remember { mutableStateOf(lastname) }
    var tempUri by remember { mutableStateOf<Uri?>(null) }
    var showDialog by remember { mutableStateOf(false) }
    var showPrivacyDialog by remember { mutableStateOf(false) }
    var showPhotoOptDialog by remember { mutableStateOf(false) }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            CoroutineScope(Dispatchers.IO).launch {
                dataStoreManager.saveProfilePhotoUri(it.toString())
            }
        }
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success: Boolean ->
        if (success) {
            tempUri?.let { uri ->
                CoroutineScope(Dispatchers.IO).launch {
                    dataStoreManager.saveProfilePhotoUri(uri.toString())
                }
            }
        }
    }

    fun getTempFileUri(context: Context): Uri {
        val cacheDir = context.cacheDir
        if (!cacheDir.exists()) {
            cacheDir.mkdirs()
        }

        val imageDir = File(cacheDir, "camera_photos")
        if (!imageDir.exists()) {
            imageDir.mkdirs()
        }

        val tempFile = File(imageDir, "profile_photo_${System.currentTimeMillis()}.jpg")

        return FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            tempFile
        )
    }

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            val newUri = getTempFileUri(context)
            tempUri = newUri
            cameraLauncher.launch(newUri)
        } else {
            Toast.makeText(
                context,
                "Trenger kamera-tillatelse for å ta profilbilde",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    Scaffold (
        topBar = { TopAppBarProfile() }
    ) { padding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(padding),
            // verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(32.dp))

            Card (
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                ),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 10.dp
                ),
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
            ) {
                Row (
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(20.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(140.dp)
                            .clip(CircleShape)
                            .clickable { showPhotoOptDialog = true }
                            .background(Color.White),
                        contentAlignment = Alignment.Center,
                    ) {
                        if (profilePhotoUri.isNotEmpty()) {
                            Image(
                                painter = rememberAsyncImagePainter(Uri.parse(profilePhotoUri)),
                                contentDescription = "profile",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = "profile",
                                tint = Purple40,
                                modifier = Modifier.size(124.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.padding(12.dp))

                    Box(modifier = Modifier.clickable { showDialog = true }) {
                        Text(
                            text = if (firstname.isBlank() && lastname.isBlank()) "Fornavn \nEtternavn" else "$firstname \n$lastname",
                            fontSize = 28.sp,
                            modifier = Modifier.padding(end = 20.dp),
                            lineHeight = 34.sp
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
                }
            }
            Spacer(modifier = Modifier.padding(16.dp))
            Button(
                onClick = {},
                enabled = true,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xff3b7d9c)
                ),
                modifier = Modifier
                    .fillMaxWidth(0.7f)
                    .height(48.dp)
            ) {
                Text("Innstillinger")
            }

            Spacer(modifier = Modifier.padding(4.dp))

            // Personvernerklæringsknapp
            Button(
                onClick = {
                    // Når knappen trykkes, vis personvernerklæringen i en popup
                    showPrivacyDialog = true
                },
                enabled = true,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xff3b7d9c)
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

            if (showPhotoOptDialog) {
                AlertDialog(
                    onDismissRequest = { showPhotoOptDialog = false },
                    title = { Text("Profilbilde") },
                    text = { Text("Velg hvordan du vil legge til profilbilde") },
                    confirmButton = {
                        Button(
                            onClick = {
                                photoPickerLauncher.launch("image/*")
                                showPhotoOptDialog = false
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Purple40)
                        ) {
                            Text("Velg fra galleri")
                        }
                    },
                    dismissButton = {
                        Button(
                            onClick = {
                                cameraPermissionLauncher.launch(android.Manifest.permission.CAMERA)
                                showPhotoOptDialog = false
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Purple40)
                        ) {
                            Text("Ta nytt bilde")
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
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopAppBarProfile(
    modifier: Modifier = Modifier,
) {
    CenterAlignedTopAppBar(
        title = { Text("Profil") },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color(0xff88aabd),
            titleContentColor = Color.White
        )
    )
}
