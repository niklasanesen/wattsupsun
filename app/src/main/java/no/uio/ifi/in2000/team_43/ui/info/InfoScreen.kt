package no.uio.ifi.in2000.team_43.ui.info

// --- Standard imports ---
import android.util.Log
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.*
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.Instant

// --- Project-specific imports ---
import no.uio.ifi.in2000.team_43.model.ctx.Ctx
import no.uio.ifi.in2000.team_43.model.input.SavedAddress
import no.uio.ifi.in2000.team_43.model.dataStore.DataStoreManager
import no.uio.ifi.in2000.team_43.model.graphRes.getMonthName
import no.uio.ifi.in2000.team_43.ui.PVGIS.*
import no.uio.ifi.in2000.team_43.ui.met.*
import no.uio.ifi.in2000.team_43.ui.electricityPrice.ElectricityPriceViewModel
import no.uio.ifi.in2000.team_43.ui.info.graphComponents.*
import no.uio.ifi.in2000.team_43.ui.info.infoScreenComponents.*
import no.uio.ifi.in2000.team_43.utils.helperfuctions.calculations.*

@Composable
fun InfoScreen(
    modifier: Modifier = Modifier,
    metViewModel: MetViewModel,
    pvgisViewModel: PVGISViewModel = viewModel(),
    electricityPriceViewModel: ElectricityPriceViewModel = viewModel(),
    ctx: Ctx
) {
    val weatherList by metViewModel.dataSortedByDate.collectAsState()
    val pvgisSolData by pvgisViewModel.productionData.collectAsState()
    val electricityPriceDataPerMonth by electricityPriceViewModel.dataPerMnd.collectAsState()
    val scrollState = rememberScrollState()
    val context = LocalContext.current
    val dataStoreManager = remember { DataStoreManager(context) }

    var isMetSelected by remember { mutableStateOf(true) }
    var showDialog by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(true) }
    var selectedMonth by remember { mutableStateOf<String?>(null) }
    var selectedKwh by remember { mutableStateOf<String?>(null) }

    val address = if (ctx.address != null) ctx.address else "address is unknown"
    val roofLength = ctx.input?.length?.toDouble()
    val roofWidth = ctx.input?.width?.toDouble()
    val panelLength = ctx.input?.panel?.len
    val panelWidth = ctx.input?.panel?.width
    val panelArea = if (panelLength != null && panelWidth != null) panelLength * panelWidth else 0.0
    val numPanel = calculateNumPanels(roofLength, roofWidth, panelLength, panelWidth)
    val totalPanelAreal = numPanel * panelArea
    val panelPrice = ctx.input?.panel?.price ?: 0
    val totalPanelPrice = numPanel * panelPrice

    Log.d("sui", "$weatherList")
    val calculatedMetData = remember(weatherList, ctx.input) {
        val result = mutableListOf<Pair<Instant, Double>>()
        val angle = ctx.input?.angle
        val dir = ctx.input?.direction

        weatherList?.dailyObservation?.forEach { (date, weatherDataList) ->
            var temperature: Double? = null
            var solarInsolation: Double? = null

            weatherDataList.dataList.forEach { weatherData ->
                when (weatherData.elementId) {
                    "mean(air_temperature P1D)" -> temperature = weatherData.value
                    "mean(solar_irradiance P1D)" -> solarInsolation = weatherData.value
                }
            }

            if (temperature != null && solarInsolation != null && angle != null && dir != null) {
                isLoading = false
                val production = calculateElectricityProduction(
                    areal = totalPanelAreal,
                    angle = angle,
                    temperature = temperature,
                    solarInsolation = solarInsolation,
                    direction = dir
                )
                result.add(Pair(date, production))
            }
        }
        result
    }

    val calculatedPvgisData = remember(pvgisSolData, weatherList, ctx.input) {
        val result = mutableListOf<Double>()
        val angle = ctx.input?.angle ?: 35
        val weatherEntries = weatherList?.dailyObservation ?: emptyList()

        pvgisSolData.forEachIndexed { index, solarInsolation ->
            val temperature = weatherEntries.getOrNull(index * 30)?.second?.dataList
                ?.firstOrNull { it.elementId == "mean(air_temperature P1D)" }?.value ?: 25.0

            val calculated = productionCalc(
                panelLength = ctx.input?.length ?: 0,
                panelWidth = ctx.input?.width ?: 0,
                panelAngle = angle,
                airTemperature = temperature,
                solarInsolation = solarInsolation
            )

            result.add(BigDecimal(calculated).setScale(2, RoundingMode.HALF_UP).toDouble())
        }
        result
    }

    val metData = remember(calculatedMetData) {
        convertMetToMeasurements(calculatedMetData)
    }
    val pvgisData = remember(calculatedPvgisData) {
        convertPVGISToMeasurements(calculatedPvgisData)
    }

    val displayedData = if (isMetSelected) metData else pvgisData
    val direction = ctx.input?.direction ?: 0
    val moneySavedList = calcMoneySaved(direction, electricityPriceDataPerMonth, displayedData)
    val totalMoneySaved = moneySavedList.sum()
    Log.d("saveIssue", "moneysavedlist = $moneySavedList and totalMoneySaved = $totalMoneySaved")
    val totalProduction = calculatedMetData.sumOf { it.second }


    LaunchedEffect(ctx.electricityDistrict) {
        electricityPriceViewModel.fetchElectricityData(ctx.electricityDistrict)
    }

    // Testing if it works to fetch electricity price per month
    LaunchedEffect(electricityPriceDataPerMonth) {
        electricityPriceDataPerMonth?.forEachIndexed { index, data ->
        }
    }

    Column(modifier = modifier
        .padding(20.dp)
        .verticalScroll(scrollState))
    {
        Text(
            text = "$address",
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 26.sp,
                color = MaterialTheme.colorScheme.onBackground
            ),
            modifier = Modifier
                .padding(top = 16.dp, bottom = 8.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Rooftop values box
        Card(
            modifier = Modifier
                .fillMaxWidth(),
            elevation = CardDefaults.cardElevation(8.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
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

                // ---- 1. that shows ----
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ValueBox(label = "Lengde", value = "${ctx.input?.length} m", modifier = Modifier.weight(1f))
                    ValueBox(label = "Bredde", value = "${ctx.input?.width} m", modifier = Modifier.weight(1f))
                }

                Spacer(modifier = Modifier.height(8.dp))

                // ---- 2. row that shows  ----
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ValueBox(label = "Vinkel", value = "${ctx.input?.angle}°", modifier = Modifier.weight(1f))

                    val directionText = when (ctx.input?.direction) {
                        0 -> "Nord"
                        90 -> "Øst"
                        180 -> "Sør"
                        270 -> "Vest"
                        else -> "${ctx.input?.direction}°"
                    }

                    ValueBox(label = "Retning", value = directionText, modifier = Modifier.weight(1f))
                }
            }
        }


        // Coffee card on screen
        Spacer(modifier = Modifier.height(16.dp))
        CoffeeCard(calculateCoffee(totalProduction) )
        Log.i("testCoffee", "CoffeeData: ${calculateCoffee(totalProduction)}")

        // Enova on info screen:
        val wPerPanel = ctx.input?.panel?.effect?.toDouble()
        val kwpPerPanel = ((wPerPanel ?: 1.0) / 1000.0)
        Spacer(modifier = Modifier.height(16.dp))
        EnovaCard(calculateEnova(kwpPerPanel, numPanel))


        // Total money card on screen
        // pls ask before touching any of this
        Spacer(modifier = Modifier.height(16.dp))
        TotalSavedLogic(totalMoneySaved, isLoading)

        // total price for panels
        // Pls ask before touching this
        Spacer(modifier = Modifier.height(16.dp))
        TotalCostLogic(numPanel = numPanel, totalPanelPrice = totalPanelPrice, isLoading = false) // It does not need to load, since data is already ready, therefor send with false


        // ---- graph + switch + saved money for each month ----
        Spacer(modifier = Modifier.height(32.dp))
        Column {
            when {
                isLoading -> {
                    CircularProgressIndicator()
                }

                else -> {
                    MonthlySavingsDisplay(savingsPerMonth = moneySavedList)
                }
            }
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
                when {
                    isLoading -> {
                        CircularProgressIndicator()
                    }

                    displayedData.isNotEmpty() -> {
                        YearlyChartWithValueDisplay(
                            allMeasurements = displayedData,
                            modifier = Modifier.fillMaxWidth().height(300.dp),
                            onValueSelected = { month, kwh ->
                                selectedMonth = month
                                selectedKwh = kwh
                                Log.i("YearlyChart", kwh)
                            }
                        )
                    }
                }
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // ---- box with month data ----
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .background(
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                            shape = RoundedCornerShape(12.dp)
                        )
                        .border(
                            width = 2.dp,
                            color = MaterialTheme.colorScheme.primary,
                            shape = RoundedCornerShape(12.dp)
                        )
                        .padding(16.dp)
                ) {
                    Text(
                        text = selectedMonth?.let { getMonthName(it.toInt()) }
                            ?: "Trykk på grafen",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                // ---- box with kwh data ----
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .background(
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                            shape = RoundedCornerShape(12.dp)
                        )
                        .border(
                            width = 2.dp,
                            color = MaterialTheme.colorScheme.primary,
                            shape = RoundedCornerShape(12.dp)
                        )
                        .padding(16.dp)
                ) {
                    Text(
                        text = selectedKwh?.let { "$it kWh" } ?: "for å vise verdi",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }

        // ---- Button for saving address ----
        Button(
            onClick = {
                showDialog = true
            },
            modifier = Modifier.align(alignment = Alignment.End)
        ) {
            Text("Lagre adresse")
        }

        // ---- popUp for saved address ----
        AddressNamePopup(
            showDialog = showDialog,
            onDismiss = { showDialog = false },
            onConfirm = { userGivenName ->
                val address = SavedAddress(
                    latitude = ctx.coordinates?.latitude() ?: 0.0,
                    longitude = ctx.coordinates?.longitude() ?: 0.0,
                    height = ctx.input?.length?.toDouble() ?: 0.0,
                    width = ctx.input?.width?.toDouble() ?: 0.0,
                    angle = ctx.input?.angle?.toDouble() ?: 0.0,
                    direction = ctx.input?.direction?.toDouble() ?: 0.0,
                    addressString = ctx.address ?: "Ukjent adresse",
                    savedName = userGivenName,
                    numPanel = numPanel,
                    totalPanelAreal = totalPanelAreal,
                    kwp = ctx.input?.panel?.effect?: 0,
                    price = ctx.input?.panel?.price?: 0,
                    moneySaved = totalMoneySaved,
                    totalPanelPrice = totalPanelPrice
                )


                CoroutineScope(Dispatchers.IO).launch {
                    dataStoreManager.saveAddress(address)
                }
            }
        )
    }
}