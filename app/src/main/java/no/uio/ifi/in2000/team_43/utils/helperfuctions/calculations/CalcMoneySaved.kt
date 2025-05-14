package no.uio.ifi.in2000.team_43.utils.helperfuctions.calculations


import android.util.Log
import no.uio.ifi.in2000.team_43.model.electricityPrice.ElectricityPriceRes
import no.uio.ifi.in2000.team_43.model.graphRes.GraphMeasurement

fun calcMoneySaved(
    direction: Int,
    electricityPriceDataPerMonth: List<List<ElectricityPriceRes>?>?,
    productionPerMonth: List<GraphMeasurement>
): List<Float> {
    Log.d("saveIssue", "dir = $direction, electric = $electricityPriceDataPerMonth, prodPrMonth = $productionPerMonth")
    val savingsPerMonth = mutableListOf<Float>()

    // Choose electricity price index based on direction
    val priceIndex = when (direction) {
        0, 180 -> 13    // kl. 13:00
        90 -> 10        // kl. 10:00
        270 -> 16       // kl. 16:00
        else -> 13      // fallback
    }

    val adjustmentFactor = when (direction) {
        0 -> 1.0f
        90 -> 0.9f
        180 -> 0.7f
        270 -> 0.9f
        else -> 1.0f
    }

    productionPerMonth.forEachIndexed { index, graphMeasurement ->
        val production = graphMeasurement.value.toFloat()
        val pricesForMonth = electricityPriceDataPerMonth?.getOrNull(index) ?: return@forEachIndexed
        if (priceIndex >= pricesForMonth.size) return@forEachIndexed

        val chosenPrice = pricesForMonth[priceIndex].NOK_per_kWh.toFloat()
        val saved = production * chosenPrice * adjustmentFactor
        savingsPerMonth.add(saved)
    }

    return savingsPerMonth
}
