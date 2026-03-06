package com.example.trendcrafters.Home





import com.google.gson.annotations.SerializedName

// ─────────────────────────────────────────────
// Raw Gson Response Models (matches your API JSON)
// ─────────────────────────────────────────────

data class ReelsQueryResponse(
    val answer: AnswerRemote,
    val sources: List<ReelSourceRemote>?
)

data class AnswerRemote(
    val analysis: AnalysisRemote,
    val patterns: List<String>,
    val ideas: List<IdeaRemote>,
    @SerializedName("best_fit_recommendation") val bestFitRecommendation: BestFitRemote?,
    @SerializedName("optimization_suggestion") val optimizationSuggestion: OptimizationRemote?
)

data class AnalysisRemote(
    @SerializedName("performance_drivers") val performanceDrivers: List<String>,
    @SerializedName("engagement_triggers") val engagementTriggers: List<String>
)

data class IdeaRemote(
    val concept: String?,
    val hook: String?,
    val structure: List<String>?,
    val emotion: String?,
    @SerializedName("why_it_works") val whyItWorks: String?
)

data class BestFitRemote(
    @SerializedName("best_idea_index") val bestIdeaIndex: Int?,
    val reason: String?
)

data class OptimizationRemote(
    val change: String?,
    val add: String?,
    val result: String?
)

data class ReelSourceRemote(
    val id: String?,
    val owner: String?,
    val likes: Int?,
    val duration: Double?,
    val score: Double?,
    val url: String?
)

// ─────────────────────────────────────────────
// Mapper: Remote → UI Model
// ─────────────────────────────────────────────

fun ReelsQueryResponse.toApiResult(): ApiResult {
    val ideas = answer.ideas.map { remote ->
        Idea(
            concept = remote.concept.orEmpty(),
            hook = remote.hook.orEmpty(),
            structure = remote.structure.orEmpty(),
            emotion = remote.emotion.orEmpty(),
            whyItWorks = remote.whyItWorks.orEmpty()
        )
    }

    val bestFitIndex = (answer.bestFitRecommendation?.bestIdeaIndex ?: 0)
        .coerceIn(0, (ideas.size - 1).coerceAtLeast(0))

    val opt = answer.optimizationSuggestion
    val optimization = if (opt != null &&
        (!opt.change.isNullOrBlank() || !opt.add.isNullOrBlank() || !opt.result.isNullOrBlank())
    ) {
        OptimizationSuggestion(
            change = opt.change.orEmpty(),
            add = opt.add.orEmpty(),
            result = opt.result.orEmpty()
        )
    } else null

    return ApiResult(
        analysis = Analysis(
            performanceDrivers = answer.analysis.performanceDrivers,
            engagementTriggers = answer.analysis.engagementTriggers
        ),
        patterns = answer.patterns,
        ideas = ideas,
        bestFitIndex = bestFitIndex,
        bestFitReason = answer.bestFitRecommendation?.reason.orEmpty()
            .ifBlank { "High engagement potential" },
        optimizationSuggestion = optimization,
        sources = sources?.map { s ->
            ReelSource(
                id = s.id.orEmpty(),
                owner = s.owner.orEmpty(),
                likes = s.likes ?: 0,
                duration = (s.duration ?: 0).toDouble(),
                score = s.score ?: 0.0,
                url = s.url.orEmpty()
            )
        } ?: emptyList()
    )
}