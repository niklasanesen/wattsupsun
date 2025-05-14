package no.uio.ifi.in2000.team_43.utils.helperfuctions.calculations

import android.util.Log


fun productionCalc(
    panelLength: Int,
    panelWidth: Int,
    panelAngle: Int,
    airTemperature: Double,
    solarInsolation: Double  // kWh/m²
): Double {
    val areal = panelLength * panelWidth // m²
    val optimalAngle = 35.0              // degrees
    val optimalTemp = 25.0                // °C
    val tempTapPerGrad = 0.003            // 0.3% per degree


    var production = solarInsolation * areal

    if (airTemperature > optimalTemp) {
        val tempOver = airTemperature - optimalTemp
        val correctionFactor = 1 - (tempOver * tempTapPerGrad)
        production *= correctionFactor
    }


    val angleDeviancy = kotlin.math.abs(panelAngle - optimalAngle)
    val tapPerGrad = 0.01
    val effectLoss = 1 - (angleDeviancy * tapPerGrad)
    production *= effectLoss.coerceAtLeast(0.0)

    Log.d("ProductionCalc", "Production: $production kWh")
    return production
}