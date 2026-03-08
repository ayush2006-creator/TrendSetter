package com.example.trendcrafters.Auth



import com.example.trendcrafters.ApiService.RetrofitClient


// Sealed class to cleanly represent success or error states
sealed class AuthResult<out T> {
    data class Success<T>(val data: T) : AuthResult<T>()
    data class Error(val message: String) : AuthResult<Nothing>()
}

object AuthService {

    private val api = RetrofitClient.apiInterface

    suspend fun signup(name: String, email: String, password: String): AuthResult<SignupResponse> {
        return try {
            val response = api.signup(SignupRequest(name, email, password))
            if (response.isSuccessful) {
                AuthResult.Success(response.body()!!)
            } else {
                val errorMsg = response.errorBody()?.string() ?: "Signup failed"
                AuthResult.Error(parseError(errorMsg))
            }
        } catch (e: Exception) {
            AuthResult.Error(e.message ?: "Network error. Please check your connection.")
        }
    }

    suspend fun login(email: String, password: String): AuthResult<LoginResponse> {
        return try {
            val response = api.login(LoginRequest(email, password))
            if (response.isSuccessful) {
                AuthResult.Success(response.body()!!)
            } else {
                val errorMsg = response.errorBody()?.string() ?: "Login failed"
                AuthResult.Error(parseError(errorMsg))
            }
        } catch (e: Exception) {
            AuthResult.Error(e.message ?: "Network error. Please check your connection.")
        }
    }

    // Extracts the "detail" field from FastAPI error JSON: {"detail": "..."}
    private fun parseError(raw: String): String {
        return try {
            val json = org.json.JSONObject(raw)
            json.optString("detail", raw)
        } catch (e: Exception) {
            raw
        }
    }
}