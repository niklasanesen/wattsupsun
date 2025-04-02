package no.uio.ifi.in2000.team_43.data.met

import no.uio.ifi.in2000.team_43.model.met.Coordinates
import no.uio.ifi.in2000.team_43.model.met.ValidYears
import no.uio.ifi.in2000.team_43.model.met.WeatherList
import no.uio.ifi.in2000.team_43.model.met.WeatherStation

class MetRepository() {
    private val metDataSource = MetDataSource()

    suspend fun getWeatherStation(coordinates: Coordinates): WeatherStation? {
        return metDataSource.getWeatherStationData(coordinates)
    }

    suspend fun getValidTime(weatherStation: WeatherStation, weatherElement: String): ValidYears? {
        return metDataSource.getStartTime(weatherStation, weatherElement)
    }

    suspend fun getDataSet(weatherStation: WeatherStation, validYears: ValidYears): WeatherList? {
        return metDataSource.getWeatherData(weatherStation, validYears)
    }
}