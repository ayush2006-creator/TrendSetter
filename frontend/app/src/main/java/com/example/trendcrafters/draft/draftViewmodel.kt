package com.example.trendcrafters.draft

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.trendcrafters.ApiService.RetrofitClient
import com.example.trendcrafters.Auth.TokenManager
import com.example.trendcrafters.Home.Draft
import com.example.trendcrafters.Home.DraftStatus
import com.example.trendcrafters.Home.sampleDrafts
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class DraftViewModel : ViewModel() {

    private val api = RetrofitClient.apiInterface

    private val _drafts = MutableStateFlow<List<Draft>>(emptyList())
    val drafts: StateFlow<List<Draft>> = _drafts

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    init { loadDraftsOrSeed() }

    // ── Init: load from API; if empty, create a dummy draft then reload ──
    private fun loadDraftsOrSeed() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val token = TokenManager.bearerHeader()
                val remote = api.getDrafts(token)
                if (remote.isEmpty()) {
                    // No drafts exist → create a dummy one via the real API
                    val dummy = DraftContent(
                        topic = "My First Draft ✨",
                        description = "This is a starter draft auto-created for testing. Feel free to edit or delete it.",
                        platform = "Instagram Reels",
                        platformIcon = "📸",
                        status = DraftStatus.IN_PROGRESS.name,
                        tags = listOf("#StarterDraft", "#Testing")
                    )
                    api.createDraft(token, DraftCreateRequest(dummy))
                    // Re-fetch so the server-assigned ID is captured
                    _drafts.value = api.getDrafts(token).map { it.toDraft() }
                } else {
                    _drafts.value = remote.map { it.toDraft() }
                }
            } catch (e: Exception) {
                // API unreachable — fall back silently to sample data
                _drafts.value = sampleDrafts
            } finally {
                _isLoading.value = false
            }
        }
    }

    // ── READ ALL ──────────────────────────────────────────
    fun loadDrafts() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val token = TokenManager.bearerHeader()
                _drafts.value = api.getDrafts(token).map { it.toDraft() }
            } catch (e: Exception) {
                _error.value = e.message ?: "Failed to load drafts"
            } finally {
                _isLoading.value = false
            }
        }
    }

    // ── CREATE ────────────────────────────────────────────
    fun createDraft(content: DraftContent, onResult: (Boolean) -> Unit = {}) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val token = TokenManager.bearerHeader()
                val response = api.createDraft(token, DraftCreateRequest(content))
                if (response.isSuccessful) {
                    loadDrafts()
                    onResult(true)
                } else {
                    _error.value = "Create failed: ${response.code()}"
                    onResult(false)
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "Failed to create draft"
                onResult(false)
            } finally {
                _isLoading.value = false
            }
        }
    }

    // ── UPDATE ────────────────────────────────────────────
    fun updateDraft(id: Int, content: DraftContent, onResult: (Boolean) -> Unit = {}) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val token = TokenManager.bearerHeader()
                val response = api.updateDraft(token, id, DraftUpdateRequest(content))
                if (response.isSuccessful) {
                    response.body()?.let { updated ->
                        _drafts.value = _drafts.value.map { draft ->
                            if (draft.id == id.toString()) updated.toDraft() else draft
                        }
                    }
                    onResult(true)
                } else {
                    _error.value = "Update failed: ${response.code()}"
                    onResult(false)
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "Failed to update draft"
                onResult(false)
            } finally {
                _isLoading.value = false
            }
        }
    }

    // ── DELETE ────────────────────────────────────────────
    fun deleteDraft(id: Int, onResult: (Boolean) -> Unit = {}) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val token = TokenManager.bearerHeader()
                val response = api.deleteDraft(token, id)
                if (response.isSuccessful) {
                    _drafts.value = _drafts.value.filter { it.id != id.toString() }
                    onResult(true)
                } else {
                    _error.value = "Delete failed: ${response.code()}"
                    onResult(false)
                }
            } catch (e: Exception) {
                _error.value = e.message ?: "Failed to delete draft"
                onResult(false)
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun clearError() { _error.value = null }
}

// ── Extension: DraftRemote → UI Draft ─────────────────
private fun DraftRemote.toDraft() = Draft(
    id = id.toString(),
    topic = content.topic.orEmpty(),
    description = content.description.orEmpty(),
    platform = content.platform.orEmpty(),
    platformIcon = content.platformIcon ?: "📝",   // null-safe fallback icon
    createdDate = createdAt.orEmpty(),
    updatedDate = updatedAt.orEmpty(),
    status = try {
        DraftStatus.valueOf(content.status.orEmpty())
    } catch (e: IllegalArgumentException) {
        DraftStatus.IN_PROGRESS
    },
    tags = content.tags.orEmpty()
)