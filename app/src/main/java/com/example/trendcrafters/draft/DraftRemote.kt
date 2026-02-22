package com.example.trendcrafters.draft
// DraftApiService.kt
import retrofit2.http.*
// DraftRemote.kt
import com.google.gson.annotations.SerializedName

data class DraftResponse(
    val id: Int,
    @SerializedName("user_id") val userId: Int,
    val content: DraftContentDto, // Maps to the JSON column in FastAPI
    @SerializedName("created_at") val createdAt: String,
    @SerializedName("updated_at") val updatedAt: String
)

data class DraftContentDto(
    val topic: String,
    val description: String,
    val platform: String,
    val platformIcon: String,
    val status: String,
    val tags: List<String>
)

data class DraftCreateRequest(
    val content: DraftContentDto
)



interface DraftApiService {
    @GET("drafts/")
    suspend fun getDrafts(@Header("Authorization") token: String): List<DraftResponse>

    @POST("drafts/")
    suspend fun createDraft(
        @Header("Authorization") token: String,
        @Body request: DraftCreateRequest
    ): DraftResponse

    @DELETE("drafts/{draft_id}")
    suspend fun deleteDraft(
        @Header("Authorization") token: String,
        @Path("draft_id") id: Int
    )
}