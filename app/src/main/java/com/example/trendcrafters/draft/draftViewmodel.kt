package com.example.trendcrafters.draft
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.trendcrafters.Auth.TokenManager
import com.example.trendcrafters.Home.Draft
import com.example.trendcrafters.Home.DraftStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class DraftViewModel : ViewModel() { // No arguments here = No more crash

    // 1. Setup Networking directly in the class
    private val retrofit = Retrofit.Builder()
        .baseUrl("https://trendbackend.onrender.com") // Replace with your actual URL
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val apiService = retrofit.create(DraftApiService::class.java)

    // 2. State Management
    private val _drafts = MutableStateFlow<List<Draft>>(emptyList())
    val drafts: StateFlow<List<Draft>> = _drafts

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    // 3. Logic (Combining Repository logic here)
    fun loadDrafts() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val token = TokenManager.bearerHeader()
                val response = apiService.getDrafts("Bearer $token")

                // Map the remote data to your UI model
                _drafts.value = response.map { remote ->
                    Draft(
                        id = remote.id.toString(),
                        topic = remote.content.topic,
                        description = remote.content.description,
                        platform = remote.content.platform,
                        platformIcon = remote.content.platformIcon,
                        createdDate = remote.createdAt,
                        updatedDate = remote.updatedAt,
                        status = try {
                            DraftStatus.valueOf(remote.content.status)
                        } catch (e: Exception) {
                            DraftStatus.IN_PROGRESS
                        },
                        tags = remote.content.tags
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }
}