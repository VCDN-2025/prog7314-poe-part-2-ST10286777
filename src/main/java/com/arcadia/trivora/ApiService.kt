
package com.arcadia.trivora

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface ApiService {

    @GET("api/questions/random")
    suspend fun getRandomQuestion(): Response<ApiResponse<Question>>

    @GET("api/questions/random/{count}")
    suspend fun getRandomQuestions(@Path("count") count: Int): Response<ApiResponse<List<Question>>>

    @GET("profile")
    suspend fun getProfile(): Response<ApiResponse<UserProfile>>

    @GET("api/questions")
    suspend fun getQuestions(): Response<ApiResponse<List<Question>>>

    @GET("api/questions/categories")
    suspend fun getCategories(): Response<ApiResponse<List<String>>>

    @GET("api/questions/category/{category}")
    suspend fun getQuestionsByCategory(@Path("category") category: String): Response<ApiResponse<List<Question>>>
}