package no.uio.ifi.in2000.team_43.model.ctx

import android.health.connect.datatypes.units.Length
import com.mapbox.geojson.Point
import no.uio.ifi.in2000.team_43.model.areal.Areal
import no.uio.ifi.in2000.team_43.model.input.Input

data class Ctx(
    val coords: Point?,
    val input: Input?,
    var areal: Areal?,
    var address: String?
)