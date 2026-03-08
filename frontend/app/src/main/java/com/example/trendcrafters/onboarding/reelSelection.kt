package com.example.trendcrafters.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import kotlinx.coroutines.delay

// --- Theme Colors ---


// --- Data Models ---
data class ReelItem(
    val id: Int,
    val url: String,
    val description: String
)

// --- Mock Data ---
val sampleReels = listOf(
    ReelItem(1, "https://assets.mixkit.co/videos/preview/mixkit-people-having-a-bonfire-at-night-4555-large.mp4", "Lifestyle & Vlogging"),
    ReelItem(2, "https://assets.mixkit.co/videos/preview/mixkit-modern-hotel-building-near-the-beach-1203-large.mp4", "Travel & Luxury"),
    ReelItem(3, "https://assets.mixkit.co/videos/preview/mixkit-person-on-jet-ski-in-the-sea-4095-large.mp4", "Adventure & Sports")
)

/**
 * Main Entry Point for the Reel Selection Flow.
 * Connect this to your existing onboarding pipeline.
 *
 * @param onFinished Callback triggered when all reels are reviewed. Returns a map of Reel ID -> Liked(Boolean).
 */
@Composable
fun ReelOnboardingFlow(
    onFinished: (Map<Int, Boolean>) -> Unit
) {
    // State to track whether we are on the Intro screen or viewing reels
    var showIntro by remember { mutableStateOf(true) }

    // State to track current reel index
    var currentReelIndex by remember { mutableIntStateOf(0) }

    // State to collect user preferences
    val reelPreferences = remember { mutableStateMapOf<Int, Boolean>() }

    if (showIntro) {
        ReelIntroScreen(
            onStart = { showIntro = false }
        )
    } else {
        if (currentReelIndex < sampleReels.size) {
            // Using key to ensure player refreshes completely if needed, though LaunchedEffect in player handles URL changes
            key(currentReelIndex) {
                ReelSelectionScreen(
                    reel = sampleReels[currentReelIndex],
                    onResponse = { liked ->
                        // 1. Save preference
                        reelPreferences[sampleReels[currentReelIndex].id] = liked
                        // 2. Move to next reel
                        currentReelIndex++
                    }
                )
            }
        } else {
            // All reels reviewed, trigger completion
            LaunchedEffect(Unit) {
                onFinished(reelPreferences)
            }
            // Empty placeholder while transition happens
            Box(modifier = Modifier.fillMaxSize().background(Color.Black))
        }
    }
}

@Composable
fun ReelIntroScreen(onStart: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(DarkBackgroundStart, DarkBackgroundEnd)
                )),
        contentAlignment = Alignment.Center

    ) {
        LaunchedEffect(Unit){
            delay(6000)
            onStart()

        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Select the reel\nyou think suits\nyour type",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = TextWhite,
                textAlign = TextAlign.Center,
                lineHeight = 40.sp
            )

            Spacer(modifier = Modifier.height(40.dp))

        }
    }
}

@Composable
fun ReelSelectionScreen(
    reel: ReelItem,
    onResponse: (Boolean) -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        // Full Screen Video Player
        VideoPlayer(url = reel.url)

        // Overlay Gradient for Bottom Visibility
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.8f)),
                        startY = 0.6f.toFloat() // Gradient starts lower down
                    )
                )
        )

        // Controls at the Bottom
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 60.dp, start = 40.dp, end = 40.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Cross Button (Reject)
            IconButton(
                onClick = { onResponse(false) },
                modifier = Modifier
                    .size(70.dp)
                    .background(Color.White.copy(alpha = 0.2f), CircleShape)
                    .border(2.dp, Color.White.copy(alpha = 0.5f), CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Reject",
                    tint = Color.Red.copy(alpha = 0.8f),
                    modifier = Modifier.size(32.dp)
                )
            }

            // Tick Button (Accept)
            IconButton(
                onClick = { onResponse(true) },
                modifier = Modifier
                    .size(90.dp) // Slightly larger
                    .background(NeonPurple, CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Accept",
                    tint = TextWhite,
                    modifier = Modifier.size(48.dp)
                )
            }
        }
    }
}


@Preview
@Composable
fun PreviewReelFlow() {
    // Previewing the entire flow handler
    ReelOnboardingFlow(onFinished = {})
}