package com.arcadia.trivora

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header

interface ApiService {
    @GET("profile")
    fun getProfile(@Header("Authorization") authToken: String): Call<ProfileResponse>
}