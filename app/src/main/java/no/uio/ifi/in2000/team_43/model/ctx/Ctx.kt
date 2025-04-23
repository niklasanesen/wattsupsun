package no.uio.ifi.in2000.team_43.model.ctx

import com.mapbox.geojson.Point
import no.uio.ifi.in2000.team_43.model.input.Input

data class Ctx(
    val coords: Point?,
    val input: Input?,
    var areaInSquareMeters: Double?
)