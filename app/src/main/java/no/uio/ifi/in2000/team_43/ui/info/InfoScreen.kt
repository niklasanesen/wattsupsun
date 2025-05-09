package no.uio.ifi.in2000.team_43.ui.info

// --- Standard imports ---
import android.util.Log
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
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

// --- Prosjekt-spesifikke imports ---
import no.uio.ifi.in2000.team_43.model.ctx.Ctx
import no.uio.ifi.in2000.team_43.model.input.LagreAdresse
import no.uio.ifi.in2000.team_43.model.dataStore.DataStoreManager
import no.uio.ifi.in2000.team_43.model.garfStuff.getMonthName
import no.uio.ifi.in2000.team_43.ui.PVGIS.*
import no.uio.ifi.in2000.team_43.ui.met.*
import no.uio.ifi.in2000.team_43.ui.strompris.StromprisViewModel
import no.uio.ifi.in2000.team_43.ui.info.grafStuff.*
import no.uio.ifi.in2000.team_43.ui.info.infoScreenComponents.*
import no.uio.ifi.in2000.team_43.utils.helperfuctions.calculations.*

@Composable
fun InfoScreen(
    modifier: Modifier = Modifier,
    metViewModel: MetViewModel,
    pvgisViewModel: PVGISViewModel = viewModel(),
    stromprisViewModel: StromprisViewModel = viewModel(),
    ctx: Ctx
) {
    // ---- collect data from viewModels ----
    val weatherList by metViewModel.dataSortedByDate.collectAsState() //Henter all værdata fra met
    val pvgisSolData by pvgisViewModel.productionData.collectAsState() // Henter pvgis solinnstrålingsdata


    // ---- calculate weather data to kwh----
    val calculatedPvgisData = mutableListOf<Double>() // legger inn beregnet strøm produksjonsdata
    val calculatedMetData = mutableListOf<Pair<Instant, Double>>()  // strømproduskjon for alle dager i et år


    // ---- variables to control state of data ----
    var isLoading by remember { mutableStateOf(true) }
    var isMetSelected by remember { mutableStateOf(true) }
    var showDialog by remember { mutableStateOf(false) }

    val totalProduksjon = remember { mutableStateOf(0.0) }





    // ---- crate context for data storage ----
    val context = LocalContext.current
    val dataStoreManager = remember { DataStoreManager(context) }


    // Strompris data:
    val stromprisDataPerMnd by stromprisViewModel.dataPerMnd.collectAsState()
    val scrollState = rememberScrollState()









    val adress = if (ctx.address != null) ctx.address else "adresse er ukjent"
    val panelPrice = if (ctx.input?.panel != null) ctx.input.panel.pris else 0
    val wPerPanel = ctx.input?.panel?.effekt?.toDouble()
    val kwpPerPanel = ((wPerPanel ?: 1.0) / 1000.0)
    val roofLength = ctx.input?.length?.toDouble()
    val roofWidth = ctx.input?.width?.toDouble()
    val panelLength = ctx.input?.panel?.len
    val panelWidth = ctx.input?.panel?.width
    val panelArel = if (panelLength != null && panelWidth != null) panelLength * panelWidth else 0.0
    val numPanel = calculateNumPanels(roofLength = roofLength, roofWidth = roofWidth, panelLength = panelLength, panelWidth = panelWidth)
    val totalPanelAreal = numPanel * panelArel
    val totalPanelPrice =  numPanel * panelPrice





    weatherList?.dailyObservation?.forEach { (date, weatherDataList) ->
        var temperatur: Double? = null
        var solinnstraling: Double? = null

        weatherDataList.dataList.forEach { weatherData ->
            when (weatherData.elementId) {
                "mean(air_temperature P1D)" -> temperatur = weatherData.value
                "mean(solar_irradiance P1D)" -> solinnstraling = weatherData.value
            }
        }

        val angle = ctx.input?.angle
        val dir = ctx.input?.direction

        if (temperatur != null && solinnstraling != null && angle != null && dir != null) {
            isLoading = false
            val produksjon = beregnStromproduksjon(
                areal = totalPanelAreal,
                vinkel = angle,
                temperatur = temperatur,
                solinnstraling = solinnstraling,
                direction = dir
            )

            // Legg til i lista
            calculatedMetData.add(Pair(date, produksjon))
        }
        Log.i("testNoe", "metdata caclulated: $calculatedMetData")
    }

























    val weatherEntries = weatherList?.dailyObservation ?: emptyList()

    pvgisSolData.forEachIndexed { index, solinnstraaling ->
        val weatherIndex = index * 30  // Henter omtrent én verdi per måned

        val temperatur = weatherEntries.getOrNull(weatherIndex)?.second?.dataList
            ?.firstOrNull { it.elementId == "mean(air_temperature P1D)" }
            ?.value ?: 25.0  //  hvis mangler temp

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

        val avrundet =  BigDecimal(beregnet).setScale(2, RoundingMode.HALF_UP).toDouble()
        calculatedPvgisData.add(avrundet)
    }












    // ---- Generere månedlige målinger ----

    val metData = remember(calculatedMetData) {
        convertMetToMeasurements(calculatedMetData)
    }
    val pvgisData = remember(calculatedPvgisData) {
        convertPVGISToMeasurements(calculatedPvgisData)
    }

    val displayedData = if (isMetSelected) metData else pvgisData
    val direction = ctx.input?.direction ?: 0  // fallback til sør
    val moneySavedList = calcMoneySaved(direction, stromprisDataPerMnd, displayedData)
    var selectedMonth by remember { mutableStateOf<String?>(null) }
    var selectedKwh by remember { mutableStateOf<String?>(null) }
    val totalMoneySaved = moneySavedList.sum()

    // Fetcher fra stromAPI
    LaunchedEffect(ctx.stromdistrikt) {
        stromprisViewModel.fetchStromData(ctx.stromdistrikt)
    }

    // Testing if it works to fetch stromprisen per month
    LaunchedEffect(stromprisDataPerMnd) {
        stromprisDataPerMnd?.forEachIndexed { index, data ->
        }
    }










    Column(modifier = modifier
        .padding(20.dp)
        .verticalScroll(scrollState)) {

        Text(
            text = "$adress",
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 26.sp,
                color = MaterialTheme.colorScheme.onBackground // Sørger for god kontrast
            ),
            modifier = Modifier
                .padding(top = 16.dp, bottom = 8.dp)
        )



        Spacer(modifier = Modifier.height(16.dp))



        // Takflate-verdier boks
        Card(
            modifier = Modifier
                .fillMaxWidth(),
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

                // ---- 3. row that shows  ----
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ValueBox(label = "Vinkel", value = "${ctx.input?.angle}°", modifier = Modifier.weight(1f))

                    val directionText = when (ctx.input?.direction?.toInt()) {
                        0 -> "Nord"
                        90 -> "Øst"
                        180 -> "Sør"
                        270 -> "Vest"
                        else -> "${ctx.input?.direction}°"
                    }

                    ValueBox(label = "Retning", value = directionText, modifier = Modifier.weight(1f))
                }

                Spacer(modifier = Modifier.height(8.dp))

                // ---- 3. row that shows  ----
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (isLoading) {
                        // Midlertidige "loading"-bokser
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(80.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(80.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    } else {
                        // Vanlige ValueBox-er med tekst
                        ValueBox(label = "kostnader av $numPanel paneler", value = "$totalPanelPrice kr", modifier = Modifier.weight(1f))
                        ValueBox(label = "Totalt Spart (2024)", value = "${totalMoneySaved.toInt()} kr", modifier = Modifier.weight(1f))
                    }
                }



                Spacer(modifier = Modifier.height(8.dp))

            }
        }



        Spacer(modifier = Modifier.height(12.dp))

<<<<<<< HEAD
        // NED HIT
        when (ctx.input?.direction) {
            0 -> {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = 8.dp,
                    shape = RoundedCornerShape(16.dp),
                    backgroundColor = Color(0xFFFFCDD2) // Lys rød bakgrunn
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Nord: Basert på retningen du har valgt lønner det seg ikke å ha solcellepaneler.",
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp,
                                color = Color(0xFFB71C1C) // Mørkere rød tekst
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
                    backgroundColor = Color(0xFFC8E6C9) // Lys grønn bakgrunn
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Øst: Basert på retningen du har valgt genererer du mest strøm på starten av dagen.",
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp,
                                color = Color(0xFF388E3C) // Mørkere grønn tekst
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
                    backgroundColor = Color(0xFFC8E6C9) // Lys grønn bakgrunn
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Sør: Basert på retningen  du har valgt lønner det seg å ha solcellepaneler.",
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp,
                                color = Color(0xFF388E3C) // Mørkere grønn tekst
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
                    backgroundColor = Color(0xFFC8E6C9) // Lys grønn bakgrunn
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Vest: Basert på retningen du har valgt genererer du mest strøm på slutten av dagen.",
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp,
                                color = Color(0xFF388E3C) // Mørkere grønn tekst
                            ),
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                    }
                }
            }
        }
=======
        // Enova on infoscreen:
        val wPerPanel = ctx.input?.panel?.effekt?.toDouble()
        val kwpPerPanel = ((wPerPanel ?: 1.0) / 1000.0)

        val roofLength = ctx.input?.length?.toDouble()
        val roofWidth = ctx.input?.width?.toDouble()
        val panelLength = ctx.input?.panel?.len
        val panelWidth = ctx.input?.panel?.width






        EnovaCard(calculateEnova(kwpPerPanel, numPanel))

        Spacer(modifier = Modifier.height(12.dp))

        val produksjonsMap: MutableMap<Instant, Double> = calculatedMetData.toMap().toMutableMap()
        totalProduksjon.value = produksjonsMap.values.sum()

        CoffeeCard(calculateCoffe(totalProduksjon.value) )
        Log.i("testcoffee", "coffedata: ${calculateCoffe(totalProduksjon.value)}")
>>>>>>> 7830a4d03c34043f780b30a05e29101411546f55

        Spacer(modifier = Modifier.height(32.dp))






        // ---- graf + switch + saved money for each month ----
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
                        text = selectedMonth?.let { "${getMonthName(it.toInt())}" }
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



        // ---- Button for saving adress ----
        Button(
            onClick = {
                showDialog = true
            },
            modifier = Modifier.align(alignment = Alignment.End)
        ) {
            Text("Lagre adresse")
        }



        // ---- popUp for saved adress ----
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
                    lagretNavn = userGivenName,
                    numPanel = numPanel,
                    totalPanelAreal = totalPanelAreal,
                    kwp = ctx.input?.panel?.effekt?: 0,
                    pris = ctx.input?.panel?.pris?: 0,
                )

                CoroutineScope(Dispatchers.IO).launch {
                    dataStoreManager.saveAddress(adresse)
                }
            }
        )
    }
}



