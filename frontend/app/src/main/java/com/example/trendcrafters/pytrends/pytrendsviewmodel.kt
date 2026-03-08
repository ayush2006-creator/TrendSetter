package com.example.trendcrafters.pytrends

import android.util.Log
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.trendcrafters.ApiService.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
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

// com/example/trendcrafters/pytrends/TrendViewModel.kt


class TrendViewModel : ViewModel() {

    private val api = RetrofitClient.apiInterface

    private val _chips = MutableStateFlow<List<HashtagChip>>(emptyList())
    val chips: StateFlow<List<HashtagChip>> = _chips

    fun fetchTrends(niche: String) {
        viewModelScope.launch {
            try {
                Log.d("TrendVM", "▶ Fetching trends for niche=$niche")
                api.fetchAndStoreTrends(niche = niche)
                val response = api.getTrendKeywords(niche = niche)
                Log.d("TrendVM", "✅ Got ${response.size} items")

                _chips.value = response.map { item ->
                    HashtagChip(
                        tag = "#${item.keyword.replace(" ", "")}",
                        color = listOf(Color(0xFFE879F9), Color(0xFF67E8F9), Color(0xFFA5F3FC)).random(),
                        nudgeX = Random.nextInt(-4, 5).dp,
                        nudgeY = Random.nextInt(-4, 5).dp,
                        rotation = Random.nextInt(-5, 6).toFloat(),
                        scale = Random.nextFloat() * 0.16f + 0.92f
                    )
                }
            } catch (e: Exception) {
                Log.e("TrendVM", "❌ ${e::class.simpleName}: ${e.message}")
            }
        }
    }
}