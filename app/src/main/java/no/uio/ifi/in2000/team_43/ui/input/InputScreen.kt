package no.uio.ifi.in2000.team_43.ui.input

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.paddingFromBaseline
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import no.uio.ifi.in2000.team_43.model.ctx.Ctx
import no.uio.ifi.in2000.team_43.model.input.Direction
import no.uio.ifi.in2000.team_43.model.input.Input
import no.uio.ifi.in2000.team_43.model.met.Coordinates
import no.uio.ifi.in2000.team_43.ui.met.MetViewModel
import no.uio.ifi.in2000.team_43.ui.pages.HomeScreen
import no.uio.ifi.in2000.team_43.ui.pages.TopAppBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InputScreen(
    ctx: Ctx,
    metViewModel: MetViewModel? = null,
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
            metViewModel?.fetchWeatherData(Coordinates(it.latitude(), it.longitude()))
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
        Column (
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = modifier
                .fillMaxWidth()
                .padding(top = innerPadding.calculateTopPadding())
        ) {
            val keyboardController = LocalSoftwareKeyboardController.current

            var length by remember { mutableStateOf("") }
            var width by remember { mutableStateOf("") }
            var angle by remember { mutableStateOf("") }
            var direction: Direction by remember { mutableStateOf(Direction.NORTH)}

            var angleError by remember { mutableStateOf("") }
            var error by remember { mutableStateOf("") }

            // For dropdownmeny retning:
            var expanded: Boolean by remember { mutableStateOf(false) }

            if (ctx.areal != null && length.isEmpty() && width.isEmpty()) {
                length = ctx.areal!!.length.toInt().toString()
                width = ctx.areal!!.width.toInt().toString()
            }

            Column (
                modifier = modifier
                    .fillMaxWidth()
                    .paddingFromBaseline(top = 52.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Takflate 1",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = length,
                    onValueChange = {
                        if (it == "" || it.toIntOrNull() is Int) {
                            length = it
                        }
                    },
                    label = { Text("Lengde") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done, keyboardType = KeyboardType.Number),
                    keyboardActions = KeyboardActions(onDone = { keyboardController?.hide() })
                )

                OutlinedTextField(
                    value = width,
                    onValueChange = {
                        if (it == "" || it.toIntOrNull() is Int) {
                            width = it
                        }
                    },
                    label = { Text("Bredde") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done, keyboardType = KeyboardType.Number),
                    keyboardActions = KeyboardActions(onDone = { keyboardController?.hide() })
                )

                OutlinedTextField(
                    value = angle,
                    onValueChange = {
                        if (it == "" || it.toIntOrNull() is Int) {
                            angle = it
                            angleError = ""
                            if (it == "" || it.toInt() !in 0..90) {
                                angleError = "Vinkel må være mellom 0° og 90°"
                            }
                        }
                    },
                    label = { Text("Takvinkel i °") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done, keyboardType = KeyboardType.Number),
                    keyboardActions = KeyboardActions(onDone = { keyboardController?.hide() })
                )

                if (angleError != "") {
                    Text(
                        text = angleError,
                        color = Color.Red,
                        modifier = Modifier.padding(12.dp)
                    )
                }

                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = {expanded = it},
                ) {
                    OutlinedTextField(
                        value = getDirection(direction),
                        onValueChange = {},
                        label = { Text("Retning") },
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        modifier = Modifier
                            .menuAnchor()
                            .align(Alignment.CenterHorizontally)
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
                                },
                                contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(12.dp))

            Button(
                onClick = {
                    if (length != "" && width != "" && angle != "") {
                        onNextBtnClicked(Input(length.toInt(), width.toInt(), angle.toInt(), getDegrees(direction)))
                    } else {
                        error = "Vennligst fyll ut alle feltene"
                    }
                },
            ) {
                Text("Neste ")
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = "Navigate to Info"
                )
            }

            if (error != "") {
                Text(
                    text = error,
                    modifier = Modifier.padding(8.dp)
                )
            }
        }
    }
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