package no.uio.ifi.in2000.team_43.data.met

import no.uio.ifi.in2000.team_43.data.APIClient
import no.uio.ifi.in2000.team_43.model.met.Coordinates
import no.uio.ifi.in2000.team_43.model.met.ValidYears
import no.uio.ifi.in2000.team_43.model.met.WeatherList
import no.uio.ifi.in2000.team_43.model.met.WeatherStation
import no.uio.ifi.in2000.team_43.utils.helperfuctions.met.formatDate
import no.uio.ifi.in2000.team_43.utils.helperfuctions.met.getMostRecentStation
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime

class MetRepository(private val metDataSource: MetDataSource = MetDataSource(APIClient.client)) {

    suspend fun getWeatherStation(coordinates: Coordinates, nearestMax: Int): WeatherStation? {
        return metDataSource.getWeatherStationData(coordinates)
    }

    suspend fun getValidTime(weatherStation: WeatherStation, weatherElement: String): ValidYears? {
        val allTimes = metDataSource.getStartTime(weatherStation, weatherElement)
        return allTimes?.validYearsList?.takeIf { it.isNotEmpty() }?.let { getMostRecentStation(allTimes) }
    }

    suspend fun getDataSet(weatherStation: WeatherStation, validYears: ValidYears): WeatherList? {

        val endDate: ZonedDateTime

        if (validYears.validTo == null) {
            val today = Instant.parse("2025-01-01T00:00:00Z")
            endDate = today.atZone(ZoneId.of("UTC"))
        } else {
            endDate = validYears.validTo.atZone((ZoneId.of("UTC")))
        }

        val startDate = endDate.minusYears(1)

        val formattedStartDate = formatDate(startDate)
        val formattedEndDate = formatDate(endDate)

        return metDataSource.getWeatherData(weatherStation, validYears.elementId, formattedStartDate, formattedEndDate)
    }

    suspend fun getGlobalRadiationData(weatherStation: WeatherStation, validYears: ValidYears): WeatherList? {
        //TODO: Implement global radiation
        return null
    }

    suspend fun getSnowThickness(weatherStation: WeatherStation, validYears: ValidYears): WeatherList? {
        //TODO: implement snow thickness
        return null
    }
}