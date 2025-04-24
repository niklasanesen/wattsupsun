package no.uio.ifi.in2000.team_43.model.map

import com.mapbox.geojson.Point

data class RoofPolygon(
    val points: List<Point>,
    val area: Double
)