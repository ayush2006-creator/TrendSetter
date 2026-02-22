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
private val White90      = Color(0xE6FFFFFF)
private val White60      = Color(0x99FFFFFF)
private val White30      = Color(0x4DFFFFFF)
private val White10      = Color(0x1AFFFFFF)
private val UserBubble   = Color(0xFF2A0050)
private val AiBubble     = Color(0xFF130022)
private val InputBg      = Color(0xFF200438)
private val CardBg       = Color(0xFF1A0030)
private val CardBorder   = Color(0x33C060FF)
private val GreenAccent  = Color(0xFF4ADE80)
private val OrangeAccent = Color(0xFFFB923C)

private val sendGradient   = Brush.linearGradient(listOf(Purple, PurpleMid))
private val purpleGradient = Brush.linearGradient(listOf(Purple, PurpleDark))

// ─────────────────────────────────────────────
// ChatContent
// ─────────────────────────────────────────────

@Composable
fun ChatContent(
    modifier: Modifier = Modifier,
    chatViewModel: ChatViewModel = viewModel()
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
                    MessageSender.AI   -> AiBubbleItem(msg)
                }
            }

            // Typing indicator
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
private fun AiBubbleItem(msg: ChatMsg) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.Bottom, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            // Avatar
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(purpleGradient),
                contentAlignment = Alignment.Center
            ) {
                Text("🎬", fontSize = 14.sp)
            }

            Column {
                // Base text bubble
                Box(
                    modifier = Modifier
                        .background(AiBubble, RoundedCornerShape(4.dp, 18.dp, 18.dp, 18.dp))
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    Text(msg.text, color = White90, fontSize = 15.sp)
                }
                Spacer(Modifier.height(4.dp))
                Text(msg.time, color = White30, fontSize = 10.sp)
            }
        }

        // Rich result cards if API data is present
        msg.apiResult?.let { result ->
            Spacer(Modifier.height(12.dp))
            ApiResultCards(result = result, avatarOffset = 40.dp)
        }
    }
}

// ─────────────────────────────────────────────
// API Result Cards
// ─────────────────────────────────────────────

@Composable
private fun ApiResultCards(result: ApiResult, avatarOffset: Dp) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = avatarOffset),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {

        // ── Analysis Card ──────────────────────────────
        ResultCard(title = "📊 Analysis") {
            ChipRow(label = "Drivers", items = result.analysis.performanceDrivers, chipColor = Color(0xFF1A3A2A), textColor = GreenAccent)
            Spacer(Modifier.height(6.dp))
            ChipRow(label = "Triggers", items = result.analysis.engagementTriggers, chipColor = Color(0xFF2A1A3A), textColor = Purple)
        }

        // ── Patterns ──────────────────────────────────
        if (result.patterns.isNotEmpty()) {
            ResultCard(title = "🔁 Patterns") {
                result.patterns.forEach { pattern ->
                    Row(modifier = Modifier.padding(vertical = 2.dp)) {
                        Text("•  ", color = Purple, fontSize = 13.sp)
                        Text(pattern, color = White60, fontSize = 13.sp)
                    }
                }
            }
        }

        // ── Ideas ─────────────────────────────────────
        result.ideas.forEachIndexed { index, idea ->
            val isBest = index == result.bestFitIndex
            ResultCard(
                title = if (isBest) "⭐ Best Idea: ${idea.concept}" else "💡 Idea: ${idea.concept}",
                highlight = isBest
            ) {
                InfoRow("🪝 Hook", idea.hook)
                InfoRow("😄 Emotion", idea.emotion)
                InfoRow("✅ Why it works", idea.whyItWorks)
                Spacer(Modifier.height(6.dp))
                Text("Structure:", color = White60, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                idea.structure.forEachIndexed { i, step ->
                    Text(
                        "${i + 1}. $step",
                        color = White60,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(start = 8.dp, top = 2.dp)
                    )
                }
                if (isBest) {
                    Spacer(Modifier.height(6.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0x22C060FF))
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text(
                            "⭐ ${result.bestFitReason}",
                            color = Purple,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }

        // ── Optimization Suggestion ───────────────────
        result.optimizationSuggestion?.let { opt ->
            ResultCard(title = "🚀 Optimization Tip") {
                InfoRow("🔄 Change", opt.change)
                InfoRow("➕ Add", opt.add)
                InfoRow("📈 Result", opt.result)
            }
        }

        // ── Sources ───────────────────────────────────
        if (result.sources.isNotEmpty()) {
            ResultCard(title = "📎 Reference Reels (${result.sources.size})") {
                result.sources.forEach { source ->
                    SourceItem(source = source)
                    Spacer(Modifier.height(6.dp))
                }
            }
        }
    }
}

// ─────────────────────────────────────────────
// Reusable Sub-components
// ─────────────────────────────────────────────

@Composable
private fun ResultCard(
    title: String,
    highlight: Boolean = false,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(CardBg)
            .border(
                1.dp,
                if (highlight) Purple.copy(alpha = 0.6f) else CardBorder,
                RoundedCornerShape(16.dp)
            )
            .padding(14.dp)
    ) {
        Text(
            title,
            color = if (highlight) Purple else White90,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.height(8.dp))
        content()
    }
}

@Composable
private fun ChipRow(label: String, items: List<String>, chipColor: Color, textColor: Color) {
    Column {
        Text(label, color = White60, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
        Spacer(Modifier.height(4.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.horizontalScroll(rememberScrollState())) {
            items.forEach { item ->
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50.dp))
                        .background(chipColor)
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(item, color = textColor, fontSize = 11.sp, fontWeight = FontWeight.Medium)
                }
            }
        }
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(modifier = Modifier.padding(vertical = 2.dp)) {
        Text("$label: ", color = White60, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
        Text(value, color = White90, fontSize = 12.sp, modifier = Modifier.weight(1f))
    }
}

@Composable
private fun SourceItem(source: ReelSource) {
    val context = LocalContext.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(Color(0x0FC060FF))
            .border(1.dp, Color(0x18C060FF), RoundedCornerShape(10.dp))
            .clickable {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(source.url))
                context.startActivity(intent)
            }
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Avatar initial
        Box(
            modifier = Modifier
                .size(34.dp)
                .clip(CircleShape)
                .background(purpleGradient),
            contentAlignment = Alignment.Center
        ) {
            Text(
                source.owner.firstOrNull()?.uppercaseChar()?.toString() ?: "?",
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Column(modifier = Modifier.weight(1f)) {
            Text(
                "@${source.owner}",
                color = White90,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                MetaBadge("❤️ ${formatCount(source.likes)}", OrangeAccent)
                MetaBadge("⏱ ${source.duration}s", White60)
            }
        }

        // Score badge
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0x22C060FF))
                .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Text(
                "%.4f".format(source.score),
                color = Purple,
                fontSize = 10.sp,
                fontWeight = FontWeight.SemiBold
            )
        }

        Icon(Icons.Rounded.OpenInNew, contentDescription = "Open", tint = Purple, modifier = Modifier.size(16.dp))
    }
}

@Composable
private fun MetaBadge(text: String, color: Color) {
    Text(text, color = color, fontSize = 11.sp)
}

// ─────────────────────────────────────────────
// Typing Indicator
// ─────────────────────────────────────────────

@Composable
private fun TypingIndicator() {
    val infiniteTransition = rememberInfiniteTransition(label = "typing")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            tween(600), RepeatMode.Reverse
        ),
        label = "alpha"
    )

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

private fun formatCount(count: Int): String {
    return when {
        count >= 1_000_000 -> "%.1fM".format(count / 1_000_000f)
        count >= 1_000     -> "%.1fK".format(count / 1_000f)
        else               -> count.toString()
    }
}