package by.ssrlab.domain.utils

import retrofit2.Call

sealed class Resource<out T> {
    data object Loading : Resource<Nothing>()
    data class Success<out T>(val data: T) : Resource<T>()
    data class Error(val message: String) : Resource<Nothing>()
}

fun <T> Resource<Call<List<T>>>.unwrap(): Call<List<T>>? {
    return when (this) {
        is Resource.Success -> this.data
        else -> null
    }
}
