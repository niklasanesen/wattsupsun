package no.uio.ifi.in2000.team_43.ui.met

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import no.uio.ifi.in2000.team_43.data.met.MetRepository
import no.uio.ifi.in2000.team_43.model.met.WeatherList
import no.uio.ifi.in2000.team_43.model.met.WeatherStation
import no.uio.ifi.in2000.team_43.model.met.ValidYears

class MetViewModel : ViewModel() {
    private val metRepo = MetRepository()

    private val _data = MutableStateFlow<WeatherList?>(null)
    val data: StateFlow<WeatherList?> = _data.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    init {
        fetchWeatherData()
    }

    private fun fetchWeatherData() {
        viewModelScope.launch {
            try {
                val station: WeatherStation? = metRepo.getWeatherStation()
                if (station == null) {
                    _errorMessage.value = "Weather station not found"
                    return@launch
                }

                val validYears: ValidYears? = metRepo.getValidTime(station)
                if (validYears == null) {
                    _errorMessage.value = "No valid years found"
                    return@launch
                }

                val weatherList: WeatherList? = metRepo.getDataSet(station, validYears)
                if (weatherList != null) {
                    _data.value = weatherList
                } else {
                    _errorMessage.value = "No weather data available"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error fetching data: ${e.message}"
            }
        }
    }
}