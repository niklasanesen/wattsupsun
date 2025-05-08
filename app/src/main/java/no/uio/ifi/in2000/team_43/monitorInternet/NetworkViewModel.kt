package no.uio.ifi.in2000.team_43.monitorInternet

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn

class NetworkViewModel(application: Application) : AndroidViewModel(application) {
    private val observer = NetworkConnectivityObserver(application)

    // Vær sikker på at vi starter med en fornuftig tilstand (Unavailable), hvis ikke tilgjengelig
    val networkStatus = observer.observe()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), NetworkStatus.Unavailable)
}
