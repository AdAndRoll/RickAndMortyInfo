package com.example.data.remote.api

import com.example.data.remote.dto.LocationRemoteResponse
import retrofit2.http.GET
import retrofit2.http.Path

// Интерфейс для работы только с локациями
interface LocationApiService {
    @GET("location/{id}")
    suspend fun getLocationDetails(@Path("id") id: Int): LocationRemoteResponse
}