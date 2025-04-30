package no.uio.ifi.in2000.team_43.ui.satellite

import android.annotation.SuppressLint
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mapbox.geojson.Feature
import com.mapbox.geojson.Point
import com.mapbox.geojson.Polygon
import com.mapbox.maps.MapboxMap
import com.mapbox.maps.ScreenCoordinate
import com.mapbox.maps.Style
import com.mapbox.maps.extension.compose.MapEffect
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
import no.uio.ifi.in2000.team_43.model.areal.Areal
import no.uio.ifi.in2000.team_43.model.ctx.Ctx
import no.uio.ifi.in2000.team_43.model.map.RoofPolygon
import no.uio.ifi.in2000.team_43.utils.helperfuctions.map.getBitmapFromVectorDrawable
import kotlin.math.floor

@SuppressLint("DefaultLocale")
@Composable
fun SatelliteScreen(
    modifier: Modifier = Modifier,
    ctx: Ctx,
    onNextBtnClicked: (areal: Areal) -> (Unit)
) {

    var areaInSquareMeters by remember { mutableDoubleStateOf(0.0) }

    // Endre til lengde og bredde istedenfor areal
    var length by remember { mutableDoubleStateOf(0.0) }
    var width by remember { mutableDoubleStateOf(0.0) }

    var points by remember { mutableStateOf(listOf<Point>()) }
    val pointAnnotations = remember { mutableStateListOf<PointAnnotationOptions>() }
    val context = LocalContext.current
    val savedPolygon = remember { mutableStateListOf<RoofPolygon>() }
    var registeredPolygon by remember { mutableStateOf(false) }

    var trashAreaOffset by remember { mutableStateOf(ScreenCoordinate(0.0, 0.0)) }
    var trashAreaSize by remember { mutableStateOf(ScreenCoordinate(0.0, 0.0)) }
    val mapboxMapState = remember { mutableStateOf<MapboxMap?>(null) }


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
                if (!registeredPolygon) {
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
                }

                
                true
            }
        ) {
            MapEffect(Unit) { mapView ->
                mapboxMapState.value = mapView.mapboxMap
            }
            if (mapIconImage != null && points.isNotEmpty()) {
                points.forEachIndexed { index, point ->
                    PointAnnotation(point = point) {
                        iconImage = mapIconImage
                        iconSize = 1.0
                        iconColor = Color.Red  // Red color as a string

                    }
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

                // Vil heller ha lengde og bredde:
                val length1 = TurfMeasurement.distance(points[0], points[1], "metres")
                val length2 = TurfMeasurement.distance(points[2], points[3], "metres")
                val width1 = TurfMeasurement.distance(points[1], points[2], "metres")
                val width2 = TurfMeasurement.distance(points[3], points[0], "metres")

                length = (length1 + length2)/2
                width = (width1 + width2)/2
                Log.i("Lengde og bredde", "$length, $width")

                savedPolygon.add(
                    RoofPolygon(points = closedPoints, area = areaInSquareMeters)
                )

                registeredPolygon = true
                // totalAreaInSquareMeters += areaInSquareMeters

                points = emptyList()
                // areaInSquareMeters = 0.0 -- Kommenterer ut dette for må fikse så man ikke kan legge til flere takflater
            } else {
                Log.e("PolygonValidation", "Polygon must have exactly 4 points. Current count: ${points.size}")
            }


            savedPolygon.forEach { roofPolygon ->
                MapEffect(Unit) { }
                PolygonAnnotation(
                    points = listOf(roofPolygon.points),
                ) {
                    fillColor = Color.LightGray
                    fillOpacity = 0.5
                    fillOutlineColor = Color.Yellow
                    interactionsState.onClicked {
                        true
                    }.onLongClicked {
                        true
                    }.onDragged { dragEvent ->
                        // Access the polygon's current geometry
                        val currentGeometry = dragEvent.geometry

                        // If you need a specific point (like the center), you can calculate it
                        val polygonFeature = Feature.fromGeometry(currentGeometry)
                        val centerFeature = TurfMeasurement.center(polygonFeature)
                        val centerPoint = centerFeature.geometry() as Point

                        mapboxMapState.value?.let { mapboxMap ->
                            val screenPos = mapboxMap.pixelForCoordinate(centerPoint)
                            Log.d("position", "${screenPos.x} is the x, ${screenPos.y} is the y")
                            // Check if inside trash area
                            val insideTrash =
                                screenPos.x in trashAreaOffset.x..(trashAreaOffset.x + trashAreaSize.x) &&
                                        screenPos.y in trashAreaOffset.y..(trashAreaOffset.y + trashAreaSize.y)
                            Log.d("TrashCheck", "Inside trash: $insideTrash")

                            if (insideTrash) {
                                registeredPolygon = false
                                savedPolygon.remove(roofPolygon)
                                areaInSquareMeters = 0.0
                            }
                        }
                    }.also {
                        it.isDraggable = true
                    }
                }

            // Convert Polygon to Feature first
                val polygonFeature = Feature.fromGeometry(
                    Polygon.fromLngLats(listOf(roofPolygon.points))
                )

                // Now use TurfMeasurement.center with the Feature
                val centerFeature = TurfMeasurement.center(polygonFeature)

                // Extract the Point from the returned Feature
                val centerPoint = (centerFeature.geometry() as Point)

                PointAnnotation(point = centerPoint) {
                    iconImage = mapIconImage // or null if just text
                    iconSize = 0.5
                    textField = "${String.format("%.1f", roofPolygon.area)} m²"
                    textColor = Color.White
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
                    text = "Klikk for å legge til punkt på takflaten. Drag & Drop til søppelkasta for å slette.",
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
            if (savedPolygon.isNotEmpty()) {
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
                        onClick = {onNextBtnClicked(Areal(areaInSquareMeters, floor(length), floor(width)))},
                        enabled = true,
                    ) {
                        Text("Neste")
                    }
                }
            }
            // Trash area (bottom left)
            Box(
                modifier = Modifier
                    .width(100.dp)
                    .height(100.dp)
                    .background(Color.Red.copy(alpha = 0.6f), RoundedCornerShape(16.dp))
                    .onGloballyPositioned {
                        val position = it.localToWindow(Offset.Zero)
                        trashAreaOffset = ScreenCoordinate(position.x.toDouble(), position.y.toDouble())
                        trashAreaSize = ScreenCoordinate(it.size.width.toDouble(), it.size.height.toDouble())
                        Log.d("TrashArea", "Trash area: x: ${trashAreaOffset.x}, y: ${trashAreaOffset.y}, width: ${trashAreaSize.x}, height: ${trashAreaSize.y}")
                    },
                contentAlignment = Alignment.Center
            ) {
                Text("🗑", fontSize = 32.sp)
            }
            //Undo last point button
            Button(
                onClick = {
                    if (points.isNotEmpty()) {
                        points = points.dropLast(1)
                    }
                },
                enabled = points.isNotEmpty()
            ) {
                Text("Angre punkt")
            }
        }
    }
}