package com.manojmourya.weathernow.domain.model

/**
 * A city, either a transient geocoding search result (id == null) or a
 * favorited city persisted locally (id != null).
 */
data class City(
    val id: Long? = null,
    val name: String,
    val country: String?,
    val admin1: String?,
    val latitude: Double,
    val longitude: Double,
    val isSelected: Boolean = false,
) {
    /** Short human-readable subtitle, e.g. "England, United Kingdom". */
    val subtitle: String
        get() = listOfNotNull(admin1, country).joinToString(", ")
}
