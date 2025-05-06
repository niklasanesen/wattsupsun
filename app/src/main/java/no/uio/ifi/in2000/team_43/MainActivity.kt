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

        // ViewModels
        val metViewModel = MetViewModel()
        val stromprisViewModel = StromprisViewModel()
        val pvgisViewModel = PVGISViewModel()

        // Logging for debugging
        setupLogging(metViewModel, stromprisViewModel, pvgisViewModel)

        setContent {
            Team_43Theme {
                // HomeScreen is now responsible for showing welcome screen or main content
                HomeScreen(modifier = Modifier.fillMaxSize())
            }
        }
    }

    // Helper function to keep onCreate clean
    private fun setupLogging(
        metViewModel: MetViewModel,
        stromprisViewModel: StromprisViewModel,
        pvgisViewModel: PVGISViewModel
    ) {
        // Log Met API data
        lifecycleScope.launch {
            metViewModel.dataSortedByDate.collect { res ->
                Log.d("MainActivity", "Met data: $res")
            }
        }

        // Log strompris API data
        lifecycleScope.launch {
            Log.i("TestStrom", "Henter strømprisdata Start")
            stromprisViewModel.data.collectLatest { dataene ->
                dataene?.forEach { data ->
                    Log.d("TestStrom", "Strømpris data: $data")
                }
                if (dataene == null) {
                    Log.d("TestStrom", "Ingen strømprisdata tilgjengelig ennå.")
                }
            }
        }

        // Log PVGIS data
        lifecycleScope.launch {
            pvgisViewModel.data.collectLatest { dataene ->
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

        // Log DRcalc data
        lifecycleScope.launch {
            Log.d("TestDRcalc", "Starter å hente data fra PVGISViewModel fra MainActivity")
            pvgisViewModel.drCalcData.collectLatest { drcalcData ->
                if (drcalcData != null) {
                    drcalcData.forEach { data ->
                        Log.d("TestDRcalc", "Solinnstråling kl: ${data.time} = ${data.G_i} W/m2")
                    }
                    val drcalcTotal = pvgisViewModel.getDRcalcTotalSum()
                    Log.d("TestDRcalc", "Totalt for hele dagen: $drcalcTotal W/m2")
                } else {
                    Log.d("TestDRcalc", "Ingen DRcalc-data tilgjengelig ennå.")
                }
            }
        }
    }
}