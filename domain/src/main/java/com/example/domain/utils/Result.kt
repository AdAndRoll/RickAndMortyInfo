package com.example.domain.utils

/**
 * Универсальный класс-обертка для инкапсуляции состояния
 * операций (успех, ошибка). Используется в доменном слое.
 */
sealed class Result<out T> {
    data class Success<out T>(val data: T) : Result<T>()
    data class Error(val exception: Throwable) : Result<Nothing>()
}
