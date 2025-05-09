package no.uio.ifi.in2000.team_43.ui.info

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.mapbox.geojson.Point
import no.uio.ifi.in2000.team_43.model.PVGIS.MonthlyEntry
import no.uio.ifi.in2000.team_43.model.garfStuff.GrafMeasurement
import no.uio.ifi.in2000.team_43.model.input.LagreAdresse
import no.uio.ifi.in2000.team_43.model.met.Coordinates
import no.uio.ifi.in2000.team_43.ui.PVGIS.PVGISViewModel
import no.uio.ifi.in2000.team_43.ui.info.grafStuff.MonthSelector
import no.uio.ifi.in2000.team_43.ui.info.grafStuff.MonthlyChartWithValueDisplay
import no.uio.ifi.in2000.team_43.ui.info.infoScreenComponents.ValueBox
import no.uio.ifi.in2000.team_43.ui.met.MetViewModel
import no.uio.ifi.in2000.team_43.ui.met.beregnStromproduksjon
import no.uio.ifi.in2000.team_43.ui.pages.HomeScreen
import no.uio.ifi.in2000.team_43.ui.pages.TopAppBar
import java.time.Instant
import java.time.ZoneId

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
    modifier: Modifier = Modifier,
    address: LagreAdresse,
    navController: NavHostController = rememberNavController(),
    pvgisViewModel: PVGISViewModel = viewModel(),
    metViewModel: MetViewModel = viewModel(),

    ) {

    val backStackEntry by navController.currentBackStackEntryAsState()
    val route = backStackEntry?.destination?.route
    val currentScreen = HomeScreen.entries.find { it.route == route } ?: HomeScreen.Input

    //val dataStoreManager = remember { DataStoreManager(context) }
    //val drCalcData by pvgisViewModel.drCalcData.collectAsState() // Henter DRcalc data
    //val drCalcTotalSum by pvgisViewModel.drCalcDataTotalSum.collectAsState() // Henter kalkulert totalsum
    //val stromprisData by stromprisViewModel.data.collectAsState()
    //val context = LocalContext.current


    Scaffold (
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                currentScreen = currentScreen,
                canNavigateBack = navController.previousBackStackEntry != null,
                navigateUp = { navController.navigateUp() }
            )
        }
    ) { innerPadding ->

        // Samler data fra ViewModels
        val pvgisData by pvgisViewModel.data.collectAsState()
        val weatherList by metViewModel.dataSortedByDate.collectAsState() //Henter all værdata
        var isMetSelected by remember { mutableStateOf(true) }
        val produksjonsliste = mutableListOf<Pair<Instant, Double>>()  // strømproduskjon for alle dager i et år
        val pvgisSolData by pvgisViewModel.productionData.collectAsState() // Henter pvgis solinnstrålingsdata
        val ferdigBeregnetProdData = mutableListOf<Double>() // legger inn beregnet strøm produksjonsdata
        val errorMessage by metViewModel.errorMessage.collectAsState()
        val scrollState = rememberScrollState()
        var selectedMonth by remember { mutableIntStateOf(0) } // 0 = Januar
        val dataene: List<MonthlyEntry>? = pvgisData?.outputs?.monthly?.fixed

        dataene?.forEachIndexed() { index, mnd ->
            Log.d("testpvgisM", "global irradiance mnd nr. $index: ${mnd.H_i_m} kWh/m2/month")
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = innerPadding.calculateTopPadding())
                .verticalScroll(scrollState)
                .padding(20.dp)
        ) {

            // Skillelinje
            //HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            //Spacer(modifier = Modifier.height(80.dp)) // avstand fra toppen av kjermen

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
                        ValueBox(
                            label = "Lengde",
                            value = "${address.height} m",
                            modifier = Modifier.weight(1f)
                        )
                        ValueBox(
                            label = "Bredde",
                            value = "${address.width} m",
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Andre rad
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        ValueBox(
                            label = "Vinkel",
                            value = "${address.angle}°",
                            modifier = Modifier.weight(1f)
                        )
                        ValueBox(
                            label = "Retning",
                            value = "${address.direction}°",
                            modifier = Modifier.weight(1f)
                        )
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

            Spacer(modifier = Modifier.height(32.dp))

            val point: Point = Point.fromLngLat(address.latitude, address.longitude)
            LaunchedEffect(point.coordinates()) {
                address.let {
                    metViewModel.fetchWeatherData(Coordinates(it.latitude, it.longitude))
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            val selectedMonth = remember { mutableIntStateOf(0) } // 0 = Januar
            val isLoading = remember { mutableStateOf(true) } // Lastestatus
            val allMeasurements = remember { MutableList(12) { mutableListOf<GrafMeasurement>() } }

            // Fyll dataene (kun én gang)
            LaunchedEffect(weatherList) {
                // Sett isLoading til true før lasting
                isLoading.value = true

                // Simulering av datainnhenting
                weatherList?.dailyObservation?.forEach { (date, weatherDataList) ->
                    var temperatur: Double? = null
                    var solinnstraling: Double? = null

                    weatherDataList.dataList.forEach { weatherData ->
                        when (weatherData.elementId) {
                            "mean(air_temperature P1D)" -> temperatur = weatherData.value
                            "mean(solar_irradiance P1D)" -> solinnstraling = weatherData.value
                        }
                    }

                    val areal = address.height * address.width
                    if (temperatur != null && solinnstraling != null) {
                        isLoading.value = false
                        val produksjon = beregnStromproduksjon(
                            areal = areal,
                            vinkel = address.angle.toInt(),
                            temperatur = temperatur,
                            solinnstraling = solinnstraling,
                            direction = address.direction.toInt()
                        )

                        val zdt = date.atZone(ZoneId.systemDefault())
                        val monthIndex = zdt.monthValue - 1
                        allMeasurements[monthIndex].add(
                            GrafMeasurement(
                                time = zdt.dayOfMonth.toFloat(),
                                value = produksjon.toFloat()
                            )
                        )
                    }
                }

                // Når dataene er lastet, sett isLoading til false
            }

            // Hovedvisning
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    //.padding(bottom = innerPadding.calculateBottomPadding())
            ) {
                // 👉 Legg til MonthSelector
                MonthSelector(
                    selectedMonth = selectedMonth.intValue,
                    onMonthSelected = { selectedMonth.intValue = it }
                )

                // 👉 Vis enten lastesirkel eller graf
                Log.d("isLoading.value", isLoading.value.toString())
                if (isLoading.value) {
                    // Loading-indikator
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                } else {
                    // Når dataene er lastet, vis grafen
                    MonthlyChartWithValueDisplay(
                        selectedMonth = selectedMonth.intValue + 1, // 1-basert måned
                        allMeasurements = allMeasurements
                    )
                }
            }
           // Spacer(modifier = Modifier.height(50.dp))
        }
    }
}