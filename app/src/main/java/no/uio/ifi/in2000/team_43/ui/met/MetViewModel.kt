package no.uio.ifi.in2000.team_43.ui.met

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import no.uio.ifi.in2000.team_43.data.met.blabla
import no.uio.ifi.in2000.team_43.data.met.MetRepository
import no.uio.ifi.in2000.team_43.model.met.MetRes

@RequiresApi(Build.VERSION_CODES.O)
class MetViewModel() : ViewModel() {
    val metRepo = MetRepository(metDataSource = blabla())

    private val _data = MutableStateFlow<MetRes?>(null);
    val data: StateFlow<MetRes?> = _data.asStateFlow()

    init {
        viewModelScope.launch {
            val res = metRepo.getMetData()
            _data.value = res
        }
    }
}