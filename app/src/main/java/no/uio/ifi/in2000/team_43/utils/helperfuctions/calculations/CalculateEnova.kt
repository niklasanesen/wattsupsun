package no.uio.ifi.in2000.team_43.utils.helperfuctions.calculations

import kotlin.math.floor

fun calculateEnova(kWpPerPanel: Double, numPanel: Int): Double {
    if (numPanel < 1 || kWpPerPanel < 0) return 0.0

    val standardSupport = 7500
    val supportPerKWp = 1250
    val totalKWp = kWpPerPanel * numPanel
    val variableSupport = floor(minOf(totalKWp, 20.0)) * supportPerKWp


    // will return max 32500 total, according to enova
    return standardSupport + variableSupport
}