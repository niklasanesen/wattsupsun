package no.uio.ifi.in2000.team_43.ui.met

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


fun calcCCF(x: Int): Double {
    return 1 - (x.toDouble() / 8) * 0.75
}

fun calcSCF(x: Int): Double {
    return 1 - min(x.toDouble() / 30, 1.0) * 0.3
}

fun calcGHI(x: Int, y: Int, z: Int): Double {
    return (x * y * z).toDouble()
}



class MetViewModel(private val metRepo: MetRepository = MetRepository()) : ViewModel() {

    private val _dataSortedByDate = MutableStateFlow<AggregatedData?>(null)
    val dataSortedByDate: StateFlow<AggregatedData?> = _dataSortedByDate.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    var nearestMaxCount = 1
    val maxAttempts = 31 //the amount of stations that match the relevant elements in Norway

    suspend fun tryGetValidYear(coordinates: Coordinates, nearestMaxCount: Int, attempts: Int = 0): ValidYears? {
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

        // Map the grouped data to a list of pairs
        return groupedByDate.map { (date, weatherDataForDay) ->
            Pair(date, WeatherList(dataList = weatherDataForDay))
        }.sortedBy { it.first }
    }

    fun fetchWeatherData(coordinates: Coordinates) {
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
                _errorMessage.value = "Error fetching data: ${e.message}"
            }
        }
    }
}