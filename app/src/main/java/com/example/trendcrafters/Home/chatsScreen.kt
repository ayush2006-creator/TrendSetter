package com.example.trendcrafters.Home
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.*
import androidx.compose.ui.focus.*
import androidx.compose.ui.geometry.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.*
import androidx.compose.ui.unit.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

// ─────────────────────────────────────────────
//  Data Models
// ─────────────────────────────────────────────

enum class MessageSender { USER, AI }

data class ChatMsg(
    val id: String = UUID.randomUUID().toString(),
    val sender: MessageSender,
    val text: String,
    val time: String = SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date()),
    val isTyping: Boolean = false
)

// ─────────────────────────────────────────────
//  Theme Tokens
// ─────────────────────────────────────────────

private val BgDeep       = Color(0xFF06000D)
private val BgCard       = Color(0xFF110020)
private val BgTop        = Color(0xFF1E0035)
private val Purple       = Color(0xFFC060FF)
private val PurpleMid    = Color(0xFF9B40E0)
private val PurpleDark   = Color(0xFF7C3AED)
private val PurpleGlow   = Color(0x33C060FF)
private val PurpleFaint  = Color(0x0FC060FF)
private val White90      = Color(0xE6FFFFFF)
private val White60      = Color(0x99FFFFFF)
private val White30      = Color(0x4DFFFFFF)
private val White10      = Color(0x1AFFFFFF)
private val White05      = Color(0x0DFFFFFF)
private val NavBg        = Color(0xF2060010)
private val UserBubble   = Color(0xFF2A0050)
private val AiBubble     = Color(0xFF130022)

private val bgGradient = Brush.verticalGradient(
    colors = listOf(BgTop, BgDeep, BgDeep)
)
private val fabGradient = Brush.linearGradient(
    colors = listOf(Purple, PurpleDark)
)
private val sendGradient = Brush.linearGradient(
    colors = listOf(Purple, PurpleMid)
)
private val aiAvatarGradient = Brush.linearGradient(
    colors = listOf(Purple, PurpleDark)
)

// ─────────────────────────────────────────────
//  Quick Prompt Suggestions
// ─────────────────────────────────────────────

private val quickPrompts = listOf(
    "🔥" to "Write a hook for a morning routine reel",
    "🎯" to "Script a 30-sec product reveal video",
    "💡" to "Create a trending challenge concept",
    "🚀" to "Write a viral story-time opener",
    "✨" to "Generate a transition-based reel script",
    "📈" to "Script a 'before vs after' transformation"
)

// ─────────────────────────────────────────────
//  ChatScreen
// ─────────────────────────────────────────────

// ─────────────────────────────────────────────
//  Top Bar
// ─────────────────────────────────────────────

@Composable
private fun ChatTopBar(onBack: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(NavBg)
            .drawBehind {
                drawLine(
                    color = Color(0x1AC060FF),
                    start = Offset(0f, size.height),
                    end = Offset(size.width, size.height),
                    strokeWidth = 1.dp.toPx()
                )
            }
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        // Back button
        Box(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .size(36.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(White05)
                .clickable(onClick = onBack),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Rounded.ArrowBackIos, contentDescription = "Back",
                tint = White90, modifier = Modifier.size(16.dp))
        }

        // Center — AI identity
        Row(
            modifier = Modifier.align(Alignment.Center),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // AI avatar
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(CircleShape)
                    .background(aiAvatarGradient),
                contentAlignment = Alignment.Center
            ) {
                Text("🎬", fontSize = 16.sp)
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "ScriptAI",
                    color = White90,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = (-0.3).sp
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF6EE7B7))
                    )
                    Text(
                        text = "Online · Script Generator",
                        color = White60,
                        fontSize = 10.sp
                    )
                }
            }
        }

        // New chat button
        Box(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .size(36.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(White05)
                .clickable { },
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Rounded.Edit, contentDescription = "New Chat",
                tint = Purple, modifier = Modifier.size(16.dp))
        }
    }
}

// ─────────────────────────────────────────────
//  Quick Prompt Grid
// ─────────────────────────────────────────────

@Composable
private fun QuickPromptsGrid(onPromptClick: (String) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "✨  Quick starts",
            color = White30,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 0.5.sp,
            modifier = Modifier.padding(bottom = 2.dp)
        )
        quickPrompts.chunked(2).forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                row.forEach { (emoji, prompt) ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(14.dp))
                            .background(White05)
                            .border(1.dp, Color(0x18C060FF), RoundedCornerShape(14.dp))
                            .clickable { onPromptClick(prompt) }
                            .padding(horizontal = 12.dp, vertical = 10.dp)
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(emoji, fontSize = 18.sp)
                            Text(
                                text = prompt,
                                color = White60,
                                fontSize = 11.sp,
                                lineHeight = 15.sp,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }
        }
    }
}

// ─────────────────────────────────────────────
//  AI Message Bubble
// ─────────────────────────────────────────────

@Composable
private fun AiMessageBubble(msg: ChatMsg) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Bottom,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // AI avatar
        Box(
            modifier = Modifier
                .size(30.dp)
                .clip(CircleShape)
                .background(aiAvatarGradient),
            contentAlignment = Alignment.Center
        ) {
            Text("🎬", fontSize = 14.sp)
        }

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            // Bubble
            Box(
                modifier = Modifier
                    .clip(
                        RoundedCornerShape(
                            topStart = 4.dp,
                            topEnd = 18.dp,
                            bottomStart = 18.dp,
                            bottomEnd = 18.dp
                        )
                    )
                    .background(AiBubble)
                    .border(
                        1.dp,
                        Color(0x1EC060FF),
                        RoundedCornerShape(
                            topStart = 4.dp, topEnd = 18.dp,
                            bottomStart = 18.dp, bottomEnd = 18.dp
                        )
                    )
                    .padding(horizontal = 14.dp, vertical = 12.dp)
            ) {
                // Render simple markdown-like bold (**text**)
                MarkdownText(
                    text = msg.text,
                    color = White90,
                    fontSize = 14.sp,
                    lineHeight = 21.sp
                )
            }

            // Actions row
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(msg.time, color = White30, fontSize = 10.sp)
                Spacer(modifier = Modifier.width(2.dp))
                // Copy
                ActionChip(icon = Icons.Rounded.ContentCopy, label = "Copy")
                // Save to draft
                ActionChip(icon = Icons.Rounded.BookmarkBorder, label = "Save Draft")
            }
        }

        Spacer(modifier = Modifier.width(30.dp))
    }
}

// ─────────────────────────────────────────────
//  User Message Bubble
// ─────────────────────────────────────────────

@Composable
private fun UserMessageBubble(msg: ChatMsg) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End
    ) {
        Spacer(modifier = Modifier.width(50.dp))

        Column(
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Box(
                modifier = Modifier
                    .clip(
                        RoundedCornerShape(
                            topStart = 18.dp,
                            topEnd = 4.dp,
                            bottomStart = 18.dp,
                            bottomEnd = 18.dp
                        )
                    )
                    .background(UserBubble)
                    .border(
                        1.dp,
                        Purple.copy(alpha = 0.3f),
                        RoundedCornerShape(
                            topStart = 18.dp, topEnd = 4.dp,
                            bottomStart = 18.dp, bottomEnd = 18.dp
                        )
                    )
                    .padding(horizontal = 14.dp, vertical = 12.dp)
            ) {
                Text(
                    text = msg.text,
                    color = White90,
                    fontSize = 14.sp,
                    lineHeight = 21.sp
                )
            }

            Text(msg.time, color = White30, fontSize = 10.sp)
        }
    }
}

// ─────────────────────────────────────────────
//  AI Typing Bubble
// ─────────────────────────────────────────────

@Composable
private fun AiTypingBubble() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Bottom,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Box(
            modifier = Modifier
                .size(30.dp)
                .clip(CircleShape)
                .background(aiAvatarGradient),
            contentAlignment = Alignment.Center
        ) {
            Text("🎬", fontSize = 14.sp)
        }

        Box(
            modifier = Modifier
                .clip(
                    RoundedCornerShape(
                        topStart = 4.dp, topEnd = 18.dp,
                        bottomStart = 18.dp, bottomEnd = 18.dp
                    )
                )
                .background(AiBubble)
                .border(
                    1.dp, Color(0x1EC060FF),
                    RoundedCornerShape(
                        topStart = 4.dp, topEnd = 18.dp,
                        bottomStart = 18.dp, bottomEnd = 18.dp
                    )
                )
                .padding(horizontal = 18.dp, vertical = 14.dp)
        ) {
            TypingDots()
        }
    }
}

// ─────────────────────────────────────────────
//  Typing Dots Animation
// ─────────────────────────────────────────────

@Composable
private fun TypingDots() {
    val dotCount = 3
    val infiniteTransition = rememberInfiniteTransition(label = "typing")

    Row(
        horizontalArrangement = Arrangement.spacedBy(5.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.height(20.dp)
    ) {
        repeat(dotCount) { index ->
            val alpha by infiniteTransition.animateFloat(
                initialValue = 0.2f,
                targetValue = 1f,
                animationSpec = infiniteRepeatable(
                    animation = keyframes {
                        durationMillis = 900
                        0.2f at 0
                        1f at 300
                        0.2f at 600
                    },
                    repeatMode = RepeatMode.Restart,
                    initialStartOffset = StartOffset(index * 150)
                ),
                label = "dot_$index"
            )
            val offsetY by infiniteTransition.animateFloat(
                initialValue = 0f,
                targetValue = -4f,
                animationSpec = infiniteRepeatable(
                    animation = keyframes {
                        durationMillis = 900
                        0f at 0
                        -4f at 300
                        0f at 600
                    },
                    repeatMode = RepeatMode.Restart,
                    initialStartOffset = StartOffset(index * 150)
                ),
                label = "dot_offset_$index"
            )
            Box(
                modifier = Modifier
                    .size(7.dp)
                    .offset(y = offsetY.dp)
                    .clip(CircleShape)
                    .background(Purple.copy(alpha = alpha))
            )
        }
    }
}

// ─────────────────────────────────────────────
//  Action Chip
// ─────────────────────────────────────────────

@Composable
private fun ActionChip(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    onClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(50.dp))
            .background(White05)
            .border(1.dp, White10, RoundedCornerShape(50.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(3.dp)
    ) {
        Icon(icon, contentDescription = label,
            tint = White30, modifier = Modifier.size(10.dp))
        Text(label, color = White30, fontSize = 9.sp, fontWeight = FontWeight.Medium)
    }
}

// ─────────────────────────────────────────────
//  Input Bar
// ─────────────────────────────────────────────

@Composable
private fun ChatInputBar(
    value: String,
    onValueChange: (String) -> Unit,
    onSend: () -> Unit,
    isAiTyping: Boolean
) {
    val isFocused = remember { mutableStateOf(false) }
    val borderColor by animateColorAsState(
        targetValue = if (isFocused.value) Purple.copy(alpha = 0.5f) else Color(0x1AC060FF),
        animationSpec = tween(200),
        label = "input_border"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(NavBg)
            .drawBehind {
                drawLine(
                    color = Color(0x1AC060FF),
                    start = Offset(0f, 0f),
                    end = Offset(size.width, 0f),
                    strokeWidth = 1.dp.toPx()
                )
            }
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {

            // Hint label when empty and not typing
            if (value.isEmpty() && !isAiTyping) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf("📱 Platform", "🎯 Goal", "😎 Vibe").forEach { hint ->
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(50.dp))
                                .background(White05)
                                .border(1.dp, Color(0x12C060FF), RoundedCornerShape(50.dp))
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(hint, color = White30, fontSize = 10.sp)
                        }
                    }
                }
            }

            // Text input row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .background(Color(0xFF0D0020))
                    .border(1.dp, borderColor, RoundedCornerShape(18.dp))
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Text field
                Box(modifier = Modifier.weight(1f)) {
                    if (value.isEmpty()) {
                        Text(
                            text = "Describe the script you want to create...",
                            color = White30,
                            fontSize = 14.sp,
                            lineHeight = 20.sp
                        )
                    }
                    BasicTextField(
                        value = value,
                        onValueChange = onValueChange,
                        textStyle = TextStyle(
                            color = White90,
                            fontSize = 14.sp,
                            lineHeight = 20.sp
                        ),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                        keyboardActions = KeyboardActions(onSend = { onSend() }),
                        maxLines = 5,
                        cursorBrush = SolidColor(Purple),
                        modifier = Modifier
                            .fillMaxWidth()
                            .onFocusChanged { isFocused.value = it.isFocused }
                    )
                }

                // Send button
                val canSend = value.isNotBlank() && !isAiTyping
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(
                            if (canSend) sendGradient
                            else Brush.linearGradient(listOf(White10, White10))
                        )
                        .clickable(enabled = canSend, onClick = onSend),
                    contentAlignment = Alignment.Center
                ) {
                    if (isAiTyping) {
                        CircularProgressIndicator(
                            color = White30,
                            strokeWidth = 2.dp,
                            modifier = Modifier.size(16.dp)
                        )
                    } else {
                        Icon(
                            Icons.Rounded.ArrowUpward,
                            contentDescription = "Send",
                            tint = if (canSend) Color.White else White30,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            // Footer hint
            Text(
                text = "ScriptAI may produce errors. Always review before posting.",
                color = White30,
                fontSize = 9.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp),
                textAlign = TextAlign.Center
            )
        }
    }
}

// ─────────────────────────────────────────────
//  Markdown-ish Text (handles **bold**)
// ─────────────────────────────────────────────

@Composable
private fun MarkdownText(
    text: String,
    color: Color,
    fontSize: TextUnit,
    lineHeight: TextUnit,
    modifier: Modifier = Modifier
) {
    // Build AnnotatedString handling **bold** and \n
    val annotated = buildAnnotatedStringWithBold(text)
    Text(
        text = annotated,
        color = color,
        fontSize = fontSize,
        lineHeight = lineHeight,
        modifier = modifier
    )
}

private fun buildAnnotatedStringWithBold(text: String): androidx.compose.ui.text.AnnotatedString {
    val builder = androidx.compose.ui.text.AnnotatedString.Builder()
    val parts = text.split("**")
    parts.forEachIndexed { i, part ->
        if (i % 2 == 1) {
            builder.pushStyle(
                androidx.compose.ui.text.SpanStyle(fontWeight = FontWeight.Bold, color = Color(0xFFC896FF))
            )
            builder.append(part)
            builder.pop()
        } else {
            builder.append(part)
        }
    }
    return builder.toAnnotatedString()
}

// ─────────────────────────────────────────────
//  AI Reply Generator (mock — swap with real API)
// ─────────────────────────────────────────────
@Composable
fun ChatContent(
    modifier: Modifier = Modifier,
    onScriptGenerated: (String) -> Unit = {}
) {
    val messages = remember {
        mutableStateListOf(
            ChatMsg(
                sender = MessageSender.AI,
                text = "Hey creator! 🎬\n\nTell me the **intent** of the script you want to write — your platform, vibe, topic, and goal. I'll craft a scroll-stopping script tailored to your audience.",
            )
        )
    }

    var inputText by remember { mutableStateOf("") }
    var isAiTyping by remember { mutableStateOf(false) }
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    val keyboardController = LocalSoftwareKeyboardController.current
    var showSuggestions by remember { mutableStateOf(true) }

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) listState.animateScrollToItem(messages.size - 1)
    }

    fun sendMessage(text: String) {
        if (text.isBlank()) return
        showSuggestions = false
        keyboardController?.hide()
        messages.add(ChatMsg(sender = MessageSender.USER, text = text.trim()))
        inputText = ""
        isAiTyping = true
        messages.add(ChatMsg(sender = MessageSender.AI, text = "", isTyping = true))
        scope.launch {
            delay(1800L)
            messages.removeLastOrNull()
            isAiTyping = false
            messages.add(ChatMsg(sender = MessageSender.AI, text = generateAiReply(text)))
        }
    }

    Column(modifier = modifier.fillMaxSize()) {
        // Slim chat header (no back button needed — nav handles that)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xF2060010))
                .drawBehind {
                    drawLine(
                        color = Color(0x1AC060FF),
                        start = Offset(0f, size.height),
                        end = Offset(size.width, size.height),
                        strokeWidth = 1.dp.toPx()
                    )
                }
                .padding(horizontal = 16.dp, vertical = 12.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(listOf(Color(0xFFC060FF), Color(0xFF7C3AED)))
                        ),
                    contentAlignment = Alignment.Center
                ) { Text("🎬", fontSize = 16.sp) }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "ScriptAI",
                        color = Color(0xE6FFFFFF),
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = (-0.3).sp
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF6EE7B7))
                        )
                        Text(text = "Online · Script Generator", color = Color(0x99FFFFFF), fontSize = 10.sp)
                    }
                }
            }
        }

        LazyColumn(
            state = listState,
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (showSuggestions) {
                item { QuickPromptsGrid(onPromptClick = { sendMessage(it) }) }
            }
            items(messages, key = { it.id }) { msg ->
                AnimatedVisibility(
                    visible = true,
                    enter = fadeIn(tween(300)) + slideInVertically(tween(300)) { it / 2 }
                ) {
                    when {
                        msg.isTyping -> AiTypingBubble()
                        msg.sender == MessageSender.AI -> AiMessageBubble(msg)
                        else -> UserMessageBubble(msg)
                    }
                }
            }
            item { Spacer(modifier = Modifier.height(8.dp)) }
        }

        ChatInputBar(
            value = inputText,
            onValueChange = { inputText = it },
            onSend = { sendMessage(inputText) },
            isAiTyping = isAiTyping
        )
    }
}
private fun generateAiReply(userInput: String): String {
    val input = userInput.lowercase()
    return when {
        input.contains("morning") || input.contains("routine") ->
            "🌅 **Script: Morning Routine Reel (30 sec)**\n\n**HOOK (0–3s)**\n\"I did this every morning for 30 days — and gained 20K followers.\"\n\n**BUILD (3–18s)**\n[Cut 1] Alarm at 5AM — no snooze.\n[Cut 2] Cold water splash — show the shock.\n[Cut 3] Journal — write ONE goal.\n[Cut 4] 10-min content batch — phone on tripod.\n[Cut 5] Post it. Walk out.\n\n**CTA (18–30s)**\n\"Save this. Start tomorrow. Your future self will thank you.\"\n\n🎵 **Suggested audio:** Lo-fi morning beat or trending motivational audio\n📌 **Save this to Drafts?**"

        input.contains("hook") ->
            "🎣 **5 Viral Hook Formulas**\n\n1️⃣ **The Contradiction**\n\"Everyone says X — they're wrong. Here's why.\"\n\n2️⃣ **The Number Promise**\n\"3 things I wish I knew before hitting 50K.\"\n\n3️⃣ **The Cliffhanger**\n\"Wait until you see what happened at the end.\"\n\n4️⃣ **The Call-Out**\n\"If you're a creator under 10K, watch this.\"\n\n5️⃣ **The Bold Claim**\n\"This one tweak doubled my reach in 7 days.\"\n\n🔥 Pick your favourite and I'll write the full script!"

        input.contains("product") || input.contains("reveal") ->
            "🎁 **Script: Product Reveal Reel (30 sec)**\n\n**HOOK (0–3s)**\n\"I've been hiding something for 3 months. Today it drops.\"\n\n**BUILD (3–20s)**\n[Slow pan] — Show silhouette only.\n[Text overlay] — \"Coming soon\" teaser.\n[Countdown] — 3… 2… 1…\n[Reveal] — Full product hero shot with dramatic zoom.\n[Feature cuts] — 3 quick benefit callouts.\n\n**CTA (20–30s)**\n\"Link in bio. First 100 get 20% off. Don't sleep.\"\n\n🎵 **Suggested audio:** Cinematic build + drop\n📌 **Ready to save this draft?**"

        input.contains("challenge") ->
            "💥 **Trending Challenge Concept**\n\n**Name:** #30SecCreatorChallenge\n\n**Concept:** Show your entire content creation process — idea to post — in exactly 30 seconds. Fast cuts, no talking, just action.\n\n**Why it works:**\n✅ Process content = massive saves\n✅ Easy to replicate (drives UGC)\n✅ Works for any niche\n\n**Your hook:**\n\"I made a reel in 30 seconds. You can too. #30SecCreatorChallenge\"\n\n🔁 Tag 3 creators to keep it going!\n📌 Want me to write the full script?"

        else ->
            "🎬 **Your Script Concept**\n\nBased on your intent, here's how I'd structure this:\n\n**HOOK (0–3s)**\nStart with a bold statement or unexpected visual that stops the scroll.\n\n**BUILD (3–20s)**\nDeliver your core message in punchy, fast-cut segments. Each cut = one idea. Keep it moving.\n\n**CTA (20–30s)**\nEnd with a clear action: save, follow, comment, or click link in bio.\n\n💡 **Pro tip:** The first 1.5 seconds determine everything. Make it visually or verbally shocking.\n\nWant me to refine this for a specific platform or tone? Tell me more about your audience!"
    }
}

// ─────────────────────────────────────────────
//  Preview
// ─────────────────────────────────────────────

