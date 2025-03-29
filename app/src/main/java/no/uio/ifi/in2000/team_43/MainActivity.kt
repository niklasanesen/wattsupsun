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

import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import no.uio.ifi.in2000.team_43.model.DRcalcRes.DRcalcRes
import no.uio.ifi.in2000.team_43.model.DRcalcRes.DailyProfile
import no.uio.ifi.in2000.team_43.model.stromPris.StromprisRes
import no.uio.ifi.in2000.team_43.model.stromPris.toFormattedString
import no.uio.ifi.in2000.team_43.model.PVGIS.PVGISRes
import no.uio.ifi.in2000.team_43.ui.PVGIS.PVGISViewModel
import no.uio.ifi.in2000.team_43.ui.home.HomeScreen
import no.uio.ifi.in2000.team_43.ui.met.MetViewModel
import no.uio.ifi.in2000.team_43.ui.strompris.StromprisViewModel
import no.uio.ifi.in2000.team_43.ui.theme.Team_43Theme


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

        // test strompris API
        val stromprisViewModel = StromprisViewModel()

        lifecycleScope.launch {
            Log.i("TestStrom", "Henter stromprisdata Start")
            val dataene: List<StromprisRes>? = stromprisViewModel.getStromData()

            dataene?.forEach { data ->
                Log.d("TestStrom", "Strompris data: $data")
            }



        }

//        // ny test:
//        lifecycleScope.launch {
//            Log.i("TestStrom","stromtest nr 2 start")
//
//            val dataHentet = stromprisViewModel.getStromListe()
//
//            Log.i("TestStrom", "Hentet data: ${dataHentet.size} elementer")
//            Log.d("TestStrom", "Data: $dataHentet")
//
//        }






        val pvgisViewModel = PVGISViewModel()

        // Test å hente kWh energiproduksjonsdata
        lifecycleScope.launch {
            val dataene: PVGISRes? = pvgisViewModel.getPVGISData()

            Log.d("TestPVGIS", "testytesty ikke skrevet test enda")

            // Skriver ut datene mottak til log:
            Log.d("TestPVGIS", "Tester med data fra pos Oslo. Hele input: ${dataene?.inputs}")
            Log.d("TestPVGIS", "Average daily energy production: ${dataene?.outputs?.totals?.fixed?.E_d} kWh ")
            Log.d("TestPVGIS", "Average monthly energy production: ${dataene?.outputs?.totals?.fixed?.E_m} kWh ")
            Log.d("TestPVGIS", "Average annual energy production: ${dataene?.outputs?.totals?.fixed?.E_y} kWh ")
        }

        // Test å hente pvgis solenergi/influx/solar radiation data
        lifecycleScope.launch {
            Log.d("TestDRcalc", "Starter å hente data fra PVGISviewmodel fra mainactivity")
            val drcalcData: List<DailyProfile>? = pvgisViewModel.getDRcalcData()

            Log.d("TestDRcalc", "Tester med data fra pos Oslo, mnd Juli")

            drcalcData?.forEach { data ->
                Log.d("TestDRcalc", "solinnstråling kl:${data.time} = ${data.G_i} W/m2")
            }

            // Sjekker om den beregner totalt for dagen
            val drcalcTotal = pvgisViewModel.getDRcalcTotalSum()
            Log.d("TestDRcalc", "Totalt for hele dagen: ${drcalcTotal} W/m2")


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
