package com.example.trendcrafters.Home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.trendcrafters.Auth.AuthResult
import com.example.trendcrafters.Profile.ProfileCreateUpdateRequest
import com.example.trendcrafters.Profile.ProfileResponse
import com.example.trendcrafters.Profile.ProfileService
import com.example.trendcrafters.Profile.toOnboardingProfile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class ProfileUiState(
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val profile: ProfileResponse? = null,
    val onboardingProfile: OnboardingProfile? = null,
    val saveSuccess: Boolean = false,
    val errorMessage: String? = null
)

class ProfileViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState

    fun loadProfile() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            when (val result = ProfileService.getProfile()) {
                is AuthResult.Success -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        profile = result.data,
                        onboardingProfile = result.data.toOnboardingProfile()
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

    fun saveProfile(
        displayName: String,
        creatorType: String,
        organizationType: String? = null,
        experienceLevel: String,
        audienceType: String,
        platform: String,
        goals: String? = null,
        niches: List<String>
    ) {
        if (displayName.isBlank()) {
            _uiState.value = _uiState.value.copy(errorMessage = "Display name is required.")
            return
        }

        val request = ProfileCreateUpdateRequest(
            displayName = displayName,
            creatorType = creatorType,
            organizationType = organizationType,
            experienceLevel = experienceLevel,
            audienceType = audienceType,
            platform = platform,
            goals = goals,
            niches = niches
        )

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSaving = true, errorMessage = null, saveSuccess = false)
            when (val result = ProfileService.saveProfile(request)) {
                is AuthResult.Success -> {
                    // Fix: signal success and stop saving BEFORE refreshing
                    _uiState.value = _uiState.value.copy(isSaving = false, saveSuccess = true)
                    loadProfile() // refresh in background
                }
                is AuthResult.Error -> {
                    _uiState.value = _uiState.value.copy(
                        isSaving = false,
                        errorMessage = result.message
                    )
                }
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }

    fun clearSaveSuccess() {
        _uiState.value = _uiState.value.copy(saveSuccess = false)
    }
}