package com.anonymousbeta.lakey.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.anonymousbeta.lakey.ui.screens.BuilderScreen
import com.anonymousbeta.lakey.ui.screens.PanelScreen
import com.anonymousbeta.lakey.ui.screens.SettingsScreen
import com.anonymousbeta.lakey.ui.theme.LakeyTheme

sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    object Builder : Screen("builder", "Builder", Icons.Default.Build)
    object Panel : Screen("panel", "Panel", Icons.Default.List)
    object Settings : Screen("settings", "Settings", Icons.Default.Settings)
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LakeyTheme {
                val navController = rememberNavController()
                val items = listOf(Screen.Builder, Screen.Panel, Screen.Settings)
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                Scaffold(
                    bottomBar = {
                        NavigationBar(
                            containerColor = com.anonymousbeta.lakey.ui.theme.BlackCard
                        ) {
                            items.forEach { screen ->
                                NavigationBarItem(
                                    icon = { Icon(screen.icon, contentDescription = screen.title) },
                                    label = { Text(screen.title) },
                                    selected = currentRoute == screen.route,
                                    onClick = {
                                        navController.navigate(screen.route) {
                                            popUpTo(navController.graph.startDestinationId)
                                            launchSingleTop = true
                                        }
                                    },
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = com.anonymousbeta.lakey.ui.theme.Gold,
                                        selectedTextColor = com.anonymousbeta.lakey.ui.theme.Gold,
                                        unselectedIconColor = com.anonymousbeta.lakey.ui.theme.TextSecondary,
                                        unselectedTextColor = com.anonymousbeta.lakey.ui.theme.TextSecondary,
                                        indicatorColor = com.anonymousbeta.lakey.ui.theme.BlackSoft
                                    )
                                )
                            }
                        }
                    }
                ) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = Screen.Builder.route,
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        composable(Screen.Builder.route) { BuilderScreen() }
                        composable(Screen.Panel.route) { PanelScreen() }
                        composable(Screen.Settings.route) { SettingsScreen() }
                    }
                }
            }
        }
    }
}
