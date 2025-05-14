package no.uio.ifi.in2000.team_43.monitorInternet

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn

class NetworkViewModel(application: Application) : AndroidViewModel(application) {
    private val observer = NetworkConnectivityObserver(application)

    // Be sure that we start in a sensible state (Unavailable), if not available
    val networkStatus = observer.observe()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), NetworkStatus.Unavailable)
}
