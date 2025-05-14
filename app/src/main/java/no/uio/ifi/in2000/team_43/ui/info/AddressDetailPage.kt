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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import no.uio.ifi.in2000.team_43.data.CoordinateCache
import no.uio.ifi.in2000.team_43.model.graphRes.GraphMeasurement
import no.uio.ifi.in2000.team_43.model.input.SavedAddress
import no.uio.ifi.in2000.team_43.model.met.Coordinates
import no.uio.ifi.in2000.team_43.ui.info.graphComponents.MonthSelector
import no.uio.ifi.in2000.team_43.ui.info.graphComponents.MonthlyChartWithValueDisplay
import no.uio.ifi.in2000.team_43.ui.info.infoScreenComponents.EnovaCard
import no.uio.ifi.in2000.team_43.ui.info.infoScreenComponents.TotalCostLogic
import no.uio.ifi.in2000.team_43.ui.info.infoScreenComponents.TotalSavedLogic
import no.uio.ifi.in2000.team_43.ui.info.infoScreenComponents.ValueBox
import no.uio.ifi.in2000.team_43.ui.met.MetViewModel
import no.uio.ifi.in2000.team_43.ui.met.calculateElectricityProduction
import no.uio.ifi.in2000.team_43.ui.pages.HomeScreen
import no.uio.ifi.in2000.team_43.ui.pages.TopAppBar
import no.uio.ifi.in2000.team_43.utils.helperfuctions.calculations.calculateEnova
import java.time.ZoneId


@Composable
fun AddressDetailPage(
    modifier: Modifier = Modifier,
    address: SavedAddress,
    navController: NavHostController = rememberNavController(),
    metViewModel: MetViewModel = viewModel(),
    ) {

    val weatherList by metViewModel.dataSortedByDate.collectAsState()
    val allMeasurements = remember { MutableList(12) { mutableListOf<GraphMeasurement>() } }

    val backStackEntry by navController.currentBackStackEntryAsState()
    val route = backStackEntry?.destination?.route
    val currentScreen = HomeScreen.entries.find { it.route == route } ?: HomeScreen.Input
    val scrollState = rememberScrollState()

    val selectedMonth = remember { mutableIntStateOf(0) } // 0 = January
    var isLoading by remember { mutableStateOf(true) } // Loading status


    // Fill in the data (only once)
    LaunchedEffect(weatherList) {
        // Sett isLoading til true før lasting
        isLoading = true

        // Simulation of data fetch
        weatherList?.dailyObservation?.forEach { (date, weatherDataList) ->
            var temperature: Double? = null
            var solarInsolation: Double? = null

            weatherDataList.dataList.forEach { weatherData ->
                when (weatherData.elementId) {
                    "mean(air_temperature P1D)" -> temperature = weatherData.value
                    "mean(solar_irradiance P1D)" -> solarInsolation = weatherData.value
                }
            }

            val areal = address.height * address.width
            if (temperature != null && solarInsolation != null) {
                isLoading = false
                val production = calculateElectricityProduction(
                    areal = areal,
                    angle = address.angle.toInt(),
                    temperature = temperature,
                    solarInsolation = solarInsolation,
                    direction = address.direction.toInt()
                )

                val zdt = date.atZone(ZoneId.systemDefault())
                val monthIndex = zdt.monthValue - 1
                allMeasurements[monthIndex].add(
                    GraphMeasurement(
                        time = zdt.dayOfMonth.toFloat(),
                        value = production.toFloat()
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


            // Total money card on screen
            // pls ask before touching any of this
            Spacer(modifier = Modifier.height(16.dp))
            TotalSavedLogic(address.moneySaved, isLoading)

            // total price for panels
            // Pls ask before touching this
            Spacer(modifier = Modifier.height(16.dp))
            TotalCostLogic(address.numPanel, address.totalPanelPrice, false)

            // Enova on info screen:
            val wPerPanel = address.kwp.toDouble()
            val kwpPerPanel = (wPerPanel / 1000.0)

            Spacer(modifier = Modifier.height(16.dp))

            EnovaCard(calculateEnova(kwpPerPanel, address.numPanel))

            Spacer(modifier = Modifier.height(32.dp))

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
                    // First row
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

            // ---- graph and month selector ----
            Column(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                MonthSelector(
                    selectedMonth = selectedMonth.intValue,
                    onMonthSelected = { selectedMonth.intValue = it }
                )
                if (isLoading) {
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