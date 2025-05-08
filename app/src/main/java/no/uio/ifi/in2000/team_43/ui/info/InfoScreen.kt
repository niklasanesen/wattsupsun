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
import no.uio.ifi.in2000.team_43.ui.PVGIS.PVGISViewModel
import no.uio.ifi.in2000.team_43.ui.met.MetViewModel
import no.uio.ifi.in2000.team_43.ui.strompris.StromprisViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import no.uio.ifi.in2000.team_43.model.dataStore.DataStoreManager
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row

import androidx.compose.runtime.mutableStateOf
import no.uio.ifi.in2000.team_43.ui.met.beregnStromproduksjon
import java.time.Instant

import androidx.compose.runtime.setValue
import no.uio.ifi.in2000.team_43.model.PVGIS.MonthlyEntry
import no.uio.ifi.in2000.team_43.ui.PVGIS.convertPVGISToMeasurements

import no.uio.ifi.in2000.team_43.utils.helperfuctions.calculations.ProductionCalc
import no.uio.ifi.in2000.team_43.ui.met.generateMonthlyMeasurements
import java.math.BigDecimal
import java.math.RoundingMode

import androidx.compose.foundation.border
import no.uio.ifi.in2000.team_43.ui.info.grafStuff.DataSourceSwitch
import no.uio.ifi.in2000.team_43.ui.info.grafStuff.YearlyChartWithValueDisplay

import no.uio.ifi.in2000.team_43.model.garfStuff.getMonthName
import no.uio.ifi.in2000.team_43.ui.info.infoScreenComponents.AddressNamePopup
import no.uio.ifi.in2000.team_43.ui.info.infoScreenComponents.CoffeeCard
import no.uio.ifi.in2000.team_43.ui.info.infoScreenComponents.DirectionCard
import no.uio.ifi.in2000.team_43.ui.info.infoScreenComponents.EnovaCard
import no.uio.ifi.in2000.team_43.ui.info.infoScreenComponents.MonthlySavingsDisplay
import no.uio.ifi.in2000.team_43.ui.info.infoScreenComponents.NorthDirectionCard
import no.uio.ifi.in2000.team_43.ui.info.infoScreenComponents.ValueBox
import no.uio.ifi.in2000.team_43.utils.helperfuctions.calculations.calcMoneySaved
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
    val stromprisDataPerMnd by stromprisViewModel.dataPerMnd.collectAsState()
    val errorMessage by metViewModel.errorMessage.collectAsState()
    val scrollState = rememberScrollState()

    Log.d("stromprisDataPerMnd", stromprisDataPerMnd.toString())



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


        // Enova on infoscreen:
        Log.i("TestEnova", "kwpPerPanel: ${ctx.input?.panel?.effekt}")
        val wPerPanel = ctx.input?.panel?.effekt?.toDouble()
        val kwpPerPanel = ((wPerPanel ?: 1.0) / 1000.0)

        val roofLength = ctx.input?.length?.toDouble()
        val roofWidth = ctx.input?.width?.toDouble()
        val panelLength = ctx.input?.panel?.len
        val panelWidth = ctx.input?.panel?.width


        val panelArel: Double
        var totalPanelAreal = 0.0



        Log.i("TestEnova", "roofLength = $roofLength")
        Log.i("TestEnova", "roofWidth = $roofWidth")
        Log.i("TestEnova", "panelLength = $panelLength")
        Log.i("TestEnova", "panelWidth = $panelWidth")
        val numPanel = calculateNumPanels(roofLength = roofLength, roofWidth = roofWidth, panelLength = panelLength, panelWidth = panelWidth)
        Spacer(modifier = Modifier.height(12.dp))
        Log.i("TestEnova", "kwpPerPanel: $kwpPerPanel numPanel: $numPanel")
        EnovaCard(calculateEnova(kwpPerPanel, numPanel))

        // Other fun boxes with info
        Spacer(modifier = Modifier.height(12.dp))
        CoffeeCard(calculateCoffe(totalProduksjon.value) )

        if (panelLength!=null && panelWidth!=null){
            panelArel = panelLength * panelWidth
            totalPanelAreal = numPanel * panelArel
        }

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

        val direction = ctx.input?.direction ?: 0  // fallback til sør
        val moneySavedList = calcMoneySaved(direction, stromprisDataPerMnd, pvgisData)


        var selectedMonth by remember { mutableStateOf<String?>(null) }
        var selectedKwh by remember { mutableStateOf<String?>(null) }

        Column {
            MonthlySavingsDisplay(savingsPerMonth = displayedData)
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
                // Måned-boks
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
                        .border(2.dp, MaterialTheme.colorScheme.primary)
                        .padding(16.dp)
                ) {
                    Text(
                        text = selectedMonth?.let { "${getMonthName(it.toInt())}" } ?: "Trykk på grafen",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                // kWh-boks
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
                        .border(2.dp, MaterialTheme.colorScheme.primary)
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



        //lagre knappen
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



