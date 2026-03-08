package com.example.trendcrafters.Auth


/**
 * In-memory token store. For production replace with
 * Jetpack DataStore or EncryptedSharedPreferences so the
 * token survives app restarts.
 */
object TokenManager {
    private var token: String? = null

    fun saveToken(t: String) { token = t }
    fun getToken(): String? = token
    fun clearToken() { token = null }
    fun isLoggedIn(): Boolean = token != null

    /** Returns an Authorization header value ready for Retrofit. */
    fun bearerHeader(): String = "Bearer ${token.orEmpty()}"
}