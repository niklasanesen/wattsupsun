@file:Suppress("PackageName")

package no.uio.ifi.in2000.team_43.data.PVGIS

import android.util.Log
import io.ktor.client.call.body
import io.ktor.client.request.get
import no.uio.ifi.in2000.team_43.data.APIClient
import no.uio.ifi.in2000.team_43.model.DRcalcRes.DRcalcRes
import no.uio.ifi.in2000.team_43.model.PVGIS.PVGISRes
import no.uio.ifi.in2000.team_43.model.met.Coordinates

class PVGISDatasource {
    suspend fun getPVGISData(coordinates: Coordinates): PVGISRes? {
        try {

            // Test data:

            // One of the tools that can be used:
            val toolName = "PVcalc"

            // Chosen position
            val lat = coordinates.latitude
            val lon = coordinates.longitude

            // Described further down
            val peakPower = "3.0"
            val loss = "2.0"


            var url = "https://re.jrc.ec.europa.eu/api/v5_2/"
            url += "${toolName}?"
            url += "lat=$lat"
            url += "&lon=$lon"
            url += "&peakpower=$peakPower" // Descriptor "Nominal power of the PV system, in kW"
            url += "&loss=$loss" // Descriptor "Sum of system losses, in percent."

            url += "&outputformat=json" // Specifies that you want JSON

            Log.i("TestPVGIS", "Trying to get data from $url")
            Log.i("TestPVGIS", "Trying to get data from lat: $lat and lon: $lon")

            return APIClient.client.get(url).body()
        } catch (e: Exception) {
            Log.e("TestPVGIS", "Error in Datasource: msg; ${e.message}")

            return null
        }
    }


    // Get average incoming solar energy each hour
    suspend fun getDRcalcData(): DRcalcRes? {
        try {

            // Test data:

            // Tool: DRcalc
            val toolName = "DRcalc"
            // position of Oslo
            val lat = "59"
            val lon = "10"
            val month = "7" // testing with July
            val global = "1" // Constant at 1, description: Output the global, direct and diffuse in-plane irradiances.
            val localtime = "1" // Constant at 1, shows localtime



            var url = "https://re.jrc.ec.europa.eu/api/v5_2/"
            url += "${toolName}?"
            url += "lat=$lat"
            url += "&lon=$lon"
            url += "&month=$month"
            url += "&global=$global"
            url += "&localtime=$localtime"

            url += "&outputformat=json" // Specifies that you want JSON

            Log.i("TestDRcalc", "Trying to get data from $url")
            return APIClient.client.get(url).body()
        } catch (e: Exception) {
            Log.e("TestDRcalc", "Error in Datasource: message: ${e.message}")
            return null
        }

    }

}



