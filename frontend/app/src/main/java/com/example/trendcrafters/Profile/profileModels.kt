package com.example.trendcrafters.Profile


import com.example.trendcrafters.Home.OnboardingProfile
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
    @SerializedName("niches")             val niches: List<String>  // ✅ Fixed
)

data class NicheResponse(
    @SerializedName("id")   val id: Int,
    @SerializedName("name") val name: String
)

data class ProfileSaveResponse(
    @SerializedName("message") val message: String
)

// ─── Helper: map ProfileResponse → OnboardingProfile used by the UI ──────────

fun ProfileResponse?.toOnboardingProfile(): OnboardingProfile {
    // We use "this?." to handle cases where the server response itself is null
    return OnboardingProfile(
        display_name = this?.displayName ?: "Creator",
        creator_type = this?.creatorType ?: "Individual",
        organization_type = this?.organizationType, // Optional field
        experience_level = this?.experienceLevel ?: "Just starting",
        platform = this?.platform ?: "Instagram",
        audience_type = this?.audienceType ?: "General",

        goals = this?.goals ?: "Still experimenting",
        // Safely map the niche names, or return an empty list if null
        niches = this?.niches ?: emptyList()
    )
}