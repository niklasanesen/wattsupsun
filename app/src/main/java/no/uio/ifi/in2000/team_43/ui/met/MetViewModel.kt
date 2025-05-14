package no.uio.ifi.in2000.team_43.ui.met

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import no.uio.ifi.in2000.team_43.data.met.MetRepository
import no.uio.ifi.in2000.team_43.model.graphRes.GraphMeasurement
import no.uio.ifi.in2000.team_43.model.met.AggregatedData
import no.uio.ifi.in2000.team_43.model.met.Coordinates
import no.uio.ifi.in2000.team_43.model.met.ValidYears
import no.uio.ifi.in2000.team_43.model.met.WeatherData
import no.uio.ifi.in2000.team_43.model.met.WeatherList
import no.uio.ifi.in2000.team_43.model.met.WeatherStation
import java.time.Instant
import java.time.ZoneId
import java.time.temporal.ChronoUnit
import kotlin.math.min

// Calculations for electricity production

// briefly explained:
// 1. alcGHI gives the expected effect based on cloud cover, snow per square meter
// 2. Multiply this by the number of square meters of surface
// 3. IF the temperature is above 25, you can expect a loss of -0.3% per degree (source: https://www.otovo.no/blog/solcellepanel-solceller/solceller-norge-virkningsgrad/)
// (sidenote: at below 25 degrees you can get increased production but no source has been found to justify it yet)
// 4. It must be assumed that the user has chosen the recommended optimal angle, IF NOT: own formula that adjusts

fun calculateElectricityProduction(
    areal: Double,
    angle: Int,                 // actual angle in degrees
    temperature: Double,          // air temperature in degrees C
    solarInsolation: Double,       // Wh/m²
    direction: Int
): Double {
    val optimalAngle = 35.0 // Assumed optimal angle in Norway
    val optimalTemp = 25.0
    val tempLossPerDegree = 0.003 // 0.3 % loss per degree over 25 °C

    // 1. Start with expected production
    var production = (solarInsolation/1000) * areal

    // 2. Temperature loss
    if (temperature > optimalTemp) {
        val tempOver = temperature - optimalTemp
        val correctionFactor = 1 - (tempOver * tempLossPerDegree)
        production *= correctionFactor
    }

    // 3. Angle deviation (effect loss if angle ≠ optimal)
    val angleDeviation = kotlin.math.abs(angle - optimalAngle)
    production = calculateEffectLoss(production, angleDeviation)

    // Adjust for direction
    production *= when (direction) {
        0 -> 0.258
        90 -> 0.76
        270 -> 0.75
        180 -> 1.0
        else -> 1.0
    }

    return production
}


fun calculateEffectLoss(production: Double, deviatingAngle: Double): Double {
    // Rough approach: 1 % effect loss per 1 degree of deviation
    val tapPerGrad = 0.01
    val effectLoss = 1 - (deviatingAngle * tapPerGrad)
    return production * effectLoss.coerceAtLeast(0.0)
}

fun convertMetToMeasurements(data: List<Pair<Instant, Double>>): List<GraphMeasurement> {
    return data
        .map { (instant, value) ->
            val zdt = instant.atZone(ZoneId.systemDefault())
            zdt.monthValue to value
        }
        .groupBy { it.first }
        .mapValues { entry ->
            entry.value.sumOf { it.second }
        }
        .map { (month, totalValue) ->
            GraphMeasurement(
                time = month.toFloat(),
                value = totalValue.toFloat()
            )
        }
        .sortedBy { it.time }
}


class MetViewModel(private val metRepo: MetRepository = MetRepository()) : ViewModel() {

    private val _dataSortedByDate = MutableStateFlow<AggregatedData?>(null)
    val dataSortedByDate: StateFlow<AggregatedData?> = _dataSortedByDate.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private var nearestMaxCount = 1
    private val maxAttempts = 31 //the amount of stations that match the relevant elements in Norway

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

    suspend fun tryGetLastElement(coordinates: Coordinates, nearestMaxCount: Int, otherStationValidYears: ValidYears, attempts: Int = nearestMaxCount - 1): ValidYears? {
        if (attempts >= maxAttempts) {
            _errorMessage.value = "Unable to find valid years after $maxAttempts attempts."
            return null
        }

        val station: WeatherStation? = metRepo.getWeatherStation(coordinates, nearestMaxCount)

        if (station == null) {
            _errorMessage.value = "Weather station not found or no valid year found"
            return null
        }

        val validYears: ValidYears? = metRepo.getValidTimeOneObject(station, otherStationValidYears.elementId, otherStationValidYears.validFrom, otherStationValidYears.validTo)

        if (validYears != null) {
            return validYears
        }

        return tryGetLastElement(coordinates, nearestMaxCount + 1, otherStationValidYears, attempts + 1)
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
                if (validYears == null) return@launch

                val standardElementIds = listOf(
                    "mean(air_temperature P1D)",
                    "mean(cloud_area_fraction P1D)",
                    "mean(surface_snow_thickness P1M)",
                    "mean(surface_downwelling_shortwave_flux_in_air PT1H)"
                )

                val weatherElements = mutableMapOf<String, WeatherList>()

                // Check if we can get a more specific lastElement
                if (validYears.elementId != "success") {
                    val lastElement = tryGetLastElement(coordinates, nearestMaxCount, validYears)
                    if (lastElement == null) return@launch

                    val fetchedLast = fetchWeatherDataForElement(lastElement, validYears.elementId)
                    if (fetchedLast != null && standardElementIds.contains(lastElement.elementId)) {
                        weatherElements[lastElement.elementId] = fetchedLast
                    }
                }

                // Fetch only the missing default elements
                for (elementId in standardElementIds) {
                    if (!weatherElements.containsKey(elementId)) {
                        val data = fetchWeatherDataForElement(validYears, elementId)
                        if (data == null) {
                            _errorMessage.value = "No weather data available for $elementId."
                            return@launch
                        }
                        weatherElements[elementId] = data
                    }
                }

                val aggregatedData = aggregateDataByDate(
                    weatherElements.values.flatMap { it.dataList }
                )

                _dataSortedByDate.value = AggregatedData(dailyObservation = aggregatedData)


            } catch (e: Exception) {
                println("Error fetching data: ${e.message}")
                _errorMessage.value = "Error fetching data: ${e.message}"
            }
        }
    }
}