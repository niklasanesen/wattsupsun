package no.uio.ifi.in2000.team_43.data.PVGIS

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.http.HttpHeaders
import no.uio.ifi.in2000.team_43.data.APIClient
import no.uio.ifi.in2000.team_43.model.DRcalcRes.DRcalcRes
import no.uio.ifi.in2000.team_43.model.PVGIS.PVGISRes

class PVGISDatasource {
    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun getPVGISData(): PVGISRes? {
        try {

            // Testdata:

            // Et av verktøyene som kan brukes:
            val tool_name = "PVcalc"
            // posisjonen til Oslo
            val lat: String = "59"
            val lon: String = "10"
            // Beskrevet lengre ned
            val peakpower: String = "3.0"
            val loss: String = "2.0"


            var url = "https://re.jrc.ec.europa.eu/api/v5_2/"
            url += "${tool_name}?"
            url += "lat=" + lat
            url += "&lon=" + lon
            url += "&peakpower=" + peakpower // Beskriver "Nominal power of the PV system, in kW"
            url += "&loss=" + loss // Beskriver "Sum of system losses, in percent."

            url += "&outputformat=json" // Spesifiserer at man ønsker json

            Log.i("TestPVGIS", "Prøver å hente data fra $url")
            return APIClient.client.get(url).body()
        } catch (e: Exception) {
            Log.e("TestPVGIS", "Error i Datasource: msg; ${e.message}")

            return null
        }
    }


    // Get gjennomsnittlig innkommende solenergi per time
    suspend fun getDRcalcData(): DRcalcRes? {
        try {

            // Testdata:

            // Verktøy: DRcalc
            val tool_name = "DRcalc"
            // posisjonen til Oslo
            val lat: String = "59"
            val lon: String = "10"
            val month: String = "7" // tester med juli
            val global: String = "1" // Konstant på 1, description: Output the global, direct and diffuse in-plane irradiances.
            val localtime: String = "1" // Kontant på 1, viser lokaltid



            var url = "https://re.jrc.ec.europa.eu/api/v5_2/"
            url += "${tool_name}?"
            url += "lat=" + lat
            url += "&lon=" + lon
            url += "&month=" + month
            url += "&global=" + global
            url += "&localtime=" + localtime

            url += "&outputformat=json" // Spesifiserer at man ønsker json

            Log.i("TestDRcalc", "Prøver å hente data fra $url")
            return APIClient.client.get(url).body()
        } catch (e: Exception) {
            Log.e("TestDRcalc", "Error i Datasource: msg; ${e.message}")
            return null
        }

    }

}



