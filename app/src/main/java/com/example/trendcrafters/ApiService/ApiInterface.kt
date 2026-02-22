package com.example.trendcrafters.ApiService


import com.example.trendcrafters.Auth.LoginRequest
import com.example.trendcrafters.Auth.LoginResponse
import com.example.trendcrafters.Auth.SignupRequest
import com.example.trendcrafters.Auth.SignupResponse

import com.example.trendcrafters.Profile.ProfileCreateUpdateRequest
import com.example.trendcrafters.Profile.ProfileResponse
import com.example.trendcrafters.Profile.ProfileSaveResponse
import com.example.trendcrafters.pytrends.TrendKeywordResponse


import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query

interface ApiInterface {

    @POST("auth/signup")
    suspend fun signup(@Body request: SignupRequest): Response<SignupResponse>

    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>
    @GET("profile")
    suspend fun getProfile(
        @Header("Authorization") token: String
    ): Response<ProfileResponse>

    @POST("profile")
    suspend fun saveProfile(
        @Header("Authorization") token: String,
        @Body request: ProfileCreateUpdateRequest
    ): Response<ProfileSaveResponse>



    interface TrendApiService {
        @GET("trends/keywords")
        suspend fun getKeywords(
            @Query("niche") niche: String,
            @Query("region") region: String = "IN",
            @Query("limit") limit: Int = 10
        ): List<TrendKeywordResponse>
    }
}
