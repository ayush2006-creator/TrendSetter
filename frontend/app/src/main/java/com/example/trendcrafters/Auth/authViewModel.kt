package com.example.trendcrafters.Auth



import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

// UI state for auth operations
data class AuthUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val signupSuccess: SignupResponse? = null,
    val loginSuccess: LoginResponse? = null
)

class AuthViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState

    fun signup(name: String, email: String, password: String) {
        // Basic validation
        if (name.isBlank() || email.isBlank() || password.isBlank()) {
            _uiState.value = _uiState.value.copy(errorMessage = "All fields are required.")
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            when (val result = AuthService.signup(name, email, password)) {
                is AuthResult.Success -> {
                    // Persist token for future authenticated requests
                    TokenManager.saveToken(result.data.token)
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        signupSuccess = result.data
                    )
                }
                is AuthResult.Error -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = result.message
                    )
                }
            }
        }
    }

    fun login(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            _uiState.value = _uiState.value.copy(errorMessage = "Email and password are required.")
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            when (val result = AuthService.login(email, password)) {
                is AuthResult.Success -> {
                    TokenManager.saveToken(result.data.token)
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        loginSuccess = result.data
                    )
                }
                is AuthResult.Error -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = result.message
                    )
                }
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }
}