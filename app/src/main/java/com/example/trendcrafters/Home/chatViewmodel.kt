package com.example.trendcrafters.Home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.trendcrafters.ApiService.RetrofitClient
import com.google.gson.annotations.SerializedName
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

import java.text.SimpleDateFormat
import java.util.*

// ─────────────────────────────────────────────
// Data Models
// ─────────────────────────────────────────────
data class ReelsQueryRequest(
    val query: String,
    @SerializedName("top_k") val topK: Int = 10,
    @SerializedName("min_likes") val minLikes: Int = 0,
    @SerializedName("max_duration") val maxDuration: Int = 0,
    @SerializedName("text_weight") val textWeight: Float = 0.6f
)
enum class MessageSender { USER, AI }

data class ChatMsg(
    val id: String = UUID.randomUUID().toString(),
    val sender: MessageSender,
    val text: String,
    val time: String = SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date()),
    val apiResult: ApiResult? = null
)

data class ApiResult(
    val analysis: Analysis,
    val patterns: List<String>,
    val ideas: List<Idea>,
    val bestFitIndex: Int,
    val bestFitReason: String,
    val optimizationSuggestion: OptimizationSuggestion?,
    val sources: List<ReelSource>
)

data class Analysis(
    val performanceDrivers: List<String>,
    val engagementTriggers: List<String>
)

data class Idea(
    val concept: String,
    val hook: String,
    val structure: List<String>,
    val emotion: String,
    val whyItWorks: String
)

data class OptimizationSuggestion(
    val change: String,
    val add: String,
    val result: String
)

data class ReelSource(
    val id: String,
    val owner: String,
    val likes: Int,
    val duration: Double,
    val score: Double,
    val url: String
)

// ─────────────────────────────────────────────
// UI State
// ─────────────────────────────────────────────

data class ChatUiState(
    val messages: List<ChatMsg> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

// ─────────────────────────────────────────────
// ViewModel
// ─────────────────────────────────────────────

class ChatViewModel : ViewModel() {

    private val api = RetrofitClient.apiInterface

    private val _uiState = MutableStateFlow(ChatUiState())
    val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()

    fun sendMessage(query: String, topK: Int = 10, textWeight: Float = 0.6f) {
        if (query.isBlank() || _uiState.value.isLoading) return

        val userMsg = ChatMsg(sender = MessageSender.USER, text = query)
        _uiState.value = _uiState.value.copy(
            messages = _uiState.value.messages + userMsg,
            isLoading = true,
            error = null
        )

        viewModelScope.launch {
            try {
                val response = api.queryReels(
                    ReelsQueryRequest(query = query, topK = topK, textWeight = textWeight)
                )
                if (response.isSuccessful && response.body() != null) {
                    val result = response.body()!!.toApiResult()
                    val aiMsg = ChatMsg(
                        sender = MessageSender.AI,
                        text = "I've analyzed the trends for '$query'. Here are the best concepts for your reel:",
                        apiResult = result
                    )
                    updateUiWithMsg(aiMsg)
                } else {
                    throw Exception("HTTP ${response.code()}")
                }
            } catch (e: Exception) {
                android.util.Log.e("ChatViewModel", "API Error: ${e.message}", e)
                val aiMsg = ChatMsg(
                    sender = MessageSender.AI,
                    text = "⚠️ [Offline Mode] Connection failed. Using predicted patterns for: \"$query\"",
                    apiResult = getDummyFallback(query)
                )
                updateUiWithMsg(aiMsg)
            }
        }
    }

    private fun updateUiWithMsg(msg: ChatMsg) {
        _uiState.value = _uiState.value.copy(
            messages = _uiState.value.messages + msg,
            isLoading = false
        )
    }

    private fun getDummyFallback(query: String) = ApiResult(
        analysis = Analysis(
            performanceDrivers = listOf("High contrast", "Clear CTA"),
            engagementTriggers = listOf("Fast pacing", "Trending audio")
        ),
        patterns = listOf("POV style", "Before/After format"),
        ideas = listOf(
            Idea(
                concept = "Trend Concept",
                hook = "You won't believe this hack...",
                structure = listOf("Hook (0-3s)", "Build tension (3-10s)", "Payoff + CTA (10-15s)"),
                emotion = "Excited",
                whyItWorks = "Pattern interrupts drive saves and shares"
            )
        ),
        bestFitIndex = 0,
        bestFitReason = "Matches '$query' niche with high viral potential.",
        optimizationSuggestion = OptimizationSuggestion("Add captions", "Trending sound", "30% higher retention"),
        sources = emptyList()
    )
}