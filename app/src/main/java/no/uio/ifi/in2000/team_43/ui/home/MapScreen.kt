package no.uio.ifi.in2000.team_43.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.extension.compose.MapboxMap
import com.mapbox.maps.extension.compose.animation.viewport.rememberMapViewportState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import no.uio.ifi.in2000.team_43.model.ctx.Ctx
import no.uio.ifi.in2000.team_43.ui.theme.Purple40
@Composable
fun MapScreen(
    modifier: Modifier = Modifier,
    viewModel: MapViewModel = viewModel(),
    onInputBtnClicked: (coords: Point?, address: String?) -> Unit,
    onSatelliteBtnClicked: (coords: Point?, address: String?) -> Unit,
) {
    val suggestions by viewModel.suggestions.collectAsState()
    val coords by viewModel.coords.collectAsState()
    val address by viewModel.address.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    var searchIsActive by remember { mutableStateOf(false) }
    var addressIsSelected by remember { mutableStateOf(false) }
    var showDialog by remember { mutableStateOf(false) }

    // keyboard stuff
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusRequester = remember { FocusRequester() }

    val mapViewportState = rememberMapViewportState {
        setCameraOptions {
            zoom(4.0)
            center(coords)
            pitch(0.0)
            bearing(0.0)
        }
    }

    if (addressIsSelected) {
        LaunchedEffect(coords) {
            coords?.let {
                mapViewportState.flyTo(
                    CameraOptions.Builder()
                        .center(it)
                        .zoom(16.0)
                        .build()
                )
            }
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        MapboxMap(
            Modifier.fillMaxSize(),
            mapViewportState = mapViewportState
        )

        // Søkefelt og resultater optimalisert for portrettvisning
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(horizontal = 16.dp, vertical = 24.dp)
        ) {
            if (showDialog) {
                AlertDialog(
                    title = {
                        Text(text = "Registrer takflate")
                    },
                    text = {
                        Text(text = "Vi trenger informasjon om dine takflater for å kunne gi deg relevant info. Velg å enten tegne takflate på kart eller manuelt oppgi mål. Vi anbefaler å oppgi mål for mest korrekte beregninger.")
                    },
                    onDismissRequest = {
                        showDialog = false
                    },
                    confirmButton = {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button(
                                onClick = { onInputBtnClicked(coords, address) },
                                enabled = true,
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(containerColor = Purple40)
                            ) {
                                Text("Oppgi mål")
                            }
                            Button(
                                onClick = { onSatelliteBtnClicked(coords, address) },
                                enabled = true,
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(containerColor = Purple40)
                            ) {
                                Text("Tegn takflate")
                            }
                        }
                    },
                )
            }

            if (searchIsActive && suggestions.isNotEmpty()) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 200.dp) // Litt mindre for å passe bedre i portrettmodus
                        .clip(RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
                        .background(MaterialTheme.colorScheme.surfaceContainer)
                ) {
                    items(suggestions) { suggestion ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp)
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                                .clickable {
                                    searchQuery = suggestion.name
                                    addressIsSelected = true
                                    viewModel.select(suggestion.formattedAddress)
                                    searchIsActive = false
                                    keyboardController?.hide()
                                },
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = suggestion.name,
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurface,
                                maxLines = 1
                            )
                            Text(
                                text = suggestion.city,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            TextField(
                value = searchQuery,
                onValueChange = {
                    searchQuery = it
                    viewModel.search(it)
                    searchIsActive = it.isNotEmpty()
                },
                modifier = Modifier
                    .focusRequester(focusRequester)
                    .fillMaxWidth()
                    .height(56.dp)
                    .clip(if (searchIsActive && suggestions.isNotEmpty())
                        RoundedCornerShape(bottomStart = 28.dp, bottomEnd = 28.dp)
                    else CircleShape),
                placeholder = { Text("Skriv inn en adresse...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "søk") },
                trailingIcon = {
                    if (addressIsSelected) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(Purple40)
                                .clickable { showDialog = true },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = "Naviger til Input",
                                tint = MaterialTheme.colorScheme.surfaceContainer
                            )
                        }
                    }
                },
                colors = TextFieldDefaults.colors(
                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainer,
                    focusedContainerColor = MaterialTheme.colorScheme.surfaceContainer,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent
                ),
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = { keyboardController?.hide() }),
            )
        }
    }
}