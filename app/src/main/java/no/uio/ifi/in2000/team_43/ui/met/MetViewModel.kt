package no.uio.ifi.in2000.team_43.ui.met

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import no.uio.ifi.in2000.team_43.data.met.MetRepository
import no.uio.ifi.in2000.team_43.model.met.Coordinates
import no.uio.ifi.in2000.team_43.model.met.WeatherList
import no.uio.ifi.in2000.team_43.model.met.WeatherStation
import no.uio.ifi.in2000.team_43.model.met.ValidYears
import no.uio.ifi.in2000.team_43.model.met.WeatherData
import kotlin.math.min
import kotlin.math.cos
import kotlin.math.PI


// test

fun calcCCF(x: Int): Double {
    return 1 - (x.toDouble() / 8) * 0.75
}

fun calcSCF(x: Int): Double {
    return 1 - min(x.toDouble() / 30, 1.0) * 0.3
}

fun calcGHI(x: Int, y: Int, z: Int): Double {
    return (x * y * z).toDouble()
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





class MetViewModel : ViewModel() {
    private val metRepo = MetRepository()

    private val _data = MutableStateFlow<WeatherList?>(null)
    val data: StateFlow<WeatherList?> = _data.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    fun fetchWeatherData(coordinates: Coordinates) {
        viewModelScope.launch {
            try {
                val station: WeatherStation? = metRepo.getWeatherStation(coordinates)
                if (station == null) {
                    _errorMessage.value = "Weather station not found"
                    return@launch
                }

                val validYearsTemp: ValidYears? = metRepo.getValidTime(station, "mean(air_temperature P1Y)")
                if (validYearsTemp == null) {
                    _errorMessage.value = "No valid years found"
                    return@launch
                }

                val tempWeatherList: WeatherList? = metRepo.getDataSet(station, validYearsTemp)
                var averageTempData = 0.0
                if (tempWeatherList != null) {
                    tempWeatherList.dataList.forEach { observation ->
                        averageTempData += observation.value
                    }
                    averageTempData = averageTempData/tempWeatherList.dataList.size
                } else {
                    _errorMessage.value = "No weather data available"
                }

                val averageTempObj = WeatherData(
                    elementId = "mean(air_temperature P1Y)",
                    value = averageTempData,
                    unit = "Celsius",
                    weatherStation = station

                )

                val validYearsCloud: ValidYears? = metRepo.getValidTime(station, "mean(cloud_area_fraction P1Y)")
                if (validYearsCloud == null) {
                    _errorMessage.value = "No valid years found"
                    return@launch
                }

                val cloudWeatherList: WeatherList? = metRepo.getDataSet(station, validYearsCloud)
                var averageCloudData = 0.0
                if (cloudWeatherList != null) {
                    cloudWeatherList.dataList.forEach { observation ->
                        averageCloudData += observation.value
                    }
                    averageCloudData = averageCloudData/cloudWeatherList.dataList.size
                } else {
                    _errorMessage.value = "No weather data available"
                }

                val averageCloudObj = WeatherData(
                    elementId = "mean(cloud_area_fraction P1Y)",
                    value = averageCloudData,
                    unit = "Octas",
                    weatherStation = station
                )

                val validYearsSnow: ValidYears? = metRepo.getValidTime(station, "mean(snow_coverage_type P1M)")
                if (validYearsSnow == null) {
                    _errorMessage.value = "No valid years found"
                    return@launch
                }

                val snowWeatherList: WeatherList? = metRepo.getDataSet(station, validYearsSnow)
                var averageSnowData = 0.0
                if (snowWeatherList != null) {
                    snowWeatherList.dataList.forEach { observation ->
                        averageSnowData += observation.value
                    }
                    averageSnowData = averageSnowData/snowWeatherList.dataList.size
                } else {
                    _errorMessage.value = "No weather data available"
                }

                val snowDesc: String =
                    if (averageSnowData < 1.5 && averageSnowData >= 0.5) {
                        "Mest bar mark"
                    } else if (averageSnowData < 2.5 && averageSnowData >= 1.5) {
                        "Like mye snødekt som bar mark"
                    } else if (averageSnowData < 3.5 && averageSnowData >= 2.5) {
                        "Mest snødekt mark"
                    } else if (averageSnowData >= 3.5) {
                        "Snø overalt"
                    } else {
                        "Ikke snø"
                    }

                val averageSnowObj = WeatherData(
                    elementId = "mean(snow_coverage_type P1M)",
                    value = averageSnowData,
                    unit = snowDesc,
                    weatherStation = station
                )

                _data.value = WeatherList(
                    dataList = listOf(averageTempObj, averageCloudObj, averageSnowObj)
                )

            } catch (e: Exception) {
                _errorMessage.value = "Error fetching data: ${e.message}"
            }
        }
    }
}