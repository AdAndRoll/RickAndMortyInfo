package com.example.data.remote.datasources

import android.util.Log
import com.example.data.remote.api.LocationApiService
import com.example.data.remote.dto.LocationRemoteResponse
import com.example.data.utils.NetworkResult
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

/**
 * Удаленный источник данных для локаций.
 * Отвечает за выполнение сетевых запросов к API и обработку базовых ошибок.
 *
 * @param api Экземпляр [LocationApiService], предоставляемый для выполнения запросов.
 */
class LocationRemoteDataSource @Inject constructor(
    private val api: LocationApiService
) {
    private val TAG = "LocationRemoteDataSource"

    /**
     * Получает детальную информацию об одной локации по ее ID.
     * Оборачивает результат в [NetworkResult] для явной обработки успеха или ошибки.
     *
     * @param locationId ID локации, информацию о которой нужно получить.
     * @return [NetworkResult] с [LocationRemoteResponse] в случае успеха или [Throwable] в случае ошибки.
     */
    suspend fun getLocationDetails(locationId: Int): NetworkResult<LocationRemoteResponse> {
        return try {
            Log.d(TAG, "Making API call for location details with ID: $locationId")

            val response = api.getLocationDetails(locationId)
            Log.d(TAG, "API call successful for location ID: $locationId.")
            NetworkResult.Success(response)
        } catch (e: IOException) {
            Log.e(
                TAG,
                "Network or I/O error occurred fetching details for ID $locationId: ${e.localizedMessage}"
            )
            NetworkResult.Error(e)
        } catch (e: HttpException) {
            Log.e(
                TAG,
                "HTTP error occurred fetching details for ID $locationId: ${e.code()} - ${e.localizedMessage}"
            )
            NetworkResult.Error(e)
        } catch (e: Exception) {
            Log.e(
                TAG,
                "An unexpected error occurred fetching details for ID $locationId: ${e.localizedMessage}",
                e
            )
            NetworkResult.Error(e)
        }
    }
}
