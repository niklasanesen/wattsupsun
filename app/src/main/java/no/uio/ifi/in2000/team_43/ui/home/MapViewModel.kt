package no.uio.ifi.in2000.team_43.ui.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mapbox.geojson.Point
import com.mapbox.search.ResponseInfo
import com.mapbox.search.ReverseGeoOptions
import com.mapbox.search.SearchCallback
import com.mapbox.search.SearchEngine
import com.mapbox.search.SearchEngineSettings
import com.mapbox.search.SearchOptions
import com.mapbox.search.SearchSuggestionsCallback
import com.mapbox.search.autocomplete.PlaceAutocomplete
import com.mapbox.search.autocomplete.PlaceAutocompleteSuggestion
import com.mapbox.search.common.IsoCountryCode
import com.mapbox.search.result.SearchResult
import com.mapbox.search.result.SearchSuggestion
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import no.uio.ifi.in2000.team_43.model.map.Suggestion
import no.uio.ifi.in2000.team_43.model.stromdistrikt.Fylker
import no.uio.ifi.in2000.team_43.model.stromdistrikt.StromDistrikt

private const val MAX_SUGGESTIONS = 4
private const val MAX_NAME_LEN = 56

class MapViewModel() : ViewModel() {
    private val placeAutocomplete = PlaceAutocomplete.create()
    private val searchEngine = SearchEngine.createSearchEngine(SearchEngineSettings())

    private val _suggestions = MutableStateFlow<List<Suggestion>>(emptyList())
    val suggestions: StateFlow<List<Suggestion>> = _suggestions.asStateFlow()

    private val _coords = MutableStateFlow<Point?>(Point.fromLngLat(12.30778, 63.99056))
    val coords: StateFlow<Point?> = _coords.asStateFlow()

    private val _address = MutableStateFlow<String?>(null)
    val address: StateFlow<String?> = _address.asStateFlow()

    private val _stromdistrikt = MutableStateFlow<StromDistrikt?>(null)
    val stromdistrikt: StateFlow<StromDistrikt?> = _stromdistrikt.asStateFlow()

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
                        setStromdistrikt(suggestion.coordinate)
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

    // creds: fredrik
    fun setStromdistrikt(point: Point?) {
        if (point == null) {
            return
        }
        searchEngine.search(
            ReverseGeoOptions(
                center = point,
                limit = 1,
                countries = listOf(IsoCountryCode.NORWAY)
            ),
            object : SearchCallback {
                override fun onResults(results: List<SearchResult>, responseInfo: ResponseInfo) {
                    if (results.isNotEmpty()) {
                        val result = results[0]
                        val fylke =
                            getFylke(result.address?.place, result.address?.region) ?: return
                        _stromdistrikt.value = getStromdistrikt(fylke)

                        // INTE FJERN FOR SVINGENDE
                        // Ikke vær en slyngel og fjern dette meget pedagogiske kommentaren under:
                        Log.d("MapViewModel", "Full address: ${result.address}")
                        Log.d("MapViewModel", "Street: ${result.address?.street}")
                        Log.d("MapViewModel", "Place: ${result.address?.place}")
                        Log.d("MapViewModel", "Region: ${result.address?.region}")
                        Log.d("MapViewModel", "Country: ${result.address?.country}")
                        Log.d("MapViewModel", "Postcode: ${result.address?.postcode}")
                        Log.d("MapViewModel", "Coordinates: ${result.coordinate}")
                    }
                }

                override fun onError(e: Exception) {
                    throw Exception("Error in reverseGeocode", e)
                }
            }
        )
    }

    fun getFylke(place: String?, region: String?): Fylker? {
        val text = region ?: place ?: return null
        val normalizedText = text.trim().lowercase()

        return Fylker.entries.find { fylke ->
            val normalizedFylke = fylke.name.replace("_", " ").lowercase()
            normalizedText == normalizedFylke
        }
    }

    fun getStromdistrikt(fylke: Fylker): StromDistrikt? {
        return when (fylke) {
            Fylker.OSLO -> StromDistrikt.NO1
            Fylker.AKERSHUS -> StromDistrikt.NO1
            Fylker.ØSTFOLD -> StromDistrikt.NO1
            Fylker.BUSKERUD -> StromDistrikt.NO1
            Fylker.INNLANDET -> StromDistrikt.NO1
            Fylker.VESTFOLD -> StromDistrikt.NO2
            Fylker.TELEMARK -> StromDistrikt.NO2
            Fylker.AGDER -> StromDistrikt.NO2
            Fylker.ROGALAND -> StromDistrikt.NO2
            Fylker.VESTLAND -> StromDistrikt.NO5
            Fylker.MØRE_OG_ROMSDAL -> StromDistrikt.NO3
            Fylker.TRØNDELAG -> StromDistrikt.NO3
            Fylker.NORDLAND -> StromDistrikt.NO4
            Fylker.TROMS -> StromDistrikt.NO4
            Fylker.FINNMARK -> StromDistrikt.NO4
        }
    }
}
