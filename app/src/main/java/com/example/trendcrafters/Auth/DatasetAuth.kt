package com.example.trendcrafters.Auth
import com.google.gson.annotations.SerializedName
data class SignupRequest(
    @SerializedName("name")     val name: String,
    @SerializedName("email")    val email: String,
    @SerializedName("password") val password: String
)

data class LoginRequest(
    @SerializedName("email")    val email: String,
    @SerializedName("password") val password: String
)

// ─── Response Models ──────────────────────────────────────────────────────────

data class SignupResponse(
    @SerializedName("message") val message: String,
    @SerializedName("user_id") val userId: String,
    @SerializedName("token")   val token: String
)

data class LoginResponse(
    @SerializedName("token") val token: String,
    @SerializedName("user")  val user: UserInfo
)

data class UserInfo(
    @SerializedName("id")   val id: String,
    @SerializedName("name") val name: String
)

