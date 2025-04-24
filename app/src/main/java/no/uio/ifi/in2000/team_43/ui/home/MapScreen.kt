package no.uio.ifi.in2000.team_43.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import androidx.compose.material3.TextButton
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import no.uio.ifi.in2000.team_43.model.ctx.Ctx

@Composable
fun MapScreen(
    modifier: Modifier = Modifier,
    viewModel: MapViewModel = viewModel(),
    onInputBtnClicked: (coords: Point?) -> Unit,
    onSatelliteBtnClicked: (coords: Point?) -> Unit,
) {
    val suggestions by viewModel.suggestions.collectAsState()
    val coords by viewModel.coords.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    var isSearchActive by remember { mutableStateOf(false) }
    var addressIsSelected by remember { mutableStateOf(false) }
    var addressIsConfirmed by remember { mutableStateOf(false) }

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

        if (addressIsConfirmed) {
            AlertDialog(
                title = {
                    Text(text = "Registrer takflate")
                },
                text = {
                    Text(text = "Vi trenger informasjon om dine takflater for å kunne gi deg relevant info. Velg å enten tegne takflate på kart eller manuelt oppgi mål. Vi anbefaler å oppgi mål for mest korrekte beregninger.")
                },
                onDismissRequest = {},
                confirmButton = {
                    TextButton(
                        onClick = { onInputBtnClicked(coords) },
                        enabled = true
                    ) {
                        Text("Oppgi mål")
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = { onSatelliteBtnClicked(coords) },
                        enabled = true
                    ) {
                        Text("Tegn takflate")
                    }
                }
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(16.dp)
        ) {
            if (isSearchActive && suggestions.isNotEmpty()) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 200.dp)
                        .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
                        .background(Color.White)
                ) {
                    items(suggestions) { suggestion ->
                        Text(
                            text = suggestion.name,
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    searchQuery = suggestion.name
                                    addressIsSelected = true
                                    viewModel.select(suggestion)
                                    isSearchActive = false
                                }
                                .padding(16.dp)
                        )
                    }
                }
            }
            TextField(
                value = searchQuery,
                onValueChange = {
                    searchQuery = it
                    viewModel.search(it)
                    isSearchActive = it.isNotEmpty()
                },
                modifier = Modifier
                    .focusRequester(focusRequester)
                    .fillMaxWidth()
                    .clip(
                        if (isSearchActive && suggestions.isNotEmpty())
                            RoundedCornerShape(bottomStart = 8.dp, bottomEnd = 8.dp)
                        else
                            RoundedCornerShape(8.dp)
                    ),
                placeholder = { Text("Search location...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
                trailingIcon = {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = "Navigate to Input",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.clickable {
                            addressIsConfirmed = true
                        }
                    )
                    /*
                    if (addressIsSelected) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = "Navigate to Input",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.clickable {
                                addressIsConfirmed = true
                            }
                        )
                    }

                     */
                },
                colors = TextFieldDefaults.colors(
                    unfocusedContainerColor = Color.White,
                    focusedContainerColor = Color.White
                ),
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = { keyboardController?.hide() }),
            )
        }
    }
}