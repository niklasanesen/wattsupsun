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
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
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
import com.mapbox.maps.extension.compose.annotation.IconImage
import com.mapbox.maps.extension.compose.annotation.generated.PointAnnotation
import com.mapbox.maps.extension.compose.annotation.generated.PolygonAnnotation
import com.mapbox.maps.extension.compose.style.MapStyle
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationOptions
import com.mapbox.maps.plugin.gestures.OnMapClickListener
import com.mapbox.turf.TurfMeasurement
import no.uio.ifi.in2000.team_43.R
import no.uio.ifi.in2000.team_43.model.ctx.Ctx
import no.uio.ifi.in2000.team_43.utils.helperfuctions.map.getBitmapFromVectorDrawable

@Composable
fun SatelliteScreen(
    modifier: Modifier = Modifier,
    ctx: Ctx,
    onNextBtnClicked: (area: Double) -> (Unit)
) {

    var points by remember { mutableStateOf(listOf<Point>()) }
    var areaInSquareMeters by remember { mutableDoubleStateOf(0.0) }
    val pointAnnotations = remember { mutableStateListOf<PointAnnotationOptions>() }
    val context = LocalContext.current
    val bitmap = remember {
        try {
            getBitmapFromVectorDrawable(context, R.drawable.ic_map_marker)
        } catch (e: Exception) {
            Log.e("BitmapError", "Failed to create bitmap for icon", e)
            null
        }
    }
    val mapIconImage = remember(bitmap) {
        bitmap?.let { IconImage(it) }
    }

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
            style = { MapStyle(style = Style.SATELLITE_STREETS)},
            onMapClickListener = OnMapClickListener { point ->
                points = points + point
                if (bitmap != null) {
                    pointAnnotations.add(
                        PointAnnotationOptions()
                            .withPoint(point)
                            .withIconImage(bitmap)
                            .withIconSize(1.0)
                            .withIconColor("#FF0000")
                    )
                }
                true
            }
        ) {
            var counter = 0
            if (mapIconImage != null && points.isNotEmpty()) {
                pointAnnotations.forEach { pointAnnotationOptions ->
                    PointAnnotation(
                        point = points[counter]

                    ) {
                        iconImage = mapIconImage
                        iconSize = 1.0
                        iconColor = Color.Red
                    }
                    counter++
                }
            }

            if (points.size == 4) {
                val polygonPoints = points.map { point -> Point.fromLngLat(point.longitude(), point.latitude()) }
                // Create a closed polygon (first and last points should be the same)
                val closedPoints = if (polygonPoints.first() != polygonPoints.last()) {
                    polygonPoints + polygonPoints.first()
                } else {
                    polygonPoints
                }

                val polygon = Polygon.fromLngLats(listOf(closedPoints))
                areaInSquareMeters = TurfMeasurement.area(polygon)
                Log.d("PolygonArea", "Area: $areaInSquareMeters square meters")

                PolygonAnnotation(
                    points = listOf(closedPoints),
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
            } else {
                Log.e("PolygonValidation", "Polygon must have exactly 4 points. Current count: ${points.size}")
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