package no.uio.ifi.in2000.team_43.data.electricityPrice

import no.uio.ifi.in2000.team_43.model.electricityDistrict.ElectricityDistrict
import no.uio.ifi.in2000.team_43.model.electricityPrice.ElectricityPriceRes
import java.time.LocalDate

class ElectricityPriceRepository (private val electricityPriceDataSource: ElectricityPriceDataSource){

    suspend fun getElectricityPriceList(electricityDistrict: ElectricityDistrict?): List<ElectricityPriceRes>? {
        return electricityPriceDataSource.getElectricityPriceData(electricityDistrict)
    }

    suspend fun calculateAveragePrice(electricityDistrict: ElectricityDistrict?): Double {

        //Log.i("TestElectricity", "Testing calculateAveragePrice now")
        val listWithElectricityPrice: List<ElectricityPriceRes>? = electricityPriceDataSource.getElectricityPriceData(electricityDistrict)
        if (listWithElectricityPrice == null) {
            return 0.0
        }

        var totalSum = 0.0
        val totalHoursFound: Int = listWithElectricityPrice.size
        listWithElectricityPrice.forEach { time ->
            totalSum += time.NOK_per_kWh
        }

        val averagePrice: Double = totalSum/totalHoursFound
        return averagePrice
    }

    suspend fun getElectricityPricePerMonth(electricityDistrict: ElectricityDistrict?): List<List<ElectricityPriceRes>?> {

        val dates = (1..12).map { month -> LocalDate.of(2024, month, 1) }
        val electricityListPerMonth = mutableListOf<List<ElectricityPriceRes>?>()
        dates.forEach { date ->
            //Log.i("TestElectricityPrice", "Getting from date.. ${date.dayOfMonth}-${date.month}-${date.year}")
            electricityListPerMonth.add(electricityPriceDataSource.getElectricityPriceDataWithDate(electricityDistrict, date))
        }

        //Log.d("TestElectricityPrice", "value of ePriceOerMonth = $electricityListPerMonth")
        return electricityListPerMonth
    }
}