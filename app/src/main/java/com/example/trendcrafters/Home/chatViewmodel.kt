package com.example.trendcrafters.Home



import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import java.text.SimpleDateFormat
import java.util.*

// ─────────────────────────────────────────────
// Data Models
// ─────────────────────────────────────────────

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
    val duration: Int,
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

    // 👇 Replace with your actual base URL
    private val BASE_URL = "https://trendbackend.onrender.com/transcript/reels/query"

    private val _uiState = MutableStateFlow(ChatUiState())
    val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()

    fun sendMessage(query: String, topK: Int = 8, textWeight: Float = 0.5f) {
        if (query.isBlank() || _uiState.value.isLoading) return

        val userMsg = ChatMsg(sender = MessageSender.USER, text = query)
        _uiState.value = _uiState.value.copy(
            messages = _uiState.value.messages + userMsg,
            isLoading = true,
            error = null
        )

        viewModelScope.launch {
            try {
                val result = callApi(query, topK, textWeight)
                val aiMsg = ChatMsg(
                    sender = MessageSender.AI,
                    text = buildSummaryText(result),
                    apiResult = result
                )
                _uiState.value = _uiState.value.copy(
                    messages = _uiState.value.messages + aiMsg,
                    isLoading = false
                )
            } catch (e: Exception) {
                val errorMsg = ChatMsg(
                    sender = MessageSender.AI,
                    text = "❌ Something went wrong: ${e.message}"
                )
                _uiState.value = _uiState.value.copy(
                    messages = _uiState.value.messages + errorMsg,
                    isLoading = false,
                    error = e.message
                )
            }
        }
    }

    // In ChatViewModel.kt
    private suspend fun callApi(query: String, topK: Int, textWeight: Float): ApiResult {
        return kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
            val url = URL("https://trendbackend.onrender.com/transcript/reels/query")
            val conn = url.openConnection() as HttpURLConnection
            conn.requestMethod = "POST" // Change to POST
            conn.setRequestProperty("Content-Type", "application/json")
            conn.doOutput = true

            // Create the JSON body
            val jsonBody = JSONObject().apply {
                put("query", query)
                put("top_k", topK)
                put("text_weight", textWeight)
            }

            // Write to stream
            conn.outputStream.use { os ->
                os.write(jsonBody.toString().toByteArray(Charsets.UTF_8))
            }

            val responseCode = conn.responseCode
            if (responseCode != HttpURLConnection.HTTP_OK) {
                throw Exception("HTTP $responseCode")
            }

            val response = conn.inputStream.bufferedReader().readText()
            parseApiResponse(response)
        }
    }
    private fun parseApiResponse(json: String): ApiResult {
        val root = JSONObject(json)
        val answer = root.getJSONObject("answer")

        // Analysis
        val analysisObj = answer.getJSONObject("analysis")
        val perfDrivers = analysisObj.getJSONArray("performance_drivers").let { arr ->
            (0 until arr.length()).map { arr.getString(it) }
        }
        val engTriggers = analysisObj.getJSONArray("engagement_triggers").let { arr ->
            (0 until arr.length()).map { arr.getString(it) }
        }

        // Patterns
        val patternsArr = answer.getJSONArray("patterns")
        val patterns = (0 until patternsArr.length()).map { patternsArr.getString(it) }

        // Ideas
        val ideasArr = answer.getJSONArray("ideas")
        val ideas = (0 until ideasArr.length()).map { i ->
            val obj = ideasArr.getJSONObject(i)
            val structArr = obj.getJSONArray("structure")
            Idea(
                concept = obj.getString("concept"),
                hook = obj.getString("hook"),
                structure = (0 until structArr.length()).map { structArr.getString(it) },
                emotion = obj.getString("emotion"),
                whyItWorks = obj.getString("why_it_works")
            )
        }

        // Best fit
        val bestFit = answer.getJSONObject("best_fit_recommendation")
        val bestFitIndex = bestFit.getInt("best_idea_index")
        val bestFitReason = bestFit.getString("reason")

        // Optimization
        val optObj = answer.optJSONObject("optimization_suggestion")
        val optimization = optObj?.optJSONObject("second_idea_emotional_variant")?.let {
            OptimizationSuggestion(
                change = it.optString("change", ""),
                add = it.optString("add", ""),
                result = it.optString("result", "")
            )
        }

        // Sources
        val sourcesArr = root.getJSONArray("sources")
        val sources = (0 until sourcesArr.length()).map { i ->
            val s = sourcesArr.getJSONObject(i)
            ReelSource(
                id = s.getString("id"),
                owner = s.getString("owner"),
                likes = s.getInt("likes"),
                duration = s.getInt("duration"),
                score = s.getDouble("score"),
                url = s.getString("url")
            )
        }

        return ApiResult(
            analysis = Analysis(perfDrivers, engTriggers),
            patterns = patterns,
            ideas = ideas,
            bestFitIndex = bestFitIndex,
            bestFitReason = bestFitReason,
            optimizationSuggestion = optimization,
            sources = sources
        )
    }

    private fun buildSummaryText(result: ApiResult): String {
        val bestIdea = result.ideas.getOrNull(result.bestFitIndex)
        return buildString {
            append("🎯 Best Concept: ${bestIdea?.concept ?: "—"}\n")
            append("🪝 Hook: ${bestIdea?.hook ?: "—"}\n")
            append("💡 Why it works: ${bestIdea?.whyItWorks ?: "—"}")
        }
    }
}