package com.manojmourya.weathernow.presentation.navigation

import android.net.Uri

sealed class Screen(val route: String) {
    data object Home : Screen("home")
    data object Search : Screen("search")
    data object Saved : Screen("saved")

    data object Detail : Screen("detail/{latitude}/{longitude}/{name}/{country}/{admin1}") {
        const val ARG_LATITUDE = "latitude"
        const val ARG_LONGITUDE = "longitude"
        const val ARG_NAME = "name"
        const val ARG_COUNTRY = "country"
        const val ARG_ADMIN1 = "admin1"

        fun createRoute(
            latitude: Double,
            longitude: Double,
            name: String,
            country: String?,
            admin1: String?,
        ): String {
            val encodedName = Uri.encode(name)
            val encodedCountry = Uri.encode(country.orEmpty())
            val encodedAdmin1 = Uri.encode(admin1.orEmpty())
            return "detail/$latitude/$longitude/$encodedName/$encodedCountry/$encodedAdmin1"
        }
    }
}
