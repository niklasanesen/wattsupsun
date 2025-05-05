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
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.mutableStateOf
import no.uio.ifi.in2000.team_43.ui.met.beregnStromproduksjon
import java.time.Instant
import androidx.compose.runtime.setValue
import androidx.navigation.NavController
import com.mapbox.geojson.Point
import no.uio.ifi.in2000.team_43.model.PVGIS.MonthlyEntry
import no.uio.ifi.in2000.team_43.model.input.LagreAdresse
import no.uio.ifi.in2000.team_43.ui.PVGIS.convertPVGISToMeasurements
import no.uio.ifi.in2000.team_43.ui.sharedScreenComponents.ChartWithValueDisplay //graf
import no.uio.ifi.in2000.team_43.utils.helperfuctions.calculations.ProductionCalc
import java.time.ZoneId
import no.uio.ifi.in2000.team_43.ui.met.generateMonthlyMeasurements
import no.uio.ifi.in2000.team_43.ui.sharedScreenComponents.DataSourceSwitch
import no.uio.ifi.in2000.team_43.ui.sharedScreenComponents.GrafMeasurement
import no.uio.ifi.in2000.team_43.ui.sharedScreenComponents.MonthSelector
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api


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




@OptIn(ExperimentalMaterial3Api::class) // If using Experimental APIs
@Composable
fun AddressDetailPage(
    address: LagreAdresse,
    navController: NavController,
    modifier: Modifier = Modifier,
    pvgisViewModel: PVGISViewModel = viewModel(),
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

    TopAppBar(
        title = { Text(address.addressString) },
        navigationIcon = {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Tilbake")
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color(0xff88aabd),
            titleContentColor = Color.White
        )
    )


    Column(modifier = modifier.padding(20.dp).verticalScroll(scrollState)) {

        // Skillelinje
        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

        Spacer(modifier = Modifier.height(80.dp)) // avstand fra toppen av kjermen

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
                    ValueBox(label = "Lengde", value = "${address.height} m", modifier = Modifier.weight(1f))
                    ValueBox(label = "Bredde", value = "${address.width} m", modifier = Modifier.weight(1f))
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Andre rad
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ValueBox(label = "Vinkel", value = "${address.angle}°", modifier = Modifier.weight(1f))
                    ValueBox(label = "Retning", value = "${address.direction}°", modifier = Modifier.weight(1f))
                }
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



        Column {
            MonthSelector(
                selectedMonth = selectedMonth,
                onMonthSelected = { newMonth ->
                    selectedMonth = newMonth
                }
            )

            // Filtrer og transformer data for valgt måned
            val measurements = produksjonsliste
                .map { (instant, value) ->
                    val zdt = instant.atZone(ZoneId.systemDefault())
                    zdt to value
                }
                .filter { (zdt, _) -> zdt.monthValue == selectedMonth + 1 }
                .map { (zdt, value) ->
                    GrafMeasurement(
                        time = zdt.dayOfMonth.toFloat(),
                        value = value.toFloat()
                    )
                }

            // Vis graf
            if (measurements.isNotEmpty()) {
                ChartWithValueDisplay(
                    selectedMonth = selectedMonth,
                    allMeasurements = measurements
                )
            } else {
                Text(
                    text = "Ingen data for valgt måned.",
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }
}




