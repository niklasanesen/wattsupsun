package no.uio.ifi.in2000.team_43.ui.info

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Card
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import no.uio.ifi.in2000.team_43.model.ctx.Ctx
import no.uio.ifi.in2000.team_43.model.dataStore.DataStoreManager
import no.uio.ifi.in2000.team_43.model.input.LagreAdresse
import no.uio.ifi.in2000.team_43.model.met.Coordinates
import no.uio.ifi.in2000.team_43.ui.PVGIS.PVGISViewModel
import no.uio.ifi.in2000.team_43.ui.PVGIS.convertPVGISToMeasurements
import no.uio.ifi.in2000.team_43.ui.met.MetViewModel
import no.uio.ifi.in2000.team_43.ui.met.calculateProduction
import no.uio.ifi.in2000.team_43.ui.met.generateMonthlyMeasurements
import no.uio.ifi.in2000.team_43.ui.met.preloadProductionCalculations
import no.uio.ifi.in2000.team_43.ui.sharedScreenComponents.ChartWithValueDisplay
import no.uio.ifi.in2000.team_43.ui.sharedScreenComponents.DataSourceSwitch
import no.uio.ifi.in2000.team_43.ui.sharedScreenComponents.GrafMeasurement
import no.uio.ifi.in2000.team_43.ui.strompris.StromprisViewModel
import no.uio.ifi.in2000.team_43.utils.helperfuctions.calculations.ProductionCalc
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.Instant
import java.time.ZoneId

/**
 * Optimized version of InfoScreen that loads and displays data more efficiently
 */
@Composable
fun InfoScreen(
    modifier: Modifier = Modifier,
    metViewModel: MetViewModel,
    pvgisViewModel: PVGISViewModel = viewModel(),
    stromprisViewModel: StromprisViewModel = viewModel(),
    ctx: Ctx
) {
    // Basic UI state
    var showDialog by remember { mutableStateOf(false) }
    var isMetSelected by remember { mutableStateOf(true) }
    val scrollState = rememberScrollState()
    val context = LocalContext.current
    val dataStoreManager = remember { DataStoreManager(context) }

    // State for tracking data loading
    var isChartDataReady by remember { mutableStateOf(false) }
    var monthlyMeasurements by remember { mutableStateOf(emptyList<GrafMeasurement>()) }
    var pvgisData by remember { mutableStateOf(emptyList<GrafMeasurement>()) }

    // Preload calculations
    LaunchedEffect(ctx.input) {
        ctx.input?.let {
            metViewModel.preloadProductionCalculations(
                length = it.length,
                width = it.width,
                angle = it.angle
            )
        }
    }

    // Trigger API calls once
    LaunchedEffect(ctx.coords) {
        ctx.coords?.let {
            pvgisViewModel.fetchPVGISData(Coordinates(it.latitude(), it.longitude()))
        }

        ctx.stromdistrikt?.let {
            stromprisViewModel.fetchStromData(it)
        }
    }

    // Process data in the background
    LaunchedEffect(
        ctx.input,
        metViewModel.dataSortedByDate.collectAsState().value,
        pvgisViewModel.productionData.collectAsState().value
    ) {
        val weatherList = metViewModel.dataSortedByDate.value
        val pvgisSolData = pvgisViewModel.productionData.value

        // Only process if we have the essential data
        if (weatherList != null && ctx.input != null && pvgisSolData.isNotEmpty()) {
            // Calculate monthly measurements from weather data
            val productionList = mutableListOf<Pair<Instant, Double>>()

            weatherList.dailyObservation.forEach { (date, weatherDataList) ->
                var temperatur: Double? = null
                var solinnstraling: Double? = null

                weatherDataList.dataList.forEach { weatherData ->
                    when (weatherData.elementId) {
                        "mean(air_temperature P1D)" -> temperatur = weatherData.value
                        "mean(solar_irradiance P1D)" -> solinnstraling = weatherData.value
                    }
                }

                val length = ctx.input?.length
                val width = ctx.input?.width
                val angle = ctx.input?.angle

                if (temperatur != null && solinnstraling != null && length != null && width != null && angle != null) {
                    // Use cached calculations
                    val produksjon = metViewModel.calculateProduction(
                        length = length,
                        width = width,
                        angle = angle,
                        temperature = temperatur,
                        solarIrradiance = solinnstraling
                    )
                    productionList.add(Pair(date, produksjon))
                }
            }

            // Transform to monthly format
            monthlyMeasurements = generateMonthlyMeasurements(productionList)

            // Calculate PVGIS data
            val weatherEntries = weatherList.dailyObservation
            val ferdigBeregnetProdData = mutableListOf<Double>()

            pvgisSolData.forEachIndexed { index, solinnstraaling ->
                val weatherIndex = index * 30  // Approximate monthly value

                val temperatur = weatherEntries.getOrNull(weatherIndex)?.second?.dataList
                    ?.firstOrNull { it.elementId == "mean(air_temperature P1D)" }
                    ?.value ?: 25.0  // Default if missing

                val length = ctx.input?.length ?: 0
                val width = ctx.input?.width ?: 0
                val angle = ctx.input?.angle ?: 35

                val beregnet = ProductionCalc(
                    panelLengde = length,
                    panelBredde = width,
                    panelVinkel = angle,
                    luftTemperatur = temperatur,
                    solinnstraling = solinnstraaling
                )

                val avrundet = BigDecimal(beregnet).setScale(2, RoundingMode.HALF_UP).toDouble()
                ferdigBeregnetProdData.add(avrundet)
            }

            pvgisData = convertPVGISToMeasurements(ferdigBeregnetProdData)
            isChartDataReady = true
        }
    }

    // UI Rendering
    Column(modifier = modifier.padding(20.dp).verticalScroll(scrollState)) {
        // Header with address
        Text(
            text = "Viser data for ${ctx.address}",
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 26.sp,
                color = MaterialTheme.colorScheme.onBackground
            ),
            modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Roof data card
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = 8.dp,
            shape = RoundedCornerShape(16.dp),
            backgroundColor = Color.White
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Dette vet vi om taket ditt:",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    ),
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                // First row of values
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ValueBox(label = "Lengde", value = "${ctx.input?.length} m", modifier = Modifier.weight(1f))
                    ValueBox(label = "Bredde", value = "${ctx.input?.width} m", modifier = Modifier.weight(1f))
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Second row of values
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ValueBox(label = "Vinkel", value = "${ctx.input?.angle}°", modifier = Modifier.weight(1f))
                    ValueBox(label = "Retning", value = "${ctx.input?.direction}°", modifier = Modifier.weight(1f))
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Direction-based recommendation card
        when (ctx.input?.direction) {
            0 -> {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = 8.dp,
                    shape = RoundedCornerShape(16.dp),
                    backgroundColor = Color(0xFFFFCDD2)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Nord: Basert på retningen du har valgt lønner det seg ikke å ha solcellepaneler.",
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp,
                                color = Color(0xFFB71C1C)
                            ),
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                    }
                }
            }
            90 -> {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = 8.dp,
                    shape = RoundedCornerShape(16.dp),
                    backgroundColor = Color(0xFFC8E6C9)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Øst: Basert på retningen du har valgt genererer du mest strøm på starten av dagen.",
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp,
                                color = Color(0xFF388E3C)
                            ),
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                    }
                }
            }
            180 -> {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = 8.dp,
                    shape = RoundedCornerShape(16.dp),
                    backgroundColor = Color(0xFFC8E6C9)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Sør: Basert på retningen du har valgt lønner det seg å ha solcellepaneler.",
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp,
                                color = Color(0xFF388E3C)
                            ),
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                    }
                }
            }
            270 -> {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = 8.dp,
                    shape = RoundedCornerShape(16.dp),
                    backgroundColor = Color(0xFFC8E6C9)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Vest: Basert på retningen du har valgt genererer du mest strøm på slutten av dagen.",
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp,
                                color = Color(0xFF388E3C)
                            ),
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Chart section
        Column {
            DataSourceSwitch(
                isMetSelected = isMetSelected,
                onToggle = { isMetSelected = it }
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp),
                contentAlignment = Alignment.Center
            ) {
                if (isChartDataReady) {
                    val displayedData = if (isMetSelected) monthlyMeasurements else pvgisData
                    ChartWithValueDisplay(
                        selectedMonth = null,
                        allMeasurements = displayedData,
                    )
                } else {
                    // Show loading indicator and load the chart data in background
                    CircularProgressIndicator()
                }
            }
        }

        // Save address button
        Button(
            onClick = {
                showDialog = true
            },
            modifier = Modifier
                .align(alignment = Alignment.End)
                .padding(top = 16.dp)
        ) {
            Text("Lagre adresse")
        }

        // Dialog for saving address
        AddressNamePopup(
            showDialog = showDialog,
            onDismiss = { showDialog = false },
            onConfirm = { userGivenName ->
                val adresse = LagreAdresse(
                    latitude = ctx.coords?.latitude() ?: 0.0,
                    longitude = ctx.coords?.longitude() ?: 0.0,
                    height = ctx.input?.length?.toDouble() ?: 0.0,
                    width = ctx.input?.width?.toDouble() ?: 0.0,
                    angle = ctx.input?.angle?.toDouble() ?: 0.0,
                    direction = ctx.input?.direction?.toDouble() ?: 0.0,
                    addressString = ctx.address ?: "Ukjent adresse",
                    lagretNavn = userGivenName
                )

                CoroutineScope(Dispatchers.IO).launch {
                    dataStoreManager.saveAddress(adresse)
                }
            }
        )
    }
}

@Composable
fun ValueBox(label: String, value: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .background(color = Color(0xFFF0F0F0), shape = RoundedCornerShape(12.dp))
            .padding(vertical = 12.dp, horizontal = 8.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium.copy(
                color = Color(0xFF6D6D8D),
                fontWeight = FontWeight.Medium,
                fontSize = 14.sp
            )
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = Color.Black
            )
        )
    }
}

@Composable
fun AddressNamePopup(
    showDialog: Boolean,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var input by remember { mutableStateOf("") }

    if (showDialog) {
        androidx.compose.material3.AlertDialog(
            onDismissRequest = { onDismiss() },
            title = { Text("Navngi adresse") },
            text = {
                Column {
                    Text("Skriv inn et navn for denne adressen")
                    Spacer(modifier = Modifier.height(8.dp))
                    androidx.compose.material3.TextField(
                        value = input,
                        onValueChange = { input = it },
                        placeholder = { Text("F.eks. Hjem, Hytta, Jobb...") },
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    if (input.isNotBlank()) {
                        onConfirm(input.trim())
                        onDismiss()
                    }
                }) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = onDismiss) {
                    Text("Avbryt")
                }
            }
        )
    }
}