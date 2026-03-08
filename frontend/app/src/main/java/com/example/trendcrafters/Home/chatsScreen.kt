package com.example.trendcrafters.Home

import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.ViewGroup
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.*
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView

// ─────────────────────────────────────────────
// URL Helper
// ─────────────────────────────────────────────

fun String.toUnsignedReelUrl(): String {
    val mp4Index = indexOf(".mp4")
    return if (mp4Index != -1) substring(0, mp4Index + 4) else this
}

// ─────────────────────────────────────────────
// Theme
// ─────────────────────────────────────────────

private val PurpleMid     = Color(0xFF9B40E0)
private val PurpleDark    = Color(0xFF7C3AED)
private val PurpleDeep    = Color(0xFF4C1D95)

private val White90       = Color(0xE6FFFFFF)
private val White60       = Color(0x99FFFFFF)
private val White30       = Color(0x4DFFFFFF)
private val White10       = Color(0x1AFFFFFF)

private val AiBubble      = Color(0xFF130022)
private val InputBg       = Color(0xFF1A0330)

private val OrangeAccent  = Color(0xFFFB923C)
private val PinkAccent    = Color(0xFFF472B6)

private val sendGradient    = Brush.linearGradient(listOf(Purple, PurpleMid))
private val purpleGradient  = Brush.linearGradient(listOf(Purple, PurpleDark))
private val cardGlowBrush   = Brush.radialGradient(
    colors = listOf(Color(0x22A855F7), Color.Transparent),
    radius = 600f
)

// ─────────────────────────────────────────────
// ChatContent
// ─────────────────────────────────────────────

@Composable
fun ChatContent(
    modifier: Modifier = Modifier,
    chatViewModel: ChatViewModel = viewModel(),
    onViewDraft: (ApiResult) -> Unit = {}
) {
    val uiState by chatViewModel.uiState.collectAsStateWithLifecycle()
    var inputText by remember { mutableStateOf("") }
    val listState = rememberLazyListState()
    val keyboard = LocalSoftwareKeyboardController.current

    // Track whether we have messages to decide input bar positioning
    val hasMessages = uiState.messages.isNotEmpty() || uiState.isLoading

    // List needs enough bottom padding to not hide behind the input bar
    // Input bar height ≈ 72dp. Add nav bar clearance on top.
    val listBottomPadding by animateDpAsState(
        targetValue = if (hasMessages) 84.dp else 140.dp,
        animationSpec = tween(durationMillis = 350, easing = FastOutSlowInEasing),
        label = "listBottomPadding"
    )

    // Empty state: bar floats higher above bottom nav
    // With messages: bar drops right above bottom nav (just enough clearance for nav bar ~72dp)
    val inputBottomPadding by animateDpAsState(
        targetValue = if (hasMessages) 76.dp else 120.dp,
        animationSpec = tween(durationMillis = 350, easing = FastOutSlowInEasing),
        label = "inputBottomPadding"
    )

    LaunchedEffect(uiState.messages.size) {
        if (uiState.messages.isNotEmpty()) {
            listState.animateScrollToItem(uiState.messages.size - 1)
        }
    }

    fun send() {
        val text = inputText.trim()
        if (text.isBlank()) return
        keyboard?.hide()
        inputText = ""
        chatViewModel.sendMessage(text)
    }

    Box(modifier = modifier.fillMaxSize()) {

        // ── Empty State (centered)
        AnimatedVisibility(
            visible = !hasMessages,
            modifier = Modifier.align(Alignment.Center),
            enter = fadeIn(tween(300)) + scaleIn(initialScale = 0.92f, animationSpec = tween(300)),
            exit = fadeOut(tween(200)) + scaleOut(targetScale = 0.92f, animationSpec = tween(200))
        ) {
            EmptyStateContent()
        }

        // ── Message list
        AnimatedVisibility(
            visible = hasMessages,
            enter = fadeIn(tween(250)),
            exit = fadeOut(tween(150))
        ) {
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(top = 24.dp, bottom = listBottomPadding)
            ) {
                items(uiState.messages, key = { it.id }) { msg ->
                    AnimatedVisibility(
                        visible = true,
                        enter = fadeIn(tween(300)) + slideInVertically(
                            initialOffsetY = { it / 2 },
                            animationSpec = tween(300, easing = FastOutSlowInEasing)
                        )
                    ) {
                        when (msg.sender) {
                            MessageSender.USER -> UserBubbleItem(msg)
                            MessageSender.AI   -> AiBubbleItem(msg, onViewDraft = onViewDraft)
                        }
                    }
                }
                if (uiState.isLoading) {
                    item {
                        AnimatedVisibility(
                            visible = true,
                            enter = fadeIn() + slideInVertically { it / 2 }
                        ) {
                            TypingIndicator()
                        }
                    }
                }
            }
        }

        // ── Input Bar — same pill shape always, only bottom padding changes
        InputBar(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(
                    bottom = inputBottomPadding,
                    start = 16.dp,
                    end = 16.dp
                ),
            value = inputText,
            onValueChange = { inputText = it },
            isLoading = uiState.isLoading,
            onSend = ::send
        )
    }
}

// ─────────────────────────────────────────────
// Empty State
// ─────────────────────────────────────────────

@Composable
private fun EmptyStateContent() {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(96.dp)
                .background(
                    Brush.radialGradient(listOf(Color(0x44A855F7), Color.Transparent)),
                    CircleShape
                )
                .border(1.dp, CardBorder, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text("🎬", fontSize = 42.sp)
        }
        Spacer(Modifier.height(20.dp))
        Text(
            text = "TrendCrafters AI",
            color = Color.White,
            fontSize = 24.sp,
            fontWeight = FontWeight.ExtraBold,
            style = TextStyle(brush = purpleGradient, fontSize = 24.sp, fontWeight = FontWeight.ExtraBold)
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = "Analyze trends • Generate viral scripts • Watch reference reels",
            color = White60,
            fontSize = 13.sp,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            modifier = Modifier.padding(horizontal = 32.dp)
        )
        Spacer(Modifier.height(24.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            listOf("🔥 Trending", "💡 Script Ideas", "📊 Analysis").forEach { chip ->
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(White10)
                        .border(1.dp, CardBorder, RoundedCornerShape(20.dp))
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(chip, color = White82, fontSize = 12.sp)
                }
            }
        }
    }
}

// ─────────────────────────────────────────────
// Input Bar
// ─────────────────────────────────────────────

@Composable
private fun InputBar(
    modifier: Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    isLoading: Boolean,
    onSend: () -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(32.dp))
            .background(InputBg)
            .border(1.dp, CardBorder, RoundedCornerShape(32.dp))
            .padding(horizontal = 6.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 14.dp, vertical = 10.dp)
        ) {
            if (value.isEmpty()) {
                Text("Describe your reel idea...", color = White30, fontSize = 15.sp)
            }
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                textStyle = TextStyle(color = Color.White, fontSize = 15.sp),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                keyboardActions = KeyboardActions(onSend = { onSend() }),
                maxLines = 4,
                cursorBrush = SolidColor(Purple)
            )
        }

        Box(
            modifier = Modifier
                .size(48.dp)
                .background(
                    if (isLoading) Brush.linearGradient(listOf(Color(0xFF333333), Color(0xFF222222)))
                    else sendGradient,
                    CircleShape
                )
                .clickable(enabled = !isLoading) { onSend() },
            contentAlignment = Alignment.Center
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    color = Color.White,
                    modifier = Modifier.size(18.dp),
                    strokeWidth = 2.dp
                )
            } else {
                Icon(
                    Icons.Rounded.ArrowUpward,
                    contentDescription = "Send",
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

// ─────────────────────────────────────────────
// Message Bubbles
// ─────────────────────────────────────────────

@Composable
private fun UserBubbleItem(msg: ChatMsg) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
        Column(horizontalAlignment = Alignment.End, modifier = Modifier.fillMaxWidth(0.85f)) {
            Box(
                modifier = Modifier
                    .background(
                        Brush.linearGradient(listOf(Color(0xFF3B0066), Color(0xFF2A0050))),
                        RoundedCornerShape(20.dp, 4.dp, 20.dp, 20.dp)
                    )
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Text(msg.text, color = Color.White, fontSize = 15.sp, lineHeight = 22.sp)
            }
            Spacer(Modifier.height(4.dp))
            Text(msg.time, color = White30, fontSize = 10.sp)
        }
    }
}

@Composable
private fun AiBubbleItem(msg: ChatMsg, onViewDraft: (ApiResult) -> Unit = {}) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier
                .size(38.dp)
                .clip(CircleShape)
                .background(purpleGradient)
                .border(1.dp, CardBorder, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text("🎬", fontSize = 16.sp)
        }

        Column(modifier = Modifier.weight(1f)) {
            Box(
                modifier = Modifier
                    .background(AiBubble, RoundedCornerShape(4.dp, 20.dp, 20.dp, 20.dp))
                    .border(1.dp, White10, RoundedCornerShape(4.dp, 20.dp, 20.dp, 20.dp))
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Text(msg.text, color = White90, fontSize = 15.sp, lineHeight = 22.sp)
            }

            Spacer(Modifier.height(4.dp))
            Text(msg.time, color = White30, fontSize = 10.sp)

            msg.apiResult?.let { result ->
                Spacer(Modifier.height(16.dp))
                ResultSummaryCard(result = result, onViewDraft = { onViewDraft(result) })
            }
        }
    }
}

// ─────────────────────────────────────────────
// Result Summary Card
// ─────────────────────────────────────────────

@Composable
private fun ResultSummaryCard(result: ApiResult, onViewDraft: () -> Unit) {
    val bestIdea = result.ideas.getOrNull(result.bestFitIndex) ?: result.ideas.firstOrNull()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(CardBg)
            .border(1.dp, CardBorder, RoundedCornerShape(24.dp))
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // ── Header with glow
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(
                    Brush.linearGradient(listOf(Color(0x33A855F7), Color(0x11A855F7)))
                )
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Brush.linearGradient(listOf(Color(0xFF6D28D9), Color(0xFF4C1D95)))),
                    contentAlignment = Alignment.Center
                ) {
                    Text("✨", fontSize = 20.sp)
                }
                Column {
                    Text("AI Analysis Ready", color = White60, fontSize = 11.sp, fontWeight = FontWeight.Medium)
                    Text(
                        text = bestIdea?.concept ?: "Script Ideas",
                        style = TextStyle(brush = purpleGradient, fontSize = 16.sp, fontWeight = FontWeight.ExtraBold)
                    )
                }
            }
        }

        // ── Quick stats row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            QuickStatChip(icon = "💡", label = "${result.ideas.size}", sublabel = "Ideas", modifier = Modifier.weight(1f))
            QuickStatChip(icon = "📊", label = "${result.patterns.size}", sublabel = "Patterns", modifier = Modifier.weight(1f))
            QuickStatChip(icon = "🎬", label = "${result.sources.size}", sublabel = "Reels", modifier = Modifier.weight(1f))
        }

        bestIdea?.let { idea ->
            SectionDivider()

            // ── Hook
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color(0x1AA855F7))
                    .border(1.dp, Color(0x22A855F7), RoundedCornerShape(14.dp))
                    .padding(14.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text("🪝", fontSize = 14.sp)
                        Text("HOOK", color = Purple, fontSize = 10.sp, fontWeight = FontWeight.Black,
                            letterSpacing = 1.5.sp)
                    }
                    Text(idea.hook, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.SemiBold,
                        lineHeight = 20.sp)
                }
            }

            // ── Emotion + Why It Works
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                InfoChipCard(
                    modifier = Modifier.weight(1f),
                    icon = "😮", label = "EMOTION",
                    value = idea.emotion,
                    bg = Color(0x1AFACC15),
                    accent = YellowAccent
                )
                InfoChipCard(
                    modifier = Modifier.weight(1f),
                    icon = "✅", label = "WHY IT WORKS",
                    value = idea.whyItWorks,
                    bg = Color(0x1A4ADE80),
                    accent = GreenAccent
                )
            }

            // ── Script Steps
            SectionLabel(icon = "🎬", text = "Script Structure", color = Purple)
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                idea.structure.forEachIndexed { index, step ->
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.Top,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(White10)
                            .padding(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .clip(CircleShape)
                                .background(Brush.linearGradient(listOf(Purple, PurpleDark))),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("${index + 1}", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                        Text(step, color = White90, fontSize = 13.sp, lineHeight = 19.sp, modifier = Modifier.weight(1f))
                    }
                }
            }
        }

        // ── Best Fit Reason
        if (result.bestFitReason.isNotBlank()) {
            SectionDivider()
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color(0x15A855F7))
                    .border(1.dp, Color(0x22A855F7), RoundedCornerShape(14.dp))
                    .padding(14.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text("🎯", fontSize = 13.sp)
                        Text("WHY THIS FITS YOU", color = Purple, fontSize = 10.sp, fontWeight = FontWeight.Black, letterSpacing = 1.sp)
                    }
                    Text(result.bestFitReason, color = White90, fontSize = 13.sp, lineHeight = 19.sp)
                }
            }
        }

        // ── Performance Drivers
        if (result.analysis.performanceDrivers.isNotEmpty()) {
            SectionDivider()
            SectionLabel(icon = "📈", text = "Performance Drivers", color = GreenAccent)
            Row(
                modifier = Modifier.horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                result.analysis.performanceDrivers.forEach { driver ->
                    TagChip(text = driver, bg = Color(0x1A4ADE80), border = Color(0x334ADE80), color = GreenAccent)
                }
            }
        }

        // ── Engagement Triggers
        if (result.analysis.engagementTriggers.isNotEmpty()) {
            SectionLabel(icon = "⚡", text = "Engagement Triggers", color = OrangeAccent)
            Row(
                modifier = Modifier.horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                result.analysis.engagementTriggers.forEach { trigger ->
                    TagChip(text = trigger, bg = Color(0x1AFB923C), border = Color(0x33FB923C), color = OrangeAccent)
                }
            }
        }

        // ── Viral Patterns
        if (result.patterns.isNotEmpty()) {
            SectionDivider()
            SectionLabel(icon = "🔁", text = "Viral Patterns", color = YellowAccent)
            Row(
                modifier = Modifier.horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                result.patterns.forEach { pattern ->
                    TagChip(text = pattern, bg = Color(0x1AFACC15), border = Color(0x33FACC15), color = YellowAccent)
                }
            }
        }

        // ── Other Ideas
        if (result.ideas.size > 1) {
            SectionDivider()
            SectionLabel(icon = "💡", text = "Other Ideas", color = Purple)
            val otherIdeas = result.ideas.filterIndexed { index, _ -> index != result.bestFitIndex }
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                otherIdeas.forEachIndexed { pos, idea ->
                    val originalIndex = result.ideas.indexOf(idea)
                    OtherIdeaCard(idea = idea, index = originalIndex)
                }
            }
        }

        // ── Optimization
        result.optimizationSuggestion?.let { opt ->
            SectionDivider()
            SectionLabel(icon = "🚀", text = "Optimization Tips", color = PinkAccent)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color(0x10F472B6))
                    .border(1.dp, Color(0x22F472B6), RoundedCornerShape(14.dp))
                    .padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OptimizationRow(icon = "✏️", label = "Change", value = opt.change, color = OrangeAccent)
                OptimizationRow(icon = "➕", label = "Add", value = opt.add, color = YellowAccent)
                OptimizationRow(icon = "📊", label = "Result", value = opt.result, color = GreenAccent)
            }
        }

        // ── Reference Reels (ExoPlayer)
        if (result.sources.isNotEmpty()) {
            SectionDivider()
            SectionLabel(icon = "🎥", text = "Reference Reels", color = Purple)
            ReelsPlayer(sources = result.sources)
            Spacer(Modifier.height(4.dp))
        }

        // ── View Full Draft CTA

    }
}

// ─────────────────────────────────────────────
// Reels Player (ExoPlayer + HorizontalPager swipe)
// ─────────────────────────────────────────────

@androidx.annotation.OptIn(UnstableApi::class)
@Composable
fun ReelsPlayer(sources: List<ReelSource>) {
    if (sources.isEmpty()) return

    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val pagerState = rememberPagerState(pageCount = { sources.size })

    // One ExoPlayer per reel, keyed to URL so it rebuilds only when source changes
    val players = remember(sources) {
        sources.map { source ->
            ExoPlayer.Builder(context).build().apply {
                setMediaItem(MediaItem.fromUri(Uri.parse(source.url.toUnsignedReelUrl())))
                repeatMode = Player.REPEAT_MODE_ONE
                volume = 0f
                prepare()
            }
        }
    }

    // Play only the currently visible page, pause others
    LaunchedEffect(pagerState.currentPage) {
        players.forEachIndexed { index, player ->
            if (index == pagerState.currentPage) player.play() else player.pause()
        }
    }

    // Lifecycle management — pause/resume active player with app lifecycle
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> players.getOrNull(pagerState.currentPage)?.play()
                Lifecycle.Event.ON_PAUSE  -> players.forEach { it.pause() }
                else -> {}
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            players.forEach { it.release() }
        }
    }

    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {

        // ── Swipeable pager
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxWidth(),
            pageSpacing = 12.dp,
            beyondViewportPageCount = 1
        ) { page ->
            val source = sources[page]
            val player = players[page]

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(Color.Black)
                    .border(1.dp, CardBorder, RoundedCornerShape(18.dp))
            ) {
                AndroidView(
                    factory = {
                        PlayerView(it).apply {
                            this.player = player
                            useController = true
                            resizeMode = AspectRatioFrameLayout.RESIZE_MODE_ZOOM
                            layoutParams = ViewGroup.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.MATCH_PARENT
                            )
                        }
                    },
                    update = { pv -> pv.player = player },
                    modifier = Modifier.fillMaxSize()
                )

                // Owner badge
                Box(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(10.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color(0xAA000000))
                        .padding(horizontal = 10.dp, vertical = 5.dp)
                ) {
                    Text(
                        text = "@${source.owner}",
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                // Likes badge
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(10.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color(0xAA000000))
                        .padding(horizontal = 10.dp, vertical = 5.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text("❤️", fontSize = 11.sp)
                        Text(
                            formatCount(source.likes),
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Swipe hint on first page only
                if (page == 0 && sources.size > 1) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 48.dp)
                            .clip(RoundedCornerShape(20.dp))
                            .background(Color(0xAA000000))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "← swipe for more reels →",
                            color = White60,
                            fontSize = 10.sp
                        )
                    }
                }
            }
        }

        // ── Dot indicators
        if (sources.size > 1) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                sources.indices.forEach { index ->
                    val isSelected = index == pagerState.currentPage
                    val dotWidth by animateDpAsState(
                        targetValue = if (isSelected) 20.dp else 6.dp,
                        animationSpec = tween(200),
                        label = "dotWidth$index"
                    )
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 3.dp)
                            .height(6.dp)
                            .width(dotWidth)
                            .clip(CircleShape)
                            .background(if (isSelected) Purple else White30)
                    )
                }
            }
        }
    }
}

// ─────────────────────────────────────────────
// Small Reusable Components
// ─────────────────────────────────────────────

@Composable
private fun SectionDivider() {
    HorizontalDivider(
        color = White10,
        modifier = Modifier.padding(vertical = 2.dp)
    )
}

@Composable
private fun SectionLabel(icon: String, text: String, color: Color) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Text(icon, fontSize = 14.sp)
        Text(text, color = color, fontSize = 13.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun TagChip(text: String, bg: Color, border: Color, color: Color) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(bg)
            .border(1.dp, border, RoundedCornerShape(20.dp))
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Text(text, color = color, fontSize = 12.sp, fontWeight = FontWeight.Medium)
    }
}

@Composable
private fun InfoChipCard(modifier: Modifier, icon: String, label: String, value: String, bg: Color, accent: Color) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(bg)
            .padding(10.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(icon, fontSize = 11.sp)
                Text(label, color = accent, fontSize = 9.sp, fontWeight = FontWeight.Black, letterSpacing = 1.sp)
            }
            Text(value, color = Color.White, fontSize = 12.sp, lineHeight = 17.sp)
        }
    }
}

@Composable
private fun QuickStatChip(icon: String, label: String, sublabel: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(White10)
            .border(1.dp, White10, RoundedCornerShape(12.dp))
            .padding(vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(icon, fontSize = 16.sp)
            Text(label, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            Text(sublabel, color = White30, fontSize = 9.sp)
        }
    }
}

@Composable
private fun OtherIdeaCard(idea: Idea, index: Int) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(White10)
            .border(1.dp, White10, RoundedCornerShape(14.dp))
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Box(
                modifier = Modifier
                    .size(22.dp)
                    .clip(CircleShape)
                    .background(Color(0x33C060FF)),
                contentAlignment = Alignment.Center
            ) {
                Text("${index + 1}", color = Purple, fontSize = 10.sp, fontWeight = FontWeight.Bold)
            }
            Text(idea.concept, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
        }
        Text("🪝 ${idea.hook}", color = White82, fontSize = 12.sp, lineHeight = 17.sp)
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            TagChip(text = "😮 ${idea.emotion}", bg = Color(0x1AFACC15), border = Color(0x22FACC15), color = YellowAccent)
        }
        Text(idea.whyItWorks, color = White60, fontSize = 12.sp, lineHeight = 17.sp)
    }
}

@Composable
private fun OptimizationRow(icon: String, label: String, value: String, color: Color) {
    Row(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.Top) {
        Text(icon, fontSize = 14.sp)
        Column {
            Text(label, color = color, fontSize = 10.sp, fontWeight = FontWeight.Black, letterSpacing = 0.8.sp)
            Text(value, color = White90, fontSize = 13.sp, lineHeight = 18.sp)
        }
    }
}

// ─────────────────────────────────────────────
// Typing Indicator
// ─────────────────────────────────────────────

@Composable
private fun TypingIndicator() {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Box(
            modifier = Modifier
                .size(34.dp)
                .clip(CircleShape)
                .background(purpleGradient)
                .border(1.dp, CardBorder, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text("🎬", fontSize = 14.sp)
        }

        Box(
            modifier = Modifier
                .background(AiBubble, RoundedCornerShape(4.dp, 18.dp, 18.dp, 18.dp))
                .border(1.dp, White10, RoundedCornerShape(4.dp, 18.dp, 18.dp, 18.dp))
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(5.dp), verticalAlignment = Alignment.CenterVertically) {
                repeat(3) { i ->
                    val dotAlpha by rememberInfiniteTransition(label = "dot$i").animateFloat(
                        initialValue = 0.15f,
                        targetValue = 1f,
                        animationSpec = infiniteRepeatable(
                            tween(500, delayMillis = i * 160, easing = EaseInOutSine),
                            RepeatMode.Reverse
                        ),
                        label = "dot${i}alpha"
                    )
                    Box(
                        modifier = Modifier
                            .size(7.dp)
                            .clip(CircleShape)
                            .background(Purple.copy(alpha = dotAlpha))
                    )
                }
            }
        }
    }
}

// ─────────────────────────────────────────────
// Helpers
// ─────────────────────────────────────────────

fun formatCount(count: Int): String = when {
    count >= 1_000_000 -> "%.1fM".format(count / 1_000_000f)
    count >= 1_000     -> "%.1fK".format(count / 1_000f)
    else               -> count.toString()
}