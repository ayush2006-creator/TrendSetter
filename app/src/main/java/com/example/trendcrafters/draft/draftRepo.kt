package com.example.trendcrafters.draft

import com.example.trendcrafters.Auth.TokenManager
import com.example.trendcrafters.Home.Draft
import com.example.trendcrafters.Home.DraftStatus

// DraftRepository.kt
class DraftRepository(private val apiService: DraftApiService) {

    suspend fun fetchMyDrafts(): List<Draft> {
        val token = TokenManager.bearerHeader()
        val response = apiService.getDrafts("Bearer $token")
        return response.map { remote ->
            Draft(
                id = remote.id.toString(),
                topic = remote.content.topic,
                description = remote.content.description,
                platform = remote.content.platform,
                platformIcon = remote.content.platformIcon,
                createdDate = remote.createdAt, // You can add a formatter here
                updatedDate = remote.updatedAt,
                status = DraftStatus.valueOf(remote.content.status),
                tags = remote.content.tags
            )
        }
    }
}