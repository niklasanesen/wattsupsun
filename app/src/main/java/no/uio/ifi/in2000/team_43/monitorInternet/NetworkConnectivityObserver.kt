package no.uio.ifi.in2000.team_43.monitorInternet

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class NetworkConnectivityObserver(private val context: Context) {
    fun observe(): Flow<NetworkStatus> {
        return callbackFlow {
            val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

            val callback = object : ConnectivityManager.NetworkCallback() {
                override fun onAvailable(network: Network) {
                    trySend(NetworkStatus.Available).isSuccess
                }

                override fun onLost(network: Network) {
                    trySend(NetworkStatus.Unavailable).isSuccess
                }
            }

            connectivityManager.registerDefaultNetworkCallback(callback)

            // Initial status check:
            val currentStatus = if (isNetworkAvailable()) {
                NetworkStatus.Available
            } else {
                NetworkStatus.Unavailable
            }

            trySend(currentStatus).isSuccess

            // Closing flow when observer is no longer needed
            awaitClose { connectivityManager.unregisterNetworkCallback(callback) }
        }
    }

    private fun isNetworkAvailable(): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkCapabilities = connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
        return networkCapabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true
    }
}



