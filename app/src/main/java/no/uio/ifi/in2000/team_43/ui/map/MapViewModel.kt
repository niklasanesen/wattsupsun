package no.uio.ifi.in2000.team_43.ui.map

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mapbox.search.autocomplete.PlaceAutocomplete
import com.mapbox.search.autocomplete.PlaceAutocompleteSuggestion
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MapViewModel() : ViewModel() {
    private val placeAutocomplete = PlaceAutocomplete.create()

    private val _suggestions = MutableStateFlow<List<PlaceAutocompleteSuggestion>>(emptyList())
    val suggestions: StateFlow<List<PlaceAutocompleteSuggestion>> = _suggestions.asStateFlow()

    fun search(text: String)  {
        viewModelScope.launch {
            val res = placeAutocomplete.suggestions(text)
            res.onValue {
                _suggestions.value = it
            }.onError {
                _suggestions.value = emptyList()
            }
        }
    }
}
