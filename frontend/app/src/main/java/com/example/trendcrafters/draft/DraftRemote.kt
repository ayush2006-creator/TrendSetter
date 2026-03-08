// com/example/trendcrafters/draft/DraftRemote.kt

package com.example.trendcrafters.draft

import com.google.gson.annotations.SerializedName

data class DraftRemote(
    val id: Int,
    val content: DraftContentRemote,
    @SerializedName("created_at") val createdAt: String,
    @SerializedName("updated_at") val updatedAt: String
)

data class DraftContentRemote(
    val topic: String,
    val description: String,
    val platform: String?,
    @SerializedName("platform_icon") val platformIcon: String,
    val status: String,
    val tags: List<String>
)


data class DraftContent(
    val topic: String?,
    val description: String?,
    val platform: String?,
    val platformIcon: String?,   // ← was non-null, API can return null
    val status: String?,
    val tags: List<String>?
)
// ── Request bodies ─────────────────────────────────────
data class DraftCreateRequest(
    val content: DraftContent
)

data class DraftUpdateRequest(
    val content: DraftContent? = null   // all fields optional for PATCH-style PUT
)