package no.uio.ifi.in2000.team_43.ui.met

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.concurrent.ConcurrentHashMap

/**
 * Extension functions for MetViewModel to add caching for production calculations
 */

// Cache for production calculations to improve performance
private val productionCache = ConcurrentHashMap<String, Double>()

/**
 * Calculate solar panel production with caching to improve performance.
 * This caches results to avoid redundant calculations with the same parameters.
 */
fun MetViewModel.calculateProduction(
    length: Int,
    width: Int,
    angle: Int,
    temperature: Double,
    solarIrradiance: Double
): Double {
    // Create a cache key based on input parameters
    val cacheKey = "$length-$width-$angle-${temperature.toInt()}-${solarIrradiance.toInt()}"

    // Check if result is already in cache
    return productionCache.computeIfAbsent(cacheKey) {
        // Calculate if not in cache
        val result = beregnStromproduksjon(
            lengde = length,
            bredde = width,
            vinkel = angle,
            temperatur = temperature,
            solinnstraling = solarIrradiance
        )

        // Round to 2 decimal places
        BigDecimal(result).setScale(2, RoundingMode.HALF_UP).toDouble()
    }
}

/**
 * Preload production calculations for common values to improve performance
 */
fun MetViewModel.preloadProductionCalculations(
    length: Int,
    width: Int,
    angle: Int
) {
    viewModelScope.launch(Dispatchers.Default) {
        // Preload some common temperature and irradiance values
        for (temp in -10..40 step 5) {
            for (irradiance in 50..1000 step 50) {
                calculateProduction(length, width, angle, temp.toDouble(), irradiance.toDouble())
            }
        }
    }
}

/**
 * Clear the production cache - useful if calculation model changes
 */
fun MetViewModel.clearProductionCache() {
    productionCache.clear()
}