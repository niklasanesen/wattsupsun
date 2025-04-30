package no.uio.ifi.in2000.team_43.ui.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mapbox.geojson.Point
import com.mapbox.search.ResponseInfo
import com.mapbox.search.SearchEngine
import com.mapbox.search.SearchEngineSettings
import com.mapbox.search.SearchOptions
import com.mapbox.search.SearchSuggestionsCallback
import com.mapbox.search.autocomplete.PlaceAutocomplete
import com.mapbox.search.autocomplete.PlaceAutocompleteSuggestion
import com.mapbox.search.result.SearchSuggestion
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import no.uio.ifi.in2000.team_43.model.map.Suggestion

private const val MAX_SUGGESTIONS = 4;
private const val MAX_NAME_LEN = 48;

class MapViewModel() : ViewModel() {
    private val placeAutocomplete = PlaceAutocomplete.create()
    private val searchEngine = SearchEngine.createSearchEngine(SearchEngineSettings())

    private val _suggestions = MutableStateFlow<List<Suggestion>>(emptyList())
    val suggestions: StateFlow<List<Suggestion>> = _suggestions.asStateFlow()

    private val _coords = MutableStateFlow<Point?>(Point.fromLngLat(12.30778, 63.99056))
    val coords: StateFlow<Point?> = _coords.asStateFlow()

    private val _address = MutableStateFlow<String?>(null)
    val address: StateFlow<String?> = _address.asStateFlow()

    fun search(text: String)  {
        if (text.isEmpty()) {
            _suggestions.value = emptyList()
            return
        }

        viewModelScope.launch {
            val res = placeAutocomplete.suggestions(text)
            res.onValue { autocompleteSuggestions ->
                _suggestions.value = autocompleteSuggestions
                    .take(MAX_SUGGESTIONS * 3)
                    .mapNotNull { suggestion -> parseSuggestion(suggestion) }
                    .filter { it.country.equals("Norway", ignoreCase = true) }
                    .take(MAX_SUGGESTIONS)
            }.onError {
                _suggestions.value = emptyList()
            }
        }
    }

    fun select(addr: String) {
        searchEngine.search(
            addr,
            SearchOptions(limit = 1),
            object : SearchSuggestionsCallback {
                override fun onSuggestions(suggestions: List<SearchSuggestion>, responseInfo: ResponseInfo) {
                    val suggestion = suggestions.firstOrNull()
                    if (suggestion != null) {
                        _coords.value = suggestion.coordinate
                        _address.value = suggestion.fullAddress
                    }

                }
                override fun onError(e: Exception) {
                    _coords.value = null
                }
            }
        )
    }

    private fun parseSuggestion(suggestion: PlaceAutocompleteSuggestion): Suggestion? {
        if (suggestion.name.isEmpty() || suggestion.name.length > MAX_NAME_LEN || suggestion.formattedAddress == null) {
            return null
        }

        val formattedAddress = suggestion.formattedAddress!!

        val parts = formattedAddress.split(",").map { it.trim() }
        if (parts.size != 3) {
            return null
        }

        val streetAddress = parts[0]
        val cityParts = parts[1].split(" ").filter { it.isNotEmpty() }

        if (cityParts.size != 2) {
            return null
        }

        val postalCode = cityParts[0]
        val city = cityParts[1]
        val country = parts[2]

        return Suggestion(
            name = suggestion.name,
            formattedAddress = formattedAddress,
            streetAddress = streetAddress,
            postalCode = postalCode,
            city = city,
            country = country
        )
    }
}
