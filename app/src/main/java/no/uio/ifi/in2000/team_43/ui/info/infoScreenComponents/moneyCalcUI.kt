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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.animation.core.tween


@Composable
fun TotalMoneyAnimated(supportAmount: Double, isLoading: Boolean) {
    var flipped by remember { mutableStateOf(false) }
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
            // Disable flip when loading
            .then(if (isLoading) Modifier else Modifier.clickable { flipped = !flipped }),
        contentAlignment = Alignment.Center
    ) {
        if (isLoading) {
            CalculatingCard("Teller pengene - snart klart!")
        } else {
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
                            text = "${supportAmount.toInt()} kr",
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
                    colors = CardDefaults.cardColors(
                        containerColor = if (isZeroSupport) Color(0xFFFFE0E0) else Color(0xFFE0F2F1)
                    )
                ) {
                    Box(modifier = Modifier.padding(16.dp), contentAlignment = Alignment.Center) {
                        Text(
                            text = "Beregningene er basert på METs klimadata og strømpriser for 2024. Faktisk inntjening avhenger av fremtidig vær og strømpriser.",
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
    }
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

@Preview(showBackground = true)
@Composable
fun TotalMoneyAnimatedPreview() {
    Column(modifier = Modifier.padding(16.dp)) {
        TotalMoneyAnimated(supportAmount = 18500.0, false)
        Spacer(modifier = Modifier.height(16.dp))
        TotalMoneyAnimated(supportAmount = 0.0, false)
        TotalMoneyAnimated(supportAmount = 0.0, true)
        
    }
}
