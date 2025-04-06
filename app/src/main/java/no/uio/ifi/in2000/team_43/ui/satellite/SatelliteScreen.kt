package no.uio.ifi.in2000.team_43.ui.satellite

import android.util.Log
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.mapbox.geojson.Point
import com.mapbox.maps.Style
import com.mapbox.maps.extension.compose.MapboxMap
import com.mapbox.maps.extension.compose.animation.viewport.rememberMapViewportState
import com.mapbox.maps.extension.compose.annotation.generated.PolygonAnnotation
import com.mapbox.maps.extension.compose.annotation.rememberIconImage
import com.mapbox.maps.extension.compose.style.MapStyle
import com.mapbox.maps.plugin.gestures.OnMapClickListener
import no.uio.ifi.in2000.team_43.R
import no.uio.ifi.in2000.team_43.model.ctx.Ctx

@Composable
fun SatelliteScreen(modifier: Modifier = Modifier, ctx: Ctx) {
    var points by remember { mutableStateOf(listOf<Point>()) }

    val mapViewportState = rememberMapViewportState {
        setCameraOptions {
            zoom(19.0)  // Zoom in close to the roof
            center(ctx.coords ?: Point.fromLngLat(12.30778, 63.99056))
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        MapboxMap(
            Modifier.fillMaxSize(),
            mapViewportState = mapViewportState,
            style = { MapStyle(style = Style.SATELLITE_STREETS)},
            onMapClickListener = OnMapClickListener { point ->
                points = points + point
                true
            }
        ) {
            PolygonAnnotation(
                points = listOf(points),
            ) {
                fillColor = Color.LightGray
                fillOpacity = 0.5
                fillOutlineColor = Color.Yellow
                interactionsState.onClicked {
                    true
                }.onLongClicked {
                    true
                }.onDragged {
                }.also {
                    it.isDraggable = true
                }
            }
        }
    }
}