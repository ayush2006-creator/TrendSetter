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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.trendcrafters.Auth.BackgroundGradientEnd
import com.example.trendcrafters.Auth.NeonPurple
import com.example.trendcrafters.assets.LottieAnimationView
import com.example.trendcrafters.draft.DraftViewModel
import com.example.trendcrafters.onboarding.VideoCard
import com.example.trendcrafters.onboarding.VideoItem
import com.example.trendcrafters.onboarding.getCardPosition
import com.example.trendcrafters.pytrends.HashtagChip   // ← use ViewModel's version
import com.example.trendcrafters.pytrends.TrendViewModel
import kotlinx.coroutines.delay

// ── Configuration & Data ──────────────────────────────────────────────────────

// REMOVED: local HashtagChip data class (use the one from pytrends package)
// REMOVED: local TrendKeywordResponse data class (use the one from pytrends package)
// REMOVED: chipData static list (now comes from API)

private data class BottomNavItem(
    val title: String,
    val icon: ImageVector
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
        val viewModel: DraftViewModel = viewModel()
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
                1 -> DraftContent(viewModel)
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
            VideoItem("https://scontent-atl3-1.cdninstagram.com/o1/v/t16/f2/m84/AQMBN5bRQfGJUCKa47HzZKHC3hJXDpoQ1Zsi2hPNiACeKOIT3xlDbNbRYw2cfVxjql3doZA123duezoTc1kDEgmagT9N6ojpnnJIA1A.mp4?_nc_cat=100&_nc_sid=5e9851&_nc_ht=scontent-atl3-1.cdninstagram.com&_nc_ohc=82wGZ8b7GLAQ7kNvwFbmnkg&efg=eyJ2ZW5jb2RlX3RhZyI6Inhwdl9wcm9ncmVzc2l2ZS5JTlNUQUdSQU0uSUdUVi5DMy4xMjgwLmRhc2hfYmFzZWxpbmVfMV92MSIsInhwdl9hc3NldF9pZCI6MTc5MzkyOTgwNzcyMzU2OCwiYXNzZXRfYWdlX2RheXMiOjE5ODEsInZpX3VzZWNhc2VfaWQiOjEwMTQ1LCJkdXJhdGlvbl9zIjoxMjUsInVybGdlbl9zb3VyY2UiOiJ3d3cifQ%3D%3D&ccb=17-1&vs=4815fe30c67eb001&_nc_vs=HBksFQIYTGlnX2JhY2tmaWxsX3RpbWVsaW5lX3ZvZC82NTRGQ0M1QzFCMUU4RjYyNkFCRTYwNDJCNThGNUI5MV92aWRlb19kYXNoaW5pdC5tcDQVAALIARIAFQIYOnBhc3N0aHJvdWdoX2V2ZXJzdG9yZS9HSzFPdUJaZVQtMFhZeTRCQVBkaWExRllmdTBnYnBrd0FBQUYVAgLIARIAKAAYABsCiAd1c2Vfb2lsATEScHJvZ3Jlc3NpdmVfcmVjaXBlATEVAAAm4PCHz7rkrwYVAigCQzMsF0BfW7ZFocrBGBJkYXNoX2Jhc2VsaW5lXzFfdjERAHXsB2XCngEA&_nc_gid=UvzafTZezoof3XrqvpEe6g&_nc_zt=28&oh=00_AftFajcZQ7_T2vJhYVAJj7gW6VH2QFIFag-hFZbEEciZ8w&oe=699C3764", 1),
            VideoItem("https://scontent-lax3-1.cdninstagram.com/o1/v/t16/f2/m69/AQNOiBnGxHzdXbcJ7g2aOm4dSCZsXycYoUKY9pOWyZGI_HsvXcFwIy6-dFO2BJt87RXGoiqxayyZxQAlGHaNppoL.mp4?strext=1&_nc_cat=110&_nc_sid=5e9851&_nc_ht=scontent-lax3-1.cdninstagram.com&_nc_ohc=4kLVjWIy8tIQ7kNvwFsMN7_&efg=eyJ2ZW5jb2RlX3RhZyI6Inhwdl9wcm9ncmVzc2l2ZS5JTlNUQUdSQU0uSUdUVi5DMy4xMjgwLmRhc2hfYmFzZWxpbmVfMV92MSIsInhwdl9hc3NldF9pZCI6NTY4NTM0Mzk3Nzc5Mzc3LCJhc3NldF9hZ2VfZGF5cyI6MTM5NiwidmlfdXNlY2FzZV9pZCI6MTAxNDUsImR1cmF0aW9uX3MiOjE5LCJ1cmxnZW5fc291cmNlIjoid3d3In0%3D&ccb=17-1&_nc_gid=6y0CyDe2hmUX_J5WhcwipQ&_nc_zt=28&vs=e95a628973666755&_nc_vs=HBksFQIYOnBhc3N0aHJvdWdoX2V2ZXJzdG9yZS9HSGdscUJETXdGeFk5MmdCQUpVSHFnUV9rQWdkYnZWQkFBQUYVAALIARIAFQIYOnBhc3N0aHJvdWdoX2V2ZXJzdG9yZS9HQjc0b1JESEwza2tTeGtJQUUydlp3UWViSFZOYnZWQkFBQUYVAgLIARIAKAAYABsCiAd1c2Vfb2lsATEScHJvZ3Jlc3NpdmVfcmVjaXBlATEVAAAm4vaTrYfFggIVAigCQzMsF0AzszMzMzMzGBJkYXNoX2Jhc2VsaW5lXzFfdjERAHXsB2XCngEA&oh=00_Afs_BvKTEKTBSMHw08Z0dxYQoqKGxf3CYe1eZcbz8L0wJQ&oe=69A050CA", 2),
            VideoItem("https://scontent-lax3-1.cdninstagram.com/o1/v/t16/f2/m69/AQPDaK57iJduyDhMDmKycGEpSA4cOEVYNiBFbxQXqItYXkE_CSmL8YxtAgln7fU9JfURti2hTikgkD1cpKwRmb3i.mp4?strext=1&_nc_cat=110&_nc_sid=5e9851&_nc_ht=scontent-lax3-1.cdninstagram.com&_nc_ohc=p8UZJUGfjpwQ7kNvwH4zP8-&efg=eyJ2ZW5jb2RlX3RhZyI6Inhwdl9wcm9ncmVzc2l2ZS5JTlNUQUdSQU0uSUdUVi5DMy43MjAuZGFzaF9iYXNlbGluZV8xX3YxIiwieHB2X2Fzc2V0X2lkIjo2MzY2MDcwNTc0NTQ4ODYsImFzc2V0X2FnZV9kYXlzIjoxNDQ5LCJ2aV91c2VjYXNlX2lkIjoxMDE0NSwiZHVyYXRpb25fcyI6NiwidXJsZ2VuX3NvdXJjZSI6Ind3dyJ9&ccb=17-1&_nc_gid=6y0CyDe2hmUX_J5WhcwipQ&_nc_zt=28&vs=a335b3d984e3c973&_nc_vs=HBkcFQIYOnBhc3N0aHJvdWdoX2V2ZXJzdG9yZS9HQ0VkWkJCSUNVUXUyV2dDQUI5dEFpMlNaODh0YnZWQkFBQUYVAALIARIAKAAYABsCiAd1c2Vfb2lsATEScHJvZ3Jlc3NpdmVfcmVjaXBlATEVAAAmzJyrmLS_oQIVAigCQzMsF0AZMzMzMzMzGBJkYXNoX2Jhc2VsaW5lXzFfdjERAHXsB2XCngEA&oh=00_AfszuZwV2GHdJpgK7pNPYUMwzM3ocwngzRRc0hQ_IYTI2Q&oe=69A06146", 3)
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
        Column(modifier = Modifier.fillMaxSize()) {
            BoxWithConstraints(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                val safeBaseSize = min(maxWidth, maxHeight)
                val cardWidth = safeBaseSize * 0.65f
                val cardHeight = cardWidth * 1.15f
                val playerSize = safeBaseSize * 0.45f

                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
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

                    LottiePlayerButton(
                        playerSize = playerSize,
                        isClicked = isPlayerClicked,
                        onClick = { isPlayerClicked = !isPlayerClicked },
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .offset(x = (-16).dp, y = 110.dp)
                            .zIndex(3f)
                    )
                }
            }

            HashtagSection(niche = "dance")
        }

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
            .navigationBarsPadding()
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
            modifier = Modifier.size(playerSize * playerScale)
        )
    }
}

@Composable
private fun HashtagSection(
    modifier: Modifier = Modifier,
    niche: String
) {
    val trendViewModel: TrendViewModel = viewModel()
    val chips by trendViewModel.chips.collectAsState()  // ← List<HashtagChip> from API

    LaunchedEffect(niche) {
        trendViewModel.fetchTrends(niche)
    }

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
                width = 1.dp,
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color.White.copy(alpha = 0.40f),
                        Color.White.copy(alpha = 0.10f)
                    )
                ),
                shape = RoundedCornerShape(24.dp)
            )
            .padding(horizontal = 16.dp, vertical = 22.dp)
    ) {
        HashtagGridPanel(chips = chips)  // ← FIXED: pass API chips
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
private fun HashtagGridPanel(
    modifier: Modifier = Modifier,
    chips: List<HashtagChip> = emptyList()  // ← FIXED: param instead of hardcoded chipData
) {
    val columns = 3
    val rows = (chips.size + columns - 1) / columns

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

        // ← FIXED: show loading state while API hasn't responded yet
        if (chips.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Loading trends…",
                    color = Color.White.copy(alpha = 0.45f),
                    fontSize = 12.sp
                )
            }
        } else {
            for (row in 0 until rows) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = if (row % 2 == 1) 12.dp else 0.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    for (col in 0 until columns) {
                        val idx = row * columns + col
                        if (idx >= chips.size) {
                            Spacer(modifier = Modifier.weight(1f))
                        } else {
                            HashtagChipItem(
                                chip = chips[idx],  // ← FIXED: uses API chips
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }
        }
    }
}