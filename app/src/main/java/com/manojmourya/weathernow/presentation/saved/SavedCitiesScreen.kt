package com.manojmourya.weathernow.presentation.saved

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.LocationCity
import androidx.compose.material3.Badge
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.manojmourya.weathernow.R
import com.manojmourya.weathernow.domain.model.City
import com.manojmourya.weathernow.presentation.components.EmptyState
import com.manojmourya.weathernow.presentation.components.LoadingState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SavedCitiesScreen(
    onCityClick: (City) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SavedCitiesViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(title = { Text(stringResource(R.string.saved_title)) })
        },
    ) { padding ->
        when {
            uiState.isLoading -> LoadingState(modifier = Modifier.padding(padding))
            uiState.isEmpty -> EmptyState(
                title = stringResource(R.string.saved_empty_title),
                message = stringResource(R.string.saved_empty_message),
                modifier = Modifier.padding(padding),
            )
            else -> LazyColumn(modifier = Modifier.padding(padding).fillMaxSize()) {
                items(uiState.cities, key = { it.id ?: "${it.latitude},${it.longitude}" }) { city ->
                    SwipeableSavedCityRow(
                        city = city,
                        onClick = { onCityClick(city) },
                        onDelete = { viewModel.deleteCity(city) },
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SwipeableSavedCityRow(
    city: City,
    onClick: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = { value ->
            if (value == SwipeToDismissBoxValue.EndToStart || value == SwipeToDismissBoxValue.StartToEnd) {
                true
            } else {
                false
            }
        },
    )

    LaunchedEffect(dismissState.currentValue) {
        if (dismissState.currentValue == SwipeToDismissBoxValue.EndToStart ||
            dismissState.currentValue == SwipeToDismissBoxValue.StartToEnd
        ) {
            onDelete()
        }
    }

    SwipeToDismissBox(
        state = dismissState,
        modifier = modifier,
        backgroundContent = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.errorContainer)
                    .padding(horizontal = 24.dp),
                contentAlignment = Alignment.CenterEnd,
            ) {
                Icon(
                    imageVector = Icons.Filled.Delete,
                    contentDescription = stringResource(R.string.saved_delete_action),
                    tint = MaterialTheme.colorScheme.onErrorContainer,
                )
            }
        },
    ) {
        ListItem(
            headlineContent = { Text(city.name) },
            supportingContent = { if (city.subtitle.isNotBlank()) Text(city.subtitle) },
            leadingContent = { Icon(Icons.Filled.LocationCity, contentDescription = null) },
            trailingContent = {
                if (city.isSelected) {
                    Badge { Text(stringResource(R.string.saved_current_badge)) }
                }
            },
            modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        )
    }
}

