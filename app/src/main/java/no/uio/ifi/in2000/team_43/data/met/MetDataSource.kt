package no.uio.ifi.in2000.team_43.data.met

import android.os.Build
import androidx.annotation.RequiresApi
import io.ktor.client.call.body
import io.ktor.client.request.get
import no.uio.ifi.in2000.team_43.data.APIClient
import no.uio.ifi.in2000.team_43.model.met.MetRes
import io.ktor.client.request.headers
import io.ktor.http.HttpHeaders
import java.util.Base64

class MetDataSource {
    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun getMetData(): MetRes? {
        try {
            val clientId = "576d17b2-5647-4cd4-ba76-e852f7e4b6cb"
            val encoded = String(Base64.getEncoder().encode("$clientId:".toByteArray(Charsets.UTF_8)))

            var url = "https://frost.met.no/observations/v0.jsonld?"
            url += "sources=" + "SN18700"; // blindern
            url += "&elements=" + "mean(air_temperature P1D)"; // temp
            url += "&referencetime=" + "2025-03-19";

            return APIClient.client.get(url) {
                headers {
                    append(HttpHeaders.Authorization, "Basic $encoded")
                }
            }.body()
        } catch (e: Exception) {
            return null
        }
    }
}