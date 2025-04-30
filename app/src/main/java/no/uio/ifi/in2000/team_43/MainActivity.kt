package no.uio.ifi.in2000.team_43

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import no.uio.ifi.in2000.team_43.ui.PVGIS.PVGISViewModel
import no.uio.ifi.in2000.team_43.ui.home.HomeScreen
import no.uio.ifi.in2000.team_43.ui.met.MetViewModel
import no.uio.ifi.in2000.team_43.ui.strompris.StromprisViewModel
import no.uio.ifi.in2000.team_43.ui.theme.Team_43Theme


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Lås skjermen i portrettmodus
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        // test met api
        val viewModel = MetViewModel()
        lifecycleScope.launch {
            viewModel.dataSortedByDate.collect { res ->
                Log.d("MainActivity", "Met data: $res")
            }
        }

        // test strompris API
        val stromprisViewModel = StromprisViewModel()

        lifecycleScope.launch {
            Log.i("TestStrom", "Henter strømprisdata Start")

            stromprisViewModel.data.collectLatest { dataene ->
                // Logger hver oppdatering i strømprisdata
                dataene?.forEach { data ->
                    Log.d("TestStrom", "Strømpris data: $data")
                }

                if (dataene == null) {
                    Log.d("TestStrom", "Ingen strømprisdata tilgjengelig ennå.")
                }
            }
        }

        val pvgisViewModel = PVGISViewModel()

        // Test å hente kWh energiproduksjonsdata
        lifecycleScope.launch {
            pvgisViewModel.data.collectLatest { dataene ->
                Log.d("TestPVGIS", "testytesty ikke skrevet test enda")

                if (dataene != null) {
                    Log.d("TestPVGIS", "Tester med data fra pos Oslo. Hele input: ${dataene.inputs}")
                    Log.d("TestPVGIS", "Average daily energy production: ${dataene.outputs.totals.fixed.E_d} kWh ")
                    Log.d("TestPVGIS", "Average monthly energy production: ${dataene.outputs.totals.fixed.E_m} kWh ")
                    Log.d("TestPVGIS", "Average annual energy production: ${dataene.outputs.totals.fixed.E_y} kWh ")
                } else {
                    Log.d("TestPVGIS", "Data er null, venter på data...")
                }
            }
        }

        // Test å hente pvgis solenergi/influx/solar radiation data
        lifecycleScope.launch {
            Log.d("TestDRcalc", "Starter å hente data fra PVGISViewModel fra MainActivity")

            pvgisViewModel.drCalcData.collectLatest { drcalcData ->
                Log.d("TestDRcalc", "Tester med data fra pos Oslo, mnd Juli")

                if (drcalcData != null) {
                    drcalcData.forEach { data ->
                        Log.d("TestDRcalc", "Solinnstråling kl: ${data.time} = ${data.G_i} W/m2")
                    }

                    //beregner totalt for dagen med egen funksjon
                    val drcalcTotal = pvgisViewModel.getDRcalcTotalSum()
                    Log.d("TestDRcalc", "Totalt for hele dagen: $drcalcTotal W/m2")
                } else {
                    Log.d("TestDRcalc", "Ingen DRcalc-data tilgjengelig ennå.")
                }
            }
        }

        setContent {
            Team_43Theme {
                Scaffold(modifier = Modifier) { innerPadding ->
                    HomeScreen()
                    //InfoScreen()

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