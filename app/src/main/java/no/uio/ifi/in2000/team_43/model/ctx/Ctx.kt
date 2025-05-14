package no.uio.ifi.in2000.team_43.model.ctx

import com.mapbox.geojson.Point
import no.uio.ifi.in2000.team_43.model.area.Area
import no.uio.ifi.in2000.team_43.model.input.Input
import no.uio.ifi.in2000.team_43.model.electricityDistrict.ElectricityDistrict


data class Ctx(
    val coordinates: Point?,
    val input: Input?,
    var area: Area?,
    var address: String?,
    var electricityDistrict: ElectricityDistrict?,
)