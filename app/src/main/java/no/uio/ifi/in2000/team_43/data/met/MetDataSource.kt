package no.uio.ifi.in2000.team_43.data.met

import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.http.HttpHeaders
import no.uio.ifi.in2000.team_43.data.APIClient
import no.uio.ifi.in2000.team_43.model.met.WeatherStation
import java.util.Base64
import com.google.gson.JsonParser
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import no.uio.ifi.in2000.team_43.model.met.ValidYears
import no.uio.ifi.in2000.team_43.model.met.WeatherData
import no.uio.ifi.in2000.team_43.model.met.WeatherList
import okhttp3.internal.UTC
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

fun formatDate(zonedDateTime: ZonedDateTime): String {
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    return formatter.format(zonedDateTime)
}

class MetDataSource {
    val clientId = "576d17b2-5647-4cd4-ba76-e852f7e4b6cb"
    val encoded = String(Base64.getEncoder().encode("$clientId:".toByteArray(Charsets.UTF_8)))

    suspend fun getWeatherStationData(): WeatherStation? {
        var url = "https://frost.met.no/sources/v0.jsonld?"
        url += "elements=cloud_area_fraction&"
        url += "geometry=" + "nearest(POINT(10 59))"

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

    suspend fun getStartTime(station: WeatherStation): ValidYears? {
        var url = "https://frost.met.no/observations/availableTimeSeries/v0.jsonld?"
        url += "sources=" + station.id + "&"
        url += "elements=" + "cloud_area_fraction"

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
                    val firstTime = times[0].asJsonObject
                    return ValidYears(
                        validTo = Instant.parse(firstTime.get("validTo").asString),
                        elementId = firstTime.get("elementId").asString,
                        weatherStation = station
                    )
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

        val endDate = validYears.validTo.atZone(ZoneId.of("UTC"))
        val startDate = endDate.minusYears(1)


        val formattedStartDate = formatDate(startDate)
        val formattedEndDate = formatDate(endDate)

        println("The years used are: ${formattedStartDate}, ${formattedEndDate}")
        var url = "https://frost.met.no/observations/v0.jsonld?"
        url += "sources=" + station.id + "&"
        url += "referenceTime=$formattedStartDate/$formattedEndDate&"
        url += "elements=" + "cloud_area_fraction"

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