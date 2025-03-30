package no.uio.ifi.in2000.team_43.data.stromPris

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import io.ktor.client.call.body
import io.ktor.client.request.get
import no.uio.ifi.in2000.team_43.data.APIClient
import no.uio.ifi.in2000.team_43.model.stromPris.StromprisRes

class StromprisDataSource {
    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun getStromprisData(): List<StromprisRes>? {
        try {

            // Testdata:
            val year = "2025"
            val month = "01" // MÅ ha 2 siffer
            val day = "25" // MÅ ha 2 siffer
            val priceLocationCode = "NO1" // Totalt 5 prisområder i Norge, NO1 er ment for Oslo NB; bokstaven O ikke tallet


            var url = "https://www.hvakosterstrommen.no/api/v1/prices/"
            url += year + "/"
            url += month + "-"
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