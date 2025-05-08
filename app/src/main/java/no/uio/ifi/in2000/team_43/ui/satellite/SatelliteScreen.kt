package no.uio.ifi.in2000.team_43.ui.satellite

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
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
import com.mapbox.maps.Style
import com.mapbox.maps.extension.compose.MapboxMap
import com.mapbox.maps.extension.compose.animation.viewport.rememberMapViewportState
import com.mapbox.maps.extension.compose.annotation.IconImage
import com.mapbox.maps.extension.compose.annotation.generated.PointAnnotation
import com.mapbox.maps.extension.compose.annotation.generated.PolygonAnnotation
import com.mapbox.maps.extension.compose.style.MapStyle
import com.mapbox.maps.plugin.gestures.OnMapClickListener
import no.uio.ifi.in2000.team_43.R
import no.uio.ifi.in2000.team_43.model.areal.Areal
import no.uio.ifi.in2000.team_43.model.ctx.Ctx
import no.uio.ifi.in2000.team_43.utils.helperfuctions.map.getBitmapFromVectorDrawable
import kotlin.math.floor

@Composable
fun SatelliteScreen(
    modifier: Modifier = Modifier,
    ctx: Ctx,
    satelliteViewModel: SatelliteViewModel = remember { SatelliteViewModel() },
    onNextBtnClicked: (areal: Areal) -> (Unit)
) {
    val areaInSquareMeters by satelliteViewModel.areaInSquareMeters.collectAsState()
    val length by satelliteViewModel.length.collectAsState()
    val width by satelliteViewModel.width.collectAsState()
    val points by satelliteViewModel.points.collectAsState()
    val savedPolygons by satelliteViewModel.savedPolygons.collectAsState()

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
            zoom(19.0)
            center(ctx.coords ?: Point.fromLngLat(12.30778, 63.99056))
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        MapboxMap(
            modifier = Modifier.fillMaxSize(),
            mapViewportState = mapViewportState,
            style = { MapStyle(style = Style.SATELLITE_STREETS) },
            onMapClickListener = OnMapClickListener { point ->
                satelliteViewModel.addPoint(point)
                true
            }
        ) {
            mapIconImage?.let { icon ->
                points.forEach { point ->
                    PointAnnotation(point = point) {
                        iconImage = icon
                        iconSize = 1.0
                        iconColor = Color.Red
                    }
                }
            }

            savedPolygons.forEach { polygon ->
                PolygonAnnotation(points = listOf(polygon.points)) {
                    fillColor = Color.LightGray
                    fillOpacity = 0.7
                    fillOutlineColor = Color.White
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(42.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier
                        .background(color = Color.White, shape = RoundedCornerShape(8.dp))
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Klikk for å legge til punkt på kartet for å tegne et rektangel av takflaten din.",
                        style = TextStyle(
                            fontSize = 16.sp,
                            lineHeight = 24.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.Black,
                            textAlign = TextAlign.Center
                        )
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                if (savedPolygons.isNotEmpty()) {
                    Box(
                        modifier = Modifier
                            .background(Color(0xFF394D3E), RoundedCornerShape(8.dp))
                            .padding(8.dp)
                    ) {
                        Text(
                            text = "$areaInSquareMeters m²",
                            style = TextStyle(
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color.White
                            )
                        )
                    }
                }
            }

            when {
                points.isNotEmpty() -> {
                    Button(onClick = { satelliteViewModel.undoLastPoint() }) {
                        Text("Angre punkt")
                    }
                }

                savedPolygons.isNotEmpty() -> {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        Button(
                            onClick = { satelliteViewModel.deletePolygon() },
                            colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.error)
                        ) {
                            Text("Slett takflate")
                        }

                        Button(
                            onClick = {
                                onNextBtnClicked(
                                    Areal(
                                        areaInSquareMeters,
                                        floor(length),
                                        floor(width)
                                    )
                                )
                            }
                        ) {
                            Text("Neste")
                        }
                    }
                }
            }
        }
    }
}