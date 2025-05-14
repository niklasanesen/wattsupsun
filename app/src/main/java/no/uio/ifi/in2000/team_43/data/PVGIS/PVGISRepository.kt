@file:Suppress("PackageName")

package no.uio.ifi.in2000.team_43.data.PVGIS

import android.util.Log
import no.uio.ifi.in2000.team_43.model.DRcalcRes.DailyProfile
import no.uio.ifi.in2000.team_43.model.PVGIS.PVGISRes
import no.uio.ifi.in2000.team_43.model.met.Coordinates


class PVGISRepository (private val pvgisDatasource: PVGISDatasource) {
    suspend fun getPVGISData(coordinates: Coordinates): PVGISRes? {
        Log.i("TestPVGIS", "Getting data via function in PVGIS Repository")
        return pvgisDatasource.getPVGISData(coordinates)
    }

    // returns a list of data, average solar insolation each hour
    suspend fun getDRcalcData(): List<DailyProfile>? {
        Log.i("TestDRcalc", "Getting DRcalc data via function in PVGIS Repository")
        return pvgisDatasource.getDRcalcData()?.outputs?.dailyProfile

    }

    // Calculates total solar insolation per dag
    suspend fun getDRcalcDataTotal(): Double {
        val listWithData: List<DailyProfile>?  = getDRcalcData()
        var totalSum = 0.0
        listWithData?.forEach { data ->
            totalSum += data.gI
        }
        return totalSum

    }
}