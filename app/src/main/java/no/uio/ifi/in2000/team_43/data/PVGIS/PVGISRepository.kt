package no.uio.ifi.in2000.team_43.data.PVGIS

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import no.uio.ifi.in2000.team_43.model.DRcalcRes.DRcalcRes
import no.uio.ifi.in2000.team_43.model.DRcalcRes.DailyProfile
import no.uio.ifi.in2000.team_43.model.PVGIS.PVGISRes


class PVGISRepository (private val pvgisDatasource: PVGISDatasource) {
    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun getPVGISdata(): PVGISRes? {
        Log.i("TestPVGIS", "Henter data via funksjon i PVGIS Repository")
        return pvgisDatasource.getPVGISData()
    }

    // returnerer en liste med data, gjennomsnittlig solinnstråling per time
    suspend fun getDRcalcData(): List<DailyProfile>? {
        Log.i("TestDRcalc", "Henter DRcalc data via funksjon i PVGIS Repository")
        return pvgisDatasource.getDRcalcData()?.outputs?.daily_profile

    }

    // Utregner totalt sollinnstrpling per dag
    suspend fun getDRcalcDataTotal(): Double {
        val listeMedData: List<DailyProfile>?  = getDRcalcData()
        var totalSum: Double = 0.0
        listeMedData?.forEach { data ->
            totalSum += data.G_i
        }
        return totalSum

    }
}