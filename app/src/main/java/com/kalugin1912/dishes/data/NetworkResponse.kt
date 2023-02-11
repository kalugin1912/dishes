package com.kalugin1912.dishes.data

data class NetworkResponse<T>(
    val isLoading: Boolean,
    val data: T,
)