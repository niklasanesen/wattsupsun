package no.uio.ifi.in2000.team_43.data.electricityPrice

import android.util.Log
import io.ktor.client.call.body
import io.ktor.client.request.get
import no.uio.ifi.in2000.team_43.data.APIClient
import no.uio.ifi.in2000.team_43.model.electricityPrice.ElectricityPriceRes
import no.uio.ifi.in2000.team_43.model.electricityDistrict.ElectricityDistrict
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class ElectricityPriceDataSource {
    suspend fun getElectricityPriceData(electricityDistrict: ElectricityDistrict?): List<ElectricityPriceRes>? {
        try {

            // Getting today's date:):
            val today = LocalDate.now()
            val year = today.year.toString()
            val month = today.format(DateTimeFormatter.ofPattern("MM"))
            val day = today.format(DateTimeFormatter.ofPattern("dd"))
            val priceLocationCode = electricityDistrict?.name ?: "NO1" // Total of 5 price areas in Norway, NO1 is meant for Oslo; letter O not number

            Log.i("TestElectricity", "Today's date: $year-$month-$day")

            var url = "https://www.hvakosterstrommen.no/api/v1/prices/"
            url += "$year/"
            url += "$month-"
            url +=  day + "_"
            url += priceLocationCode

            url += ".json" // Json format

            Log.i("TestElectricity", "Trying to get data from: $url")
            return APIClient.client.get(url).body()
        } catch (e: Exception) {
            Log.e("TestElectricity", "Error in datasource")
            Log.e("TestElectricity", "ErrorMessage: ${e.message}")
            return null
        }
    }


    suspend fun getElectricityPriceDataWithDate(electricityDistrict: ElectricityDistrict?, date: LocalDate): List<ElectricityPriceRes>? {
        try {

            // To format month:
            val formatter = DateTimeFormatter.ofPattern("MM")

            // Fetching for chosen date
            val year = date.year
            val month = date.format(formatter)
            val day = "%02d".format(date.dayOfMonth)
            val priceLocationCode = electricityDistrict?.name ?: "NO1" // Total of 5 price areas in Norway, NO1 is meant for Oslo; letter O not number



            Log.i("TestElectricityPrice", "Getting from date: $year-$month-$day")


            var url = "https://www.hvakosterstrommen.no/api/v1/prices/"
            url += "$year/"
            url += "$month-"
            url +=  day + "_"
            url += priceLocationCode

            url += ".json" // Json format

            //Log.i("TestElectricityPrice", "Trying to get data from: $url")
            return APIClient.client.get(url).body()
        } catch (e: Exception) {
            Log.e("TestElectricity", "error in datasource")
            Log.e("TestElectricity", "ErrorMessage: ${e.message}")
            return null
        }
    }


}