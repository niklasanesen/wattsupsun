package no.uio.ifi.in2000.team_43.data.stromPris

import android.util.Log
import no.uio.ifi.in2000.team_43.model.stromPris.StromprisRes
import no.uio.ifi.in2000.team_43.model.stromdistrikt.StromDistrikt

class StromprisRepository (private val stromprisDataSource: StromprisDataSource){
    suspend fun geStromPris(stromdistrikt: StromDistrikt?): List<StromprisRes>? {
        return stromprisDataSource.getStromprisData(stromdistrikt)
    }

    suspend fun getStromPrisListe(stromdistrikt: StromDistrikt?): List<StromprisRes>? {
        return stromprisDataSource.getStromprisData(stromdistrikt)
    }

    suspend fun calculateAveragePrice(stromdistrikt: StromDistrikt?): Double {
        //Log.i("TestStrom", "Tester calculateAveragePrice nå")
        val listeMedStrompris: List<StromprisRes>? = stromprisDataSource.getStromprisData(stromdistrikt)
        if (listeMedStrompris == null) {
            return 0.0
        }
        if (listeMedStrompris.size != 24) {
            //Log.i("TestStrom", "Noe rart: 24 timer IKKE returnert")
        }

        var totalSum: Double = 0.0
        val antallTimerFunnet: Int = listeMedStrompris.size
        listeMedStrompris.forEach { time ->
            totalSum += time.NOK_per_kWh
        }

        val snittPris: Double = totalSum/antallTimerFunnet

        return snittPris
    }
}