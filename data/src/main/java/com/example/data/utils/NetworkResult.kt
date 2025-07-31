package com.example.data.utils

/**
 * Универсальный sealed class для инкапсуляции результатов сетевых операций.
 * Представляет либо успешный результат с данными, либо ошибку с исключением.
 *
 * @param T Тип данных, который ожидается при успешном выполнении.
 */
sealed class NetworkResult<out T : Any> {
    /**
     * Успешный результат операции.
     * @param data Данные, полученные в результате успешного выполнения.
     */
    data class Success<out T : Any>(val data: T) : NetworkResult<T>()

    /**
     * Результат операции с ошибкой.
     * @param exception Исключение, произошедшее во время выполнения операции.
     */
    data class Error(val exception: Throwable) : NetworkResult<Nothing>()
}