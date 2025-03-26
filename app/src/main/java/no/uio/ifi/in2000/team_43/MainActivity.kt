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
import no.uio.ifi.in2000.team_43.model.stromPris.StromprisRes
import no.uio.ifi.in2000.team_43.model.stromPris.toFormattedString
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
