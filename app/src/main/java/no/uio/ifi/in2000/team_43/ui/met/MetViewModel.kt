package no.uio.ifi.in2000.team_43.ui.met

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import no.uio.ifi.in2000.team_43.data.met.MetRepository
import no.uio.ifi.in2000.team_43.model.met.AggregatedData
import no.uio.ifi.in2000.team_43.model.met.Coordinates
import no.uio.ifi.in2000.team_43.model.met.ValidYears
import no.uio.ifi.in2000.team_43.model.met.WeatherData
import no.uio.ifi.in2000.team_43.model.met.WeatherList
import no.uio.ifi.in2000.team_43.model.met.WeatherStation
import java.time.Instant
import java.time.temporal.ChronoUnit
import kotlin.math.min
import kotlin.math.cos
import kotlin.math.PI

fun calcInsolation(cloudData: Double, snowData: Double, globalRadiation: Double): Double {
    val calcCloud = 1 - (cloudData.toDouble() / 8) * 0.75
    val calcSnow = 1 - min(snowData.toDouble() / 30, 1.0) * 0.3
    return (calcCloud * calcSnow * globalRadiation).toDouble()
}

// Beregninger for stromproduksjon

// kort forklart:
// 1. alcGHI gir forveentet effekt basert på skydekke, snø per kvadratmeter
// 2. Gange dette med antall kvadratmeter med flate
// 3. HVIS temp er over 25 kan man forvente tap på -0.3% per grad (kilde: https://www.otovo.no/blog/solcellepanel-solceller/solceller-norge-virkningsgrad/)
// (sidenote: ved under 25 grad kan man få økt produksjon men ikke funnet kilde som begrunner enda)
// 4. Man må anta at bruker har valgt anbefalt optimalvinkel, HVIS IKKE: egen formel som justerer


fun beregnStromproduksjon(
    solinnstraling: Double, // data hentet via calcGHI i kwh/m2
    paneletsKvadratmeter: Double, // i m2
    temperatur: Double, // fra met?
    anbefaltVinkelValgt: Boolean, // Legg inn false hvis bruker IKKE har valgt optimal vinkel
    avvikVinkel: Double // Antall grader vinkelen avviker fra
): Double {

    val OPTIMAL_TEMP: Double = 25.0;
    val FEIL_TEMP_KORREKSJON: Double = 0.003; // 0.3%
    var beregnetSum: Double = 0.0; // kwh/m2

    beregnetSum = solinnstraling  * paneletsKvadratmeter;

    if (temperatur > OPTIMAL_TEMP) {
        val tempOverOptimal = temperatur - OPTIMAL_TEMP;
        val korreksjonsfaktor = 1 - (tempOverOptimal * FEIL_TEMP_KORREKSJON);

        beregnetSum = beregnetSum * korreksjonsfaktor

    }

    if(!anbefaltVinkelValgt) {
        beregnetSum = beregnEffekttap(beregnetSum, avvikVinkel)
    }

    return beregnetSum;
}

// Hjelpefunksjon for beregnStromproduksjon()
fun beregnEffekttap(optimalEffekt: Double, avvikVinkelGrader: Double): Double {
    val vinkelRad = avvikVinkelGrader * PI / 180  // konverter til radianer (altså fra grader til tallverdi)
    val effekt = optimalEffekt * cos(vinkelRad)
    return effekt.coerceAtLeast(0.0)
}

class MetViewModel(private val metRepo: MetRepository = MetRepository()) : ViewModel() {

    private val _dataSortedByDate = MutableStateFlow<AggregatedData?>(null)
    val dataSortedByDate: StateFlow<AggregatedData?> = _dataSortedByDate.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    var nearestMaxCount = 1
    val maxAttempts = 31 //the amount of stations that match the relevant elements in Norway

    fun calcInsolation(cloudData: Double, snowData: Double, globalRadiation: Double): Double {
        val calcCloud = 1 - (cloudData.toDouble() / 8) * 0.75
        val calcSnow = 1 - min(snowData.toDouble() / 30, 1.0) * 0.3

        return (calcCloud * calcSnow * globalRadiation).toDouble()
    }

    suspend fun tryGetValidYear(coordinates: Coordinates, nearestMaxCount: Int, attempts: Int = 0): ValidYears? {
        println("MetViewModel Trying to get valid year for station, attempt $attempts")
        if (attempts >= maxAttempts) {
            _errorMessage.value = "Unable to find valid years after $maxAttempts attempts."
            return null
        }

        val station: WeatherStation? = metRepo.getWeatherStation(coordinates, nearestMaxCount)

        if (station == null) {
            _errorMessage.value = "Weather station not found or no valid year found"
            return null
        }

        val validYears: ValidYears? = metRepo.getValidTime(station)

        if (validYears != null) {
            return validYears
        }

        return tryGetValidYear(coordinates, nearestMaxCount + 1, attempts + 1)
    }

    private suspend fun fetchWeatherDataForElement(validYears: ValidYears, elementId: String): WeatherList? {
        val validYearsElement = ValidYears(
            elementId = elementId,
            validFrom = validYears.validFrom,
            validTo = validYears.validTo,
            weatherStation = validYears.weatherStation
        )

        return when (elementId) {
            "mean(air_temperature P1D)" -> metRepo.getDataSet(validYearsElement.weatherStation, validYearsElement)
            "mean(cloud_area_fraction P1D)" -> metRepo.getDataSet(validYearsElement.weatherStation, validYearsElement)
            "mean(surface_snow_thickness P1M)" -> metRepo.getSnowThickness(validYearsElement.weatherStation, validYearsElement)
            "mean(surface_downwelling_shortwave_flux_in_air PT1H)" -> metRepo.getGlobalRadiationData(validYearsElement.weatherStation, validYearsElement)
            else -> null
        }
    }

    private fun aggregateDataByDate(weatherDataList: List<WeatherData>): List<Pair<Instant, WeatherList>> {
        //Group the weather data by date
        val groupedByDate = weatherDataList.groupBy { it.timeMeasured.truncatedTo(ChronoUnit.DAYS) }

        groupedByDate.forEach { date ->
            println("groupCheck Given date: ${date.key} which has amount of values: ${date.value.size}")
        }
        // Map the grouped data to a list of pairs
        return groupedByDate.map { (date, weatherDataForDay) ->
            val temp = weatherDataForDay.find { it.elementId == "mean(air_temperature P1D)" }
            val cloud = weatherDataForDay.find { it.elementId == "mean(cloud_area_fraction P1D)" }
            val snow = weatherDataForDay.find { it.elementId == "mean(surface_snow_thickness P1M)" }
            val radiation = weatherDataForDay.find { it.elementId == "mean(surface_downwelling_shortwave_flux_in_air PT1H)" }

            val insolationForDate = WeatherData(
                elementId = "mean(solar_irradiance P1D)",
                unit = "W/m2",
                value = calcInsolation(cloud!!.value, snow!!.value, radiation!!.value),
                timeMeasured = weatherDataForDay[0].timeMeasured,
                weatherStation = weatherDataForDay[0].weatherStation
            )
            val completeList = listOf<WeatherData>(temp!!, insolationForDate)

            Pair(date, WeatherList(dataList = completeList))
        }.sortedBy { it.first }
    }

    fun fetchWeatherData(coordinates: Coordinates) {
        println("MetViewModel fetchWeatherData() launched")
        viewModelScope.launch {
            try {

                val validYears = tryGetValidYear(coordinates, nearestMaxCount)

                if (validYears == null) {
                    return@launch
                }

                // Fetch weather data for different elements
                val tempWeatherList = fetchWeatherDataForElement(validYears, "mean(air_temperature P1D)")
                val cloudWeatherList = fetchWeatherDataForElement(validYears, "mean(cloud_area_fraction P1D)")
                val snowWeatherList = fetchWeatherDataForElement(validYears, "mean(surface_snow_thickness P1M)")
                val radiationWeatherList = fetchWeatherDataForElement(validYears, "mean(surface_downwelling_shortwave_flux_in_air PT1H)")

                // Check for null responses
                if (tempWeatherList == null || cloudWeatherList == null || snowWeatherList == null || radiationWeatherList == null) {
                    _errorMessage.value = "No weather data available for one or more elements."
                    return@launch
                }
                
                // Aggregate data by date
                val aggregatedData = aggregateDataByDate(
                    tempWeatherList.dataList + cloudWeatherList.dataList + snowWeatherList.dataList + radiationWeatherList.dataList
                )

                // Create the AggregatedData object
                _dataSortedByDate.value = AggregatedData(dailyObservation = aggregatedData)

            } catch (e: Exception) {
                println("Error fetching data: ${e.message}")
                _errorMessage.value = "Error fetching data: ${e.message}"
            }
        }
    }
}