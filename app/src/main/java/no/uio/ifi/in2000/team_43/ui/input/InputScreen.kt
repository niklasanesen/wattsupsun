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
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import no.uio.ifi.in2000.team_43.model.ctx.Ctx
import no.uio.ifi.in2000.team_43.model.input.Input
import no.uio.ifi.in2000.team_43.ui.home.HomeScreen
import no.uio.ifi.in2000.team_43.ui.home.TopAppBar


// Vil ha en sånn ?hjelp knapp som gir bruker en guide med hva som menes med de forskjellige input-feltene
// Vil også legge til en knapp så man kan legge til flere takflater

@Composable
fun InputScreen(
    ctx: Ctx,
    onNextBtnClicked: (input: Input) -> Unit,
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController()
) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentScreen = HomeScreen.valueOf(
        backStackEntry?.destination?.route ?: HomeScreen.Input.name
    )

    Scaffold(
        modifier = Modifier
            .fillMaxSize(),
        topBar =  { TopAppBar(
            currentScreen = currentScreen,
            canNavigateBack = navController.previousBackStackEntry != null,
            navigateUp = { navController.navigateUp() },
            navController = navController
        ) },
        // bottomBar = { BottomAppBar() },
    ) { innerPadding ->

        Column (
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = modifier
                .fillMaxWidth()
        ) {
            // Text("coords: ${ctx.coords?.coordinates()}")

            val keyboardController = LocalSoftwareKeyboardController.current
            var length by remember { mutableStateOf("") }
            var width by remember { mutableStateOf("") }
            var angle by remember { mutableStateOf("") }
            var direction by remember { mutableStateOf("") }

            var angleError by remember { mutableStateOf("") }
            var directionError by remember { mutableStateOf("") }

            var error by remember { mutableStateOf("") }


            Column (
                modifier = modifier
                    // .wrapContentSize(Alignment.Center)
                    .fillMaxWidth()
                    .paddingFromBaseline(top = 52.dp),
                //.padding(40.dp),
                // verticalArrangement = Arrangement.Center,
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
                    label = { Text("Vinkel i °") },
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

                OutlinedTextField(
                    value = direction,
                    onValueChange = {
                        if (it == "" || it.toIntOrNull() is Int) {
                            direction = it
                            directionError = ""
                            if (it == "" || it.toInt() !in 0..359) {
                                directionError = "Retning må være mellom 0° og 359°"
                            }
                        }
                    },
                    label = { Text("Retning i ° (kompass)") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done, keyboardType = KeyboardType.Number),
                    keyboardActions = KeyboardActions(onDone = { keyboardController?.hide() })
                )

                if (directionError != "") {
                    Text(
                        text = directionError,
                        color = Color.Red,
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }

            Button(
                onClick = {
                    if (length != "" && width != "" && angle != "" && direction != "") {
                        onNextBtnClicked(Input(length.toInt(), width.toInt(), angle.toInt(), direction.toInt()))
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



// Vil egt ha dette i egen funksjon så man kan gjenbruke det til hvis man skal ha flere takflater, men for MVP-en (pga sjekk for om inputfeltene er fylt ut) så er det bare flytta opp i selve InputScreen-funksjonen

/*
@Composable
fun InputFields(modifier: Modifier = Modifier) {
    val keyboardController = LocalSoftwareKeyboardController.current
    var length by remember { mutableStateOf("") }
    var width by remember { mutableStateOf("") }
    var angle by remember { mutableStateOf("") }
    var direction by remember { mutableStateOf("") }

    var angleError by remember { mutableStateOf("") }
    var directionError by remember { mutableStateOf("") }


    Column (
        modifier = modifier
            // .wrapContentSize(Alignment.Center)
            .fillMaxWidth()
            .paddingFromBaseline(top = 52.dp),
        //.padding(40.dp),
        // verticalArrangement = Arrangement.Center,
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
            label = { Text("Vinkel i °") },
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

        OutlinedTextField(
            value = direction,
            onValueChange = {
                if (it == "" || it.toIntOrNull() is Int) {
                    direction = it
                    directionError = ""
                    if (it == "" || it.toInt() !in 0..359) {
                        directionError = "Retning må være mellom 0° og 359°"
                    }
                }
            },
            label = { Text("Retning i ° (kompass)") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done, keyboardType = KeyboardType.Number),
            keyboardActions = KeyboardActions(onDone = { keyboardController?.hide() })
        )

        if (directionError != "") {
            Text(
                text = directionError,
                color = Color.Red,
                modifier = Modifier.padding(12.dp)
            )
        }
    }
}

 */

/*
@Preview (
    showBackground = true,
    showSystemUi = true,
    name = "Input preview"
)
@Composable
fun InputScreenPreview() {
    InputScreen()
}

 */
