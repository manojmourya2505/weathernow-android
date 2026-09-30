package com.manojmourya.weathernow.presentation.detail

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import com.manojmourya.weathernow.domain.util.WeatherCodeMapper
import com.manojmourya.weathernow.presentation.components.ErrorState
import com.manojmourya.weathernow.presentation.components.LoadingState
import com.manojmourya.weathernow.presentation.components.weatherCodeToIcon
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CityDetailScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: CityDetailViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text(uiState.city.name) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = stringResource(R.string.content_description_back))
                    }
                },
                actions = {
                    IconButton(onClick = viewModel::toggleFavorite) {
                        Icon(
                            imageVector = if (uiState.isSaved) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                            contentDescription = if (uiState.isSaved) {
                                stringResource(R.string.detail_remove_favorite)
                            } else {
                                stringResource(R.string.detail_add_favorite)
                            },
                        )
                    }
                },
            )
        },
    ) { padding ->
        when {
            uiState.isLoading -> LoadingState(modifier = Modifier.padding(padding))
            uiState.errorMessage != null -> ErrorState(
                message = uiState.errorMessage!!,
                icon = Icons.Filled.CloudOff,
                modifier = Modifier.padding(padding),
            )
            uiState.forecast != null -> CityDetailContent(
                uiState = uiState,
                onSetAsHome = viewModel::setAsHomeCity,
                modifier = Modifier.padding(padding),
            )
        }
    }
}

@Composable
private fun CityDetailContent(
    uiState: CityDetailUiState,
    onSetAsHome: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val forecast = uiState.forecast ?: return
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    if (uiState.city.subtitle.isNotBlank()) {
                        Text(text = uiState.city.subtitle, style = MaterialTheme.typography.bodyMedium)
                        Spacer(Modifier.height(8.dp))
                    }
                    Icon(
                        imageVector = weatherCodeToIcon(forecast.current.weatherCode),
                        contentDescription = null,
                        modifier = Modifier.size(56.dp),
                        tint = MaterialTheme.colorScheme.secondary,
                    )
                    Text(
                        text = "${forecast.current.temperature.roundToInt()}°",
                        style = MaterialTheme.typography.displayMedium,
                        fontWeight = FontWeight.Light,
                    )
                    Text(
                        text = WeatherCodeMapper.description(forecast.current.weatherCode),
                        style = MaterialTheme.typography.bodyLarge,
                    )
                    Spacer(Modifier.height(16.dp))
                    if (uiState.isCurrentHomeCity) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Filled.Home, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                            Spacer(Modifier.width(8.dp))
                            Text(
                                text = stringResource(R.string.detail_already_home),
                                color = MaterialTheme.colorScheme.primary,
                            )
                        }
                    } else {
                        Button(onClick = onSetAsHome, modifier = Modifier.fillMaxWidth()) {
                            Icon(Icons.Filled.Home, contentDescription = null)
                            Spacer(Modifier.width(8.dp))
                            Text(stringResource(R.string.detail_set_as_home))
                        }
                    }
                }
            }
        }
        item {
            Text(text = stringResource(R.string.home_daily_forecast), style = MaterialTheme.typography.titleMedium)
        }
        items(forecast.daily) { day -> DetailDailyRow(day) }
    }
}

@Composable
private fun DetailDailyRow(day: DailyForecastItem, modifier: Modifier = Modifier) {
    Card(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(text = day.date.substringAfter("-"))
            Icon(imageVector = weatherCodeToIcon(day.weatherCode), contentDescription = null, tint = MaterialTheme.colorScheme.secondary)
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                Icon(Icons.Filled.WaterDrop, contentDescription = null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.primary)
                Text(text = "${day.precipitationProbability}%")
            }
            Text(text = "${day.maxTemperature.roundToInt()}° / ${day.minTemperature.roundToInt()}°")
        }
    }
}

