package com.example.trendcrafters.Home

import android.content.Intent
import android.net.Uri
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
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.*
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel

// ─────────────────────────────────────────────
// Theme
// ─────────────────────────────────────────────

private val Purple       = Color(0xFFC060FF)
private val PurpleMid    = Color(0xFF9B40E0)
private val PurpleDark   = Color(0xFF7C3AED)
private val PurpleDim    = Color(0xFF7C3AED)
private val PurpleMuted  = Color(0xFFB08FCC)
private val White90      = Color(0xE6FFFFFF)
private val White82      = Color(0xD1FFFFFF)
private val White60      = Color(0x99FFFFFF)
private val White30      = Color(0x4DFFFFFF)
private val White10      = Color(0x1AFFFFFF)
private val UserBubble   = Color(0xFF2A0050)
private val AiBubble     = Color(0xFF130022)
private val InputBg      = Color(0xFF200438)
private val CardBg       = Color(0xFF1A0030)
private val CardBorder   = Color(0x33C060FF)
private val GreenAccent  = Color(0xFF4ADE80)
private val YellowAccent = Color(0xFFFACC15)
private val OrangeAccent = Color(0xFFFB923C)
private val NavBg        = Color(0xFF0D001A)

private val bgGradient     = Brush.verticalGradient(listOf(Color(0xFF0D001A), Color(0xFF1A0030)))
private val sendGradient   = Brush.linearGradient(listOf(Purple, PurpleMid))
private val purpleGradient = Brush.linearGradient(listOf(Purple, PurpleDark))

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

        // Empty state
        AnimatedVisibility(
            visible = uiState.messages.isEmpty(),
            modifier = Modifier.align(Alignment.Center),
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("🎬", fontSize = 48.sp)
                Spacer(Modifier.height(12.dp))
                Text(
                    text = "Start Creating Viral Scripts",
                    color = Color.White,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    text = "Describe your reel idea below",
                    color = White30,
                    fontSize = 14.sp
                )
            }
        }

        // Messages list
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
                .padding(bottom = 140.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            contentPadding = PaddingValues(top = 20.dp, bottom = 8.dp)
        ) {
            items(uiState.messages, key = { it.id }) { msg ->
                when (msg.sender) {
                    MessageSender.USER -> UserBubbleItem(msg)
                    MessageSender.AI   -> AiBubbleItem(msg, onViewDraft = onViewDraft)
                }
            }

            if (uiState.isLoading) {
                item { TypingIndicator() }
            }
        }

        // Input bar
        InputBar(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 120.dp, start = 16.dp, end = 16.dp),
            value = inputText,
            onValueChange = { inputText = it },
            isLoading = uiState.isLoading,
            onSend = ::send
        )
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
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .background(InputBg, RoundedCornerShape(28.dp))
                .border(1.dp, Color(0x5DC060FF), RoundedCornerShape(28.dp))
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {
            if (value.isEmpty()) {
                Text("Describe your reel idea...", color = White30, fontSize = 16.sp)
            }
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                textStyle = TextStyle(color = Color.White, fontSize = 16.sp),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                keyboardActions = KeyboardActions(onSend = { onSend() }),
                maxLines = 4,
                cursorBrush = SolidColor(Purple)
            )
        }

        Spacer(Modifier.width(10.dp))

        Box(
            modifier = Modifier
                .size(52.dp)
                .background(
                    if (isLoading) Brush.linearGradient(listOf(Color(0xFF444444), Color(0xFF333333)))
                    else sendGradient,
                    CircleShape
                )
                .clickable(enabled = !isLoading) { onSend() },
            contentAlignment = Alignment.Center
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    color = Color.White,
                    modifier = Modifier.size(20.dp),
                    strokeWidth = 2.dp
                )
            } else {
                Icon(Icons.Rounded.ArrowUpward, contentDescription = "Send", tint = Color.White)
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
        Column(horizontalAlignment = Alignment.End) {
            Box(
                modifier = Modifier
                    .background(UserBubble, RoundedCornerShape(18.dp, 4.dp, 18.dp, 18.dp))
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Text(msg.text, color = Color.White, fontSize = 15.sp)
            }
            Spacer(Modifier.height(4.dp))
            Text(msg.time, color = White30, fontSize = 10.sp)
        }
    }
}

@Composable
private fun AiBubbleItem(msg: ChatMsg, onViewDraft: (ApiResult) -> Unit = {}) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Row(
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Avatar
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(purpleGradient),
                contentAlignment = Alignment.Center
            ) {
                Text("🎬", fontSize = 16.sp)
            }

            Column(modifier = Modifier.weight(1f)) {
                // Intro bubble
                Box(
                    modifier = Modifier
                        .background(AiBubble, RoundedCornerShape(4.dp, 20.dp, 20.dp, 20.dp))
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    Text(msg.text, color = White90, fontSize = 15.sp)
                }

                Spacer(Modifier.height(4.dp))
                Text(msg.time, color = White30, fontSize = 10.sp)

                // Result card
                msg.apiResult?.let { result ->
                    Spacer(Modifier.height(16.dp))
                    ResultSummaryCard(result = result, onViewDraft = { onViewDraft(result) })
                }
            }
        }
    }
}

// ─────────────────────────────────────────────
// Result Summary Card (shown in chat)
// ─────────────────────────────────────────────
@Composable
private fun ResultSummaryCard(result: ApiResult, onViewDraft: () -> Unit) {
    val bestIdea = result.ideas.getOrNull(result.bestFitIndex) ?: result.ideas.firstOrNull()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(CardBg)
            .border(1.dp, CardBorder, RoundedCornerShape(20.dp))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {

        // ── Best Idea Title
        bestIdea?.let { idea ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color(0x22C060FF))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text("⭐ Best Fit", color = Purple, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
                Spacer(Modifier.width(8.dp))
                Text(
                    text = idea.concept,
                    style = TextStyle(brush = sendGradient, fontSize = 17.sp, fontWeight = FontWeight.ExtraBold),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
            }

            // Hook
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(White10)
                    .padding(10.dp)
            ) {
                Column {
                    Text("🪝 HOOK", color = White60, fontSize = 9.sp, fontWeight = FontWeight.Black)
                    Spacer(Modifier.height(3.dp))
                    Text(idea.hook, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                }
            }

            // Emotion + Why It Works
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0x1AFACC15))
                        .padding(10.dp)
                ) {
                    Column {
                        Text("😮 EMOTION", color = YellowAccent, fontSize = 9.sp, fontWeight = FontWeight.Black)
                        Spacer(Modifier.height(3.dp))
                        Text(idea.emotion, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                    }
                }
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0x1A4ADE80))
                        .padding(10.dp)
                ) {
                    Column {
                        Text("✅ WHY IT WORKS", color = GreenAccent, fontSize = 9.sp, fontWeight = FontWeight.Black)
                        Spacer(Modifier.height(3.dp))
                        Text(idea.whyItWorks, color = Color.White, fontSize = 12.sp, lineHeight = 16.sp)
                    }
                }
            }

            // Structure steps
            Text("Script Steps", color = Purple, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
            idea.structure.forEachIndexed { index, step ->
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.Top) {
                    Box(
                        modifier = Modifier
                            .size(20.dp)
                            .clip(CircleShape)
                            .background(Color(0x33C060FF)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("${index + 1}", color = Purple, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                    Text(step, color = White90, fontSize = 13.sp, lineHeight = 18.sp, modifier = Modifier.weight(1f))
                }
            }
        }

        HorizontalDivider(color = White10)

        // ── Best Fit Reason
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(Color(0x1AC060FF))
                .padding(10.dp)
        ) {
            Column {
                Text("🎯 WHY THIS FITS YOU", color = Purple, fontSize = 9.sp, fontWeight = FontWeight.Black)
                Spacer(Modifier.height(3.dp))
                Text(result.bestFitReason, color = White90, fontSize = 13.sp, lineHeight = 18.sp)
            }
        }

        HorizontalDivider(color = White10)

        // ── Analysis: Performance Drivers
        if (result.analysis.performanceDrivers.isNotEmpty()) {
            Text("📈 Performance Drivers", color = GreenAccent, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                result.analysis.performanceDrivers.forEach { driver ->
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(GreenAccent)
                        )
                        Text(driver, color = White90, fontSize = 13.sp)
                    }
                }
            }
        }

        // ── Analysis: Engagement Triggers
        if (result.analysis.engagementTriggers.isNotEmpty()) {
            Text("⚡ Engagement Triggers", color = OrangeAccent, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                result.analysis.engagementTriggers.forEach { trigger ->
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(OrangeAccent)
                        )
                        Text(trigger, color = White90, fontSize = 13.sp)
                    }
                }
            }
        }

        HorizontalDivider(color = White10)

        // ── Patterns
        if (result.patterns.isNotEmpty()) {
            Text("🔁 Viral Patterns", color = YellowAccent, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                result.patterns.forEach { pattern ->
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(Color(0x1AFACC15))
                            .border(1.dp, Color(0x33FACC15), RoundedCornerShape(20.dp))
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(pattern, color = YellowAccent, fontSize = 12.sp)
                    }
                }
            }
        }

        HorizontalDivider(color = White10)

        // ── Other Ideas
        if (result.ideas.size > 1) {
            Text("💡 Other Ideas", color = Purple, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
            result.ideas.forEachIndexed { index, idea ->
                if (index == result.bestFitIndex) return@forEachIndexed
                OtherIdeaCard(idea = idea, index = index)
            }
        }

        HorizontalDivider(color = White10)

        // ── Optimization Suggestion
        result.optimizationSuggestion?.let { opt ->
            Text("🚀 Optimization Tips", color = Purple, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0x1AC060FF))
                    .border(1.dp, CardBorder, RoundedCornerShape(12.dp))
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OptimizationRow(icon = "✏️", label = "Change", value = opt.change, color = OrangeAccent)
                OptimizationRow(icon = "➕", label = "Add", value = opt.add, color = YellowAccent)
                OptimizationRow(icon = "📊", label = "Result", value = opt.result, color = GreenAccent)
            }
        }

        HorizontalDivider(color = White10)

        // ── Quick stats row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            QuickStatChip(icon = "💡", label = "${result.ideas.size} Ideas", modifier = Modifier.weight(1f))
            QuickStatChip(icon = "📊", label = "${result.patterns.size} Patterns", modifier = Modifier.weight(1f))
            QuickStatChip(icon = "📎", label = "${result.sources.size} Sources", modifier = Modifier.weight(1f))
        }

        // ── Sources preview
        if (result.sources.isNotEmpty()) {
            Text("Reference Reels", color = White30, fontSize = 11.sp)
            Row(
                modifier = Modifier.horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                result.sources.take(5).forEach { source ->
                    SourceChip(source)
                }
            }
        }

        // ── View Full Draft Button
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(sendGradient)
                .clickable { onViewDraft() }
                .padding(vertical = 12.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(Icons.Rounded.OpenInNew, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                Text("View Full Analysis", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

// ── Other Idea Card
@Composable
private fun OtherIdeaCard(idea: Idea, index: Int) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(White10)
            .border(1.dp, White10, RoundedCornerShape(12.dp))
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Box(
                modifier = Modifier
                    .size(20.dp)
                    .clip(CircleShape)
                    .background(Color(0x33C060FF)),
                contentAlignment = Alignment.Center
            ) {
                Text("${index + 1}", color = Purple, fontSize = 10.sp, fontWeight = FontWeight.Bold)
            }
            Text(idea.concept, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
        }
        Text("🪝 ${idea.hook}", color = White82, fontSize = 12.sp, lineHeight = 16.sp)
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(Color(0x1AFACC15))
                    .padding(horizontal = 8.dp, vertical = 3.dp)
            ) {
                Text("😮 ${idea.emotion}", color = YellowAccent, fontSize = 11.sp)
            }
        }
        Text(idea.whyItWorks, color = White60, fontSize = 12.sp, lineHeight = 16.sp)
    }
}

// ── Optimization Row
@Composable
private fun OptimizationRow(icon: String, label: String, value: String, color: Color) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.Top
    ) {
        Text(icon, fontSize = 14.sp)
        Column {
            Text(label, color = color, fontSize = 10.sp, fontWeight = FontWeight.Black)
            Text(value, color = White90, fontSize = 13.sp, lineHeight = 18.sp)
        }
    }
}
@Composable
private fun QuickStatChip(icon: String, label: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(White10)
            .padding(vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(icon, fontSize = 14.sp)
            Text(label, color = White60, fontSize = 10.sp)
        }
    }
}

@Composable
private fun SourceChip(source: ReelSource) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(White10)
            .padding(horizontal = 10.dp, vertical = 6.dp)
    ) {
        Text("@${source.owner}", color = White90, fontSize = 11.sp)
    }
}

// ─────────────────────────────────────────────
// Typing Indicator
// ─────────────────────────────────────────────

@Composable
private fun TypingIndicator() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(purpleGradient),
            contentAlignment = Alignment.Center
        ) {
            Text("🎬", fontSize = 14.sp)
        }

        Box(
            modifier = Modifier
                .background(AiBubble, RoundedCornerShape(4.dp, 18.dp, 18.dp, 18.dp))
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp), verticalAlignment = Alignment.CenterVertically) {
                repeat(3) { i ->
                    val dotAlpha by rememberInfiniteTransition(label = "dot$i").animateFloat(
                        initialValue = 0.2f,
                        targetValue = 1f,
                        animationSpec = infiniteRepeatable(
                            tween(500, delayMillis = i * 150), RepeatMode.Reverse
                        ),
                        label = "dot${i}alpha"
                    )
                    Box(
                        modifier = Modifier
                            .size(6.dp)
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

fun formatCount(count: Int): String {
    return when {
        count >= 1_000_000 -> "%.1fM".format(count / 1_000_000f)
        count >= 1_000     -> "%.1fK".format(count / 1_000f)
        else               -> count.toString()
    }
}