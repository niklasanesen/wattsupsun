package no.uio.ifi.in2000.team_43.ui.map

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.lifecycle.Lifecycle
import com.mapbox.search.autocomplete.PlaceAutocomplete
import com.mapbox.common.MapboxOptions
import kotlinx.coroutines.launch
import no.uio.ifi.in2000.team_43.R

class MapActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        Log.i("SearchExample", "funka")
        super.onCreate(savedInstanceState)

        // Set your Mapbox access token, reading from the string resource file
        MapboxOptions.accessToken = getString(R.string.mapbox_access_token)
        val placeAutocomplete = PlaceAutocomplete.create(locationProvider = null)

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                // get suggestions for the search query "Washington DC"
                val response = placeAutocomplete.suggestions(query = "Washington DC")
                if (response.isValue) {
                    val suggestions = response.value.orEmpty()
                    Log.i("SearchExample", "Suggestions: $suggestions")

                    if (suggestions.isNotEmpty()) {
                        val result = placeAutocomplete.select(suggestions.first())
                        result.onValue { Log.i("SearchExample", "Result: $it") }
                        result.onError { Log.e("SearchExample", "Error selecting suggestion", it) }
                    }
                } else {
                    Log.e("SearchExample", "Error fetching suggestions: ${response.error}")
                }
            }
        }
    }

    fun test() {
        Log.i("SearchExample", "funka")
        // Set your Mapbox access token, reading from the string resource file
        MapboxOptions.accessToken = getString(R.string.mapbox_access_token)
        val placeAutocomplete = PlaceAutocomplete.create(locationProvider = null)

        // get suggestions for the search query "Washington DC"
        val response = placeAutocomplete.suggestions(query = "Washington DC")
        if (response.isValue) {
            val suggestions = response.value.orEmpty()
            Log.i("SearchExample", "Suggestions: $suggestions")

            if (suggestions.isNotEmpty()) {
                val result = placeAutocomplete.select(suggestions.first())
                result.onValue { Log.i("SearchExample", "Result: $it") }
                result.onError { Log.e("SearchExample", "Error selecting suggestion", it) }
            }
        } else {
            Log.e("SearchExample", "Error fetching suggestions: ${response.error}")
        }
    }
}