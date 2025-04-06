package no.uio.ifi.in2000.team_43.ui.satellite

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.material3.Text
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mapbox.geojson.Point
import com.mapbox.geojson.Polygon
import com.mapbox.maps.Style
import com.mapbox.maps.extension.compose.MapboxMap
import com.mapbox.maps.extension.compose.animation.viewport.rememberMapViewportState
import com.mapbox.maps.extension.compose.annotation.generated.PolygonAnnotation
import com.mapbox.maps.extension.compose.style.MapStyle
import com.mapbox.maps.plugin.gestures.OnMapClickListener
import com.mapbox.turf.TurfMeasurement
import no.uio.ifi.in2000.team_43.model.ctx.Ctx

@Composable
fun SatelliteScreen(
    modifier: Modifier = Modifier,
    ctx: Ctx,
    onNextBtnClicked: (area: Double) -> Unit,
) {
    var points by remember { mutableStateOf(listOf<Point>()) }
    var areaInSquareMeters by remember { mutableDoubleStateOf(0.0) }

    val mapViewportState = rememberMapViewportState {
        setCameraOptions {
            zoom(19.0)  // Zoom in close to the roof
            center(ctx.coords ?: Point.fromLngLat(12.30778, 63.99056))
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        MapboxMap(
            Modifier.fillMaxSize(),
            mapViewportState = mapViewportState,
            style = { MapStyle(style = Style.SATELLITE_STREETS) },
            onMapClickListener = OnMapClickListener { point ->
                points = points + point
                true
            }
        ) {
            val pointsList = listOf(points)
            val polygon = Polygon.fromLngLats(pointsList)
            areaInSquareMeters = TurfMeasurement.area(polygon)
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(42.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier
                    .width(231.dp)
                    .height(78.dp)
                    .background(color = Color(0xFFFFFFFF), shape = RoundedCornerShape(size = 8.dp))
                    .align(Alignment.CenterHorizontally),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Klikk for å legge til punkt på takflaten",
                    style = TextStyle(
                        fontSize = 16.sp,
                        lineHeight = 24.sp,
                        fontWeight = FontWeight(500),
                        color = Color(0xFF000000),
                        textAlign = TextAlign.Center,
                        letterSpacing = 0.15.sp,
                    )
                )
            }
            if (points.size >= 3) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${String.format("%.3f", areaInSquareMeters)} m²",
                        style = TextStyle(
                            fontSize = 16.sp,
                            lineHeight = 24.sp,
                            fontWeight = FontWeight(500),
                            color = Color(0xFFFFFFFF),
                            letterSpacing = 0.15.sp,
                        )
                    )
                    Button(
                        onClick = {onNextBtnClicked(areaInSquareMeters)},
                        enabled = true,
                    ) {
                        Text("Neste")
                    }
                }
            }
        }
    }
}