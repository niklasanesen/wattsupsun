package no.uio.ifi.in2000.team_43.ui.strompris

import android.os.Build
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
class StromprisViewModel() : ViewModel() {
    val stromprisRepo = StromprisRepository(stromprisDataSource = StromprisDataSource())

    private val _data = MutableStateFlow<StromprisRes?>(null);
    val data: StateFlow<StromprisRes?> = _data.asStateFlow()


    suspend fun getStromData(): List<StromprisRes>? {
        return stromprisRepo.getStromPrisListe()
    }
}