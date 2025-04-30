package no.uio.ifi.in2000.team_43.utils.helperfuctions.calculations

import android.util.Log

// Denne brukes når solinnstrålig er målt til kwh !!!!
fun ProductionCalc(
    panelLengde: Int,
    panelBredde: Int,
    panelVinkel: Int,
    luftTemperatur: Double,
    solinnstraling: Double  // kWh/m²
): Double {
    val areal = panelLengde * panelBredde // m²
    val optimalVinkel = 35.0              // grader
    val optimalTemp = 25.0                // °C
    val tempTapPerGrad = 0.003            // 0.3% per grad

    // 1. Start med forventet produksjon (kWh)
    var produksjon = solinnstraling * areal

    // 2. Temperaturtap
    if (luftTemperatur > optimalTemp) {
        val tempOver = luftTemperatur - optimalTemp
        val korreksjonsfaktor = 1 - (tempOver * tempTapPerGrad)
        produksjon *= korreksjonsfaktor
    }

    // 3. Vinkelavvik (tap per grad avvik fra optimal)
    val vinkelAvvik = kotlin.math.abs(panelVinkel - optimalVinkel)
    val tapPerGrad = 0.01 // 1 % tap per grad
    val effektTap = 1 - (vinkelAvvik * tapPerGrad)
    produksjon *= effektTap.coerceAtLeast(0.0)

    Log.d("ProductionCalc", "Produksjon: $produksjon kWh")
    return produksjon
}