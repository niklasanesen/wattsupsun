package no.uio.ifi.in2000.team_43.ui.input

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import no.uio.ifi.in2000.team_43.R
import no.uio.ifi.in2000.team_43.model.areal.Areal
import no.uio.ifi.in2000.team_43.model.ctx.Ctx
import no.uio.ifi.in2000.team_43.model.input.Direction
import no.uio.ifi.in2000.team_43.model.input.Input
import no.uio.ifi.in2000.team_43.model.met.Coordinates
import no.uio.ifi.in2000.team_43.model.panel.SunPanel
import no.uio.ifi.in2000.team_43.ui.PVGIS.PVGISViewModel
import no.uio.ifi.in2000.team_43.ui.info.infoScreenComponents.DirectionCard
import no.uio.ifi.in2000.team_43.ui.met.MetViewModel
import no.uio.ifi.in2000.team_43.ui.pages.HomeScreen
import no.uio.ifi.in2000.team_43.ui.pages.TopAppBar


@Composable
fun InputScreen(
    ctx: Ctx,
    metViewModel: MetViewModel,
    pvgisViewModel: PVGISViewModel,
    onNextBtnClicked: (input: Input) -> Unit,
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentScreen = HomeScreen.valueOf(
        backStackEntry?.destination?.route ?: HomeScreen.Input.name
    )

    LaunchedEffect(ctx.coords) {
        ctx.coords?.let {
            metViewModel.fetchWeatherData(Coordinates(it.latitude(), it.longitude()))
        }
    }

    LaunchedEffect(ctx.coords) {
        ctx.coords?.let {
            pvgisViewModel.fetchPVGISData(Coordinates(it.latitude(), it.longitude()))
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                currentScreen = currentScreen,
                canNavigateBack = navController.previousBackStackEntry != null,
                navigateUp = { navController.navigateUp() }
            )
        }
    ) { innerPadding ->
        InputForm(
            ctx = ctx,
            onNextButtonClicked = onNextBtnClicked,
            modifier = modifier
        )
    }
}

@OptIn(ExperimentalMaterialApi::class, ExperimentalMaterial3Api::class)
@Composable
fun InputForm(
    ctx: Ctx,
    onNextButtonClicked: (input: Input) -> Unit,
    modifier: Modifier = Modifier,
) {
    val keyboardController = LocalSoftwareKeyboardController.current

    var length by remember { mutableStateOf("") }
    var width by remember { mutableStateOf("") }
    var angle by remember { mutableStateOf("") }
    var direction: Direction by remember { mutableStateOf(Direction.NORTH)}
    var selectedPanel by remember { mutableStateOf<SunPanel?>(null) }

    var angleError by remember { mutableStateOf("") }
    var error by remember { mutableStateOf("") }

    var expanded: Boolean by remember { mutableStateOf(false) }
    var directionChange: Boolean by remember { mutableStateOf(false) }


    fun updateDimensionsFromContext(areal: Areal) {
        if (length.isEmpty() && width.isEmpty()) {
            length = areal.length.toInt().toString()
            width = areal.width.toInt().toString()
        }
    }

    fun onAngleChanged(newAngle: String) {
        angle = newAngle
        angleError = validateAngle(newAngle)
    }

    fun onDirectionSelected(newDirection: Direction) {
        direction = newDirection
        expanded = false
    }

    fun onNextClicked() {
        if (length.isNotEmpty() && width.isNotEmpty() && angle.isNotEmpty() && angleError.isEmpty() && selectedPanel != null) {
            onNextButtonClicked(
                Input(
                    length.toInt(),
                    width.toInt(),
                    angle.toInt(),
                    getDegrees(direction),
                    selectedPanel!!
                )
            )
        } else {
            error = "Pass på at du har fylt ut alle feltene og valgt en type solcellepanel før du går videre."
        }
    }

    ctx.areal?.let { updateDimensionsFromContext(it) }

    LazyColumn(
        modifier = modifier
            .fillMaxWidth()
            // .padding(top = 40.dp)
            .padding(horizontal = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            Spacer(modifier = Modifier.height(40.dp))
            Text(
                text = "Takflate-verdier",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
            )

            Spacer(modifier = Modifier.height(8.dp))

            InputField(
                value = length,
                onValueChange = { length = it },
                label = "Lengde i meter",
                keyboardController = keyboardController
            )

            InputField(
                value = width,
                onValueChange = { width = it },
                label = "Bredde i meter",
                keyboardController = keyboardController
            )

            InputField(
                value = angle,
                onValueChange = { onAngleChanged(it) },
                label = "Takvinkel i °",
                keyboardController = keyboardController
            )

            if (angleError.isNotEmpty()) {
                Text(
                    text = angleError,
                    color = Color.Red,
                    modifier = Modifier.padding(12.dp)
                )
            }

            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = it },
            ) {
                OutlinedTextField(
                    value = getDirection(direction),
                    onValueChange = { expanded = !expanded },
                    label = { Text("Retning") },
                    readOnly = true,
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(
                            expanded = expanded
                        )
                    },
                    modifier = Modifier.menuAnchor()
                )

                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    Direction.entries.forEach { entry ->
                        DropdownMenuItem(
                            text = { Text(getDirection(entry)) },
                            onClick = {
                                direction = entry
                                expanded = false
                                directionChange = false
                            },
                            contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (!expanded) {
                when(direction){
                    Direction.NORTH -> {
                        DirectionCard("Obs! Nordvendte paneler er ikke veldig lønnsomt i Norge.", color = Color(0xFFFFCDD2), textcolor = Color(0xFFB71C1C))
                    }
                    Direction.EAST -> {
                        DirectionCard("Østvendte paneler gir mest strømproduksjon tidlig på dagen.")
                    }
                    Direction.SOUTH -> {
                        DirectionCard("Sørvendte paneler gir jevn og høy produksjon midt på dagen. Dette er den mest effektive retningen for solcellepaneler!")
                    }
                    Direction.WEST -> {
                        DirectionCard("Vestvendte paneler produserer mest strøm på ettermiddagen. Det er da strømprisen normalt er høyest!")
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            Text(
                text = "Velg type solcellepanel",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(12.dp)
            )

            PanelSelector(
                selectedPanel = selectedPanel,
                onPanelSelected = { selectedPanel = it }
            )

            Spacer(modifier = Modifier.height(20.dp))

            if (error.isNotEmpty()) {
                Text(
                    text = error,
                    color = Color.Red,
                    fontSize = 14.sp,
                    lineHeight = 22.sp,
                    modifier = Modifier
                        // .padding(20.dp)
                        .width(300.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))
            }

            Button(
                onClick = {
                    onNextClicked()
                },
            ) {
                Text("Neste")
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = "Navigate to Info"
                )
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}


@Composable
fun InputField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    keyboardController: androidx.compose.ui.platform.SoftwareKeyboardController?,
) {
    OutlinedTextField(
        value = value,
        onValueChange = {
            if (it.isEmpty() || it.toIntOrNull() != null) {
                onValueChange(it)
            }
        },
        label = { Text(label) },
        singleLine = true,
        keyboardOptions = KeyboardOptions(
            imeAction = ImeAction.Done,
            keyboardType = KeyboardType.Number
        ),
        keyboardActions = KeyboardActions(onDone = { keyboardController?.hide() })
    )
}

fun getDirection(direction: Direction): String =
    when (direction) {
        Direction.NORTH -> "Nord"
        Direction.EAST -> "Øst"
        Direction.SOUTH -> "Sør"
        Direction.WEST -> "Vest"
    }

fun getDegrees(direction: Direction): Int =
    when (direction) {
        Direction.NORTH -> 0
        Direction.EAST -> 90
        Direction.SOUTH -> 180
        Direction.WEST -> 270
    }

fun validateAngle(angle: String): String =
    when {
        angle.isEmpty() || angle.toIntOrNull() == null || angle.toInt() !in 0..90 -> "Vinkel må være mellom 0° og 90°"
        else -> ""
    }



@Composable
fun PanelSelector(
    selectedPanel: SunPanel?,
    onPanelSelected: (SunPanel?) -> Unit
) {
    val paneler = listOf(
        SunPanel("Sunwind 405W", pris = 3000, effekt = 405, bildeResId = R.drawable.sunwind405w_panel, len = 1.72, width = 1.12),
        SunPanel("Economy", pris = 3730, effekt = 455, bildeResId = R.drawable.economy_panel, len = 1.85, width = 1.0),
        SunPanel("Performance", pris = 2906, effekt = 435, bildeResId = R.drawable.performance_panel, len = 1.90, width = 1.35),
        SunPanel("Premium", pris = 2547, effekt = 405, bildeResId = R.drawable.premium_panel, len = 1.78, width = 1.15),
        SunPanel("Sunwind 380W", pris = 3190, effekt = 380, bildeResId = R.drawable.sunwind380w_panel, len = 1.75, width = 1.0),
    )

    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        // Using itemsIndexed instead of items
        itemsIndexed(paneler) { index, panel ->
            val isSelected = selectedPanel == panel

            // Card for each panel
            Card(
                onClick = { onPanelSelected(if (isSelected) null else panel) },
                colors = CardDefaults.cardColors(
                    containerColor = if (isSelected)
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                    else
                        MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(4.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .width(160.dp)
            ) {
                Column(
                    modifier = Modifier.padding(12.dp).fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Image of the panel
                    Image(
                        painter = painterResource(id = panel.bildeResId),
                        contentDescription = panel.navn,
                        modifier = Modifier
                            .size(100.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .padding(top = 8.dp),
                        contentScale = ContentScale.Crop
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Panel name
                    Text(
                        text = panel.navn,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )

                    // Panel specifications (effect and price)
                    Text("• Effekt: ${panel.effekt} W", style = MaterialTheme.typography.bodySmall)
                    Text("• Pris: ${panel.pris} kr", style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }
}





