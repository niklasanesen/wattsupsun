package no.uio.ifi.in2000.team_43.ui.info

import androidx.compose.foundation.layout.Column
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import no.uio.ifi.in2000.team_43.model.ctx.Ctx
import no.uio.ifi.in2000.team_43.model.input.LagreAdresse
import no.uio.ifi.in2000.team_43.model.met.Coordinates
import no.uio.ifi.in2000.team_43.ui.PVGIS.PVGISViewModel
import no.uio.ifi.in2000.team_43.ui.met.MetViewModel
import no.uio.ifi.in2000.team_43.ui.strompris.StromprisViewModel
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import no.uio.ifi.in2000.team_43.model.dataStore.DataStoreManager
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Surface

import androidx.compose.runtime.mutableStateOf
import no.uio.ifi.in2000.team_43.ui.met.beregnStromproduksjon
import java.time.Instant

import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.Preview
import no.uio.ifi.in2000.team_43.model.PVGIS.MonthlyEntry
import no.uio.ifi.in2000.team_43.ui.PVGIS.convertPVGISToMeasurements

// for måned velger
import no.uio.ifi.in2000.team_43.utils.helperfuctions.calculations.ProductionCalc
import no.uio.ifi.in2000.team_43.ui.met.generateMonthlyMeasurements
import java.math.BigDecimal
import java.math.RoundingMode

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.size
import no.uio.ifi.in2000.team_43.ui.info.grafStuff.DataSourceSwitch
import no.uio.ifi.in2000.team_43.ui.info.grafStuff.YearlyChartWithValueDisplay

import androidx.compose.material3.*
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.ui.res.painterResource
import no.uio.ifi.in2000.team_43.R
import no.uio.ifi.in2000.team_43.utils.helperfuctions.calculations.calculateCoffe
import no.uio.ifi.in2000.team_43.utils.helperfuctions.calculations.calculateEnova
import no.uio.ifi.in2000.team_43.utils.helperfuctions.calculations.calculateNumPanels

// test

@Composable
fun InfoScreen(
    modifier: Modifier = Modifier,
    metViewModel: MetViewModel,
    pvgisViewModel: PVGISViewModel = viewModel(),
    stromprisViewModel: StromprisViewModel = viewModel(),
    ctx: Ctx
) {
    // Samler data fra ViewModels
    val pvgisData by pvgisViewModel.data.collectAsState()
    val drCalcData by pvgisViewModel.drCalcData.collectAsState() // Henter DRcalc data
    val drCalcTotalSum by pvgisViewModel.drCalcDataTotalSum.collectAsState() // Henter kalkulert totalsum
    val weatherList by metViewModel.dataSortedByDate.collectAsState() //Henter all værdata
    var isLoading by remember { mutableStateOf(true) }

    var isMetSelected by remember { mutableStateOf(true) }


    var showDialog by remember { mutableStateOf(false) }


    val produksjonsliste = mutableListOf<Pair<Instant, Double>>()  // strømproduskjon for alle dager i et år
    val produksjonsMap: MutableMap<Instant, Double> = produksjonsliste.toMap().toMutableMap()
    val totalProduksjon = remember { mutableStateOf(0.0) }

    val context = LocalContext.current
    val dataStoreManager = remember { DataStoreManager(context) }

    // For pvgis beregning/data
    val pvgisSolData by pvgisViewModel.productionData.collectAsState() // Henter pvgis solinnstrålingsdata
    val ferdigBeregnetProdData = mutableListOf<Double>() // legger inn beregnet strøm produksjonsdata

    // Strompris data:
    val stromprisData by stromprisViewModel.data.collectAsState()
    val stromprisDataPerMnd by stromprisViewModel.dataPerMnd.collectAsState()

    val errorMessage by metViewModel.errorMessage.collectAsState()

    val scrollState = rememberScrollState()

    var selectedMonth by remember { mutableStateOf(0) } // 0 = Januar

    //val testctx = ctx.input.valgtPanle.effekt

    // test
    // For å vise hvordan man henter månedlig data fra pvgis tool
    val dataene: List<MonthlyEntry>? = pvgisData?.outputs?.monthly?.fixed

    if (dataene != null) {
        dataene.forEachIndexed() {index, mnd ->
            Log.d("testpvgisM", "global irradiance mnd nr. $index: ${mnd.H_i_m} kWh/m2/month")

        }
    }

    // Fetcher fra stromAPI
    LaunchedEffect(ctx.stromdistrikt) {
        stromprisViewModel.fetchStromData(ctx.stromdistrikt)
    }

    // Testing if it works to fetch stromprisen per month
    LaunchedEffect(stromprisDataPerMnd) {
        Log.i("TestStromPris", "Sjekker ${stromprisDataPerMnd?.get(0)}")
        stromprisDataPerMnd?.forEachIndexed { index, data ->
            Log.i("TestStrompris", "mndnr: $index, Strompris kl 12: ${data?.getOrNull(12)?.NOK_per_kWh}")
        }
    }




    Column(modifier = modifier
        .padding(20.dp)
        .verticalScroll(scrollState)) {

        Text(
            text = "Viser data for ${ctx.address}",
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 26.sp,
                color = MaterialTheme.colorScheme.onBackground // Sørger for god kontrast
            ),
            modifier = Modifier
                .padding(top = 16.dp, bottom = 8.dp)
        )



        // Koordinater boks
       // slettet

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

                // Første rad
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ValueBox(label = "Lengde", value = "${ctx.input?.length} m", modifier = Modifier.weight(1f))
                    ValueBox(label = "Bredde", value = "${ctx.input?.width} m", modifier = Modifier.weight(1f))
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Andre rad
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ValueBox(label = "Vinkel", value = "${ctx.input?.angle}°", modifier = Modifier.weight(1f))
                    ValueBox(label = "Retning", value = "${ctx.input?.direction}°", modifier = Modifier.weight(1f))
                }

                Spacer(modifier = Modifier.height(8.dp))

                when (ctx.input?.direction) {
                    0 -> {
                        NorthDirectionCard("Nord: Basert på retningen du har valgt lønner det seg ikke å ha solcellepaneler.")
                    }
                    90 -> {
                        DirectionCard("Østvendte paneler gir mest strømproduksjon tidlig på dagen.")
                    }
                    180 -> {
                        DirectionCard("Sørvendte paneler gir jevn og høy produksjon midt på dagen. Dette er den mest effektive retningen for solcellepaneler!")

                    }
                    270 -> {
                        DirectionCard(text = "Vestvendte paneler produserer mest strøm på ettermiddagen. Det er da strømprisen normalt er høyest!")
                    }
                }
            }
        }


        Spacer(modifier = Modifier.height(12.dp))

        // NED HIT
//        when (ctx.input?.direction) {
//            0 -> {
//                NorthDirectionCard("Nord: Basert på retningen du har valgt lønner det seg ikke å ha solcellepaneler.")
//            }
//            90 -> {
//                DirectionCard("Østvendte paneler gir mest strømproduksjon tidlig på dagen.")
//            }
//            180 -> {
//                DirectionCard("Sørvendte paneler gir jevn og høy produksjon midt på dagen. Dette er den mest effektive retningen for solcellepaneler!")
//
//            }
//            270 -> {
//                DirectionCard(text = "Vestvendte paneler produserer mest strøm på ettermiddagen. Det er da strømprisen normalt er høyest!")
//            }
//        }

        // Enova on infoscreen:
        Log.i("TestEnova", "kwpPerPanel: ${ctx.input?.valgtPanle?.effekt}")
        val wPerPanel = ctx.input?.valgtPanle?.effekt?.toDouble()
        val kwpPerPanel = ((wPerPanel ?: 1.0) / 1000.0)

        val roofLength = ctx.areal?.length
        val roofWidth = ctx.areal?.width
        val panelLength = ctx.input?.valgtPanle?.len
        val panelWidth = ctx.input?.valgtPanle?.width





        val numPanel = calculateNumPanels(roofLength = roofLength, roofWidth = roofWidth, panelLength = panelLength, panelWidth = panelWidth)
        Spacer(modifier = Modifier.height(12.dp))
        Log.i("TestEnova", "kwpPerPanel: $kwpPerPanel numPanel: $numPanel")
        EnovaCard(calculateEnova(kwpPerPanel, numPanel))

        // Other fun boxes with info
        Spacer(modifier = Modifier.height(12.dp))
        CoffeeCard(calculateCoffe(totalProduksjon.value) )





        Spacer(modifier = Modifier.height(32.dp))

        when {
            errorMessage != null -> {
                Text(
                    text = "Error: $errorMessage",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
            weatherList?.dailyObservation?.isNotEmpty() == true -> {
                val weatherDataForFirstDay = weatherList!!.dailyObservation[1] //dato og værliste i en pair, alle datoer er sortert fra 1 januar til 31 desember
                val weatherDataList = weatherDataForFirstDay.second //dette aksesserer værlisten med alle målinger for relevant dag
                val observationForTemp = weatherDataList.dataList[0]
                val insolationWeatherData = weatherDataList.dataList[1] //solinnstråling
            }

            else -> {
                //CircularProgressIndicator()
            }


            // NED HIT
        }



        weatherList?.dailyObservation?.forEach { (date, weatherDataList) ->
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
                isLoading = false
                val produksjon = beregnStromproduksjon(
                    lengde = length,
                    bredde = width,
                    vinkel = angle,
                    temperatur = temperatur,
                    solinnstraling = solinnstraling
                )

                // Legg til i lista
                produksjonsliste.add(Pair(date, produksjon))

            }
        }
        // Legg til som map
        produksjonsMap.clear() // Fjern gamle data
        produksjonsMap.putAll(produksjonsliste)
        // adderer sammen totalsum
        totalProduksjon.value = produksjonsMap.values.sum()
        Log.d("produksjonsliste", produksjonsliste.toString())
        Log.d("produksjonslisteMap", produksjonsMap.toString())
        Log.d("produksjonslisteMap", totalProduksjon.toString())




        Spacer(modifier = Modifier.height(16.dp))



        Spacer(modifier = Modifier.height(16.dp))


        // PVGIS beregninger
        LaunchedEffect(ctx.coords) {
            ctx.coords?.let {
                pvgisViewModel.fetchPVGISData(Coordinates(it.latitude(), it.longitude()))

            }
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
            ferdigBeregnetProdData.add(avrundet)
        }

        ferdigBeregnetProdData.forEachIndexed{ index, data ->
            Log.d("testpvgisBeregn", "mndnr $index: $data kWh/month")

        }
        Log.d("testpvgisBeregn", " $ferdigBeregnetProdData")

        // Generere månedlige målinger
        val monthlyMeasurements = remember(produksjonsliste) {
            generateMonthlyMeasurements(produksjonsliste)
        }
        val pvgisData = remember(ferdigBeregnetProdData) {
            convertPVGISToMeasurements(ferdigBeregnetProdData)
        }

        val displayedData = if (isMetSelected) monthlyMeasurements else pvgisData

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
                if (displayedData.isNotEmpty()) {
                    YearlyChartWithValueDisplay(
                        allMeasurements = displayedData,
                        isLoading = isLoading,
                        modifier = Modifier.fillMaxWidth().height(300.dp)
                    )

                } else {
                    CircularProgressIndicator()
                }
            }
        }




        Button(
            onClick = {
                showDialog = true
            },
            modifier = Modifier.align(alignment = Alignment.End)
        ) {
            Text("Lagre adresse")
        }

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

@Preview(showBackground = true)
@Composable
fun PreviewValueBox() {
    MaterialTheme {
        Surface {
            ValueBox(
                label = "Lengde",
                value = "34 m",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            )
        }
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
        AlertDialog(
            onDismissRequest = { onDismiss() },
            title = { Text("Navngi adresse") },
            text = {
                Column {
                    Text("Skriv inn et navn for denne adressen")
                    Spacer(modifier = Modifier.height(8.dp))
                    TextField(
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


// For testing av card til enovastøtte:
@Composable
fun EnovaCard(supportAmount: Double) {
    val isZeroSupport = supportAmount <= 0
    val backgroundColor = if (isZeroSupport) Color(0xFFFFCDD2) else Color(0xFF304745)
    val textColor = if (isZeroSupport) Color(0xFFD32F2F) else Color(0xFFFFFFFF)
    val context = LocalContext.current
    val enovaInfoLink = "https://www.enova.no/privat/alle-energitiltak/solenergi/solcelleanlegg/?gad_source=1&gad_campaignid=16624425572&gbraid=0AAAAADLywwiacDJN5I7llRB-oOuYbvBmW&gclid=Cj0KCQjw5ubABhDIARIsAHMighb65GEWXVeJy3jCq8CYd0rcsR4L1n7G4GHf9wBGVB-ngIYtISqzJIIaAp3fEALw_wcB"

    Card(
        modifier = Modifier
            .fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Text(
                    text = if (isZeroSupport) "Ingen beregnet Enova-støtte" else "Estimert Enova-støtte",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = textColor
                    ),
                    modifier = Modifier.padding(end = 8.dp)
                )
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = "Les mer om Enova-støtte",
                    tint = textColor,
                    modifier = Modifier
                        .clickable {
                            val intent = Intent(
                                Intent.ACTION_VIEW,
                                Uri.parse(enovaInfoLink)
                            )
                            context.startActivity(intent)
                        }
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            if (isZeroSupport) {
                Text(
                    text = "Du mottar dessverre ingen støtte fra Enova",
                    style = MaterialTheme.typography.bodyMedium.copy(color = textColor)
                )
            } else {
                Text(
                    text = "${supportAmount.toInt()} kr",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = textColor
                    )
                )
            }
        }
    }
}



// preview av testcardet for enova:
@Preview(showBackground = true)
@Composable
fun EnovaSupportCardPreview_Positive() {
    MaterialTheme {
        EnovaCard(supportAmount = 15000.0)
    }
}

@Preview(showBackground = true)
@Composable
fun EnovaSupportCardPreview_Zero() {
    MaterialTheme {
        EnovaCard(supportAmount = 0.0)
    }
}


@Composable
fun CoffeeCard(production: Int) {
    val isZeroProduction = production <= 0
    val backgroundColor = Color(0xFFFFE0B2)  // Lys oransje
    val textColor = Color(0xFFBF360C)  // Mørk rød

    Card(
        modifier = Modifier
            .fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Bruker VectorDrawable (XML) kaffekopp-ikonet
                Icon(
                    painter = painterResource(id = R.drawable.coffee_cup), // Bruker .xml VectorDrawable
                    contentDescription = "Kaffekopp",
                    modifier = Modifier.size(70.dp).padding(end = 8.dp),
                    tint = textColor
                )
                // Teksten som beskriver produksjon
                Text(
                    text = if (isZeroProduction) "Koker opp kaffe, et øyeblikk..." else "Solcellepanel hos deg kan koke opp ${production.toInt()} kopper kaffe årlig!",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = textColor
                    )
                )
            }
        }
    }
}




@Preview(showBackground = true)
@Composable
fun NoProdPreviewCoffeeCard() {
    CoffeeCard(production = 0)
}

@Preview(showBackground = true)
@Composable
fun PreviewCoffeeCard() {
    CoffeeCard(production = 500)
}

// Card for tips basert på retning:

@Composable
fun DirectionCard(text: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD)) // Lys blå bakgrunn
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = text,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = Color(0xFF1976D2) // Mørkere blå tekst, men ikke for intens
                )
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DirectionCardPreview() {
    MaterialTheme {
        DirectionCard("Østvendte paneler gir mest strømproduksjon tidlig på dagen.")
    }
}

@Composable
fun NorthDirectionCard(text: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        backgroundColor = Color(0xFFFFCDD2) // Lys rød bakgrunn
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = text,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = Color(0xFFB71C1C) // Mørkere rød tekst
                )
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun NorthDirectionCardPreview() {
    MaterialTheme {
        NorthDirectionCard("Nord: Basert på retningen du har valgt lønner det seg ikke å ha solcellepaneler.")
    }
}


// Parser tidspunktdataene fra stromAPI til leselig klokkeslett
fun getTimeFromDate(dateString: String): String? {
    return try {
        // Definerer formatet på inngangsstrengen
        val formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME

        // Parser datoen og tiden til et ZonedDateTime-objekt
        val dateTime = ZonedDateTime.parse(dateString, formatter)

        // Henter ut klokkeslettet i ønsket format
        dateTime.toLocalTime().toString() // For eksempel "23:00:00"
    } catch (e: DateTimeParseException) {
        e.printStackTrace()
        // Håndterer feil hvis strengen ikke kan parses
        null
    }
}




