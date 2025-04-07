package no.uio.ifi.in2000.team_43.data.met

import com.google.gson.JsonParser
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpHeaders
import no.uio.ifi.in2000.team_43.data.APIClient
import no.uio.ifi.in2000.team_43.model.met.Coordinates
import no.uio.ifi.in2000.team_43.model.met.ValidYears
import no.uio.ifi.in2000.team_43.model.met.WeatherData
import no.uio.ifi.in2000.team_43.model.met.WeatherList
import no.uio.ifi.in2000.team_43.model.met.WeatherStation
import no.uio.ifi.in2000.team_43.utils.helperfuctions.met.formatDate
import no.uio.ifi.in2000.team_43.utils.helperfuctions.met.getMostRecentStation
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.Base64

class MetDataSource {
    val clientId = "576d17b2-5647-4cd4-ba76-e852f7e4b6cb"
    val encoded = String(Base64.getEncoder().encode("$clientId:".toByteArray(Charsets.UTF_8)))

    suspend fun getWeatherStationData(coordinates: Coordinates): WeatherStation? {
        var url = "https://frost.met.no/sources/v0.jsonld?"
        url += "elements=mean(cloud_area_fraction P1Y),mean(air_temperature P1Y),mean(snow_coverage_type P1M)&"
        url += "geometry=" + "nearest(POINT(${coordinates.longitude} ${coordinates.latitude}))"


        // Debugging: Print the final API URL
        println("Final API URL: $url")

        return try {
            val response: HttpResponse = APIClient.client.get(url) {
                headers {
                    append(HttpHeaders.Authorization, "Basic $encoded")
                }
            }

            if (response.status.value != 200) {
                println("API request failed with status: ${response.status.value}")
                null
            } else {
                val json = response.bodyAsText()
                println("API Response: $json")  // Debugging line
                val jsonObject = JsonParser.parseString(json).asJsonObject

                if (!jsonObject.has("data") || jsonObject.getAsJsonArray("data").size() == 0) {
                    println("No weather stations found in API response")
                    return null
                }

                val stations = jsonObject.getAsJsonArray("data")
                val firstStation = stations[0].asJsonObject

                return WeatherStation(
                    id = firstStation.get("id").asString,
                    name = firstStation.get("name").asString
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun getStartTime(station: WeatherStation, weatherElement: String): ValidYears? {
        var url = "https://frost.met.no/observations/availableTimeSeries/v0.jsonld?"
        url += "sources=" + station.id + "&"
        url += "elements=$weatherElement"

        // Debugging: Print the final API URL
        println("Final API URL: $url")

        return try {
            val response: HttpResponse = APIClient.client.get(url) {
                headers {
                    append(HttpHeaders.Authorization, "Basic $encoded")
                }
            }

            if (response.status.value != 200) {
                println("API request failed with status: ${response.status.value}")
                null
            } else {
                val json = response.bodyAsText()
                println("API Response: $json")  // Debugging line
                val jsonObject = JsonParser.parseString(json).asJsonObject
                val times = jsonObject.getAsJsonArray("data")

                if (times.size() > 0) {
                    val firstTime = getMostRecentStation(times)
                    if (firstTime != null) {
                        ValidYears(
                            validTo = firstTime.get("validTo")?.asString?.let { Instant.parse(it) },
                            elementId = firstTime.get("elementId").asString,
                            weatherStation = station
                        )
                    } else {
                        null
                    }
                } else {
                    null
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun getWeatherData(station: WeatherStation, validYears: ValidYears): WeatherList? {

        val time: ZonedDateTime

        if (validYears.validTo == null) {
            val today = Instant.parse("2025-01-01T00:00:00Z")
            time = today.atZone(ZoneId.of("UTC"))
        } else {
            time = validYears.validTo.atZone((ZoneId.of("UTC")))
        }
        val endDate = time

        val startDate = endDate.minusYears(2)

        val formattedStartDate = formatDate(startDate)
        val formattedEndDate = formatDate(endDate)

        println("The years used are: ${formattedStartDate}, $formattedEndDate")
        var url = "https://frost.met.no/observations/v0.jsonld?"
        url += "sources=" + station.id + "&"
        url += "referencetime=$formattedStartDate/$formattedEndDate&"
        url += "elements=${validYears.elementId}"

        // Debugging: Print the final API URL
        println("Final API URL: $url")

        return try {
            val response: HttpResponse = APIClient.client.get(url) {
                headers {
                    append(HttpHeaders.Authorization, "Basic $encoded")
                }
            }

            if (response.status.value != 200) {
                println("API request failed with status: ${response.status.value}")
                null
            } else {
                val json = response.bodyAsText()
                println("API Response: $json")  // Debugging line
                val jsonObject = JsonParser.parseString(json).asJsonObject
                val dataArray = jsonObject.getAsJsonArray("data")
                val weatherList = dataArray
                    .map { it.asJsonObject }
                    .flatMap { dataObj ->
                        dataObj.getAsJsonArray("observations")
                            .map { obsEntry ->
                                obsEntry.asJsonObject.let { obsObj ->
                                    WeatherData(
                                        elementId = obsObj.get("elementId").asString,
                                        value = obsObj.get("value").asDouble,
                                        unit = obsObj.get("unit").asString,
                                        weatherStation = station
                                    )
                                }
                            }
                    }

                WeatherList(weatherList)

            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}