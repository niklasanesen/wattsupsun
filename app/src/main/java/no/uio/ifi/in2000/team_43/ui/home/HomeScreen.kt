package no.uio.ifi.in2000.team_43.ui.home

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.outlined.Done
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import no.uio.ifi.in2000.team_43.R
import no.uio.ifi.in2000.team_43.ui.appinfo.AppInfoScreen
import no.uio.ifi.in2000.team_43.ui.info.InfoScreen
import no.uio.ifi.in2000.team_43.ui.input.InputScreen
import no.uio.ifi.in2000.team_43.ui.satellite.SatelliteScreen

/**
 * enum values that represent the screens in the app
 */
enum class HomeScreen(@StringRes val title: Int) {
    Start(title = R.string.app_name),
    Input(title = R.string.input),
    Info(title = R.string.info),
    Satellite(title = R.string.satellite),
    InfoApp(title = R.string.app_info)
}


@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = viewModel(),
    navController: NavHostController = rememberNavController()
) {
    // Get current back stack entry
    val backStackEntry by navController.currentBackStackEntryAsState()

    // Get the name of the current screen
    val currentScreen = HomeScreen.valueOf(
        backStackEntry?.destination?.route ?: HomeScreen.Start.name
    )

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                currentScreen = currentScreen,
                canNavigateBack = navController.previousBackStackEntry != null,
                navigateUp = { navController.navigateUp() },
                navController = navController
            )
        },
        bottomBar = { BottomAppBar(navController) },
    ) { innerPadding ->
        val ctx by viewModel.ctx.collectAsState()

        NavHost(
            navController = navController,
            startDestination = HomeScreen.Start.name,
            modifier = Modifier
                .fillMaxSize()
        ) {
            composable(route = HomeScreen.Start.name) {
                MapScreen(
                    onInfoBtnClicked = {
                        viewModel.setCoords(it)
                        navController.navigate(HomeScreen.Input.name)
                    },
                    onSatelliteBtnClicked = {
                        viewModel.setCoords(it)
                        navController.navigate(HomeScreen.Satellite.name)
                    },
                    modifier = Modifier.padding(innerPadding)
                )
            }
            composable(route = HomeScreen.Input.name) {
                InputScreen(
                    ctx = ctx,
                    onNextBtnClicked = {
                        viewModel.setInput(it)
                        navController.navigate(HomeScreen.Info.name)
                    },
                    modifier = Modifier.padding(innerPadding)
                )
            }
            composable(route = HomeScreen.Satellite.name) {
                SatelliteScreen(
                    ctx = ctx,
                    modifier = Modifier.padding(innerPadding)
                )
            }
            composable(route = HomeScreen.Info.name) {
                InfoScreen(
                    ctx = ctx,
                    modifier = Modifier.padding(innerPadding)
                )
            }
            composable(route = HomeScreen.InfoApp.name) {
                AppInfoScreen(
                    modifier = Modifier.padding(innerPadding)
                )
            }
        }

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopAppBar(
    currentScreen: HomeScreen,
    canNavigateBack: Boolean,
    navigateUp: () -> Unit,
    navController: NavController
) {
    CenterAlignedTopAppBar(
        title = { Text(stringResource(currentScreen.title)) },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            titleContentColor = MaterialTheme.colorScheme.primary,
        ),
        navigationIcon = {
            if (canNavigateBack) {
                IconButton(onClick = navigateUp) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.back_button)
                    )
                }
            }
        },
        actions = {
            IconButton(onClick = { navController.navigate(HomeScreen.InfoApp.name) }) {
                Icon(
                    imageVector = Icons.Filled.Info,
                    contentDescription = "Info"
                )
            }
        },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomAppBar(
    navController: NavController
) {
    BottomAppBar(
        actions = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(onClick = { navController.navigate(HomeScreen.Start.name) }) {
                    Icon(Icons.Outlined.Home, contentDescription = "Goto home")
                }
                IconButton(onClick = { navController.navigate(HomeScreen.Input.name) }) {
                    Icon(Icons.Outlined.Edit, contentDescription = "Goto input")
                }
                IconButton(onClick = { navController.navigate(HomeScreen.Satellite.name) }) {
                    Icon(Icons.Outlined.LocationOn , contentDescription = "Goto satellite")
                }
                IconButton(onClick = { navController.navigate(HomeScreen.Info.name) }) {
                    Icon(Icons.Outlined.Done, contentDescription = "Goto information")
                }
            }
        },
    )
}