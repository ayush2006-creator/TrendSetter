package com.example.trendcrafters.Home

import com.example.trendcrafters.R
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ripple
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.min
import androidx.compose.ui.zIndex
import androidx.navigation.NavHostController
import com.example.trendcrafters.Auth.BackgroundGradientEnd
import com.example.trendcrafters.Auth.NeonPurple
import com.example.trendcrafters.assets.LottieAnimationView
import com.example.trendcrafters.onboarding.VideoCard
import com.example.trendcrafters.onboarding.VideoItem
import com.example.trendcrafters.onboarding.getCardPosition
import kotlinx.coroutines.delay

// ── Configuration & Data ──────────────────────────────────────────────────────

private data class HashtagChip(
    val tag: String,
    val color: Color,
    val nudgeX: Dp,
    val nudgeY: Dp,
    val rotation: Float,
    val scale: Float
)

private data class BottomNavItem(
    val title: String,
    val icon: ImageVector
)

private val chipData = listOf(
    HashtagChip("#TrendingN",       Color(0xFFE879F9), (-4).dp,  2.dp,  -3f, 1.05f),
    HashtagChip("#ContentCr",       Color(0xFF67E8F9),  2.dp, (-3).dp,  2f, 0.95f),
    HashtagChip("#ViralVideo",      Color(0xFFA5F3FC), (-4).dp,  4.dp,  -5f, 1.00f),
    HashtagChip("#ReelsLife",       Color(0xFFE879F9),  4.dp, (-2).dp,  4f, 1.08f),
    HashtagChip("#CreatorMo",       Color(0xFFA5F3FC),  4.dp, (-4).dp,  6f, 1.03f),
    HashtagChip("#VideoMark",       Color(0xFFE879F9), (-3).dp,  2.dp,  -4f, 0.97f),
    HashtagChip("#ForYouPag",       Color(0xFF67E8F9), (-2).dp,  4.dp,  -2f, 0.92f),
    HashtagChip("#ShortForm",       Color(0xFF67E8F9),  2.dp, (-4).dp,  3f, 1.06f),
    HashtagChip("#GrowYour",        Color(0xFFA5F3FC), (-4).dp,  3.dp,  -6f, 0.94f),
    HashtagChip("#DigitalCre",      Color(0xFFE879F9),  3.dp, (-2).dp,  5f, 1.01f),
)

private val navItems = listOf(
    BottomNavItem("Home", Icons.Default.Home),
    BottomNavItem("Drafts", Icons.Default.Edit),
    BottomNavItem("Chat", Icons.Default.Email),
    BottomNavItem("Profile", Icons.Default.Person)
)

// ── Main Screen ──────────────────────────────────────────────────────────────

@Preview
@Composable
fun Home() {
    var selectedNavIndex by remember { mutableStateOf(0) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(NeonPurple, BackgroundGradientEnd)
                )
            )
    ) {
        Scaffold(
            containerColor = Color.Transparent,
            bottomBar = {
                CustomBottomNavBar(
                    selectedIndex = selectedNavIndex,
                    onItemSelected = { selectedNavIndex = it }
                )
            }
        ) { innerPadding ->
            when (selectedNavIndex) {
                0 -> HomeContent(modifier = Modifier.padding(innerPadding))
                1 -> DraftContent(modifier = Modifier.padding(innerPadding))
                2 -> ChatContent(modifier = Modifier.padding(innerPadding))
                3 -> ProfileContent(modifier = Modifier.padding(innerPadding))
                else -> HomeContent(modifier = Modifier.padding(innerPadding))
            }
        }
    }
}
// ── Screen Content ───────────────────────────────────────────────────────────

@Composable
private fun HomeContent(modifier: Modifier = Modifier) {
    val videos = remember {
        listOf(
            VideoItem("https://your-video-url-1.mp4", 1),
            VideoItem("https://your-video-url-2.mp4", 2),
            VideoItem("https://your-video-url-3.mp4", 3)
        )
    }

    var currentRotation by remember { mutableStateOf(0) }
    var selectedVideoIndex by remember { mutableStateOf<Int?>(null) }
    var isPlayerClicked by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        while (true) {
            delay(3000)
            currentRotation = (currentRotation + 1) % 3
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // TOP SECTION: Dynamically sized flexible space for Cards & Player
            BoxWithConstraints(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                // Determine responsive sizes using min constraint for landscape/portrait safety
                val safeBaseSize = min(maxWidth, maxHeight)
                val cardWidth = safeBaseSize * 0.65f
                val cardHeight = cardWidth * 1.15f
                val playerSize = safeBaseSize * 0.45f

                // Center container that holds both the cards and the overlapping player
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    // Container for the cards, slightly offset to the left
                    Box(
                        modifier = Modifier
                            .offset(x = (-20).dp)
                            .size(width = cardWidth * 1.2f, height = cardHeight * 1.2f)
                            .zIndex(1f)
                    ) {
                        VideoCardStack(
                            videos = videos,
                            currentRotation = currentRotation,
                            cardWidth = cardWidth,
                            cardHeight = cardHeight,
                            onVideoSelected = { selectedVideoIndex = it },
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }

                    // Container for the Lottie Player, positioned on the right edge
                    LottiePlayerButton(
                        playerSize = playerSize,
                        isClicked = isPlayerClicked,
                        onClick = { isPlayerClicked = !isPlayerClicked },
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .offset(x = (-16).dp, y = 110.dp) // Moved further downward
                            .zIndex(3f)
                    )
                }
            }

            // BOTTOM SECTION: Hashtags wrapped nicely at the bottom
            HashtagSection(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 20.dp)
            )
        }

        // Floating Snackbar Feedback
        selectedVideoIndex?.let { idx ->
            LaunchedEffect(idx) {
                delay(1800)
                selectedVideoIndex = null
            }
            SnackbarFeedback(
                message = "Opening video ${idx + 1}…",
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 16.dp)
            )
        }
    }
}

// ── Extracted Components ─────────────────────────────────────────────────────

@Composable
fun CustomBottomNavBar(
    selectedIndex: Int,
    onItemSelected: (Int) -> Unit
) {
    NavigationBar(
        containerColor = Color.Black.copy(alpha = 0.20f),
        contentColor = Color.White,
        tonalElevation = 0.dp,
        modifier = Modifier
            .navigationBarsPadding() // Ensures it sits properly above system gestures
            .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
            .border(
                width = 1.dp,
                color = Color.White.copy(alpha = 0.15f),
                shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
            )
    ) {
        navItems.forEachIndexed { index, item ->
            val isSelected = selectedIndex == index
            NavigationBarItem(
                selected = isSelected,
                onClick = { onItemSelected(index) },
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.title,
                        modifier = Modifier.size(26.dp)
                    )
                },
                label = {
                    Text(
                        text = item.title,
                        fontSize = 10.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color(0xFFE879F9),
                    selectedTextColor = Color(0xFFE879F9),
                    unselectedIconColor = Color.White.copy(alpha = 0.6f),
                    unselectedTextColor = Color.White.copy(alpha = 0.6f),
                    indicatorColor = Color.White.copy(alpha = 0.15f)
                )
            )
        }
    }
}

@Composable
private fun VideoCardStack(
    videos: List<VideoItem>,
    currentRotation: Int,
    cardWidth: Dp,
    cardHeight: Dp,
    onVideoSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier) {
        videos.forEachIndexed { index, video ->
            val interactionSource = remember { MutableInteractionSource() }
            VideoCard(
                videoItem = video,
                position  = getCardPosition(index, currentRotation),
                width     = cardWidth,
                height    = cardHeight,
                modifier  = Modifier
                    .align(Alignment.Center)
                    .clickable(
                        interactionSource = interactionSource,
                        indication = ripple(bounded = true, color = Color(0xFFE879F9))
                    ) { onVideoSelected(index) }
            )
        }
    }
}

@Composable
private fun LottiePlayerButton(
    playerSize: Dp,
    isClicked: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val playerScale by animateFloatAsState(
        targetValue = if (isClicked) 0.84f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness    = Spring.StiffnessMedium
        ),
        label = "playerScale"
    )
    val interactionSource = remember { MutableInteractionSource() }

    Box(
        modifier = modifier
            .size(playerSize)
            .clickable(
                interactionSource = interactionSource,
                indication        = null,
                onClick           = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        // Soft radial halo behind the player
        Box(
            modifier = Modifier
                .size(playerSize * playerScale)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color(0xFFE879F9).copy(alpha = 0.25f),
                            Color.Transparent
                        )
                    ),
                    shape = RoundedCornerShape(50)
                )
        )

        LottieAnimationView(
            resId    = R.raw.mp4player,
            modifier = Modifier
                .size(playerSize * playerScale)
        )
    }
}

@Composable
private fun HashtagSection(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(24.dp))
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color.White.copy(alpha = 0.15f),
                        Color.White.copy(alpha = 0.05f)
                    )
                )
            )
            .border(
                width  = 1.dp,
                brush  = Brush.verticalGradient(
                    colors = listOf(
                        Color.White.copy(alpha = 0.40f),
                        Color.White.copy(alpha = 0.10f)
                    )
                ),
                shape  = RoundedCornerShape(24.dp)
            )
            .padding(horizontal = 16.dp, vertical = 22.dp)
    ) {
        HashtagGridPanel()
    }
}

@Composable
private fun SnackbarFeedback(
    message: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .zIndex(5f)
            .clip(RoundedCornerShape(50))
            .background(Color.Black.copy(alpha = 0.45f))
            .border(1.dp, Color.White.copy(alpha = 0.20f), RoundedCornerShape(50))
            .padding(horizontal = 22.dp, vertical = 10.dp)
    ) {
        Text(
            text       = message,
            color      = Color.White,
            fontSize   = 13.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

// ── Shared Subcomponents (Hashtag Elements) ──────────────────────────────────

@Composable
private fun HashtagChipItem(chip: HashtagChip, modifier: Modifier = Modifier) {
    var pressed by remember { mutableStateOf(false) }
    val interactionSource = remember { MutableInteractionSource() }
    val animScale by animateFloatAsState(
        targetValue = if (pressed) chip.scale * 0.88f else chip.scale,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "chipScale"
    )

    Box(
        modifier = modifier
            .offset(x = chip.nudgeX, y = chip.nudgeY)
            .rotate(chip.rotation)
            .scale(animScale)
            .clip(RoundedCornerShape(50))
            .background(
                if (pressed) chip.color.copy(alpha = 0.28f)
                else Color.White.copy(alpha = 0.08f)
            )
            .border(
                width = 1.dp,
                color = chip.color.copy(alpha = if (pressed) 0.85f else 0.42f),
                shape = RoundedCornerShape(50)
            )
            .clickable(
                interactionSource = interactionSource,
                indication = ripple(bounded = true, color = chip.color)
            ) { pressed = !pressed }
            .padding(horizontal = 6.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = chip.tag,
            color = if (pressed) chip.color else chip.color.copy(alpha = 0.90f),
            fontSize = 11.sp,
            fontWeight = if (pressed) FontWeight.Bold else FontWeight.Medium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun HashtagGridPanel(modifier: Modifier = Modifier) {
    val columns = 3
    val rows = (chipData.size + columns - 1) / columns

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "CREATE & TREND",
            color = Color.White.copy(alpha = 0.70f),
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 2.sp,
            modifier = Modifier.padding(start = 8.dp, bottom = 4.dp)
        )
        for (row in 0 until rows) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    // Adds staggered zigzag look per row safely
                    .padding(horizontal = if (row % 2 == 1) 12.dp else 0.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                for (col in 0 until columns) {
                    val idx = row * columns + col
                    if (idx >= chipData.size) {
                        Spacer(modifier = Modifier.weight(1f))
                    } else {
                        // Using weight(1f) dynamically allocates width so they NEVER overflow!
                        HashtagChipItem(
                            chip = chipData[idx],
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
    }
}