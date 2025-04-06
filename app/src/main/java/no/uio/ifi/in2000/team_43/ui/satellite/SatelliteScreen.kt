package no.uio.ifi.in2000.team_43.ui.satellite

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.mapbox.geojson.Point
import com.mapbox.maps.Style
import com.mapbox.maps.extension.compose.MapboxMap
import com.mapbox.maps.extension.compose.animation.viewport.rememberMapViewportState
import com.mapbox.maps.extension.compose.style.MapStyle
import no.uio.ifi.in2000.team_43.model.ctx.Ctx

@Composable
fun SatelliteScreen(modifier: Modifier = Modifier, ctx: Ctx,coords: Point?) {
    val mapViewportState = rememberMapViewportState {
        setCameraOptions {
            zoom(19.0)  // Zoom in close to the roof
            center(coords)
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        MapboxMap(
            Modifier.fillMaxSize(),
            mapViewportState = mapViewportState,
            style = { MapStyle(style = Style.SATELLITE_STREETS)}
        )
    }
}