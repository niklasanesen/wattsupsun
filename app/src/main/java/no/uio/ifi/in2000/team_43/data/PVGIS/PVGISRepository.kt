package no.uio.ifi.in2000.team_43.data.PVGIS

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import no.uio.ifi.in2000.team_43.model.PVGIS.PVGISRes


class PVGISRepository (private val pvgisDatasource: PVGISDatasource) {
    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun getPVGISdata(): PVGISRes? {
        Log.i("TestPVGIS", "Henter data via funksjon i PVGIS Repository")
        return pvgisDatasource.getPVGISData()
    }
}