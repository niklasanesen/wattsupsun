package no.uio.ifi.in2000.team_43.utils.helperfuctions.calculations


import no.uio.ifi.in2000.team_43.model.garfStuff.GrafMeasurement
import no.uio.ifi.in2000.team_43.model.stromPris.StromprisRes

fun calcMoneySaved(
    direction: Int,
    stromprisDataPerMnd: List<List<StromprisRes>?>?,
    produksjonPerMnd: List<GrafMeasurement>
): List<Float> {
    val savingsPerMonth = mutableListOf<Float>()

    // Velg strømpris-indeks basert på retning
    val prisIndex = when (direction) {
        0, 180 -> 13    // kl. 13:00
        90 -> 10        // kl. 10:00
        270 -> 16       // kl. 16:00
        else -> 13      // fallback
    }

    val justeringsFaktor = when (direction) {
        0 -> 1.0f
        90 -> 0.9f
        180 -> 0.7f
        270 -> 0.9f
        else -> 1.0f
    }

    produksjonPerMnd.forEachIndexed { index, grafMeasurement ->
        val produksjon = grafMeasurement.value.toFloat()
        val priserForMnd = stromprisDataPerMnd?.getOrNull(index) ?: return@forEachIndexed
        if (prisIndex >= priserForMnd.size) return@forEachIndexed

        val valgtPris = priserForMnd[prisIndex].NOK_per_kWh.toFloat()
        val spart = produksjon * valgtPris * justeringsFaktor
        savingsPerMonth.add(spart)
    }

    return savingsPerMonth
}
