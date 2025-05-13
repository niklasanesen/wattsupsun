package no.uio.ifi.in2000.team_43

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import no.uio.ifi.in2000.team_43.monitorInternet.AppRoot
import no.uio.ifi.in2000.team_43.monitorInternet.NetworkViewModel
import no.uio.ifi.in2000.team_43.ui.PVGIS.PVGISViewModel
import no.uio.ifi.in2000.team_43.ui.home.HomeScreen
import no.uio.ifi.in2000.team_43.ui.met.MetViewModel
import no.uio.ifi.in2000.team_43.ui.electricityPrice.ElectricityPriceViewModel
import no.uio.ifi.in2000.team_43.ui.theme.Team_43Theme


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Lock screen in portrait mode
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        // ViewModels
        val metViewModel = MetViewModel()
        val electricityPriceViewModel = ElectricityPriceViewModel()
        val pvgisViewModel = PVGISViewModel()

        // Logging for debugging
        setupLogging(metViewModel, electricityPriceViewModel, pvgisViewModel)

        setContent {
            Team_43Theme {
                val viewModel: NetworkViewModel = viewModel()
                val networkStatus by viewModel.networkStatus.collectAsState()

                AppRoot(networkStatus = networkStatus) {
                    HomeScreen(
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }

    // Helper function to keep onCreate clean
    private fun setupLogging(
        metViewModel: MetViewModel,
        electricityPriceViewModel: ElectricityPriceViewModel,
        pvgisViewModel: PVGISViewModel
    ) {
        // Log Met API data
        lifecycleScope.launch {
            metViewModel.dataSortedByDate.collect { res ->
                Log.d("MainActivity", "Met data: $res")
            }
        }

        // Log electricity price API data
        lifecycleScope.launch {
            Log.i("TestElectricityPrice", "Fetching electricity price data Start")
            electricityPriceViewModel.data.collectLatest { data ->
                data?.forEach { data ->
                    Log.d("TestElectricityPrice", "electricity price data: $data")
                }
                if (data == null) {
                    Log.d("TestElectricityPrice", "No electricity price data available yet.")
                }
            }
        }

        // Log PVGIS data
        lifecycleScope.launch {
            pvgisViewModel.data.collectLatest { data ->
                if (data != null) {
                    Log.d("TestPVGIS", "Testing with data from pos Oslo. Whole input: ${data.inputs}")
                    Log.d("TestPVGIS", "Average daily energy production: ${data.outputs.totals.fixed.eD} kWh ")
                    Log.d("TestPVGIS", "Average monthly energy production: ${data.outputs.totals.fixed.eM} kWh ")
                    Log.d("TestPVGIS", "Average annual energy production: ${data.outputs.totals.fixed.eY} kWh ")
                } else {
                    Log.d("TestPVGIS", "Data is null, waiting for data...")
                }
            }
        }

        // Log DRcalc data
        lifecycleScope.launch {
            Log.d("TestDRcalc", "Starting to fetch data from PVGISViewModel from MainActivity")
            pvgisViewModel.drCalcData.collectLatest { drcalcData ->
                if (drcalcData != null) {
                    drcalcData.forEach { data ->
                        Log.d("TestDRcalc", "Solar insolation kl: ${data.time} = ${data.gI} W/m2")
                    }
                    val drcalcTotal = pvgisViewModel.getDRcalcTotalSum()
                    Log.d("TestDRcalc", "Total for whole day: $drcalcTotal W/m2")
                } else {
                    Log.d("TestDRcalc", "No DRcalc data available yet.")
                }
            }
        }
    }
}