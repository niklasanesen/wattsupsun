package no.uio.ifi.in2000.team_43.ui.info

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import no.uio.ifi.in2000.team_43.model.met.Coordinates
import no.uio.ifi.in2000.team_43.ui.PVGIS.PVGISViewModel
import no.uio.ifi.in2000.team_43.ui.met.MetViewModel
import no.uio.ifi.in2000.team_43.ui.strompris.StromprisViewModel
import no.uio.ifi.in2000.team_43.model.dataStore.DataStoreManager
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import no.uio.ifi.in2000.team_43.ui.met.beregnStromproduksjon
import java.time.Instant
import androidx.compose.runtime.setValue
import com.mapbox.geojson.Point
import no.uio.ifi.in2000.team_43.model.PVGIS.MonthlyEntry
import no.uio.ifi.in2000.team_43.model.input.LagreAdresse
import no.uio.ifi.in2000.team_43.ui.PVGIS.convertPVGISToMeasurements
import no.uio.ifi.in2000.team_43.ui.sharedScreenComponents.ChartWithValueDisplay //graf
import no.uio.ifi.in2000.team_43.utils.helperfuctions.calculations.ProductionCalc
import java.time.ZoneId
import no.uio.ifi.in2000.team_43.ui.met.generateMonthlyMeasurements
import no.uio.ifi.in2000.team_43.ui.sharedScreenComponents.DataSourceSwitch

data class DirectionInfo(val backgroundColor: Color, val textColor: Color, val message: String)

fun directionText(direction: Double): DirectionInfo {
    return when (direction) {
        0.0 -> DirectionInfo(
            backgroundColor = Color(0xFFFFCDD2), // Lys rød
            textColor = Color(0xFFB71C1C),       // Mørk rød
            message = "Nord: Basert på retningen du har valgt lønner det seg ikke å ha solcellepaneler."
        )
        90.0 -> DirectionInfo(
            backgroundColor = Color(0xFFC8E6C9), // Lys grønn
            textColor = Color(0xFF388E3C),       // Mørk grønn
            message = "Øst: Basert på retningen du har valgt genererer du mest strøm på starten av dagen."
        )
        180.0 -> DirectionInfo(
            backgroundColor = Color(0xFFC8E6C9), // Lys grønn
            textColor = Color(0xFF388E3C),       // Mørk grønn
            message = "Sør: Basert på retningen  du har valgt lønner det seg å ha solcellepaneler."
        )
        270.0 -> DirectionInfo(
            backgroundColor = Color(0xFFC8E6C9), // Lys grønn
            textColor = Color(0xFF388E3C),       // Mørk grønn
            message = "Vest: Basert på retningen du har valgt genererer du mest strøm på slutten av dagen."
        )
        else -> DirectionInfo(
            backgroundColor = Color.LightGray,
            textColor = Color.Black,
            message = "Ukjent retning"
        )
    }
}





@Composable
fun AddressDetailPage(
    address: LagreAdresse,
    modifier: Modifier = Modifier,
    pvgisViewModel: PVGISViewModel = viewModel(),
    //stromprisViewModel: StromprisViewModel = viewModel(),
    metViewModel: MetViewModel = viewModel()
) {

    //val dataStoreManager = remember { DataStoreManager(context) }
    //val drCalcData by pvgisViewModel.drCalcData.collectAsState() // Henter DRcalc data
    //val drCalcTotalSum by pvgisViewModel.drCalcDataTotalSum.collectAsState() // Henter kalkulert totalsum
    //val stromprisData by stromprisViewModel.data.collectAsState()
    //val context = LocalContext.current


    // Samler data fra ViewModels
    val pvgisData by pvgisViewModel.data.collectAsState()
    val weatherList by metViewModel.dataSortedByDate.collectAsState() //Henter all værdata
    var isMetSelected by remember { mutableStateOf(true) }
    val produksjonsliste = mutableListOf<Pair<Instant, Double>>()  // strømproduskjon for alle dager i et år
    val pvgisSolData by pvgisViewModel.productionData.collectAsState() // Henter pvgis solinnstrålingsdata
    val ferdigBeregnetProdData = mutableListOf<Double>() // legger inn beregnet strøm produksjonsdata
    val errorMessage by metViewModel.errorMessage.collectAsState()
    val scrollState = rememberScrollState()
    var selectedMonth by remember { mutableStateOf(0) } // 0 = Januar
    val dataene: List<MonthlyEntry>? = pvgisData?.outputs?.monthly?.fixed

    if (dataene != null) {
        dataene.forEachIndexed() {index, mnd ->
            Log.d("testpvgisM", "global irradiance mnd nr. $index: ${mnd.H_i_m} kWh/m2/month")

        }
    }


    Column(modifier = modifier.padding(20.dp).verticalScroll(scrollState)) {

        // Tittel for skjermen
        Text(
            text = "Info Screen",
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 28.sp, // Større skrift for å matche den boblete stilen
                color = MaterialTheme.colorScheme.onBackground
            ),
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Skillelinje
        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

        // Koordinater boks
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = 8.dp,
            shape = RoundedCornerShape(16.dp),
            backgroundColor = Color.White // Hvit bakgrunn
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Koordinater:",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.Medium,
                        fontSize = 22.sp, // Økt størrelse på overskriften
                        color = MaterialTheme.colorScheme.onSurface
                    ),
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Text(
                    text = "Coords: \n latitude: ${address.latitude}$ \n longitude: ${address.longitude}$",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontSize = 20.sp, // Økt skriftstørrelse for bedre lesbarhet
                        color = MaterialTheme.colorScheme.primary,
                        letterSpacing = 0.5.sp
                    ),
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Takflate-verdier boks
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = 8.dp,
            shape = RoundedCornerShape(16.dp),
            backgroundColor = Color.White // Hvit bakgrunn
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Takflate-verdier:",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp, // Økt størrelse på takflate overskrift
                        color = MaterialTheme.colorScheme.onSurface
                    ),
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Text(
                    text = """
                    Lengde: ${address.height} m
                    Bredde: ${address.width} m
                    Vinkel: ${address.angle}°
                    Retning: ${address.direction}°
                """.trimIndent(),
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontSize = 20.sp, // Økt størrelse på tekstinnhold
                        color = MaterialTheme.colorScheme.onSurface,
                        letterSpacing = 0.5.sp
                    ),
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }
        }




        Spacer(modifier = Modifier.height(12.dp))

        val directionInfo = directionText(address.direction)

        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = 8.dp,
            shape = RoundedCornerShape(16.dp),
            backgroundColor = directionInfo.backgroundColor
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = directionInfo.message,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = directionInfo.textColor
                    ),
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }
        }

        address.longitude
        address.latitude
        val point: Point = Point.fromLngLat(address.latitude, address.longitude)


        LaunchedEffect(point.coordinates()) {
            address.let {
                metViewModel.fetchWeatherData(Coordinates(it.latitude, it.longitude))
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        weatherList?.dailyObservation?.forEach { (date, weatherDataList) ->
            var temperatur: Double? = null
            var solinnstraling: Double? = null

            weatherDataList.dataList.forEach { weatherData ->
                when (weatherData.elementId) {
                    "mean(air_temperature P1D)" -> temperatur = weatherData.value
                    "mean(solar_irradiance P1D)" -> solinnstraling = weatherData.value
                }
            }

            val length = address.height.toInt()
            val width = address.width.toInt()
            val angle = address.angle.toInt()

            if (temperatur != null && solinnstraling != null && length != null && width != null && angle != null) {
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
        Log.d("produksjonsliste", produksjonsliste.toString())

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

                Text(
                    text = "Dato: ${weatherDataForFirstDay.first}",
                    style = MaterialTheme.typography.headlineMedium
                )

                Spacer(modifier = Modifier.height(16.dp))

                val weatherDataList = weatherDataForFirstDay.second //dette aksesserer værlisten med alle målinger for relevant dag
                val observationForTemp = weatherDataList.dataList[0]

                Text(
                    text = "Temperatur: ${observationForTemp.value} ${observationForTemp.unit}",
                    style = MaterialTheme.typography.headlineMedium
                )

                Spacer(modifier = Modifier.height(16.dp))

                val insolationWeatherData = weatherDataList.dataList[1] //solinnstråling

                Text(
                    text = "Solinnstråling: ${insolationWeatherData.value} ${insolationWeatherData.unit}",
                    style = MaterialTheme.typography.headlineMedium
                )
            }

            else -> {
                CircularProgressIndicator()
            }


            // NED HIT
        }

        Spacer(modifier = Modifier.height(16.dp))


        Text(text = "Valgt måned: ${selectedMonth + 1}") // Viser måned som 1–12

        Spacer(modifier = Modifier.height(16.dp))

        val groupedByMonth = produksjonsliste
            .map { (instant, value) ->
                val zdt = instant.atZone(ZoneId.systemDefault())
                zdt.monthValue to value
            }
            .groupBy { it.first }
            .mapValues { entry ->
                entry.value.sumOf { it.second }
            }

        val weatherEntries = weatherList?.dailyObservation ?: emptyList()

        pvgisSolData.forEachIndexed { index, solinnstraaling ->
            val weatherIndex = index * 30  // Henter omtrent én verdi per måned

            val temperatur = weatherEntries.getOrNull(weatherIndex)?.second?.dataList
                ?.firstOrNull { it.elementId == "mean(air_temperature P1D)" }
                ?.value ?: 25.0  //  hvis mangler temp

            val length = address.height.toInt()
            val width = address.width.toInt()
            val angle = address.angle.toInt()

            val beregnet = ProductionCalc(
                panelLengde = length,
                panelBredde = width,
                panelVinkel = angle,
                luftTemperatur = temperatur,
                solinnstraling = solinnstraaling
            )

            val avrundet = String.format("%.2f", beregnet).toDouble()
            ferdigBeregnetProdData.add(avrundet)
        }

        ferdigBeregnetProdData.forEachIndexed{ index, data ->
            Log.d("testpvgisBeregn", "mndnr $index: $data kWh/month")

        }
        Log.d("testpvgisBeregn", " $ferdigBeregnetProdData")


        val monthlyMeasurements = remember(produksjonsliste) {
            generateMonthlyMeasurements(produksjonsliste)
        }
        val pvgisData = remember(ferdigBeregnetProdData) {
            convertPVGISToMeasurements(ferdigBeregnetProdData)
        }


        val displayedData by remember(isMetSelected) {
            mutableStateOf(if (isMetSelected) monthlyMeasurements else pvgisData)
        }

        Column {
            DataSourceSwitch(
                isMetSelected = isMetSelected,
                onToggle = { isMetSelected = it }
            )

            if (displayedData.isNotEmpty()) {
                ChartWithValueDisplay(
                    selectedMonth = null,
                    allMeasurements = displayedData
                )
            } else {
                Text(
                    text = "Ingen data tilgjengelig.",
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }
}




