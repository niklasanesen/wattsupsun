package no.uio.ifi.in2000.team_43.ui.satellite

import androidx.lifecycle.ViewModel
import com.mapbox.geojson.Point
import com.mapbox.geojson.Polygon
import com.mapbox.turf.TurfMeasurement
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import no.uio.ifi.in2000.team_43.model.map.RoofPolygon
import kotlin.math.roundToInt

class SatelliteViewModel : ViewModel() {

    private val _points = MutableStateFlow<List<Point>>(emptyList())
    val points: StateFlow<List<Point>> = _points.asStateFlow()

    private val _savedPolygons = MutableStateFlow<List<RoofPolygon>>(emptyList())
    val savedPolygons: StateFlow<List<RoofPolygon>> = _savedPolygons.asStateFlow()

    private val _areaInSquareMeters = MutableStateFlow(0)
    val areaInSquareMeters: StateFlow<Int> = _areaInSquareMeters.asStateFlow()

    private val _length = MutableStateFlow(0.0)
    val length: StateFlow<Double> = _length.asStateFlow()

    private val _width = MutableStateFlow(0.0)
    val width: StateFlow<Double> = _width.asStateFlow()

    var registeredPolygon = false

    fun addPoint(point: Point) {
        if (registeredPolygon) return

        val newPoints = _points.value + point
        _points.value = newPoints

        if (newPoints.size == 4) {
            val closed = if (newPoints.first() != newPoints.last())
                newPoints + newPoints.first() else newPoints

            val polygon = Polygon.fromLngLats(listOf(closed))
            _areaInSquareMeters.value = TurfMeasurement.area(polygon).roundToInt()

            val length1 = TurfMeasurement.distance(newPoints[0], newPoints[1], "metres")
            val length2 = TurfMeasurement.distance(newPoints[2], newPoints[3], "metres")
            val width1 = TurfMeasurement.distance(newPoints[1], newPoints[2], "metres")
            val width2 = TurfMeasurement.distance(newPoints[3], newPoints[0], "metres")

            _length.value = (length1 + length2) / 2
            _width.value = (width1 + width2) / 2

            _savedPolygons.value = listOf(RoofPolygon(closed, _areaInSquareMeters.value))
            registeredPolygon = true
            _points.value = emptyList()
        }
    }

    fun undoLastPoint() {
        if (_points.value.isNotEmpty()) {
            _points.value = _points.value.dropLast(1)
        }
    }

    fun deletePolygon() {
        if (_savedPolygons.value.isNotEmpty()) {
            _savedPolygons.value = emptyList()
            _areaInSquareMeters.value = 0
            _length.value = 0.0
            _width.value = 0.0
            registeredPolygon = false
        }
    }
}