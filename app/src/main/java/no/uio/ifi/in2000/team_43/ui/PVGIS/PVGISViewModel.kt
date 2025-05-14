@file:Suppress("PackageName")

package no.uio.ifi.in2000.team_43.ui.PVGIS

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import no.uio.ifi.in2000.team_43.data.PVGIS.PVGISDatasource
import no.uio.ifi.in2000.team_43.data.PVGIS.PVGISRepository
import no.uio.ifi.in2000.team_43.model.DRcalcRes.DailyProfile
import no.uio.ifi.in2000.team_43.model.PVGIS.PVGISRes
import no.uio.ifi.in2000.team_43.model.graphRes.GraphMeasurement
import no.uio.ifi.in2000.team_43.model.met.Coordinates

fun convertPVGISToMeasurements(data: List<Double>): List<GraphMeasurement> {
    return data.mapIndexed { index, value ->
        GraphMeasurement(
            time = (index + 1).toFloat(), // Months 1–12
            value = value.toFloat()
        )
    }
}

class PVGISViewModel : ViewModel() {

    @Suppress("PrivatePropertyName")
    private val PVGISRepo = PVGISRepository(pvgisDatasource = PVGISDatasource())

    // Using StateFlow to hold data
    private val _data = MutableStateFlow<PVGISRes?>(null)
    val data: StateFlow<PVGISRes?> = _data.asStateFlow()

    // Getting DRcalc-data
    private val _drCalcData = MutableStateFlow<List<DailyProfile>?>(null)
    val drCalcData: StateFlow<List<DailyProfile>?> = _drCalcData.asStateFlow()
    /*
    for use at a later time
    val drCalcDataTotalSum: StateFlow<Double?> = drCalcData
        .map { listWithData -> listWithData?.sumOf { it.gI } ?: 0.0 } // Handles null-cases
        .stateIn(viewModelScope, SharingStarted.Lazily, 0.0)
     */
    // Calculates average production per month based on pvgis
    val productionData: StateFlow<List<Double>> = data
        .map { res ->
            res?.outputs?.monthly?.fixed?.map { (it.hIM) } ?: emptyList()
        }
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    // Loads data when ViewModel instantiates
    init {
        fetchDRcalcData()
    }

    // Fetches PVGIS-data and stores it in StateFlow
    fun fetchPVGISData(coordinates: Coordinates) {
        viewModelScope.launch {
            try {
                Log.i("TestPVGIS", "Getting data via PVGISViewModel")
                val response = PVGISRepo.getPVGISData(coordinates)
                _data.value = response // Setting data so that the UI can observe it
            } catch (e: Exception) {
                Log.e("TestPVGIS", "Error in fetch of data: ${e.message}")
            }
        }
    }

    fun fetchDRcalcData() {
        viewModelScope.launch {
            try {
                Log.i("TestDRcalc", "Fetching DRcalc data via PVGISViewModel")
                val response = PVGISRepo.getDRcalcData()
                _drCalcData.value = response
            } catch (e: Exception) {
                Log.e("TestDRcalc", "Error at fetch of DRcalc-data: ${e.message}")
            }
        }
    }

    suspend fun getDRcalcTotalSum(): Double {
        return PVGISRepo.getDRcalcDataTotal()
    }
}
