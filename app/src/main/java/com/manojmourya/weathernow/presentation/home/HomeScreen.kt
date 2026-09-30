package com.manojmourya.weathernow.presentation.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.manojmourya.weathernow.R
import com.manojmourya.weathernow.domain.model.DailyForecastItem
import com.manojmourya.weathernow.domain.model.HourlyForecastItem
import com.manojmourya.weathernow.domain.util.WeatherCodeMapper
import com.manojmourya.weathernow.presentation.components.EmptyState
import com.manojmourya.weathernow.presentation.components.ErrorState
import com.manojmourya.weathernow.presentation.components.LoadingState
import com.manojmourya.weathernow.presentation.components.weatherCodeToIcon
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToSearch: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text(uiState.city?.name ?: stringResource(R.string.app_name)) },
            )
        },
        floatingActionButton = {
            if (uiState.hasNoSelectedCity) {
                ExtendedFloatingActionButton(
                    onClick = onNavigateToSearch,
                    icon = { Icon(Icons.Filled.Search, contentDescription = null) },
                    text = { Text(stringResource(R.string.home_search_cta)) },
                )
            }
        },
    ) { padding ->
        when {
            uiState.isLoading -> LoadingState(modifier = Modifier.padding(padding))
            uiState.hasNoSelectedCity -> EmptyState(
                title = stringResource(R.string.home_no_city_title),
                message = stringResource(R.string.home_no_city_message),
                modifier = Modifier.padding(padding),
            )
            uiState.errorMessage != null -> ErrorState(
                message = uiState.errorMessage!!,
                icon = Icons.Filled.CloudOff,
                onRetry = viewModel::retry,
                modifier = Modifier.padding(padding),
            )
            uiState.forecast != null -> HomeContent(
                cityName = uiState.city?.name.orEmpty(),
                citySubtitle = uiState.city?.subtitle.orEmpty(),
                forecast = uiState.forecast!!,
                modifier = Modifier.padding(padding),
            )
        }
    }
}

@Composable
private fun HomeContent(
    cityName: String,
    citySubtitle: String,
    forecast: com.manojmourya.weathernow.domain.model.WeatherForecast,
    modifier: Modifier = Modifier,
) {
    androidx.compose.foundation.lazy.LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item {
            CurrentConditionsCard(citySubtitle = citySubtitle, current = forecast.current)
        }
        item {
            Text(
                text = stringResource(R.string.home_hourly_forecast),
                style = MaterialTheme.typography.titleMedium,
            )
        }
        item {
            HourlyForecastRow(hourly = forecast.hourly)
        }
        item {
            Text(
                text = stringResource(R.string.home_daily_forecast),
                style = MaterialTheme.typography.titleMedium,
            )
        }
        items(forecast.daily) { day ->
            DailyForecastRow(day = day)
        }
    }
}

@Composable
private fun CurrentConditionsCard(
    citySubtitle: String,
    current: com.manojmourya.weathernow.domain.model.CurrentWeather,
    modifier: Modifier = Modifier,
) {
    Card(modifier = modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)) {
        Column(modifier = Modifier.padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            if (citySubtitle.isNotBlank()) {
                Text(text = citySubtitle, style = MaterialTheme.typography.bodyMedium)
                Spacer(Modifier.height(8.dp))
            }
            Icon(
                imageVector = weatherCodeToIcon(current.weatherCode),
                contentDescription = stringResource(R.string.content_description_weather_icon),
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.secondary,
            )
            Text(
                text = "${current.temperature.roundToInt()}°",
                style = MaterialTheme.typography.displayLarge,
                fontWeight = FontWeight.Light,
            )
            Text(
                text = WeatherCodeMapper.description(current.weatherCode),
                style = MaterialTheme.typography.bodyLarge,
            )
            Text(
                text = stringResource(R.string.home_feels_like, "${current.feelsLike.roundToInt()}°"),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(Modifier.height(16.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                StatColumn(
                    icon = Icons.Filled.WaterDrop,
                    label = stringResource(R.string.home_humidity),
                    value = "${current.humidity}%",
                )
                StatColumn(
                    icon = Icons.Filled.Air,
                    label = stringResource(R.string.home_wind),
                    value = "${current.windSpeed.roundToInt()} km/h",
                )
            }
        }
    }
}

@Composable
private fun StatColumn(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
        Text(text = value, style = MaterialTheme.typography.titleMedium)
        Text(text = label, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
private fun HourlyForecastRow(hourly: List<HourlyForecastItem>, modifier: Modifier = Modifier) {
    LazyRow(modifier = modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        items(hourly) { hour ->
            Card {
                Column(
                    modifier = Modifier.width(64.dp).padding(vertical = 12.dp, horizontal = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(text = formatHourLabel(hour.time), style = MaterialTheme.typography.bodyMedium)
                    Spacer(Modifier.height(8.dp))
                    Icon(
                        imageVector = weatherCodeToIcon(hour.weatherCode),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.secondary,
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(text = "${hour.temperature.roundToInt()}°", style = MaterialTheme.typography.titleMedium)
                }
            }
        }
    }
}

@Composable
private fun DailyForecastRow(day: DailyForecastItem, modifier: Modifier = Modifier) {
    Card(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(text = formatDayLabel(day.date), modifier = Modifier.width(96.dp))
            Icon(imageVector = weatherCodeToIcon(day.weatherCode), contentDescription = null, tint = MaterialTheme.colorScheme.secondary)
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                Icon(Icons.Filled.WaterDrop, contentDescription = null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.primary)
                Text(text = "${day.precipitationProbability}%", style = MaterialTheme.typography.bodyMedium)
            }
            Text(text = "${day.maxTemperature.roundToInt()}° / ${day.minTemperature.roundToInt()}°")
        }
    }
}

private fun formatHourLabel(isoTime: String): String {
    // isoTime format: "2026-09-30T18:00"
    val hourPart = isoTime.substringAfter("T").substringBefore(":").toIntOrNull() ?: return isoTime
    return when {
        hourPart == 0 -> "12AM"
        hourPart < 12 -> "${hourPart}AM"
        hourPart == 12 -> "12PM"
        else -> "${hourPart - 12}PM"
    }
}

private fun formatDayLabel(isoDate: String): String {
    // isoDate format: "2026-09-30"
    val parts = isoDate.split("-")
    if (parts.size != 3) return isoDate
    val (year, month, day) = parts
    val monthNames = listOf(
        "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec",
    )
    val monthIndex = month.toIntOrNull()?.minus(1) ?: return isoDate
    return "${monthNames.getOrElse(monthIndex) { month }} ${day.toIntOrNull() ?: day}"
}
