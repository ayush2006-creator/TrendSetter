package com.example.trendcrafters.Profile



import com.example.trendcrafters.ApiService.RetrofitClient
import com.example.trendcrafters.Auth.AuthResult
import com.example.trendcrafters.Auth.TokenManager

import org.json.JSONObject

object ProfileService {

    private val api = RetrofitClient.apiInterface

    suspend fun getProfile(): AuthResult<ProfileResponse> {
        return try {
            val token = TokenManager.bearerHeader()
            val response = api.getProfile(token)
            if (response.isSuccessful && response.body() != null) {
                AuthResult.Success(response.body()!!)
            } else {
                val errorMsg = response.errorBody()?.string() ?: "Failed to load profile"
                AuthResult.Error(parseError(errorMsg))
            }
        } catch (e: Exception) {
            AuthResult.Error(e.message ?: "Network error. Please check your connection.")
        }
    }

    suspend fun saveProfile(request: ProfileCreateUpdateRequest): AuthResult<ProfileSaveResponse> {
        return try {
            val token = TokenManager.bearerHeader()
            val response = api.saveProfile(token, request)
            if (response.isSuccessful && response.body() != null) {
                AuthResult.Success(response.body()!!)
            } else {
                val errorMsg = response.errorBody()?.string() ?: "Failed to save profile"
                AuthResult.Error(parseError(errorMsg))
            }
        } catch (e: Exception) {
            AuthResult.Error(e.message ?: "Network error. Please check your connection.")
        }
    }

    private fun parseError(raw: String): String {
        return try {
            JSONObject(raw).optString("detail", raw)
        } catch (e: Exception) {
            raw
        }
    }
}