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
import java.time.Instant

class MetDataSource {
    val clientId = "576d17b2-5647-4cd4-ba76-e852f7e4b6cb"
    val encoded = String(Base64.getEncoder().encode("$clientId:".toByteArray(Charsets.UTF_8)))

    suspend fun getWeatherStationData(): WeatherStation? {
        var url = "https://frost.met.no/sources/v0.jsonld?"
        url += "elements=" + "elements=cloud_area_fraction"
        url += "geometry=" + "nearest(POINT(10 59))"

        return try {
            val response: HttpResponse = APIClient.client.get(url) {
                headers {
                    append(HttpHeaders.Authorization, "Basic $encoded")
                }
            }

            if (response.status.value != 200) {
                null
            } else {
                val json = response.bodyAsText()
                val jsonObject = JsonParser.parseString(json).asJsonObject
                val stations = jsonObject.getAsJsonArray("data")

                if (stations.size() > 0) {
                    val firstStation = stations[0].asJsonObject
                    WeatherStation(
                        id = firstStation.get("id").asString,
                        name = firstStation.get("name").asString
                    )
                } else {
                    null
                }
            }
        } catch (e: Exception) {
            null
        }
    }

    suspend fun getStartTime(station: WeatherStation): ValidYears? {
        var url = "https://frost.met.no/observations/availableTimeSeries/v0.jsonld?"
        url += "sources=" + station.id
        url += "elements=" + "cloud_area_fraction"

        return try {
            val response: HttpResponse = APIClient.client.get(url) {
                headers {
                    append(HttpHeaders.Authorization, "Basic $encoded")
                }
            }

            if (response.status.value != 200) {
                null
            } else {
                val json = response.bodyAsText()
                val jsonObject = JsonParser.parseString(json).asJsonObject
                val times = jsonObject.getAsJsonArray("data")

                if (times.size() > 0) {
                    val firstTime = times[0].asJsonObject
                    ValidYears(
                        validTo = Instant.parse(firstTime.get("validTo").asString),
                        elementId = firstTime.get("elementId").asString,
                        weatherStation = station
                    )
                } else {
                    null
                }
            }
        } catch (e: Exception) {
            null
        }
    }
}