package com.manojmourya.weathernow.domain.util

/**
 * Generic wrapper describing the state of an operation that can be loading,
 * successful, or failed. Used throughout the domain/presentation layers so the
 * UI can render loading/empty/error states consistently.
 */
sealed class Resource<out T> {
    data object Loading : Resource<Nothing>()
    data class Success<T>(val data: T) : Resource<T>()
    data class Error(val message: String) : Resource<Nothing>()
}
