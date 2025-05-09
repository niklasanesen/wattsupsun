package no.uio.ifi.in2000.team_43.utils.helperfuctions.met

import no.uio.ifi.in2000.team_43.model.met.AllValidYears
import no.uio.ifi.in2000.team_43.model.met.ValidYears
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

fun formatDate(time: Instant): String {
    val zonedDateTime = time.atZone((ZoneId.of("UTC")))
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    return formatter.format(zonedDateTime)
}

fun getMostRecentStation(allStationYears: AllValidYears): ValidYears? {

    return allStationYears.validYearsList.maxByOrNull { it.validTo }
}