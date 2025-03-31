package no.uio.ifi.in2000.team_43.ui.strompris

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import no.uio.ifi.in2000.team_43.data.stromPris.StromprisDataSource
import no.uio.ifi.in2000.team_43.data.stromPris.StromprisRepository
import no.uio.ifi.in2000.team_43.model.stromPris.StromprisRes

@RequiresApi(Build.VERSION_CODES.O)
class StromprisViewModel : ViewModel() {

    private val stromprisRepo = StromprisRepository(stromprisDataSource = StromprisDataSource())

    private val _data = MutableStateFlow<List<StromprisRes>?>(null)
    val data: StateFlow<List<StromprisRes>?> = _data.asStateFlow()

    // Henter data
    init {
        fetchStromData()
    }

    private fun fetchStromData() {
        viewModelScope.launch {
            try {
                Log.i("TestStrompris", "Henter strømprisdata fra API...")
                val response = stromprisRepo.getStromPrisListe()
                _data.value = response
            } catch (e: Exception) {
                Log.e("TestStrompris", "Feil ved henting av strømprisdata: ${e.message}")
            }
        }
    }
}
