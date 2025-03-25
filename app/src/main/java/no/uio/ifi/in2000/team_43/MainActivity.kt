package no.uio.ifi.in2000.team_43

import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import no.uio.ifi.in2000.team_43.ui.home.HomeScreen
import no.uio.ifi.in2000.team_43.ui.met.MetViewModel
import no.uio.ifi.in2000.team_43.ui.theme.Team_43Theme
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.lifecycle.Lifecycle
import com.mapbox.search.autocomplete.PlaceAutocomplete
import com.mapbox.common.MapboxOptions
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // test met api
        val viewModel = MetViewModel()
        lifecycleScope.launch {
            viewModel.data.collect { res ->
                Log.d("MainActivity", "Met data: $res")
            }
        }

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

        setContent {
            Team_43Theme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->

                    HomeScreen()

                    /*
                    Greeting(
                        name = "team 43",
                        modifier = Modifier.padding(innerPadding)
                    )
                     */
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    Team_43Theme {
        Greeting("Android")
    }
}
