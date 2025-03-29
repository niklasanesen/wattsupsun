package no.uio.ifi.in2000.team_43.ui.info

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import no.uio.ifi.in2000.team_43.ui.PVGIS.PVGISViewModel
import no.uio.ifi.in2000.team_43.ui.strompris.StromprisViewModel
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

// test

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun InfoScreen(
    pvgisViewModel: PVGISViewModel = viewModel(),
    stromprisViewModel: StromprisViewModel = viewModel()
) {
    // Samler data fra ViewModels
    val pvgisData by pvgisViewModel.data.collectAsState()
    val stromprisData by stromprisViewModel.data.collectAsState()
    val drCalcData by pvgisViewModel.drCalcData.collectAsState() // Henter DRcalc data

    val scrollState = rememberScrollState()
    Column(modifier = Modifier.padding(16.dp).verticalScroll(scrollState)) {


        // Tittel for skjermen
        Text(text = "Info Screen", style = MaterialTheme.typography.titleMedium)
        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))


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
            val drcalcTotal = pvgisViewModel.drCalcDataTotalSum
            Text(text = "Totalt for hele dagen: $drcalcTotal W/m²")
        } else {
            Text(text = "Laster DRcalc data...")
        }

    }
}




// Parser tidspunktdataene fra stromAPI til leselig klokkeslett
@RequiresApi(Build.VERSION_CODES.O)
fun getTimeFromDate(dateString: String): String? {
    return try {
        // Definerer formatet på inngangsstrengen
        val formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME

        // Parser datoen og tiden til et ZonedDateTime-objekt
        val dateTime = ZonedDateTime.parse(dateString, formatter)

        // Henter ut klokkeslettet i ønsket format
        dateTime.toLocalTime().toString() // For eksempel "23:00:00"
    } catch (e: DateTimeParseException) {
        // Håndterer feil hvis strengen ikke kan parses
        null
    }
}


