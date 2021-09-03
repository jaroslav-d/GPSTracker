package com.example.gpstracker.managers

import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.PUT

interface DataSenderManager {

    @PUT("/something.json")
    suspend fun sendSomething(@Body str: String)

    @POST("/locations.json")
    suspend fun sendLocations(@Body str: String)
}