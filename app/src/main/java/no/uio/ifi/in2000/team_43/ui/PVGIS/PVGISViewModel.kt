package no.uio.ifi.in2000.team_43.ui.PVGIS

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import no.uio.ifi.in2000.team_43.data.PVGIS.PVGISDatasource
import no.uio.ifi.in2000.team_43.data.PVGIS.PVGISRepository
import no.uio.ifi.in2000.team_43.data.met.MetDataSource
import no.uio.ifi.in2000.team_43.data.met.MetRepository
import no.uio.ifi.in2000.team_43.model.DRcalcRes.DRcalcRes
import no.uio.ifi.in2000.team_43.model.DRcalcRes.DailyProfile
import no.uio.ifi.in2000.team_43.model.PVGIS.PVGISRes
import no.uio.ifi.in2000.team_43.model.met.MetRes

@RequiresApi(Build.VERSION_CODES.O)
class PVGISViewModel : ViewModel() {

    private val PVGISRepo = PVGISRepository(pvgisDatasource = PVGISDatasource())

    // Bruker StateFlow for å holde på data
    private val _data = MutableStateFlow<PVGISRes?>(null)
    val data: StateFlow<PVGISRes?> = _data.asStateFlow()

    // Henter DRcalc-data
    private val _drCalcData = MutableStateFlow<List<DailyProfile>?>(null)
    val drCalcData: StateFlow<List<DailyProfile>?> = _drCalcData.asStateFlow()
    var drCalcDataTotalSum: Double = 0.0;

    // Laster data når ViewModel opprettes
    init {
        fetchPVGISData()
        fetchDRcalcData()

        //bergegner totalsum

        val listeMedData: List<DailyProfile>?  = drCalcData.value
        var totalSum: Double = 0.0

        listeMedData?.forEach { data ->
            totalSum += data.G_i
        }
        drCalcDataTotalSum = totalSum
    }

    // Henter PVGIS-data og lagrer det i StateFlow
    private fun fetchPVGISData() {
        viewModelScope.launch {
            try {
                Log.i("TestPVGIS", "Henter data via PVGISViewModel")
                val response = PVGISRepo.getPVGISdata()
                _data.value = response // Setter data slik at UI kan observere det
            } catch (e: Exception) {
                Log.e("TestPVGIS", "Feil ved henting av data: ${e.message}")
            }
        }
    }



    fun fetchDRcalcData() {
        viewModelScope.launch {
            try {
                Log.i("TestDRcalc", "Henter DRcalc data via PVGISViewModel")
                val response = PVGISRepo.getDRcalcData()
                _drCalcData.value = response
            } catch (e: Exception) {
                Log.e("TestDRcalc", "Feil ved henting av DRcalc-data: ${e.message}")
            }
        }
    }

    suspend fun getDRcalcTotalSum(): Double {
        return PVGISRepo.getDRcalcDataTotal()
    }
}
