package com.manojmourya.weathernow.presentation.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AcUnit
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.CloudQueue
import androidx.compose.material.icons.filled.Grain
import androidx.compose.material.icons.filled.Thunderstorm
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.WbCloudy
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.ui.graphics.vector.ImageVector
import com.manojmourya.weathernow.domain.util.WeatherCodeMapper

/** Maps an Open-Meteo WMO weather code to a representative Material icon. */
fun weatherCodeToIcon(code: Int): ImageVector = when (WeatherCodeMapper.category(code)) {
    WeatherCodeMapper.Category.CLEAR -> Icons.Filled.WbSunny
    WeatherCodeMapper.Category.PARTLY_CLOUDY -> Icons.Filled.WbCloudy
    WeatherCodeMapper.Category.CLOUDY -> Icons.Filled.Cloud
    WeatherCodeMapper.Category.FOG -> Icons.Filled.CloudQueue
    WeatherCodeMapper.Category.DRIZZLE -> Icons.Filled.Grain
    WeatherCodeMapper.Category.RAIN -> Icons.Filled.WaterDrop
    WeatherCodeMapper.Category.SNOW -> Icons.Filled.AcUnit
    WeatherCodeMapper.Category.SHOWERS -> Icons.Filled.Grain
    WeatherCodeMapper.Category.THUNDERSTORM -> Icons.Filled.Thunderstorm
}
