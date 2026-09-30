package com.manojmourya.weathernow.presentation.search

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.LocationCity
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.manojmourya.weathernow.R
import com.manojmourya.weathernow.domain.model.City
import com.manojmourya.weathernow.presentation.components.EmptyState
import com.manojmourya.weathernow.presentation.components.ErrorState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    onCityClick: (City) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SearchViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(title = { Text(stringResource(R.string.nav_search)) })
        },
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize()) {
            TextField(
                value = uiState.query,
                onValueChange = viewModel::onQueryChanged,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .testTag("search_field"),
                placeholder = { Text(stringResource(R.string.search_placeholder)) },
                leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
                trailingIcon = {
                    if (uiState.query.isNotEmpty()) {
                        IconButton(onClick = { viewModel.onQueryChanged("") }) {
                            Icon(Icons.Filled.Clear, contentDescription = null)
                        }
                    }
                },
                singleLine = true,
            )

            when {
                uiState.isLoading -> CircularProgressIndicator(
                    modifier = Modifier.padding(32.dp).testTag("search_loading"),
                )
                uiState.errorMessage != null -> ErrorState(message = uiState.errorMessage!!)
                uiState.showEmptyPrompt -> EmptyState(
                    title = stringResource(R.string.nav_search),
                    message = stringResource(R.string.search_empty_prompt),
                )
                uiState.showNoResults -> EmptyState(
                    title = stringResource(R.string.search_no_results),
                    message = "",
                    icon = null,
                )
                else -> SearchResultsList(results = uiState.results, onCityClick = onCityClick)
            }
        }
    }
}

@Composable
private fun SearchResultsList(
    results: List<City>,
    onCityClick: (City) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(modifier = modifier.fillMaxSize().testTag("search_results")) {
        items(results, key = { "${it.latitude},${it.longitude}" }) { city ->
            ListItem(
                headlineContent = { Text(city.name) },
                supportingContent = { if (city.subtitle.isNotBlank()) Text(city.subtitle) },
                leadingContent = { Icon(Icons.Filled.LocationCity, contentDescription = null) },
                modifier = Modifier.clickable { onCityClick(city) },
            )
        }
    }
}
