package no.uio.ifi.in2000.team_43.ui.pages

import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.paddingFromBaseline
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import no.uio.ifi.in2000.team_43.ui.theme.Purple40
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.core.content.FileProvider
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import coil3.compose.rememberAsyncImagePainter
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import no.uio.ifi.in2000.team_43.model.dataStore.DataStoreManager
import no.uio.ifi.in2000.team_43.model.input.LagreAdresse
import no.uio.ifi.in2000.team_43.ui.info.AddressDetailPage
import java.io.File
import androidx.core.net.toUri
import androidx.navigation.NavController
import no.uio.ifi.in2000.team_43.ui.home.WelcomeScreen
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.rememberCoroutineScope
import no.uio.ifi.in2000.team_43.ui.theme.Cerulean
import no.uio.ifi.in2000.team_43.ui.theme.ErrorColor
import no.uio.ifi.in2000.team_43.ui.theme.MutedError
import no.uio.ifi.in2000.team_43.ui.theme.TopBarColor


@Composable
fun ProfilePage(modifier: Modifier = Modifier) {

    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "profile") {
        composable("profile") {
            ProfileScreen(
                modifier = modifier,
                onCardClick = { address ->
                    val json = Gson().toJson(address)
                    navController.navigate("detail/$json")
                },
                navController = navController
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
                modifier = modifier

            )
        }
        composable("appinfo") {
            WelcomeScreen(
                onGetStartedClick = {
                    navController.navigate("profile")
                }
            )
        }
    }
}

@Composable
fun ProfileScreen(
    modifier: Modifier = Modifier,
    onCardClick: (LagreAdresse) -> Unit = {},
    navController: NavController
){
    val context = LocalContext.current
    val dataStoreManager = remember { DataStoreManager(context) }
    val addresses by dataStoreManager.addresses.collectAsState(initial = emptyList())
    val coroutineScope = rememberCoroutineScope()


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

    // Content on the screen
    Scaffold (
        topBar = { TopAppBarProfile() }
    ) { padding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(top = padding.calculateTopPadding(), bottom = 12.dp),
            // verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            Card (
                modifier = Modifier.padding(horizontal = 12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFA8CFE5)
                )
            ) {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFF304745),
                    ),
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(
                        defaultElevation = 20.dp
                    ),
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .fillMaxWidth()
                        //.padding(horizontal = 12.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(24.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(80.dp)
                                .clip(CircleShape)
                                .clickable { showPhotoOptDialog = true }
                                .background(Color.White),
                            contentAlignment = Alignment.Center,
                        ) {
                            if (profilePhotoUri.isNotEmpty()) {
                                Image(
                                    painter = rememberAsyncImagePainter(profilePhotoUri.toUri()),
                                    contentDescription = "profile",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = "profile",
                                    tint = Cerulean,
                                    modifier = Modifier.size(124.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.padding(12.dp))

                        Column() {
                            Box(modifier = Modifier.clickable { showDialog = true }) {
                                Text(
                                    text = if (firstname.isBlank() && lastname.isBlank()) "Fornavn Etternavn" else "$firstname $lastname",
                                    fontSize = 26.sp,
                                    modifier = Modifier.padding(end = 20.dp),
                                    lineHeight = 34.sp,
                                    color = Color.White
                                )
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = "edit name",
                                    tint = Color.White,
                                    //tint = Purple40,
                                    modifier = Modifier
                                        .size(16.dp)
                                        .align(Alignment.TopEnd)
                                )
                            }

                            Row {
                                Text(
                                    text = if (addresses.size == 1) "${addresses.size} lagret adresse" else "${addresses.size} lagrede adresser",
                                    color = Color.White,
                                    modifier = Modifier.padding(top = 4.dp)
                                )
                            }
                        }
                    }
                }

                // Spacer(modifier = Modifier.height(16.dp))


                Column (
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Button(
                        onClick = { navController.navigate("appinfo") },
                        enabled = true,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xff3b7d9c)
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                    ) {
                        Text("Info om appen")
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Button(
                        onClick = {},
                        enabled = true,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xff3b7d9c)
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                    ) {
                        Text("Innstillinger")
                    }

                    Spacer(modifier = Modifier.height(4.dp))

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
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                    ) {
                        Text("Personvernerklæring")
                    }
                }
            }

            // Spacer(modifier = Modifier.height(8.dp))

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 16.dp, horizontal = 16.dp),
                thickness = 1.dp,
            )
            //Spacer(modifier = Modifier.height(16.dp))

            
            Card (
                modifier = Modifier.padding(horizontal = 12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFE8EFF5)
                )
            ) {
                Row (
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                        .padding(top = 8.dp)
                ) {
                    Text(
                        text = "Lagrede adresser ♡",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            //.padding(8.dp)
                            .padding(horizontal = 12.dp)
                            .padding(top = 12.dp)
                    )
                    if (addresses.isNotEmpty()) {
                        var showDialog by remember { mutableStateOf(false) }
                        val coroutineScope = rememberCoroutineScope()

                        Column {
                            Button(
                                onClick = { showDialog = true },
                                enabled = true,
                                modifier = Modifier.width(112.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = MutedError)
                            ) {
                                Text("Slett alle", color = MaterialTheme.colorScheme.onError)
                            }

                            if (showDialog) {
                                AlertDialog(
                                    onDismissRequest = { showDialog = false },
                                    title = { Text("Bekreft sletting") },
                                    text = { Text("Er du sikker på at du vil slette alle adresser?") },
                                    confirmButton = {
                                        TextButton(
                                            onClick = {
                                                coroutineScope.launch {
                                                    dataStoreManager.deleteAllAddresses()
                                                }
                                                showDialog = false
                                            }
                                        ) {
                                            Text("Ja", color = MaterialTheme.colorScheme.error)
                                        }
                                    },
                                    dismissButton = {
                                        TextButton(onClick = { showDialog = false }) {
                                            Text("Avbryt")
                                        }
                                    }
                                )
                            }
                        }

                    }
                }

                Column (
                    modifier = Modifier.fillMaxSize(),
                    //verticalArrangement = Arrangement.SpaceBetween,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (addresses.isEmpty()) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 16.dp)
                                .padding(top = 12.dp),
                            verticalArrangement = Arrangement.Top
                        ) {
                            Card(
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
                                    text = "Gå til kart, velg adresse og registrer takflate for å lagre en adresse!",
                                    modifier = Modifier
                                        .paddingFromBaseline(bottom = 24.dp)
                                        .padding(horizontal = 20.dp),
                                )
                            }
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),
                            //.padding(top = 8.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            items(addresses) { address ->
                                // For å legge til padding over (så skyggene blir riktig)
                                if (address == addresses[0]) {
                                    Spacer(modifier = Modifier.height(8.dp))
                                }


                                AddressCard(
                                    address = address,
                                    onClick = { onCardClick(address) },
                                    onDelete = { addressToDelete ->
                                        coroutineScope.launch {
                                            dataStoreManager.deleteAddress(addressToDelete)
                                        }
                                    }
                                )
                                // For å legge til padding under (så skyggene blir riktig)
                                if (address == addresses[addresses.size - 1]) {
                                    Spacer(modifier = Modifier.height(16.dp))
                                }
                            }
                        }
                    }
                }
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
                            "Vi samler ikke inn noen informasjon om deg. " +
                                    "All data du legger inn lagres kun lokalt på mobilen din og deles ikke.",
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
                            colors = ButtonDefaults.buttonColors(containerColor = Cerulean)
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
                            colors = ButtonDefaults.buttonColors(containerColor = Cerulean)
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
                            colors = ButtonDefaults.buttonColors(containerColor = Cerulean)
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


@Composable
fun AddressCard(
    address: LagreAdresse,
    onClick: () -> Unit,
    onDelete: (LagreAdresse) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = address.lagretNavn,
                        style = MaterialTheme.typography.titleLarge,
                        color = Cerulean
                    )

                    Box (
                        modifier = Modifier.size(24.dp)
                    ) {
                        IconButton(onClick = { expanded = true }) {
                            Icon(
                                imageVector = Icons.Default.MoreVert,
                                contentDescription = "Flere alternativer",
                            )
                        }

                        DropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Slett") },
                                onClick = {
                                    expanded = false
                                    onDelete(address)
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = address.addressString,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopAppBarProfile() {
    CenterAlignedTopAppBar(
        title = { Text("Profil") },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = TopBarColor,
            titleContentColor = Color.White
        )
    )
}
