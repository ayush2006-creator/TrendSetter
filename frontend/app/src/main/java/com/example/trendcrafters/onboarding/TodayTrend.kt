package com.example.trendcrafters.onboarding

import android.annotation.SuppressLint
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.zIndex
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import kotlinx.coroutines.delay

data class VideoItem(
    val url: String,
    val id: Int
)

@Composable
fun TodayTrend(
    onContinue: () -> Unit = {}
) {
    val videos = remember {
        listOf(
            VideoItem("https://hackathon-reels-ayush-2026.s3.us-east-1.amazonaws.com/reels/B10N-x-Bc4-.mp4", 1),
            VideoItem("https://hackathon-reels-ayush-2026.s3.amazonaws.com/reels/DUUt9BXEgaK.mp4", 2),
            VideoItem("https://hackathon-reels-ayush-2026.s3.amazonaws.com/reels/DU0aCy1E5ye.mp4", 3),

            )
    }

    var currentRotation by remember { mutableStateOf(0) }

    LaunchedEffect(Unit) {
        while (true) {
            delay(3000)
            currentRotation = (currentRotation + 1) % 3
        }
    }

    // Use BoxWithConstraints to get screen dimensions for dynamic sizing
    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF0a1929),
                        Color(0xFF1a2f4a)
                    )
                )
            )
    ) {
        val screenWidth = maxWidth
        val screenHeight = maxHeight

        // Dynamic Card Size: Decreased size (~65% width, ~45% height)
        val cardWidth = screenWidth * 0.65f
        val cardHeight = screenHeight * 0.45f

        // Status Bar
        StatusBar()

        // Video Stack
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .offset(y = (-100).dp)
                .size(width = screenWidth, height = cardHeight)
        ) {
            videos.forEachIndexed { index, video ->
                VideoCard(
                    videoItem = video,
                    position = getCardPosition(index, currentRotation),
                    width = cardWidth,
                    height = cardHeight,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }

        // Floating Emojis (Placed AFTER video stack to render on top/above)
        FloatingEmoji(
            emoji = "😲",
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(start = screenWidth * 0.05f, top = screenHeight * 0.15f)
        )

        FloatingEmoji(
            emoji = "🥰",
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(start = screenWidth * 0.02f, bottom = screenHeight * 0.1f)
        )

        FloatingEmoji(
            emoji = "✨",
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = screenWidth * 0.05f, bottom = screenHeight * 0.05f)
        )

        // Content Section
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = 40.dp, bottom = 140.dp)
        ) {
            Text(
                text = "Go Trendy",
                fontSize = 42.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                lineHeight = 52.sp
            )
            Row {
                Text(
                    text = "with ",
                    fontSize = 42.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = "TrendSetter",
                    fontSize = 42.sp,
                    fontWeight = FontWeight.SemiBold,
                    fontStyle = FontStyle.Italic,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "A smarter, faster way to share life\nwith your circle.",
                fontSize = 16.sp,
                color = Color.White.copy(alpha = 0.7f),
                lineHeight = 22.sp
            )
        }

        // Continue Button
        ContinueButton(
            onClick = onContinue,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 60.dp)
        )
    }
}

@Composable
fun StatusBar() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "9:41",
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color.White
        )

        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            Text("📶", fontSize = 15.sp)
            Text("📡", fontSize = 15.sp)
            Text("🔋", fontSize = 15.sp)
        }
    }
}

@Composable
fun FloatingEmoji(
    emoji: String,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "emoji")
    val offsetY by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = -15f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "emojiFloat"
    )

    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 5f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "emojiRotate"
    )

    Text(
        text = emoji,
        fontSize = 48.sp,
        modifier = modifier
            .offset(y = offsetY.dp)
            .rotate(rotation)
    )
}

fun getCardPosition(index: Int, rotation: Int): Int {
    return ((index - rotation + 3) % 3)
}

@Composable
fun VideoCard(
    videoItem: VideoItem,
    position: Int,
    width: Dp,
    height: Dp,
    modifier: Modifier = Modifier
) {
    val transition = updateTransition(targetState = position, label = "cardTransition")

    val offsetY by transition.animateFloat(
        transitionSpec = {
            spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            )
        },
        label = "offsetY"
    ) { pos ->
        when (pos) {
            0 -> -20f
            1 -> -40f
            2 -> 20f
            else -> 0f
        }
    }

    val offsetX by transition.animateFloat(
        transitionSpec = {
            spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            )
        },
        label = "offsetX"
    ) { pos ->
        when (pos) {
            0 -> -90f
            1 -> 90f
            2 -> 0f
            else -> 0f
        }
    }

    val rotation by transition.animateFloat(
        transitionSpec = {
            spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            )
        },
        label = "rotation"
    ) { pos ->
        when (pos) {
            0 -> -25f
            1 -> 25f
            2 -> -6f
            else -> 0f
        }
    }

    val scale by transition.animateFloat(
        transitionSpec = {
            spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            )
        },
        label = "scale"
    ) { pos ->
        when (pos) {
            0 -> 0.85f
            1 -> 0.9f
            2 -> 1f
            else -> 1f
        }
    }

    val alpha by transition.animateFloat(
        transitionSpec = {
            spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            )
        },
        label = "alpha"
    ) { pos ->
        when (pos) {
            0 -> 0.8f
            1 -> 0.85f
            2 -> 1f
            else -> 1f
        }
    }

    val zIndex by transition.animateFloat(
        transitionSpec = { tween(durationMillis = 300) },
        label = "zIndex"
    ) { pos ->
        when (pos) {
            0 -> 1f
            1 -> 2f
            2 -> 3f
            else -> 0f
        }
    }

    Box(
        modifier = modifier
            .zIndex(zIndex)
            .graphicsLayer {
                translationY = offsetY
                translationX = offsetX
                rotationZ = rotation
                scaleX = scale
                scaleY = scale
                this.alpha = alpha
            }
            .width(width)
            .height(height)
            .clip(RoundedCornerShape(20.dp))
    ) {
        VideoPlayer(url = videoItem.url)
    }
}

@SuppressLint("UnsafeOptInUsageError")
@Composable
fun VideoPlayer(url: String) {
    val context = LocalContext.current
    val exoPlayer = remember {
        ExoPlayer.Builder(context).build().apply {
            setMediaItem(MediaItem.fromUri(url))
            prepare()
            playWhenReady = true
            repeatMode = ExoPlayer.REPEAT_MODE_ALL
            volume = 0f // Muted
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            exoPlayer.release()
        }
    }

    AndroidView(
        factory = { ctx ->
            PlayerView(ctx).apply {
                player = exoPlayer
                useController = false
                resizeMode = androidx.media3.ui.AspectRatioFrameLayout.RESIZE_MODE_ZOOM
            }
        },
        modifier = Modifier.fillMaxSize()
    )
}

@Composable
fun CurvedArrow(modifier: Modifier = Modifier) {
    Text(
        text = "↘",
        fontSize = 60.sp,
        color = Color.White.copy(alpha = 0.4f),
        modifier = modifier
    )
}

@Composable
fun PaginationDots(
    totalDots: Int,
    currentPage: Int,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        repeat(totalDots) { index ->
            Box(
                modifier = Modifier
                    .height(8.dp)
                    .width(if (index == currentPage) 24.dp else 8.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(
                        if (index == currentPage) Color(0xFF007AFF)
                        else Color.White.copy(alpha = 0.3f)
                    )
            )
        }
    }
}

@Composable
fun ContinueButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier,
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFF007AFF)
        ),
        shape = RoundedCornerShape(30.dp),
        contentPadding = PaddingValues(horizontal = 60.dp, vertical = 16.dp)
    ) {
        Text(
            text = "Continue",
            fontSize = 17.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color.White
        )
    }
}