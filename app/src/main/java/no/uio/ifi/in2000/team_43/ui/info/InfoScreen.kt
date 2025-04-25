package no.uio.ifi.in2000.team_43.ui.info

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Card
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
        Text(
            text = "Info Screen",
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 28.sp, // Større skrift for å matche den boblete stilen
                color = MaterialTheme.colorScheme.onBackground
            ),
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Skillelinje
        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

        // Koordinater boks
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = 8.dp,
            shape = RoundedCornerShape(16.dp),
            backgroundColor = Color.White // Hvit bakgrunn
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Koordinater:",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.Medium,
                        fontSize = 22.sp, // Økt størrelse på overskriften
                        color = MaterialTheme.colorScheme.onSurface
                    ),
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Text(
                    text = "Coords: ${ctx.coords?.coordinates()}",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontSize = 20.sp, // Økt skriftstørrelse for bedre lesbarhet
                        color = MaterialTheme.colorScheme.primary,
                        letterSpacing = 0.5.sp
                    ),
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Takflate-verdier boks
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = 8.dp,
            shape = RoundedCornerShape(16.dp),
            backgroundColor = Color.White // Hvit bakgrunn
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Takflate-verdier:",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp, // Økt størrelse på takflate overskrift
                        color = MaterialTheme.colorScheme.onSurface
                    ),
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Text(
                    text = """
                    Lengde: ${ctx.input?.length} m
                    Bredde: ${ctx.input?.width} m
                    Vinkel: ${ctx.input?.angle}°
                    Retning: ${ctx.input?.direction}°
                """.trimIndent(),
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontSize = 20.sp, // Økt størrelse på tekstinnhold
                        color = MaterialTheme.colorScheme.onSurface,
                        letterSpacing = 0.5.sp
                    ),
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }
        }


        Spacer(modifier = Modifier.height(12.dp))

        // NED HIT
        when (ctx.input?.direction) {
            0 -> {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = 8.dp,
                    shape = RoundedCornerShape(16.dp),
                    backgroundColor = Color(0xFFFFCDD2) // Lys rød bakgrunn
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Nord: Det lønner seg ikke å ha solcellepaneler, basert på retningen du har valgt.",
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp,
                                color = Color(0xFFB71C1C) // Mørkere rød tekst
                            ),
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                    }
                }
            }
            90 -> {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = 8.dp,
                    shape = RoundedCornerShape(16.dp),
                    backgroundColor = Color(0xFFC8E6C9) // Lys grønn bakgrunn
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Øst: Du genererer mest strøm på starten av dagen, basert på retningen du har valgt.",
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp,
                                color = Color(0xFF388E3C) // Mørkere grønn tekst
                            ),
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                    }
                }
            }
            180 -> {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = 8.dp,
                    shape = RoundedCornerShape(16.dp),
                    backgroundColor = Color(0xFFC8E6C9) // Lys grønn bakgrunn
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Sør: Det lønner seg å ha solcellepaneler, basert på retningen du har valgt.",
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp,
                                color = Color(0xFF388E3C) // Mørkere grønn tekst
                            ),
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                    }
                }
            }
            270 -> {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = 8.dp,
                    shape = RoundedCornerShape(16.dp),
                    backgroundColor = Color(0xFFC8E6C9) // Lys grønn bakgrunn
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Vest: Du genererer mest strøm på slutten av dagen, basert på retningen du har valgt.",
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp,
                                color = Color(0xFF388E3C) // Mørkere grønn tekst
                            ),
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

//        // Vise PVGIS-data hvis tilgjengelig
//        Text(text = "PVGIS StrømproduksjonsData: (PVGIS) ", style = MaterialTheme.typography.titleMedium)
//        Text(text = "(Har ikke kobla så den søker etter en adresse sin posisjon, dette er en random pos i oslo)", style = MaterialTheme.typography.titleSmall)
//        if (pvgisData != null) {
//            pvgisData?.outputs?.totals?.fixed?.let { totals ->
//                Text(text = "Estimert daglig produksjon: ${totals.E_d} kWh")
//                Text(text = "Estimert månedlig produksjon: ${totals.E_m} kWh")
//                Text(text = "Estimert årlig produksjon: ${totals.E_y} kWh")
//            }
//        } else {
//            Text(text = "Laster PVGIS data...")
//        }
//
//
//
//
//        // Vise Strømpris-data hvis tilgjengelig
//        Spacer(modifier = Modifier.height(16.dp))
//        Text(text = "Strømpris (dato 29.03.2025 prisområde NO1 er selvvalgt) (StromPris):", style = MaterialTheme.typography.titleMedium)
//        Text(text = "Trenger å fikse så den viser etter dagens dato og adresse sitt prisområde", style = MaterialTheme.typography.titleSmall)
//
//        if (stromprisData != null) {
//            stromprisData?.forEach { strompris ->
//                Text(text = "Kl: ${getTimeFromDate(strompris.time_start)} - Pris: ${strompris.NOK_per_kWh} kr/kWh")
//            }
//        } else {
//            Text(text = "Laster strømpris data...")
//        }
//
//
//        // Viser DRcalc data (sol innstråling)
//        Spacer(modifier = Modifier.height(16.dp))
//        Text(text = "Gjennomsnittlig stråling i Juli (DRcalc):", style = MaterialTheme.typography.titleMedium)
//        Text(text = "Trenger å fikse så den viser etter en adresse sin pos og selvvalgt mnd", style = MaterialTheme.typography.titleSmall)
//
//        if (drCalcData != null) {
//            drCalcData!!.forEach { data ->
//                Text(text = "Kl: ${data.time} - Solinnstråling: ${data.G_i} W/m²")
//            }
//
//            // Beregn total solinnstråling for dagen
//            val drcalcTotal = drCalcTotalSum
//            Text(text = "Totalt for hele dagen: $drcalcTotal W/m²")
//        } else {
//            Text(text = "Laster DRcalc data...")
//        }
//
//        Spacer(modifier = Modifier.height(16.dp))

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

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = 8.dp,
                    shape = RoundedCornerShape(16.dp),
                    backgroundColor = Color(0xFFB3E5FC) // Lys blå bakgrunn
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Temperatur: ${tempWeatherData.value} ${tempWeatherData.unit}",
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 24.sp, // Økt størrelse for bedre visuell fremtreden
                                color = MaterialTheme.colorScheme.onSurface
                            ),
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                val cloudWeatherData = weatherList!!.dataList[1] //skydekke

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = 8.dp,
                    shape = RoundedCornerShape(16.dp),
                    backgroundColor = Color(0xFFB3E5FC) // Lys blå bakgrunn
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Skydekke: ${cloudWeatherData.value} ${cloudWeatherData.unit}",
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 24.sp, // Økt størrelse for bedre visuell fremtreden
                                color = MaterialTheme.colorScheme.onSurface
                            ),
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                val snowWeatherData = weatherList!!.dataList[2] //snødekke

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = 8.dp,
                    shape = RoundedCornerShape(16.dp),
                    backgroundColor = Color(0xFFB3E5FC) // Lys blå bakgrunn
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Snømengde: ${snowWeatherData.value} ${snowWeatherData.unit}",
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 24.sp, // Økt størrelse for bedre visuell fremtreden
                                color = MaterialTheme.colorScheme.onSurface
                            ),
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                    }
                }
            }

            else -> {
                CircularProgressIndicator()
            }


            // NED HIT
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


