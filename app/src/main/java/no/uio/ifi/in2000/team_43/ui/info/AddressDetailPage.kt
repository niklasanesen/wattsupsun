package no.uio.ifi.in2000.team_43.ui.info

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
import no.uio.ifi.in2000.team_43.data.CoordinateCache
import no.uio.ifi.in2000.team_43.model.garfStuff.GrafMeasurement
import no.uio.ifi.in2000.team_43.model.input.LagreAdresse
import no.uio.ifi.in2000.team_43.model.met.Coordinates
import no.uio.ifi.in2000.team_43.ui.info.grafStuff.MonthSelector
import no.uio.ifi.in2000.team_43.ui.info.grafStuff.MonthlyChartWithValueDisplay
import no.uio.ifi.in2000.team_43.ui.info.infoScreenComponents.ValueBox
import no.uio.ifi.in2000.team_43.ui.met.MetViewModel
import no.uio.ifi.in2000.team_43.ui.met.beregnStromproduksjon
import no.uio.ifi.in2000.team_43.ui.pages.HomeScreen
import no.uio.ifi.in2000.team_43.ui.pages.TopAppBar
import java.time.ZoneId


@OptIn(ExperimentalMaterial3Api::class) // If using Experimental APIs
@Composable
fun AddressDetailPage(
    modifier: Modifier = Modifier,
    address: LagreAdresse,
    navController: NavHostController = rememberNavController(),
    metViewModel: MetViewModel = viewModel(),

    ) {

    val weatherList by metViewModel.dataSortedByDate.collectAsState()
    val allMeasurements = remember { MutableList(12) { mutableListOf<GrafMeasurement>() } }

    val backStackEntry by navController.currentBackStackEntryAsState()
    val route = backStackEntry?.destination?.route
    val currentScreen = HomeScreen.entries.find { it.route == route } ?: HomeScreen.Input
    val scrollState = rememberScrollState()

    val selectedMonth = remember { mutableIntStateOf(0) } // 0 = Januar
    val isLoading = remember { mutableStateOf(true) } // Lastestatus

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
    }
    val point: Point = Point.fromLngLat(address.latitude, address.longitude)
    LaunchedEffect(point.coordinates()) {
        address.let {
            if (it != CoordinateCache.lastCoordinates ) {
                metViewModel.fetchWeatherData(Coordinates(it.latitude, it.longitude))
            }
        }
    }


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




        // ---- input data ----
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = innerPadding.calculateTopPadding())
                .verticalScroll(scrollState)
                .padding(20.dp)
        ) {
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








            Spacer(modifier = Modifier.height(32.dp))













            // ---- garf and month selector ----
            Column(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                MonthSelector(
                    selectedMonth = selectedMonth.intValue,
                    onMonthSelected = { selectedMonth.intValue = it }
                )
                if (isLoading.value) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                } else {
                    MonthlyChartWithValueDisplay(
                        selectedMonth = selectedMonth.intValue + 1,
                        allMeasurements = allMeasurements
                    )
                }
            }
        }











    }
}