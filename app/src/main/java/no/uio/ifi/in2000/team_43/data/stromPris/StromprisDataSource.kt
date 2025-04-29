package no.uio.ifi.in2000.team_43.data.stromPris

import android.util.Log
import io.ktor.client.call.body
import io.ktor.client.request.get
import no.uio.ifi.in2000.team_43.data.APIClient
import no.uio.ifi.in2000.team_43.model.stromPris.StromprisRes
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class StromprisDataSource {
    suspend fun getStromprisData(): List<StromprisRes>? {
        try {

            // Henter inn dagens dato:):
            val today = LocalDate.now()
            val year = today.year.toString()
            val month = today.format(DateTimeFormatter.ofPattern("MM"))
            val day = today.format(DateTimeFormatter.ofPattern("dd"))
            val priceLocationCode = "NO1" // Totalt 5 prisområder i Norge, NO1 er ment for Oslo NB; bokstaven O ikke tallet

            Log.i("TestStrom", "Dagens dato: $year-$month-$day")

            var url = "https://www.hvakosterstrommen.no/api/v1/prices/"
            url += "$year/"
            url += "$month-"
            url +=  day + "_"
            url += priceLocationCode

            url += ".json" // Json format

            Log.i("TestStrom", "Prøver å hente data fra: $url")
            return APIClient.client.get(url).body()
        } catch (e: Exception) {
            Log.e("TestStrom", "error i datasource")
            Log.e("TestStrom", "Feilmelding: ${e.message}")
            return null
        }
    }
}