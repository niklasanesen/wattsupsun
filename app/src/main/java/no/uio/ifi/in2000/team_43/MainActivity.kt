package no.uio.ifi.in2000.team_43

import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview

import kotlinx.serialization.json.Json


import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import no.uio.ifi.in2000.team_43.model.PVGIS.PVGISRes
import no.uio.ifi.in2000.team_43.ui.PVGIS.PVGISViewModel
import no.uio.ifi.in2000.team_43.ui.home.HomeScreen
import no.uio.ifi.in2000.team_43.ui.met.MetViewModel
import no.uio.ifi.in2000.team_43.ui.theme.Team_43Theme


class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // test met api
        val metViewModel = MetViewModel()
        lifecycleScope.launch {
            val res = metViewModel.data.first()
            Log.d("MainActivity", "Met data: $res")
        }

        // test PVGIS api
        val pVGISViewModel = PVGISViewModel()

        lifecycleScope.launch {
            Log.d("MainActivity", "PVGIS Start")

            pVGISViewModel.data.collect { res ->
                Log.d("MainActivity", "PVGIS Data: $res")
            }
d
            val res: PVGISRes? = pVGISViewModel.data.first() // Antar at dette ikke er null
            val jsonString = Json.encodeToString(res) // Serialiser til JSON-streng
            Log.d("MainActivity", jsonString)

        }

        // test
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
