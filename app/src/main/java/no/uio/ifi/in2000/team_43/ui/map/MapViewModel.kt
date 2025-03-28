package no.uio.ifi.in2000.team_43.ui.map

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

class MapViewModel() : ViewModel() {
    private val placeAutocomplete = PlaceAutocomplete.create()
    private val searchEngine = SearchEngine.createSearchEngine(SearchEngineSettings())

    private val _suggestions = MutableStateFlow<List<PlaceAutocompleteSuggestion>>(emptyList())
    val suggestions: StateFlow<List<PlaceAutocompleteSuggestion>> = _suggestions.asStateFlow()

    private val _coords = MutableStateFlow<Point?>(Point.fromLngLat(10.7522, 59.9139))
    val coords: StateFlow<Point?> = _coords.asStateFlow()

    fun search(text: String)  {
        if (text.isEmpty()) {
            _suggestions.value = emptyList()
            return
        }

        viewModelScope.launch {
            val res = placeAutocomplete.suggestions(text)
            res.onValue {
                _suggestions.value = it
            }.onError {
                _suggestions.value = emptyList()
            }
        }
    }

    fun select(suggestion: PlaceAutocompleteSuggestion) {
        searchEngine.search(
            suggestion.name,
            SearchOptions(limit = 1),
            object : SearchSuggestionsCallback {
                override fun onSuggestions(suggestions: List<SearchSuggestion>, responseInfo: ResponseInfo) {
                    val suggestion = suggestions.firstOrNull()
                    if (suggestion != null)
                        _coords.value = suggestion.coordinate
                }
                override fun onError(e: Exception) {
                    _coords.value = null
                }
            }
        )
    }
}
