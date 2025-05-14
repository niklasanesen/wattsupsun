package no.uio.ifi.in2000.team_43.ui.map

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
import no.uio.ifi.in2000.team_43.model.electricityDistrict.Counties
import no.uio.ifi.in2000.team_43.model.electricityDistrict.ElectricityDistrict
import no.uio.ifi.in2000.team_43.model.map.Suggestion

private const val MAX_SUGGESTIONS = 4
private const val MAX_NAME_LEN = 56

class MapViewModel() : ViewModel() {
    private val placeAutocomplete = PlaceAutocomplete.Companion.create()
    //although this function is deprecated, this was the only viable solution we found in
    // implementing search for mapbox
    @Suppress("DEPRECATION")
    private val searchEngine = SearchEngine.Companion.createSearchEngine(SearchEngineSettings())

    private val _suggestions = MutableStateFlow<List<Suggestion>>(emptyList())
    val suggestions: StateFlow<List<Suggestion>> = _suggestions.asStateFlow()

    private val _coordinates = MutableStateFlow<Point?>(Point.fromLngLat(12.30778, 63.99056))
    val coordinates: StateFlow<Point?> = _coordinates.asStateFlow()

    private val _address = MutableStateFlow<String?>(null)
    val address: StateFlow<String?> = _address.asStateFlow()

    private val _electricityDistrict = MutableStateFlow<ElectricityDistrict?>(null)
    val electricityDistrict: StateFlow<ElectricityDistrict?> = _electricityDistrict.asStateFlow()

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

    fun select(address: String) {
        searchEngine.search(
            address,
            SearchOptions(limit = 1),
            object : SearchSuggestionsCallback {
                override fun onSuggestions(suggestions: List<SearchSuggestion>, responseInfo: ResponseInfo) {
                    val suggestion = suggestions.firstOrNull()
                    if (suggestion != null) {
                        _coordinates.value = suggestion.coordinate
                        setElectricityDistrict(suggestion.coordinate)
                        _address.value = suggestion.fullAddress
                    }

                }
                override fun onError(e: Exception) {
                    _coordinates.value = null
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

    // credit to: fredrik
    fun setElectricityDistrict(point: Point?) {
        if (point == null) {
            return
        }
        searchEngine.search(
            ReverseGeoOptions(
                center = point,
                limit = 1,
                countries = listOf(IsoCountryCode.Companion.NORWAY)
            ),
            object : SearchCallback {
                override fun onResults(results: List<SearchResult>, responseInfo: ResponseInfo) {
                    if (results.isNotEmpty()) {
                        val result = results[0]
                        val county =
                            getCounty(result.address?.place, result.address?.region) ?: return
                        _electricityDistrict.value = getElectricityDistrict(county)

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

    fun getCounty(place: String?, region: String?): Counties? {
        val text = region ?: place ?: return null
        val normalizedText = text.trim().lowercase()

        return Counties.entries.find { county ->
            val normalizedCounty = county.name.replace("_", " ").lowercase()
            normalizedText == normalizedCounty
        }
    }

    fun getElectricityDistrict(county: Counties): ElectricityDistrict? {
        return when (county) {
            Counties.OSLO -> ElectricityDistrict.NO1
            Counties.AKERSHUS -> ElectricityDistrict.NO1
            Counties.ØSTFOLD -> ElectricityDistrict.NO1
            Counties.BUSKERUD -> ElectricityDistrict.NO1
            Counties.INNLANDET -> ElectricityDistrict.NO1
            Counties.VESTFOLD -> ElectricityDistrict.NO2
            Counties.TELEMARK -> ElectricityDistrict.NO2
            Counties.AGDER -> ElectricityDistrict.NO2
            Counties.ROGALAND -> ElectricityDistrict.NO2
            Counties.VESTLAND -> ElectricityDistrict.NO5
            Counties.MØRE_OG_ROMSDAL -> ElectricityDistrict.NO3
            Counties.TRØNDELAG -> ElectricityDistrict.NO3
            Counties.NORDLAND -> ElectricityDistrict.NO4
            Counties.TROMS -> ElectricityDistrict.NO4
            Counties.FINNMARK -> ElectricityDistrict.NO4
        }
    }
}