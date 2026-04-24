package com.example.retrofit

import com.example.response.ResponseEvent
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    @GET("events?active=1")
    suspend fun getUpComingEvents(): Response<ResponseEvent>

    @GET("events?active=0")
    suspend fun getFinishedEvents(): Response<ResponseEvent>

    @GET("events")
    suspend fun searchEvents(
        @Query("active") active: Int = -1,
        @Query("q") keyword: String
    ): Response<ResponseEvent>

    @GET("events/{id}")
    suspend fun getDetailEvent(
        @Path("id") id: Int
    ): Response<ResponseEvent>

    @GET("events")
    suspend fun getNearestEvent(
        @Query("active") active: Int = -1,
        @Query("limit") limit: Int = 1
    ): Response<ResponseEvent>
}
