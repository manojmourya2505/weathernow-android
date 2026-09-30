package com.manojmourya.weathernow.presentation.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.manojmourya.weathernow.R
import com.manojmourya.weathernow.domain.model.City
import com.manojmourya.weathernow.presentation.detail.CityDetailScreen
import com.manojmourya.weathernow.presentation.home.HomeScreen
import com.manojmourya.weathernow.presentation.saved.SavedCitiesScreen
import com.manojmourya.weathernow.presentation.search.SearchScreen

private data class TopLevelDestination(
    val screen: Screen,
    val labelRes: Int,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
)

private val topLevelDestinations = listOf(
    TopLevelDestination(Screen.Home, R.string.nav_home, Icons.Filled.Home),
    TopLevelDestination(Screen.Search, R.string.nav_search, Icons.Filled.Search),
    TopLevelDestination(Screen.Saved, R.string.nav_saved, Icons.Filled.Star),
)

@Composable
fun WeatherNowNavHost() {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

    val showBottomBar = topLevelDestinations.any { it.screen.route == currentRoute }

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    topLevelDestinations.forEach { destination ->
                        val selected = backStackEntry?.destination?.hierarchy
                            ?.any { it.route == destination.screen.route } == true
                        NavigationBarItem(
                            selected = selected,
                            onClick = {
                                navController.navigate(destination.screen.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = { Icon(destination.icon, contentDescription = null) },
                            label = { Text(stringResource(destination.labelRes)) },
                        )
                    }
                }
            }
        },
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(padding),
        ) {
            composable(Screen.Home.route) {
                HomeScreen(onNavigateToSearch = {
                    navController.navigate(Screen.Search.route) {
                        popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                })
            }
            composable(Screen.Search.route) {
                SearchScreen(onCityClick = { city -> navController.navigateToDetail(city) })
            }
            composable(Screen.Saved.route) {
                SavedCitiesScreen(onCityClick = { city -> navController.navigateToDetail(city) })
            }
            composable(
                route = Screen.Detail.route,
                arguments = listOf(
                    navArgument(Screen.Detail.ARG_LATITUDE) { type = NavType.StringType },
                    navArgument(Screen.Detail.ARG_LONGITUDE) { type = NavType.StringType },
                    navArgument(Screen.Detail.ARG_NAME) { type = NavType.StringType },
                    navArgument(Screen.Detail.ARG_COUNTRY) { type = NavType.StringType },
                    navArgument(Screen.Detail.ARG_ADMIN1) { type = NavType.StringType },
                ),
            ) {
                CityDetailScreen(onBack = { navController.popBackStack() })
            }
        }
    }
}

private fun androidx.navigation.NavController.navigateToDetail(city: City) {
    navigate(
        Screen.Detail.createRoute(
            latitude = city.latitude,
            longitude = city.longitude,
            name = city.name,
            country = city.country,
            admin1 = city.admin1,
        ),
    )
}
