package no.uio.ifi.in2000.team_43.data.met

import no.uio.ifi.in2000.team_43.model.met.ValidYears
import no.uio.ifi.in2000.team_43.model.met.WeatherStation

class MetRepository() {
    private val metDataSource = MetDataSource()

    suspend fun getWeatherStation(): WeatherStation? {
        return metDataSource.getWeatherStationData()
    }

    suspend fun getValidTime(weatherStation: WeatherStation): ValidYears? {
        return metDataSource.getStartTime(weatherStation)
    }
}