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
            VideoItem("https://scontent-iad3-1.cdninstagram.com/o1/v/t16/f2/m86/AQO_Uh5lahMlttjjD7xMSUgxVEoPc2jF7MT17GpWWk7tAtFix5vFvxUU1QcWWHl7kluwOavEhDFrj1xma_puJDpJvv0AWlj-oFU5WZ4.mp4?stp=dst-mp4&efg=eyJxZV9ncm91cHMiOiJbXCJpZ193ZWJfZGVsaXZlcnlfdnRzX290ZlwiXSIsInZlbmNvZGVfdGFnIjoidnRzX3ZvZF91cmxnZW4uY2xpcHMuYzIuNzIwLmJhc2VsaW5lIn0&_nc_cat=108&vs=1901863210754204_1203522180&_nc_vs=HBksFQIYUmlnX3hwdl9yZWVsc19wZXJtYW5lbnRfc3JfcHJvZC82MTQwODFEQzg0RjU0QzU5RUEwOUM2MEFCMUZDRkVCOF92aWRlb19kYXNoaW5pdC5tcDQVAALIARIAFQIYOnBhc3N0aHJvdWdoX2V2ZXJzdG9yZS9HRTRxM2lKdjg0d2k0ZlVIQU03YjFVc2lGQU1KYnN0VEFRQUYVAgLIARIAKAAYABsAFQAAJszFw47fq5ZAFQIoAkMzLBdAPqAAAAAAABgSZGFzaF9iYXNlbGluZV8xX3YxEQB1%2Fgdl5p0BAA%3D%3D&_nc_rid=b8ba62e107&ccb=9-4&oh=00_AfsjHoftzSuBwepsJ51dn-8O2jiviaA2JzVitxGTM9wIaw&oe=699C6C5B&_nc_sid=10d13b", 1),
            VideoItem("https://scontent-lax3-1.cdninstagram.com/o1/v/t16/f2/m69/AQPDaK57iJduyDhMDmKycGEpSA4cOEVYNiBFbxQXqItYXkE_CSmL8YxtAgln7fU9JfURti2hTikgkD1cpKwRmb3i.mp4?strext=1&_nc_cat=110&_nc_sid=5e9851&_nc_ht=scontent-lax3-1.cdninstagram.com&_nc_ohc=p8UZJUGfjpwQ7kNvwH4zP8-&efg=eyJ2ZW5jb2RlX3RhZyI6Inhwdl9wcm9ncmVzc2l2ZS5JTlNUQUdSQU0uSUdUVi5DMy43MjAuZGFzaF9iYXNlbGluZV8xX3YxIiwieHB2X2Fzc2V0X2lkIjo2MzY2MDcwNTc0NTQ4ODYsImFzc2V0X2FnZV9kYXlzIjoxNDQ5LCJ2aV91c2VjYXNlX2lkIjoxMDE0NSwiZHVyYXRpb25fcyI6NiwidXJsZ2VuX3NvdXJjZSI6Ind3dyJ9&ccb=17-1&_nc_gid=6y0CyDe2hmUX_J5WhcwipQ&_nc_zt=28&vs=a335b3d984e3c973&_nc_vs=HBkcFQIYOnBhc3N0aHJvdWdoX2V2ZXJzdG9yZS9HQ0VkWkJCSUNVUXUyV2dDQUI5dEFpMlNaODh0YnZWQkFBQUYVAALIARIAKAAYABsCiAd1c2Vfb2lsATEScHJvZ3Jlc3NpdmVfcmVjaXBlATEVAAAmzJyrmLS_oQIVAigCQzMsF0AZMzMzMzMzGBJkYXNoX2Jhc2VsaW5lXzFfdjERAHXsB2XCngEA&oh=00_AfszuZwV2GHdJpgK7pNPYUMwzM3ocwngzRRc0hQ_IYTI2Q&oe=69A06146", 2),
            VideoItem("https://scontent-lax3-1.cdninstagram.com/o1/v/t16/f2/m69/AQNOiBnGxHzdXbcJ7g2aOm4dSCZsXycYoUKY9pOWyZGI_HsvXcFwIy6-dFO2BJt87RXGoiqxayyZxQAlGHaNppoL.mp4?strext=1&_nc_cat=110&_nc_sid=5e9851&_nc_ht=scontent-lax3-1.cdninstagram.com&_nc_ohc=4kLVjWIy8tIQ7kNvwFsMN7_&efg=eyJ2ZW5jb2RlX3RhZyI6Inhwdl9wcm9ncmVzc2l2ZS5JTlNUQUdSQU0uSUdUVi5DMy4xMjgwLmRhc2hfYmFzZWxpbmVfMV92MSIsInhwdl9hc3NldF9pZCI6NTY4NTM0Mzk3Nzc5Mzc3LCJhc3NldF9hZ2VfZGF5cyI6MTM5NiwidmlfdXNlY2FzZV9pZCI6MTAxNDUsImR1cmF0aW9uX3MiOjE5LCJ1cmxnZW5fc291cmNlIjoid3d3In0%3D&ccb=17-1&_nc_gid=6y0CyDe2hmUX_J5WhcwipQ&_nc_zt=28&vs=e95a628973666755&_nc_vs=HBksFQIYOnBhc3N0aHJvdWdoX2V2ZXJzdG9yZS9HSGdscUJETXdGeFk5MmdCQUpVSHFnUV9rQWdkYnZWQkFBQUYVAALIARIAFQIYOnBhc3N0aHJvdWdoX2V2ZXJzdG9yZS9HQjc0b1JESEwza2tTeGtJQUUydlp3UWViSFZOYnZWQkFBQUYVAgLIARIAKAAYABsCiAd1c2Vfb2lsATEScHJvZ3Jlc3NpdmVfcmVjaXBlATEVAAAm4vaTrYfFggIVAigCQzMsF0AzszMzMzMzGBJkYXNoX2Jhc2VsaW5lXzFfdjERAHXsB2XCngEA&oh=00_Afs_BvKTEKTBSMHw08Z0dxYQoqKGxf3CYe1eZcbz8L0wJQ&oe=69A050CA", 3),

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
        // We place this BEFORE emojis in the code if we want emojis 'behind',
        // OR we place it HERE and emojis AFTER if we want emojis 'on top'.
        // Request: "emoji above video" -> We place Video Stack first, then Emojis.
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .offset(y = (-100).dp) // Adjusted offset slightly since cards are smaller
                .size(width = screenWidth, height = cardHeight) // Container matches dynamic height
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
        // Positions are now relative to screen size (percentages)
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

        // Arrow decoration


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

    // Increased translation values slightly for larger cards
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
            0 -> -20f  // Back card (left)
            1 -> -40f  // Middle card (right)
            2 -> 20f   // Front card
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
            0 -> -90f // Shift left (increased from 70f for better visibility with angle)
            1 -> 90f  // Shift right (increased from 70f)
            2 -> 0f   // Center
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
            0 -> -25f // Increased angle
            1 -> 25f  // Increased angle
            2 -> -6f  // Slight angle for center
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

    // Explicit Z-Index to ensure correct stacking order
    val zIndex by transition.animateFloat(
        transitionSpec = { tween(durationMillis = 300) }, // Faster z-index switch
        label = "zIndex"
    ) { pos ->
        when (pos) {
            0 -> 1f
            1 -> 2f
            2 -> 3f // Front card on top
            else -> 0f
        }
    }

    Box(
        modifier = modifier
            .zIndex(zIndex) // Apply Z-Index
            .graphicsLayer {
                translationY = offsetY
                translationX = offsetX
                rotationZ = rotation
                scaleX = scale
                scaleY = scale
                this.alpha = alpha
            }
            .width(width)   // Dynamic Width
            .height(height) // Dynamic Height
            .clip(RoundedCornerShape(20.dp))
    ) {
        VideoPlayer(url = videoItem.url)

        // Play indicator
        PlayIndicator(
            modifier = Modifier.align(Alignment.Center)
        )
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
fun PlayIndicator(modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    Box(
        modifier = modifier
            .size(60.dp) // Slightly larger play button for larger cards
            .scale(scale)
            .background(Color.White.copy(alpha = 0.9f), CircleShape),
        contentAlignment = Alignment.Center
    ) {
        // Play triangle
        Canvas(modifier = Modifier.size(24.dp)) {
            val path = androidx.compose.ui.graphics.Path().apply {
                moveTo(0f, 0f)
                lineTo(size.width, size.height / 2)
                lineTo(0f, size.height)
                close()
            }
            drawPath(
                path = path,
                color = Color(0xFF0a1929)
            )
        }
    }
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