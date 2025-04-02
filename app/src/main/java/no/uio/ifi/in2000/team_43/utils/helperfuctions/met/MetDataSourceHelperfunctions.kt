package no.uio.ifi.in2000.team_43.utils.helperfuctions.met

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import java.time.Instant
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

fun formatDate(zonedDateTime: ZonedDateTime): String {
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    return formatter.format(zonedDateTime)
}

fun getMostRecentStation(stations: JsonArray): JsonObject? {
    return stations.mapNotNull { station ->
        val stationObj = station.asJsonObject
        val dateRes = stationObj.get("validTo")

        // If `validTo` is missing or explicitly null, return this station immediately
        if (dateRes == null || dateRes.isJsonNull) {
            return stationObj
        }

        try {
            stationObj to Instant.parse(dateRes.asString)
        } catch (e: Exception) {
            e.printStackTrace()
            println("Skipping station due to invalid date format: ${dateRes.asString}")
            null
        }
    }.maxByOrNull { it.second }?.first // Get the station with the latest `validTo`
}