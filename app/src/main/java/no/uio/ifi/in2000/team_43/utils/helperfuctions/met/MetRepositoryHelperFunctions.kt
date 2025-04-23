package no.uio.ifi.in2000.team_43.utils.helperfuctions.met

import no.uio.ifi.in2000.team_43.model.met.AllValidYears
import no.uio.ifi.in2000.team_43.model.met.ValidYears
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

fun formatDate(zonedDateTime: ZonedDateTime): String {
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    return formatter.format(zonedDateTime)
}

fun getMostRecentStation(allStationYears: AllValidYears): ValidYears? {

    //check for null values to return
    allStationYears.validYearsList.firstOrNull { it.validTo == null }?.let {
        return it
    }

    //return station w/ latest date
    return allStationYears.validYearsList.maxByOrNull { it.validTo!! }
}