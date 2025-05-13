package no.uio.ifi.in2000.team_43.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
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
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.sp
import no.uio.ifi.in2000.team_43.model.electricityDistrict.ElectricityDistrict
import no.uio.ifi.in2000.team_43.ui.theme.Cerulean
import no.uio.ifi.in2000.team_43.ui.theme.ErrorColor
import no.uio.ifi.in2000.team_43.ui.theme.LightRed


@Composable
fun MapScreen(
    modifier: Modifier = Modifier,
    viewModel: MapViewModel = viewModel(),
    onInputBtnClicked: (coordinates: Point?, address: String?, electricityDistrict: ElectricityDistrict?) -> Unit,
    onSatelliteBtnClicked: (coordinates: Point?, address: String?, electricityDistrict: ElectricityDistrict?) -> Unit,
) {
    val suggestions by viewModel.suggestions.collectAsState()
    val coordinates by viewModel.coordinates.collectAsState()
    val address by viewModel.address.collectAsState()
    val electricityDistrict by viewModel.electricityDistrict.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    var searchIsActive by remember { mutableStateOf(false) }
    var addressIsSelected by remember { mutableStateOf(false) }
    var showDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        addressIsSelected = false
        showDialog = false
    }

    // keyboard stuff
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusRequester = remember { FocusRequester() }

    val mapViewportState = rememberMapViewportState {
        setCameraOptions {
            zoom(4.0)
            center(coordinates)
            pitch(0.0)
            bearing(0.0)
        }
    }

    if (addressIsSelected) {
        LaunchedEffect(coordinates) {
            coordinates?.let {
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
                        Text(text = "Vi trenger informasjon om din takflate for å kunne gi deg relevant info. Velg å enten tegne takflate på kart eller manuelt oppgi mål. Vi anbefaler å oppgi mål for mest korrekte beregninger.")
                    },
                    onDismissRequest = {
                        showDialog = false
                    },
                    confirmButton = {
                        Column {
                            Button(
                                onClick = {
                                    if (coordinates != null) {
                                    onInputBtnClicked(coordinates, address, electricityDistrict)
                                    }
                                          },
                                enabled = true,
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(containerColor = Cerulean)
                            ) {
                                Text("Oppgi mål")
                            }
                            Button(
                                onClick = { onSatelliteBtnClicked(coordinates, address, electricityDistrict) },
                                enabled = true,
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(containerColor = Cerulean)
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
                        .heightIn(max = 256.dp)
                        .clip(RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
                        .background(MaterialTheme.colorScheme.surfaceContainer)
                ) {
                    items(suggestions) { suggestion ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp)
                                .drawBehind {
                                    drawLine(
                                        color = Color.LightGray,
                                        start = Offset(0f, size.height),
                                        end = Offset(size.width, size.height),
                                        strokeWidth = 1.dp.toPx()
                                    )
                                }
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
                                modifier = Modifier.padding(horizontal = 16.dp),
                                text = suggestion.name,
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurface,
                                maxLines = 1
                            )
                            Text(
                                modifier = Modifier.padding(horizontal = 16.dp),
                                text = suggestion.city,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            if (!addressIsSelected) {
                Row(modifier = Modifier.fillMaxWidth().height(56.dp)) {
                    Button(
                        onClick = { addressIsSelected = false },
                        enabled = true,
                        shape = RoundedCornerShape(
                            topStart = 28.dp,
                            topEnd = 0.dp,
                            bottomStart = 28.dp,
                            bottomEnd = 0.dp
                        ),
                        modifier = Modifier
                            .weight(0.275f)
                            .fillMaxHeight(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = LightRed,
                            contentColor = ErrorColor
                        )
                    ) {
                        Text(
                            "Avbryt",
                            fontSize = 16.sp,
                        )
                    }
                    Button(
                        onClick = { showDialog = true },
                        enabled = true,
                        shape = RoundedCornerShape(
                            topStart = 0.dp,
                            topEnd = 28.dp,
                            bottomStart = 0.dp,
                            bottomEnd = 28.dp
                        ),
                        modifier = Modifier
                            .weight(0.725f)
                            .fillMaxHeight(),
                        colors = ButtonDefaults.buttonColors(containerColor = Cerulean)
                    ) {
                        Text(
                            "Bekreft adresse",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            } else {
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
}