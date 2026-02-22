package com.example.trendcrafters.pytrends

import android.util.Log
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query
import kotlin.random.Random

data class HashtagChip(
    val tag: String,
    val color: Color,
    val nudgeX: Dp,
    val nudgeY: Dp,
    val rotation: Float,
    val scale: Float
)

data class TrendKeywordResponse(
    val keyword: String,
    val score: Int,
    val fetched_at: String
)

interface TrendApiService {

    @POST("trends/fetch")
    suspend fun fetchAndStore(
        @Query("niche") niche: String,
        @Query("region") region: String = "IN"
    ): Any  // just need it to complete, don't care about response shape

    @GET("trends/keywords")
    suspend fun getKeywords(
        @Query("niche") niche: String,
        @Query("region") region: String = "IN",
        @Query("limit") limit: Int = 12
    ): List<TrendKeywordResponse>
}
class TrendViewModel : ViewModel() {
    private val _chips = MutableStateFlow<List<HashtagChip>>(emptyList())
    val chips: StateFlow<List<HashtagChip>> = _chips

    // Use 10.0.2.2 for Emulator localhost or your machine's local IP for physical device
    private val api = Retrofit.Builder()
        .baseUrl("https://trendbackend.onrender.com")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(TrendApiService::class.java)

    fun fetchTrends(niche: String) {
        viewModelScope.launch {
            try {
                Log.d("TrendVM", "▶ POSTing to fetch & store trends for niche=$niche")
                api.fetchAndStore(niche = niche)  // ← Step 1: trigger backend to fetch

                Log.d("TrendVM", "▶ GETting keywords for niche=$niche")
                val response = api.getKeywords(niche = niche)  // ← Step 2: read them
                Log.d("TrendVM", "✅ Got ${response.size} items: $response")

                _chips.value = response.map { item ->
                    HashtagChip(
                        tag = "#${item.keyword.replace(" ", "")}",
                        color = listOf(Color(0xFFE879F9), Color(0xFF67E8F9), Color(0xFFA5F3FC)).random(),
                        nudgeX = (Random.nextInt(-4, 5)).dp,
                        nudgeY = (Random.nextInt(-4, 5)).dp,
                        rotation = Random.nextInt(-5, 6).toFloat(),
                        scale = Random.nextFloat() * (1.08f - 0.92f) + 0.92f
                    )
                }
            } catch (e: Exception) {
                Log.e("TrendVM", "❌ Error: ${e::class.simpleName} - ${e.message}")
                e.printStackTrace()
            }
        }
    }
}