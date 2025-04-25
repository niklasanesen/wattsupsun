package no.uio.ifi.in2000.team_43.ui.info

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import no.uio.ifi.in2000.team_43.model.ctx.Ctx
import no.uio.ifi.in2000.team_43.model.met.Coordinates
import no.uio.ifi.in2000.team_43.ui.PVGIS.PVGISViewModel
import no.uio.ifi.in2000.team_43.ui.met.MetViewModel
import no.uio.ifi.in2000.team_43.ui.strompris.StromprisViewModel
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

// test

@Composable
fun InfoScreen(
    modifier: Modifier = Modifier,
    pvgisViewModel: PVGISViewModel = viewModel(),
    stromprisViewModel: StromprisViewModel = viewModel(),
    ctx: Ctx,
    metViewModel: MetViewModel = viewModel()
) {
    // Samler data fra ViewModels
    val pvgisData by pvgisViewModel.data.collectAsState()
    val stromprisData by stromprisViewModel.data.collectAsState()
    val drCalcData by pvgisViewModel.drCalcData.collectAsState() // Henter DRcalc data
    val drCalcTotalSum by pvgisViewModel.drCalcDataTotalSum.collectAsState() // Henter kalkulert totalsum
    val weatherList by metViewModel.data.collectAsState()

    val errorMessage by metViewModel.errorMessage.collectAsState()

    val scrollState = rememberScrollState()
    Column(modifier = modifier.padding(20.dp).verticalScroll(scrollState)) {

        // Tittel for skjermen
        Text(text = "Info Screen", style = MaterialTheme.typography.titleMedium)
        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

        // Koordinater og verdier fra bruker
        Text("coords: ${ctx.coords?.coordinates()}")
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = "Takflate-verdier:", style = MaterialTheme.typography.titleMedium)
        Text("Lengde: ${ctx.input?.length} m \nBredde: ${ctx.input?.width} m \nVinkel: ${ctx.input?.angle}° \nRetning: ${ctx.input?.direction}°")
        Spacer(modifier = Modifier.height(16.dp))
        /*
        Text("areaInSquareMeters: ${ctx.areal?.areal}")
        Text("lengde: ${ctx.areal?.length}")
        Text("bredde: ${ctx.areal?.width}")
        */
        Spacer(modifier = Modifier.height(12.dp))

        when (ctx.input?.direction) {
            0 -> Text("Nord: Det lønner seg ikke å ha solcellepaneler, basert på retningen du har valgt.")
            90 -> Text("Øst: Du genererer mest strøm på starten av dagen, basert på retningen du har valgt.")
            180 -> Text("Sør: Det lønner seg å ha solcellepaneler, basert på retningen du har valgt.")
            270 -> Text("Vest: Du genererer mest strøm på slutten av dagen, basert på retningen du har valgt.")
        }

        Spacer(modifier = Modifier.height(32.dp))


        /*
        // Vise PVGIS-data hvis tilgjengelig
        Text(text = "PVGIS StrømproduksjonsData: (PVGIS) ", style = MaterialTheme.typography.titleMedium)
        Text(text = "(Har ikke kobla så den søker etter en adresse sin posisjon, dette er en random pos i oslo)", style = MaterialTheme.typography.titleSmall)
        if (pvgisData != null) {
            pvgisData?.outputs?.totals?.fixed?.let { totals ->
                Text(text = "Estimert daglig produksjon: ${totals.E_d} kWh")
                Text(text = "Estimert månedlig produksjon: ${totals.E_m} kWh")
                Text(text = "Estimert årlig produksjon: ${totals.E_y} kWh")
            }
        } else {
            Text(text = "Laster PVGIS data...")
        }




        // Vise Strømpris-data hvis tilgjengelig
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = "Strømpris (dato 29.03.2025 prisområde NO1 er selvvalgt) (StromPris):", style = MaterialTheme.typography.titleMedium)
        Text(text = "Trenger å fikse så den viser etter dagens dato og adresse sitt prisområde", style = MaterialTheme.typography.titleSmall)

        if (stromprisData != null) {
            stromprisData?.forEach { strompris ->
                Text(text = "Kl: ${getTimeFromDate(strompris.time_start)} - Pris: ${strompris.NOK_per_kWh} kr/kWh")
            }
        } else {
            Text(text = "Laster strømpris data...")
        }


        // Viser DRcalc data (sol innstråling)
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = "Gjennomsnittlig stråling i Juli (DRcalc):", style = MaterialTheme.typography.titleMedium)
        Text(text = "Trenger å fikse så den viser etter en adresse sin pos og selvvalgt mnd", style = MaterialTheme.typography.titleSmall)

        if (drCalcData != null) {
            drCalcData!!.forEach { data ->
                Text(text = "Kl: ${data.time} - Solinnstråling: ${data.G_i} W/m²")
            }

            // Beregn total solinnstråling for dagen
            val drcalcTotal = drCalcTotalSum
            Text(text = "Totalt for hele dagen: $drcalcTotal W/m²")
        } else {
            Text(text = "Laster DRcalc data...")
        }

        Spacer(modifier = Modifier.height(16.dp))


         */
        LaunchedEffect(ctx.coords) {
            ctx.coords?.let {
                metViewModel.fetchWeatherData(Coordinates(it.latitude(), it.longitude()))
            }
        }

        when {
            errorMessage != null -> {
                Text(
                    text = "Error: $errorMessage",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyLarge
                )
            }

            weatherList?.dataList?.isNotEmpty() == true -> {
                val tempWeatherData = weatherList!!.dataList[0] //temp

                Text(
                    text = "Temperatur: ${tempWeatherData.value} ${tempWeatherData.unit}",
                    style = MaterialTheme.typography.headlineMedium
                )

                Spacer(modifier = Modifier.height(16.dp))

                val cloudWeatherData = weatherList!!.dataList[1] //skydekke

                Text(
                    text = "Skydekke: ${cloudWeatherData.value} ${cloudWeatherData.unit}",
                    style = MaterialTheme.typography.headlineMedium
                )

                Spacer(modifier = Modifier.height(16.dp))

                val snowWeatherData = weatherList!!.dataList[2] //snødekke

                Text(
                    text = "Snømengde: ${snowWeatherData.value} ${snowWeatherData.unit}",
                    style = MaterialTheme.typography.headlineMedium
                )
            }

            else -> {
                CircularProgressIndicator()
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {/*TODO*/},
            modifier = Modifier
                .align(alignment = Alignment.End)
        ) {
            Text("Lagre adresse")
        }
    }



    /*
Box(modifier = modifier
.fillMaxSize()
.padding(20.dp)
) {
Column {
Text("coords: ${ctx.coords?.coordinates()}")
Spacer(modifier = Modifier.height(16.dp))
Text("Inputverdier av takflate: \nLengde: ${ctx.input?.length} \nBredde: ${ctx.input?.width} \nVinkel: ${ctx.input?.angle}° \nRetning: ${ctx.input?.direction}°")
}
}

*/
}




// Parser tidspunktdataene fra stromAPI til leselig klokkeslett
fun getTimeFromDate(dateString: String): String? {
    return try {
        // Definerer formatet på inngangsstrengen
        val formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME

        // Parser datoen og tiden til et ZonedDateTime-objekt
        val dateTime = ZonedDateTime.parse(dateString, formatter)

        // Henter ut klokkeslettet i ønsket format
        dateTime.toLocalTime().toString() // For eksempel "23:00:00"
    } catch (e: DateTimeParseException) {
        e.printStackTrace()
        // Håndterer feil hvis strengen ikke kan parses
        null
    }
}


