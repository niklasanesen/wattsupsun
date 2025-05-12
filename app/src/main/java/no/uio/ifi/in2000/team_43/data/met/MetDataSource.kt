package no.uio.ifi.in2000.team_43.data.met

import com.google.gson.JsonParser
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpHeaders
import no.uio.ifi.in2000.team_43.model.met.AllValidYears
import no.uio.ifi.in2000.team_43.model.met.Coordinates
import no.uio.ifi.in2000.team_43.model.met.ValidYears
import no.uio.ifi.in2000.team_43.model.met.WeatherData
import no.uio.ifi.in2000.team_43.model.met.WeatherList
import no.uio.ifi.in2000.team_43.model.met.WeatherStation
import java.time.Instant
import java.util.Base64

class MetDataSource(private val client: HttpClient) {
    val clientId = "576d17b2-5647-4cd4-ba76-e852f7e4b6cb"
    val encoded = String(Base64.getEncoder().encode("$clientId:".toByteArray(Charsets.UTF_8)))

    suspend fun getWeatherStationData(coordinates: Coordinates, nearestMaxCount: Int): WeatherStation? {
        var url = "https://frost.met.no/sources/v0.jsonld?"
        url += "types=SensorSystem&"
        url += "elements=mean(cloud_area_fraction P1D),mean(air_temperature P1D),mean(surface_snow_thickness P1M),mean(surface_downwelling_shortwave_flux_in_air PT1H)&"
        url += "geometry=nearest(POINT(${coordinates.longitude} ${coordinates.latitude}))&"
        url += "nearestmaxcount=$nearestMaxCount"


        // Debugging: Print the final API URL
        println("Final API URL: $url")

        return try {
            val response: HttpResponse = client.get(url) {
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
                val station = stations[nearestMaxCount - 1].asJsonObject

                return WeatherStation(
                    id = station.get("id").asString,
                    name = station.get("name").asString,
                    distance = station.get("distance").asDouble
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun getStartTime(station: WeatherStation, weatherElement: String): AllValidYears? {
        var url = "https://frost.met.no/observations/availableTimeSeries/v0.jsonld?"
        url += "sources=${station.id}&"
        url += "elements=$weatherElement&"
        url += "timeoffsets=default"

        // Debugging: Print the final API URL
        println("Final API URL: $url")

        return try {
            val response: HttpResponse = client.get(url) {
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
                val allValidYears = times
                    .map { obj ->
                        val timeObj = obj.asJsonObject
                        val validTo = if (timeObj.has("validTo") && !timeObj["validTo"].isJsonNull) {
                            Instant.parse(timeObj["validTo"].asString)
                        } else {
                            Instant.parse("2025-01-01T00:00:00Z")
                        }
                        ValidYears(
                            validTo =  validTo,
                            validFrom = timeObj.get("validFrom").asString.let { Instant.parse(it) },
                            elementId = timeObj.get("elementId").asString,
                            weatherStation = station
                        )
                    }
                AllValidYears(allValidYears)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun getWeatherData(station: WeatherStation, elementId: String, startDate: String, endDate: String): WeatherList? {

        println("The years used are: $startDate, $endDate")
        var url = "https://frost.met.no/observations/v0.jsonld?"
        url += "sources=${station.id}&"
        url += "referencetime=$startDate/$endDate&"
        url += "elements=$elementId&"
        url += "timeoffsets=default"

        // Debugging: Print the final API URL
        println("Final API URL: $url")

        return try {
            val response: HttpResponse = client.get(url) {
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
                                        weatherStation = station,
                                        timeMeasured = dataObj.get("referenceTime").asString.let { Instant.parse(it) }
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