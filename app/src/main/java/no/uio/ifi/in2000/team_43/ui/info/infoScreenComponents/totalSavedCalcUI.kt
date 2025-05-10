package no.uio.ifi.in2000.team_43.ui.info.infoScreenComponents

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.animation.core.tween
import kotlinx.coroutines.delay


// Logic for the UI thing:
@Composable
fun TotalSavedLogic(totalMoneySaved: Float, isLoading: Boolean) {
    if (isLoading) {
        CalculatingCardBox("Teller mynter - snart klart!")
    } else {
        // Soory for this bad code
        val show = totalSavedAnimated(totalMoneySaved.toDouble() )
        if((show < 0) && (totalMoneySaved == 0.0.toFloat())) {
            CalculatingCardBox("Teller mynter - snart klart!")
        }
    }
}


// The actual animation UI thing:
@Composable
fun totalSavedAnimated(supportAmount: Double): Int {
    var flipped by remember { mutableStateOf(false) }
    var showContent by remember { mutableStateOf(false) }

    // This is really really not optimal but it is what it is
    LaunchedEffect(supportAmount) {
        showContent = false
        delay(2000)
        showContent = true
    }

    // pls dont touch this, weird things happen when this is not there
    if (!showContent && supportAmount == 0.0) {
        return -1
        //CalculatingCardBox("Teller mynter - snart klart!")
    }


    // This is great ngl
    val rotation by animateFloatAsState(
        targetValue = if (flipped) 180f else 0f,
        animationSpec = tween(durationMillis = 700),
        label = "flip"
    )

    val isZeroSupport = supportAmount <= 0
    val backgroundColor = if (isZeroSupport) Color(0xFFFFE0E0) else Color(0xFFE0F2F1)
    val textColor = if (isZeroSupport) Color(0xFFB00020) else Color(0xFF006064)
    val density = LocalDensity.current.density

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .graphicsLayer {
                rotationY = rotation
                cameraDistance = 8 * density
            }
            .clickable { flipped = !flipped },
        contentAlignment = Alignment.Center
    ) {
        if (rotation <= 90f) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(8.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = backgroundColor)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Text(
                            text = "Estimert årlig inntjening",
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp,
                                color = textColor
                            )
                        )
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = "Mer info",
                            tint = textColor
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text =
                            "${supportAmount.toInt()} kr",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = textColor
                        )
                    )
                }
            }
        } else {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .graphicsLayer { rotationY = 180f },
                elevation = CardDefaults.cardElevation(8.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = if (isZeroSupport) Color(0xFFFFE0E0) else Color(0xFFE0F2F1))
            ) {
                Box(modifier = Modifier.padding(16.dp), contentAlignment = Alignment.Center) {
                    Text(
                        text = "Beregningene er basert på MET/PVGIS sine klimadata og strømpriser for 2024. Faktisk inntjening avhenger av fremtidig vær og strømpriser.",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = if (isZeroSupport) Color(0xFFB00020) else Color(0xFF006064),
                            fontWeight = FontWeight.Medium
                        ),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
    return 1
}

@Composable
fun CalculatingCard(text: String) {
    // color when calc
    val calculatingBackgroundColor =  Color(0xFFE0F2F1)
    val calculatingTextColor = Color(0xFF006064)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp),
        elevation = CardDefaults.cardElevation(8.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = calculatingBackgroundColor)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            CircularProgressIndicator(
                color = calculatingTextColor,
                modifier = Modifier
                    .size(60.dp)
                    .padding(8.dp)
            )
            Text(
                text = text,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = calculatingTextColor
                ),
                textAlign = TextAlign.Start
            )
        }
    }
}

@Composable
fun CalculatingCardBox(text: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        CalculatingCard(text)
    }
}


@Preview(showBackground = true)
@Composable
fun TotalSavedAnimatedPreview() {
    Column(modifier = Modifier.padding(16.dp)) {
        totalSavedAnimated(supportAmount = 18500.0)
        Spacer(modifier = Modifier.height(16.dp))
        totalSavedAnimated(supportAmount = 0.0)
    }
}
