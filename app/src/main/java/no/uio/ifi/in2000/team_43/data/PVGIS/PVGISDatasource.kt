package no.uio.ifi.in2000.team_43.data.PVGIS

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.http.HttpHeaders
import no.uio.ifi.in2000.team_43.data.APIClient
import no.uio.ifi.in2000.team_43.model.PVGIS.PVGISRes

class PVGISDatasource {
    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun getPVGISData(): PVGISRes? {
        try {

            // Testdata:
            // posisjonen til Oslo
            val lat: String = "59"
            val lon: String = "10"
            // Beskrevet lengre ned
            val peakpower: String = "3.0"
            val loss: String = "2.0"


            var url = "https://re.jrc.ec.europa.eu/api/v5_2/"
            url += "lat=" + lat
            url += "&lon=" + lon
            url += "&peakpower=" + peakpower // Beskriver "Nominal power of the PV system, in kW"
            url += "&loss=" + loss // Beskriver "Sum of system losses, in percent."

            return APIClient.client.get(url).body()
        } catch (e: Exception) {
            return null
        }
    }
}