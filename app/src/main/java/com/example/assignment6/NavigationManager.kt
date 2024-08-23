/*  Assignment 6
    NavigationManager.kt

    Nicholas Herman / hermnich@oregonstate.edu
    CS 492 / Oregon State University
*/

package com.example.assignment6

import android.Manifest.permission.ACCESS_FINE_LOCATION
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.assignment6.ui.AppViewModel
import com.example.assignment6.ui.ClueScreen
import com.example.assignment6.ui.ClueSolvedScreen
import com.example.assignment6.ui.FinishScreen
import com.example.assignment6.ui.StartScreen

enum class Screen (@StringRes var title: Int) {
    Start(title = R.string.app_name),
    Clue(title = R.string.app_name),
    ClueSolved(title = R.string.app_name),
    Finish(title = R.string.app_name)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(
    currentScreen: Screen,
    canNavigateBack: Boolean,
    navigateUp: () -> Unit,
    modifier: Modifier = Modifier
) {
    TopAppBar(
        title = { Text(stringResource(currentScreen.title)) },
        colors = TopAppBarDefaults.mediumTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        modifier = modifier,
        navigationIcon = {
            if (canNavigateBack) {
                IconButton(onClick = navigateUp) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.back_button)
                    )
                }
            }
        }
    )
}

@Composable
fun TreasureHunt(
    viewModel: AppViewModel = viewModel(),
    navController: NavHostController = rememberNavController()
) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentScreen = Screen.valueOf(
        backStackEntry?.destination?.route ?: Screen.Start.name
    )

    Scaffold(
        topBar = {
            TopBar(
                currentScreen = currentScreen,
                canNavigateBack = false,
                navigateUp = { navController.navigateUp() }
            )
        }
    ) { innerPadding ->
        val uiState by viewModel.uiState.collectAsState()

        val openDialog = remember { mutableStateOf(false) }

        val requestLocationPermissions =
            rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
                if (isGranted) {
                    viewModel.onPermissionChange(ACCESS_FINE_LOCATION, true)
                    viewModel.checkLocation(uiState.clue)
                } else {
                    openDialog.value = true
                }
            }

        when {
            openDialog.value -> {
                PermissionDisabledDialog(
                    onDismissRequest = { openDialog.value = false },
                    onConfirmation = {
                        openDialog.value = false
                    }
                )
            }
        }

        NavHost(
            navController = navController,
            startDestination = Screen.Start.name,
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(innerPadding)
        ) {
            composable(route = Screen.Start.name) {
                StartScreen(
                    onStartClicked = {
                        viewModel.startTimer()
                        navController.navigate(Screen.Clue.name)
                    }
                )
            }
            composable(route = Screen.Clue.name) {
                ClueScreen(
                    time = uiState.time,
                    clue = uiState.clue,
                    distance = uiState.distance,
                    onFoundClicked = {
                        when {
                            uiState.hasLocationAccess -> viewModel.checkLocation(uiState.clue)
                            else -> requestLocationPermissions.launch(ACCESS_FINE_LOCATION)
                        }
                    },
                    onQuitClicked = {
                        navController.navigate(Screen.Start.name)
                        viewModel.reset()
                    },
                    onTimerUpdate = {
                        viewModel.updateTimer()
                    },
                    onFound = {
                        viewModel.clearDistance()
                        viewModel.pauseTimer()
                        if (uiState.isLastClue) {
                            navController.navigate(Screen.Finish.name)
                        } else {
                            navController.navigate(Screen.ClueSolved.name)
                        }
                    },
                    onNotFound = {
                        viewModel.clearDistance()
                    }
                )
            }
            composable(route = Screen.ClueSolved.name) {
                ClueSolvedScreen(
                    time = uiState.time,
                    clue = uiState.clue,
                    onContinueClicked = {
                        viewModel.updateClue()
                        viewModel.startTimer()
                        navController.navigate(Screen.Clue.name)
                    }
                )
            }
            composable(route = Screen.Finish.name) {
                FinishScreen(
                    time = uiState.time,
                    clue = uiState.clue,
                    onHomeClicked = {
                        navController.navigate(Screen.Start.name)
                        viewModel.reset()
                    }
                )
            }
        }
    }
}

@Preview
@Composable
fun PreviewTopBar() {
    TopBar(
        currentScreen = Screen.Start,
        canNavigateBack = false,
        navigateUp = {}
    )
}

@Composable
fun PermissionDisabledDialog(
    onDismissRequest: () -> Unit,
    onConfirmation: () -> Unit
) {
    AlertDialog(
        title = {
            Text(text = "Location Disabled")
        },
        text = {
            Text(text = "Location currently disabled due to denied permission.")
        },
        onDismissRequest = {
            onDismissRequest()
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirmation()
                }
            ) {
                Text("OK")
            }
        }
    )
}

@Preview
@Composable
fun PreviewPermissionDisabledDialog() {
    PermissionDisabledDialog(
        onConfirmation = {},
        onDismissRequest = {}
    )
}