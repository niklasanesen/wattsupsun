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
import no.uio.ifi.in2000.team_43.model.met.WeatherList
import no.uio.ifi.in2000.team_43.model.met.WeatherStation
import no.uio.ifi.in2000.team_43.model.met.ValidYears
import no.uio.ifi.in2000.team_43.model.met.WeatherData
import java.time.Instant
import java.time.temporal.ChronoUnit
import kotlin.math.min

fun calcInsolation(cloudData: Double, snowData: Double, globalRadiation: Double): Double {
    val calcCloud = 1 - (cloudData.toDouble() / 8) * 0.75
    val calcSnow = 1 - min(snowData.toDouble() / 30, 1.0) * 0.3

    return (calcCloud * calcSnow * globalRadiation).toDouble()
}

class MetViewModel(private val metRepo: MetRepository = MetRepository()) : ViewModel() {

    private val _dataSortedByDate = MutableStateFlow<AggregatedData?>(null)
    val dataSortedByDate: StateFlow<AggregatedData?> = _dataSortedByDate.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

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
        val groupedByDate = weatherDataList.groupBy { it.timeMeasured!!.truncatedTo(ChronoUnit.DAYS) }

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
        viewModelScope.launch {
            try {
                val station: WeatherStation? = metRepo.getWeatherStation(coordinates, 1)
                if (station == null) {
                    _errorMessage.value = "Weather station not found"
                    return@launch
                }

                val tempValidYears: ValidYears? = metRepo.getValidTime(station, "mean(air_temperature P1D)")
                if(tempValidYears == null) {
                    _errorMessage.value = "Temp year not found"
                    return@launch
                }

                val cloudValidYears: ValidYears? = metRepo.getValidTime(station, "mean(air_temperature P1D)")
                if(cloudValidYears == null) {
                    _errorMessage.value = "Cloud year not found"
                    return@launch
                }

                val snowValidYears: ValidYears? = metRepo.getValidTime(station, "mean(air_temperature P1D)")
                if(snowValidYears == null) {
                    _errorMessage.value = "Snow year not found"
                    return@launch
                }

                val sunValidYears: ValidYears? = metRepo.getValidTime(station, "mean(air_temperature P1D)")
                if(sunValidYears == null) {
                    _errorMessage.value = "Temp year not found"
                    return@launch
                }

                val tempWeatherList = fetchWeatherDataForElement(tempValidYears, "mean(air_temperature P1D)")
                val cloudWeatherList = fetchWeatherDataForElement(cloudValidYears, "mean(cloud_area_fraction P1D)")
                val snowWeatherList = fetchWeatherDataForElement(snowValidYears, "mean(surface_snow_thickness P1M)")
                val radiationWeatherList = fetchWeatherDataForElement(sunValidYears, "mean(surface_downwelling_shortwave_flux_in_air PT1H)")

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