package no.uio.ifi.in2000.team_43.utils.helperfuctions.calculations

import android.util.Log


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


    var produksjon = solinnstraling * areal

    if (luftTemperatur > optimalTemp) {
        val tempOver = luftTemperatur - optimalTemp
        val korreksjonsfaktor = 1 - (tempOver * tempTapPerGrad)
        produksjon *= korreksjonsfaktor
    }


    val vinkelAvvik = kotlin.math.abs(panelVinkel - optimalVinkel)
    val tapPerGrad = 0.01
    val effektTap = 1 - (vinkelAvvik * tapPerGrad)
    produksjon *= effektTap.coerceAtLeast(0.0)

    Log.d("ProductionCalc", "Produksjon: $produksjon kWh")
    return produksjon
}