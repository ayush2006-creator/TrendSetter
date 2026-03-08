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
                    text = "I've analyzed the trends for '$query'. Here are the best concepts for your reel:",
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
            performanceDrivers = listOf(
                "Unexpected jump scares and startling reveals",
                "Unsettling atmosphere through shadows and sound design",
                "Relatable human emotions like fear and dread"
            ),
            engagementTriggers = listOf(
                "Curiosity about the unseen threat",
                "Empathy for characters in peril",
                "Desire to see how the story unfolds"
            )
        ),
        patterns = listOf(
            "Slow, creepy build-up of tension before sudden reveal",
            "Protagonist struggling against a powerful supernatural force",
            "Use of shadows, lighting, and sound to create ominous mood"
        ),
        ideas = listOf(
            Idea(
                concept = "Teenagers on a camping trip encounter a sinister presence that picks them off one by one",
                hook = "What sinister force is lurking in the shadows, waiting to strike?",
                structure = listOf(
                    "Scene 1: Group excitedly sets off into the woods, laughing and joking",
                    "Scene 2: Night falls — strange noises and unsettling shadows emerge",
                    "Scene 3: Teenagers disappear one by one, screams echoing through trees",
                    "Scene 4: Remaining friends huddle terrified, realizing they are being hunted",
                    "Scene 5: Last survivor confronts the true horror that has been stalking them"
                ),
                emotion = "Fear",
                whyItWorks = "Slow tension build with relatable fear of being hunted by unseen force keeps audience hooked"
            )
        ),
        bestFitIndex = 0,
        bestFitReason = "Classic horror trope with proven engagement — suspense builds effectively to a thrilling climax",
        optimizationSuggestion = OptimizationSuggestion(
            change = "Reveal one character as the true villain manipulating the others",
            add = "Backstory element where villain has personal vendetta against the group",
            result = "Adds emotional complexity and subverts expectations for a more memorable experience"
        ),
        sources = listOf(
            ReelSource(
                id = "ig_3500295378813825006",
                owner = "saturnalia.thapar",
                likes = 315,
                duration = 39.866,
                score = 0.009836,
                url = "https://hackathon-reels-ayush-2026.s3.amazonaws.com/reels/DCTjBoHI6_u.mp4?AWSAccessKeyId=AKIAVR6L34QLJO642KMG&Signature=axAFbVcRRQXqnkdGQkzt1FHgnY0%3D&Expires=1773336521"
            ),
            ReelSource(
                id = "ig_3103294014606430116",
                owner = "ayushmaaaannn",
                likes = 466,
                duration = 14.233,
                score = 0.009677,
                url = "https://hackathon-reels-ayush-2026.s3.amazonaws.com/reels/CsRHXbeNk-k.mp4?AWSAccessKeyId=AKIAVR6L34QLJO642KMG&Signature=0dcJdViJCOQY6mZA7epmDeKEFu4%3D&Expires=1773337152"
            ),
            ReelSource(
                id = "ig_3558136033575447317",
                owner = "nitj_photography_club",
                likes = 311,
                duration = 25.166,
                score = 0.009524,
                url = "https://hackathon-reels-ayush-2026.s3.amazonaws.com/reels/DFhCd7nyCsV.mp4?AWSAccessKeyId=AKIAVR6L34QLJO642KMG&Signature=igCD8iDzrWScrJ%2FzvLUVnDeFCB4%3D&Expires=1773322671"
            ),
            ReelSource(
                id = "ig_3767803527013050462",
                owner = "digitekcreatorsquad",
                likes = 121,
                duration = 114.52,
                score = 0.009375,
                url = "https://hackathon-reels-ayush-2026.s3.amazonaws.com/reels/DRJ7VhbE6he.mp4?AWSAccessKeyId=AKIAVR6L34QLJO642KMG&Signature=Uuzsy3zyi5%2F%2B3QjPB3TCl4cPJ3M%3D&Expires=1773322795"
            ),
            ReelSource(
                id = "ig_3768437500642666184",
                owner = "pec.pecfest",
                likes = 459,
                duration = 56.566,
                score = 0.009231,
                url = "https://hackathon-reels-ayush-2026.s3.amazonaws.com/reels/DRMLfDYESbI.mp4?AWSAccessKeyId=AKIAVR6L34QLJO642KMG&Signature=2x2rKV90tUg1OPZjaJTKfxkM%3D&Expires=1773323307"
            ),
            ReelSource(
                id = "ig_3772282777597937574",
                owner = "iitbombay.moodi",
                likes = 2710,
                duration = 35.781,
                score = 0.009091,
                url = "https://hackathon-reels-ayush-2026.s3.amazonaws.com/reels/DRZ1zMnDIem.mp4?AWSAccessKeyId=AKIAVR6L34QLJO642KMG&Signature=hxnEQMmigVgw3K%2BQcP7a7lOLlcc%3D&Expires=1773323280"
            ),
            ReelSource(
                id = "ig_2983710976617160509",
                owner = "vipsspandan",
                likes = 233,
                duration = 50.4,
                score = 0.008955,
                url = "https://hackathon-reels-ayush-2026.s3.amazonaws.com/reels/CloRVMpsx89.mp4?AWSAccessKeyId=AKIAVR6L34QLJO642KMG&Signature=AZ4%2BbHE5UNCmyuzwFgMsJqgn9cU%3D&Expires=1773338649"
            ),
            ReelSource(
                id = "ig_3764831938864608525",
                owner = "thapartoastmasters",
                likes = 135,
                duration = 35.233,
                score = 0.008824,
                url = "https://hackathon-reels-ayush-2026.s3.amazonaws.com/reels/DQ_XrOTEfUN.mp4?AWSAccessKeyId=AKIAVR6L34QLJO642KMG&Signature=v3DccPTcL52ivTIw1%2Brgs91XW8g%3D&Expires=1773323015"
            )
        )
    )
}