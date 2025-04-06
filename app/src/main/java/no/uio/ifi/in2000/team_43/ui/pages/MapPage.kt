package no.uio.ifi.in2000.team_43.ui.pages

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.mapbox.maps.extension.style.expressions.dsl.generated.mod
import no.uio.ifi.in2000.team_43.R
import no.uio.ifi.in2000.team_43.ui.appinfo.AppInfoScreen
import no.uio.ifi.in2000.team_43.ui.home.ContentScreen
import no.uio.ifi.in2000.team_43.ui.home.HomeScreen
import no.uio.ifi.in2000.team_43.ui.home.HomeViewModel
import no.uio.ifi.in2000.team_43.ui.home.MapScreen
import no.uio.ifi.in2000.team_43.ui.info.InfoScreen
import no.uio.ifi.in2000.team_43.ui.input.InputScreen

/**
 * enum values that represent the screens in the app
 */
enum class HomeScreen(@StringRes val title: Int) {
    Start(title = R.string.app_name),
    Input(title = R.string.input),
    Info(title = R.string.info),
    InfoApp(title = R.string.app_info)
}

@Composable
fun MapPage(
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
        modifier = modifier,
        topBar = {
            TopAppBar(
                currentScreen = currentScreen,
                canNavigateBack = navController.previousBackStackEntry != null,
                navigateUp = { navController.navigateUp() },
                navController = navController
            )
        },
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
                    onNextBtnClicked = {
                        viewModel.setCoords(it)
                        navController.navigate(HomeScreen.Input.name)
                    },
                    // modifier = Modifier.padding(innerPadding)
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
    /*
    currentScreen: HomeScreen = HomeScreen.valueOf(HomeScreen.Start.name),
    canNavigateBack: Boolean = false,
    navigateUp: () -> Unit = {},
    navController: NavController = rememberNavController()

     */
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
            if (currentScreen != HomeScreen.InfoApp) {
                IconButton(onClick = { navController.navigate(HomeScreen.InfoApp.name) }) {
                    Icon(
                        imageVector = Icons.Filled.Info,
                        contentDescription = "Info"
                    )
                }
            }
        },
    )
}