
package com.example.trendcrafters.ApiService

import com.example.trendcrafters.Auth.LoginRequest
import com.example.trendcrafters.Auth.LoginResponse
import com.example.trendcrafters.Auth.SignupRequest
import com.example.trendcrafters.Auth.SignupResponse
import com.example.trendcrafters.Home.ReelsQueryRequest
import com.example.trendcrafters.Home.ReelsQueryResponse
import com.example.trendcrafters.Profile.ProfileCreateUpdateRequest
import com.example.trendcrafters.Profile.ProfileResponse
import com.example.trendcrafters.Profile.ProfileSaveResponse
import com.example.trendcrafters.draft.DraftCreateRequest
import com.example.trendcrafters.draft.DraftRemote
import com.example.trendcrafters.draft.DraftUpdateRequest

import com.example.trendcrafters.pytrends.TrendKeywordResponse
import retrofit2.Response
import retrofit2.http.*

interface ApiInterface {

    // ── Auth ──────────────────────────────────────────────
    @POST("auth/signup")
    suspend fun signup(@Body request: SignupRequest): Response<SignupResponse>

    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    // ── Profile ───────────────────────────────────────────
    @GET("profile")
    suspend fun getProfile(
        @Header("Authorization") token: String
    ): Response<ProfileResponse>

    @POST("profile")
    suspend fun saveProfile(
        @Header("Authorization") token: String,
        @Body request: ProfileCreateUpdateRequest
    ): Response<ProfileSaveResponse>

    // ── Trends ────────────────────────────────────────────
    @POST("trends/fetch")
    suspend fun fetchAndStoreTrends(
        @Query("niche") niche: String,
        @Query("region") region: String = "IN"
    ): Response<Any>

    @GET("trends/keywords")
    suspend fun getTrendKeywords(
        @Query("niche") niche: String,
        @Query("region") region: String = "IN",
        @Query("limit") limit: Int = 12
    ): List<TrendKeywordResponse>

    // ── Reels / Chat ──────────────────────────────────────
    @POST("transcript/reels/query")
    suspend fun queryReels(@Body request: ReelsQueryRequest): Response<ReelsQueryResponse>

    // ── Drafts ────────────────────────────────────────────


    // ── Drafts ────────────────────────────────────────────
    @GET("drafts/")
    suspend fun getDrafts(
        @Header("Authorization") token: String
    ): List<DraftRemote>

    @GET("drafts/{id}")
    suspend fun getDraft(
        @Header("Authorization") token: String,
        @Path("id") id: Int
    ): Response<DraftRemote>

    @POST("drafts/")
    suspend fun createDraft(
        @Header("Authorization") token: String,
        @Body request: DraftCreateRequest
    ): Response<DraftRemote>

    @PUT("drafts/{id}")
    suspend fun updateDraft(
        @Header("Authorization") token: String,
        @Path("id") id: Int,
        @Body request: DraftUpdateRequest
    ): Response<DraftRemote>

    @DELETE("drafts/{id}")
    suspend fun deleteDraft(
        @Header("Authorization") token: String,
        @Path("id") id: Int
    ): Response<Unit>

}