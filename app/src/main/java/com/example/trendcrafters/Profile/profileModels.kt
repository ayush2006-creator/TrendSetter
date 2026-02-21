package com.example.trendcrafters.Profile


import com.google.gson.annotations.SerializedName

// ─── Request Model ────────────────────────────────────────────────────────────

data class ProfileCreateUpdateRequest(
    @SerializedName("display_name")       val displayName: String,
    @SerializedName("creator_type")       val creatorType: String,
    @SerializedName("organization_type")  val organizationType: String? = null,
    @SerializedName("experience_level")   val experienceLevel: String,
    @SerializedName("audience_type")      val audienceType: String,
    @SerializedName("platform")           val platform: String,
    @SerializedName("goals")              val goals: String? = null,
    @SerializedName("niches")             val niches: List<String>
)

// ─── Response Models ──────────────────────────────────────────────────────────

data class ProfileResponse(
    @SerializedName("id")                 val id: Int,
    @SerializedName("user_id")            val userId: Int,
    @SerializedName("display_name")       val displayName: String,
    @SerializedName("creator_type")       val creatorType: String?,
    @SerializedName("organization_type")  val organizationType: String?,
    @SerializedName("experience_level")   val experienceLevel: String?,
    @SerializedName("audience_type")      val audienceType: String?,
    @SerializedName("platform")           val platform: String?,
    @SerializedName("goals")              val goals: String?,
    @SerializedName("niches")             val niches: List<NicheResponse>
)

data class NicheResponse(
    @SerializedName("id")   val id: Int,
    @SerializedName("name") val name: String
)

data class ProfileSaveResponse(
    @SerializedName("message") val message: String
)

// ─── Helper: map ProfileResponse → OnboardingProfile used by the UI ──────────

fun ProfileResponse.toOnboardingProfile(): com.example.trendcrafters.Home.OnboardingProfile {
    return com.example.trendcrafters.Home.OnboardingProfile(
        creatingFor   = listOf(creatorType ?: "Personal Brand"),
        platforms     = listOf(platform ?: "Instagram Reels"),
        experience    = experienceLevel ?: "Just starting",
        followerRange = audienceType ?: "0 – 1K",
        maxViews      = goals ?: "Unknown",
        breakthrough  = "Still experimenting"
    )
}