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

    private val _dataSortedByDate = MutableStateFlow<WeatherList?>(null)
    val dataSortedByDate: StateFlow<WeatherList?> = _dataSortedByDate.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    fun fetchWeatherData(coordinates: Coordinates) {
        viewModelScope.launch {
            try {
                val station: WeatherStation? = metRepo.getWeatherStation(coordinates, 1)
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
                    weatherStation = station,
                    timeMeasured = null

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
                    weatherStation = station,
                    timeMeasured = null
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
                    weatherStation = station,
                    timeMeasured = null
                )

                _dataSortedByDate.value = WeatherList(
                    dataList = listOf(averageTempObj, averageCloudObj, averageSnowObj)
                )

            } catch (e: Exception) {
                _errorMessage.value = "Error fetching data: ${e.message}"
            }
        }
    }
}