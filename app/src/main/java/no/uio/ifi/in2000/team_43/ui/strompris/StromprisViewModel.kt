package no.uio.ifi.in2000.team_43.ui.strompris

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import no.uio.ifi.in2000.team_43.data.stromPris.StromprisDataSource
import no.uio.ifi.in2000.team_43.data.stromPris.StromprisRepository
import no.uio.ifi.in2000.team_43.model.stromPris.StromprisRes
import no.uio.ifi.in2000.team_43.model.stromdistrikt.StromDistrikt

class StromprisViewModel : ViewModel() {

    private val stromprisRepo = StromprisRepository(stromprisDataSource = StromprisDataSource())

    private val _data = MutableStateFlow<List<StromprisRes>?>(null)
    val data: StateFlow<List<StromprisRes>?> = _data.asStateFlow()

    public fun fetchStromData(stromdistrikt: StromDistrikt?) {
        viewModelScope.launch {
            try {
                Log.i("TestStrompris", "Henter strømprisdata fra API...")
                val response = stromprisRepo.getStromPrisListe(stromdistrikt)
                _data.value = response
            } catch (e: Exception) {
                Log.e("TestStrompris", "Feil ved henting av strømprisdata: ${e.message}")
            }
        }
    }
}
