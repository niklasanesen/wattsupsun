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
import no.uio.ifi.in2000.team_43.model.PVGIS.PVGISRes
import no.uio.ifi.in2000.team_43.model.met.MetRes

@RequiresApi(Build.VERSION_CODES.O)
class PVGISViewModel() : ViewModel() {
    val PVGISRepo = PVGISRepository(pvgisDatasource = PVGISDatasource())

    private val _data = MutableStateFlow<PVGISRes?>(null);
    val data: StateFlow<PVGISRes?> = _data.asStateFlow()

    init {
        viewModelScope.launch {
            Log.d("PVGISViewModel", "Fetching PVGIS data...")
            val res = PVGISRepo.getPVGISdata()
            Log.d("PVGISViewModel", "PVGIS Response: $res")
            _data.value = res
        }
    }
}