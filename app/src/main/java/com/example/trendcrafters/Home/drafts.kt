package com.example.trendcrafters.Home

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import com.example.trendcrafters.draft.DraftViewModel

// ─────────────────────────────────────────────
// Data Model
// ─────────────────────────────────────────────
enum class DraftStatus { IN_PROGRESS, READY, NEEDS_REVIEW }

data class Draft(
    val id: String,
    val topic: String,
    val description: String,
    val platform: String,
    val platformIcon: String,
    val createdDate: String,
    val updatedDate: String,
    val status: DraftStatus,
    val tags: List<String>
)

// ─────────────────────────────────────────────
// Analysis Data Models
// ─────────────────────────────────────────────
data class ContentIdea(
    val concept: String,
    val hook: String,
    val structure: List<String>,
    val emotion: String,
    val whyItWorks: String
)

data class DraftAnalysis(
    val performanceDrivers: List<String>,
    val engagementTriggers: List<String>,
    val patterns: List<String>,
    val ideas: List<ContentIdea>,
    val bestFitIndex: Int,
    val bestFitReason: String,
    val optimizationSuggestion: String
)

// ─────────────────────────────────────────────
// Sample Data
// ─────────────────────────────────────────────
val sampleDrafts = listOf(
    Draft(
        id = "1",
        topic = "5 Morning Habits That Changed My Life",
        description = "Deep dive into the routines that helped me grow from 0 to 50K followers.",
        platform = "Instagram Reels",
        platformIcon = "📸",
        createdDate = "Feb 10, 2025",
        updatedDate = "Feb 18, 2025",
        status = DraftStatus.READY,
        tags = listOf("#MorningRoutine", "#GrowthTips")
    ),
    Draft(
        id = "2",
        topic = "How I Edit Reels in Under 30 Minutes",
        description = "Editing workflow walkthrough using CapCut + Premiere.",
        platform = "YouTube Shorts",
        platformIcon = "▶️",
        createdDate = "Feb 12, 2025",
        updatedDate = "Feb 19, 2025",
        status = DraftStatus.IN_PROGRESS,
        tags = listOf("#EditingTips", "#Workflow")
    ),
    Draft(
        id = "3",
        topic = "Viral Hook Formulas for 2025",
        description = "Breaking down 7 hook structures dominating the algorithm.",
        platform = "Instagram Reels",
        platformIcon = "📸",
        createdDate = "Feb 5, 2025",
        updatedDate = "Feb 14, 2025",
        status = DraftStatus.NEEDS_REVIEW,
        tags = listOf("#ViralHooks", "#Algorithm")
    )
)

val sampleAnalysis = DraftAnalysis(
    performanceDrivers = listOf(
        "Immediate relatability through everyday student struggles",
        "Hook lines that mirror internal thoughts viewers already have",
        "Fast pacing with quick punchlines every 2–3 seconds"
    ),
    engagementTriggers = listOf(
        "Validation of student frustration",
        "Nostalgic recognition of college routines",
        "Short tension → quick payoff structure"
    ),
    patterns = listOf(
        "POV format placing viewer inside the situation",
        "Hook begins with a thought students never say aloud"
    ),
    ideas = listOf(
        ContentIdea(
            concept = "POV: You open your laptop to study and every distraction attacks",
            hook = "POV: you finally decide to study…",
            structure = listOf("Open laptop", "Phone buzzes", "Friend interrupts", "Laptop updates", "Scrolling memes"),
            emotion = "Relatable frustration mixed with humor",
            whyItWorks = "Uses POV format and escalating micro-distractions."
        )
    ),
    bestFitIndex = 0,
    bestFitReason = "POV content performs best for college pages.",
    optimizationSuggestion = "Add heartbeat + silence for emotional intensity."
)

// ─────────────────────────────────────────────
// Colors & Theme
// ─────────────────────────────────────────────
val BgTop = Color(0xFF1E0035)
val BgMid = Color(0xFF0F001F)
val Purple = Color(0xFFC060FF)
val PurpleDim = Color(0xFF7C3AED)
val White82 = Color(0xD1FFFFFF)
val WhiteDim = Color(0x59FFFFFF)
val PurpleMuted = Color(0x80C896FF)
val NavBg = Color(0xF5080010)
val CardBg = Color(0x0DFFFFFF)
val CardBorder = Color(0x1AC060FF)
val GreenAccent = Color(0xFF6EE7B7)
val YellowAccent = Color(0xFFFBBF24)
val RedAccent = Color(0xFFFF7B7B)

val statusColors = mapOf(
    DraftStatus.READY to GreenAccent,
    DraftStatus.IN_PROGRESS to YellowAccent,
    DraftStatus.NEEDS_REVIEW to RedAccent
)
val statusLabels = mapOf(
    DraftStatus.READY to "✅ Ready",
    DraftStatus.IN_PROGRESS to "✏️ In Progress",
    DraftStatus.NEEDS_REVIEW to "🔍 Needs Review"
)
val bgGradient = Brush.linearGradient(
    colors = listOf(BgTop, BgMid, BgMid),
    start = Offset(0f, 0f),
    end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
)
val fabBrush = Brush.linearGradient(
    colors = listOf(Color(0xFFC060FF), Color(0xFF7C3AED))
)

// ─────────────────────────────────────────────
// DraftContent — main list screen
// ─────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DraftContent(
    viewModel: DraftViewModel,
    onNewDraft: () -> Unit = {},
    onEditDraft: (Draft) -> Unit = {}
) {
    val drafts by viewModel.drafts.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    var selectedFilter by remember { mutableStateOf<DraftStatus?>(null) }
    var draftToDelete by remember { mutableStateOf<Draft?>(null) }

    // ── Bottom sheet state ─────────────────────
    var selectedDraft by remember { mutableStateOf<Draft?>(null) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val filteredDrafts = remember(selectedFilter, drafts) {
        if (selectedFilter == null) drafts else drafts.filter { it.status == selectedFilter }
    }

    // ── Delete confirmation dialog ─────────────────────
    draftToDelete?.let { draft ->
        AlertDialog(
            onDismissRequest = { draftToDelete = null },
            containerColor = Color(0xFF1A0030),
            shape = RoundedCornerShape(20.dp),
            title = { Text("Delete Draft?", color = Color.White, fontWeight = FontWeight.Bold) },
            text = {
                Text(
                    "\"${draft.topic}\" will be permanently deleted.",
                    color = White82.copy(alpha = 0.65f),
                    fontSize = 13.sp
                )
            },
            confirmButton = {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50.dp))
                        .background(Color(0x22FF5555))
                        .border(1.dp, Color(0x44FF5555), RoundedCornerShape(50.dp))
                        .clickable {
                            viewModel.deleteDraft(draft.id.toInt())
                            draftToDelete = null
                        }
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text("Delete", color = RedAccent, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                }
            },
            dismissButton = {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50.dp))
                        .background(CardBg)
                        .border(1.dp, CardBorder, RoundedCornerShape(50.dp))
                        .clickable { draftToDelete = null }
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text("Cancel", color = PurpleMuted, fontSize = 13.sp)
                }
            }
        )
    }

    // ── Draft Detail Bottom Sheet ──────────────
    selectedDraft?.let { draft ->
        ModalBottomSheet(
            onDismissRequest = { selectedDraft = null },
            sheetState = sheetState,
            containerColor = Color(0xFF0F001F),
            dragHandle = {
                // Custom drag handle
                Box(
                    modifier = Modifier
                        .padding(top = 12.dp, bottom = 4.dp)
                        .width(40.dp)
                        .height(4.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(Color(0x40C060FF))
                )
            },
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
            tonalElevation = 0.dp
        ) {
            DraftDetailScreen(
                draft = draft,
                analysis = sampleAnalysis,
                onBack = { selectedDraft = null },
                onChatClick = {}
            )
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(bgGradient)) {

        // Ambient glow
        Box(
            modifier = Modifier
                .size(220.dp)
                .offset(x = (-40).dp, y = (-40).dp)
                .background(
                    Brush.radialGradient(listOf(Color(0x22C060FF), Color.Transparent)),
                    CircleShape
                )
        )

        when {
            isLoading -> {
                Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    CircularProgressIndicator(color = Purple, strokeWidth = 2.dp, modifier = Modifier.size(32.dp))
                    Text("Loading drafts…", color = PurpleMuted, fontSize = 12.sp)
                }
            }

            error != null -> {
                Column(
                    modifier = Modifier.align(Alignment.Center).padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text("⚠️", fontSize = 40.sp)
                    Text("Something went wrong", color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                    Text(error ?: "", color = PurpleMuted, fontSize = 12.sp, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(50.dp))
                            .background(Brush.linearGradient(listOf(Purple, PurpleDim)))
                            .clickable { viewModel.clearError(); viewModel.loadDrafts() }
                            .padding(horizontal = 20.dp, vertical = 10.dp)
                    ) {
                        Text("Retry", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
            }

            else -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 100.dp)
                ) {
                    item {
                        DraftHeader(
                            totalCount = drafts.size,
                            readyCount = drafts.count { it.status == DraftStatus.READY }
                        )
                    }
                    item {
                        FilterRow(
                            selectedFilter = selectedFilter,
                            onFilterSelected = { selectedFilter = if (selectedFilter == it) null else it }
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                    items(filteredDrafts, key = { it.id }) { draft ->
                        DraftCard(
                            draft = draft,
                            onClick = { selectedDraft = draft },  // ← opens bottom sheet
                            onEdit = { onEditDraft(draft) },
                            onDelete = { draftToDelete = draft }
                        )
                    }
                    if (filteredDrafts.isEmpty()) {
                        item { EmptyState() }
                    }
                }
            }
        }

        // FAB
        if (!isLoading && error == null) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 20.dp, bottom = 24.dp)
                    .size(54.dp)
                    .clip(CircleShape)
                    .background(fabBrush)
                    .clickable(onClick = onNewDraft),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Rounded.Add, contentDescription = "New Draft", tint = Color.White, modifier = Modifier.size(26.dp))
            }
        }
    }
}

// ─────────────────────────────────────────────
// DraftCard — COMPLETE implementation
// ─────────────────────────────────────────────
@Composable
fun DraftCard(
    draft: Draft,
    onClick: () -> Unit = {},
    onEdit: () -> Unit = {},
    onDelete: () -> Unit = {}
) {
    val statusColor = statusColors[draft.status] ?: Purple
    val statusLabel = statusLabels[draft.status] ?: ""

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(CardBg)
            .border(1.dp, CardBorder, RoundedCornerShape(20.dp))
            .clickable(onClick = onClick)
    ) {
        // Left accent bar
        Box(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .width(3.dp)
                .fillMaxHeight()
                .clip(RoundedCornerShape(topStart = 20.dp, bottomStart = 20.dp))
                .background(Brush.verticalGradient(listOf(Purple, PurpleDim)))
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 14.dp)
        ) {
            // ── Platform + Status row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(5.dp),
                    modifier = Modifier
                        .clip(RoundedCornerShape(50.dp))
                        .background(Color(0x14C060FF))
                        .border(1.dp, Color(0x2EC060FF), RoundedCornerShape(50.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(text = draft.platformIcon, fontSize = 11.sp)
                    Text(text = draft.platform, color = PurpleMuted, fontSize = 10.sp, fontWeight = FontWeight.Medium)
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50.dp))
                        .background(statusColor.copy(alpha = 0.12f))
                        .border(1.dp, statusColor.copy(alpha = 0.35f), RoundedCornerShape(50.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(text = statusLabel, color = statusColor, fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // ── Topic
            Text(
                text = draft.topic,
                color = Color.White,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                lineHeight = 20.sp,
                letterSpacing = (-0.2).sp
            )

            Spacer(modifier = Modifier.height(6.dp))

            // ── Description
            Text(
                text = draft.description,
                color = White82.copy(alpha = 0.55f),
                fontSize = 12.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                lineHeight = 17.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            // ── Tags
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                draft.tags.forEach { tag ->
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(50.dp))
                            .background(Color(0x0FC060FF))
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(text = tag, color = Purple.copy(alpha = 0.75f), fontSize = 10.sp, fontWeight = FontWeight.Medium)
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = Color(0x12C060FF), thickness = 1.dp)
            Spacer(modifier = Modifier.height(10.dp))

            // ── Dates + Action buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    DateRow(icon = Icons.Rounded.CalendarToday, label = "Created", date = draft.createdDate)
                    DateRow(icon = Icons.Rounded.Update, label = "Updated", date = draft.updatedDate)
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    // Edit
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(Color(0x14C060FF))
                            .border(1.dp, Color(0x2EC060FF), CircleShape)
                            .clickable(onClick = onEdit),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Rounded.Edit, contentDescription = "Edit", tint = PurpleMuted, modifier = Modifier.size(15.dp))
                    }
                    // Delete
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(Color(0x14FF5555))
                            .border(1.dp, Color(0x2EFF5555), CircleShape)
                            .clickable(onClick = onDelete),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Rounded.Delete, contentDescription = "Delete", tint = RedAccent, modifier = Modifier.size(15.dp))
                    }
                    // Post
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(50.dp))
                            .background(Brush.linearGradient(listOf(Purple, PurpleDim)))
                            .clickable { }
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(Icons.Rounded.Send, contentDescription = "Post", tint = Color.White, modifier = Modifier.size(13.dp))

                        }
                    }
                }
            }
        }
    }
}

// ─────────────────────────────────────────────
// Draft Detail Screen
// ─────────────────────────────────────────────
@Composable
fun DraftDetailScreen(
    draft: Draft,
    analysis: DraftAnalysis,
    onBack: () -> Unit = {},
    onChatClick: () -> Unit = {}
) {
    val statusColor = statusColors[draft.status] ?: Purple
    val statusLabel = statusLabels[draft.status] ?: ""

    Box(modifier = Modifier.fillMaxSize()) {
        Box(modifier = Modifier.fillMaxSize().background(bgGradient))
        Box(
            modifier = Modifier
                .size(260.dp)
                .offset(x = (-50).dp, y = (-50).dp)
                .background(Brush.radialGradient(listOf(Color(0x22C060FF), Color.Transparent)), CircleShape)
        )
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(NavBg)
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.CenterStart)
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(CardBg)
                            .border(1.dp, CardBorder, CircleShape)
                            .clickable(onClick = onBack),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Rounded.ArrowBack, contentDescription = "Back", tint = PurpleMuted, modifier = Modifier.size(18.dp))
                    }
                    Text(text = "Draft Details", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold, modifier = Modifier.align(Alignment.Center))
                    Box(
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(Brush.linearGradient(listOf(Purple, PurpleDim)))
                            .clickable(onClick = onChatClick),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Rounded.Chat, contentDescription = "Chat", tint = Color.White, modifier = Modifier.size(18.dp))
                    }
                }
            }
        ) { innerPadding ->
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(innerPadding),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // ── Draft Meta Card
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(20.dp))
                            .background(CardBg)
                            .border(1.dp, CardBorder, RoundedCornerShape(20.dp))
                            .padding(16.dp)
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(5.dp),
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(50.dp))
                                        .background(Color(0x14C060FF))
                                        .border(1.dp, Color(0x2EC060FF), RoundedCornerShape(50.dp))
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text(text = draft.platformIcon, fontSize = 11.sp)
                                    Text(text = draft.platform, color = PurpleMuted, fontSize = 10.sp, fontWeight = FontWeight.Medium)
                                }
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(50.dp))
                                        .background(statusColor.copy(alpha = 0.12f))
                                        .border(1.dp, statusColor.copy(alpha = 0.35f), RoundedCornerShape(50.dp))
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text(text = statusLabel, color = statusColor, fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
                                }
                            }
                            Text(text = draft.topic, color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, letterSpacing = (-0.3).sp)
                            Text(text = draft.description, color = White82.copy(alpha = 0.65f), fontSize = 13.sp, lineHeight = 19.sp)
                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                draft.tags.forEach { tag ->
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(50.dp))
                                            .background(Color(0x0FC060FF))
                                            .padding(horizontal = 8.dp, vertical = 3.dp)
                                    ) {
                                        Text(text = tag, color = Purple.copy(alpha = 0.75f), fontSize = 10.sp, fontWeight = FontWeight.Medium)
                                    }
                                }
                            }
                            HorizontalDivider(color = Color(0x12C060FF), thickness = 1.dp)
                            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                                DateRow(icon = Icons.Rounded.CalendarToday, label = "Created", date = draft.createdDate)
                                DateRow(icon = Icons.Rounded.Update, label = "Updated", date = draft.updatedDate)
                            }
                        }
                    }
                }

                // ── Analysis Header
                item {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Box(modifier = Modifier.size(4.dp, 20.dp).clip(RoundedCornerShape(2.dp)).background(Brush.verticalGradient(listOf(Purple, PurpleDim))))
                        Text(text = "AI Analysis", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.weight(1f))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(50.dp))
                                .background(Color(0x14C060FF))
                                .border(1.dp, Color(0x2EC060FF), RoundedCornerShape(50.dp))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(text = "✨ Generated", color = PurpleMuted, fontSize = 10.sp)
                        }
                    }
                }

                item {
                    AnalysisSectionCard(title = "🚀 Performance Drivers", accentColor = Purple) {
                        analysis.performanceDrivers.forEachIndexed { i, driver -> BulletItem(text = driver, index = i + 1) }
                    }
                }
                item {
                    AnalysisSectionCard(title = "⚡ Engagement Triggers", accentColor = YellowAccent) {
                        analysis.engagementTriggers.forEach { trigger -> TagItem(text = trigger, color = YellowAccent) }
                    }
                }
                item {
                    AnalysisSectionCard(title = "🔁 Patterns", accentColor = GreenAccent) {
                        analysis.patterns.forEachIndexed { i, pattern -> BulletItem(text = pattern, index = i + 1, accentColor = GreenAccent) }
                    }
                }
                item { Text(text = "💡 Content Ideas", color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Bold) }
                items(analysis.ideas.indices.toList()) { index ->
                    ContentIdeaCard(idea = analysis.ideas[index], isBestFit = index == analysis.bestFitIndex)
                }
                item {
                    Box(
                        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp))
                            .background(GreenAccent.copy(alpha = 0.07f))
                            .border(1.dp, GreenAccent.copy(alpha = 0.25f), RoundedCornerShape(16.dp))
                            .padding(14.dp)
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(text = "🏆 Best Fit Recommendation", color = GreenAccent, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            Text(text = analysis.bestFitReason, color = White82.copy(alpha = 0.75f), fontSize = 12.sp, lineHeight = 18.sp)
                        }
                    }
                }
                item {
                    Box(
                        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp))
                            .background(Purple.copy(alpha = 0.07f))
                            .border(1.dp, Purple.copy(alpha = 0.25f), RoundedCornerShape(16.dp))
                            .padding(14.dp)
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(text = "🎯 Optimization Tip", color = Purple, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            Text(text = analysis.optimizationSuggestion, color = White82.copy(alpha = 0.75f), fontSize = 12.sp, lineHeight = 18.sp)
                        }
                    }
                }
                item {
                    Box(
                        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp))
                            .background(Brush.linearGradient(listOf(Purple.copy(alpha = 0.15f), PurpleDim.copy(alpha = 0.10f))))
                            .border(1.dp, CardBorder, RoundedCornerShape(16.dp))
                            .clickable(onClick = onChatClick)
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            Box(
                                modifier = Modifier.size(36.dp).clip(CircleShape).background(Brush.linearGradient(listOf(Purple, PurpleDim))),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Rounded.Chat, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                            }
                            Column {
                                Text(text = "Continue in Chat", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                                Text(text = "Ask AI to refine this draft further", color = PurpleMuted, fontSize = 11.sp)
                            }
                            Spacer(modifier = Modifier.weight(1f))
                            Icon(Icons.Rounded.ArrowForwardIos, contentDescription = null, tint = PurpleMuted, modifier = Modifier.size(14.dp))
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}

// ─────────────────────────────────────────────
// Analysis Sub-components
// ─────────────────────────────────────────────
@Composable
private fun AnalysisSectionCard(title: String, accentColor: Color = Purple, content: @Composable ColumnScope.() -> Unit) {
    Box(
        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp))
            .background(CardBg).border(1.dp, accentColor.copy(alpha = 0.18f), RoundedCornerShape(16.dp)).padding(14.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(text = title, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
            content()
        }
    }
}

@Composable
private fun BulletItem(text: String, index: Int, accentColor: Color = Purple) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.Top) {
        Box(
            modifier = Modifier.padding(top = 3.dp).size(18.dp).clip(CircleShape).background(accentColor.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "$index", color = accentColor, fontSize = 9.sp, fontWeight = FontWeight.Bold)
        }
        Text(text = text, color = White82.copy(alpha = 0.75f), fontSize = 12.sp, lineHeight = 17.sp, modifier = Modifier.weight(1f))
    }
}

@Composable
private fun TagItem(text: String, color: Color = Purple) {
    Box(
        modifier = Modifier.clip(RoundedCornerShape(8.dp))
            .background(color.copy(alpha = 0.10f)).border(1.dp, color.copy(alpha = 0.25f), RoundedCornerShape(8.dp))
            .padding(horizontal = 10.dp, vertical = 5.dp)
    ) {
        Text(text = "• $text", color = color.copy(alpha = 0.85f), fontSize = 11.sp, lineHeight = 16.sp)
    }
}

@Composable
private fun ContentIdeaCard(idea: ContentIdea, isBestFit: Boolean) {
    Box(
        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp))
            .background(if (isBestFit) GreenAccent.copy(alpha = 0.06f) else CardBg)
            .border(1.dp, if (isBestFit) GreenAccent.copy(alpha = 0.40f) else CardBorder, RoundedCornerShape(16.dp))
            .padding(14.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Top) {
                Text(text = idea.concept, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, lineHeight = 18.sp, modifier = Modifier.weight(1f))
                if (isBestFit) {
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(
                        modifier = Modifier.clip(RoundedCornerShape(50.dp))
                            .background(GreenAccent.copy(alpha = 0.15f))
                            .border(1.dp, GreenAccent.copy(alpha = 0.4f), RoundedCornerShape(50.dp))
                            .padding(horizontal = 7.dp, vertical = 3.dp)
                    ) {
                        Text(text = "Best Fit", color = GreenAccent, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
            Row(
                verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(8.dp)).background(Purple.copy(alpha = 0.08f)).padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
                Icon(Icons.Rounded.FlashOn, contentDescription = null, tint = Purple, modifier = Modifier.size(12.dp))
                Text(text = idea.hook, color = PurpleMuted, fontSize = 11.sp, fontStyle = androidx.compose.ui.text.font.FontStyle.Italic)
            }
            Text(text = "Structure", color = PurpleMuted, fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
            idea.structure.forEachIndexed { i, step ->
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "${i + 1}.", color = PurpleMuted, fontSize = 10.sp)
                    Text(text = step, color = White82.copy(alpha = 0.70f), fontSize = 11.sp)
                }
            }
            HorizontalDivider(color = Color(0x10C060FF), thickness = 1.dp)
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                Text(text = "😊", fontSize = 11.sp)
                Text(text = idea.emotion, color = YellowAccent.copy(alpha = 0.85f), fontSize = 11.sp)
            }
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.Top) {
                Text(text = "💡", fontSize = 11.sp)
                Text(text = idea.whyItWorks, color = White82.copy(alpha = 0.60f), fontSize = 11.sp, lineHeight = 16.sp, modifier = Modifier.weight(1f))
            }
        }
    }
}

// ─────────────────────────────────────────────
// Header
// ─────────────────────────────────────────────
@Composable
private fun DraftHeader(totalCount: Int, readyCount: Int) {
    Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 20.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Column {
                Text(text = "Drafts", color = Color.White, fontSize = 26.sp, fontWeight = FontWeight.ExtraBold, letterSpacing = (-0.5).sp)
                Spacer(modifier = Modifier.height(2.dp))
                Text(text = "$totalCount total · $readyCount ready to post", color = PurpleMuted, fontSize = 12.sp)
            }
            Box(
                modifier = Modifier.clip(RoundedCornerShape(12.dp)).background(CardBg)
                    .border(1.dp, CardBorder, RoundedCornerShape(12.dp)).clickable { }.padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Icon(Icons.Rounded.Sort, contentDescription = "Sort", tint = PurpleMuted, modifier = Modifier.size(15.dp))
                    Text(text = "Sort", color = PurpleMuted, fontSize = 12.sp)
                }
            }
        }
    }
}

// ─────────────────────────────────────────────
// Filter Row
// ─────────────────────────────────────────────
@Composable
private fun FilterRow(selectedFilter: DraftStatus?, onFilterSelected: (DraftStatus) -> Unit) {
    Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        DraftStatus.entries.forEach { status ->
            val isSelected = selectedFilter == status
            val color = statusColors[status] ?: Purple
            val bgColor by animateColorAsState(targetValue = if (isSelected) color.copy(alpha = 0.18f) else Color(0x0AFFFFFF), animationSpec = tween(200), label = "filter_bg")
            val borderColor by animateColorAsState(targetValue = if (isSelected) color.copy(alpha = 0.55f) else Color(0x1AC060FF), animationSpec = tween(200), label = "filter_border")
            Box(
                modifier = Modifier.clip(RoundedCornerShape(50.dp)).background(bgColor)
                    .border(1.dp, borderColor, RoundedCornerShape(50.dp)).clickable { onFilterSelected(status) }.padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text(text = statusLabels[status] ?: status.name, color = if (isSelected) color else PurpleMuted, fontSize = 11.sp, fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal)
            }
        }
    }
}

// ─────────────────────────────────────────────
// Date Row helper
// ─────────────────────────────────────────────
@Composable
fun DateRow(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, date: String) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        Icon(icon, contentDescription = null, tint = PurpleMuted, modifier = Modifier.size(10.dp))
        Text(text = "$label ", color = PurpleMuted.copy(alpha = 0.6f), fontSize = 10.sp)
        Text(text = date, color = White82.copy(alpha = 0.6f), fontSize = 10.sp, fontWeight = FontWeight.Medium)
    }
}

// ─────────────────────────────────────────────
// Empty State
// ─────────────────────────────────────────────
@Composable
private fun EmptyState() {
    Column(modifier = Modifier.fillMaxWidth().padding(top = 60.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(text = "📭", fontSize = 48.sp)
        Text(text = "No drafts here", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        Text(text = "Tap + to start your first draft", color = PurpleMuted, fontSize = 13.sp)
    }
}

// ─────────────────────────────────────────────
// Bottom Nav
// ─────────────────────────────────────────────
private val draftNavItems = listOf("Home", "Drafts", "Chat", "Profile")
private val draftNavIcons = listOf(Icons.Rounded.Home, Icons.Rounded.Edit, Icons.Rounded.MailOutline, Icons.Rounded.Person)

@Composable
fun DraftBottomNavBar(selectedTab: Int, onTabSelected: (Int) -> Unit) {
    Box(
        modifier = Modifier.fillMaxWidth().height(72.dp).background(NavBg)
            .drawBehind { drawLine(color = Color(0x1AC060FF), start = Offset(0f, 0f), end = Offset(size.width, 0f), strokeWidth = 1.dp.toPx()) }
    ) {
        Row(modifier = Modifier.fillMaxSize().padding(bottom = 8.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceAround) {
            draftNavItems.forEachIndexed { index, label ->
                val isSelected = selectedTab == index
                Column(
                    modifier = Modifier.clip(RoundedCornerShape(12.dp))
                        .background(if (isSelected) Color(0x1FC060FF) else Color.Transparent)
                        .clickable { onTabSelected(index) }.padding(horizontal = 14.dp, vertical = 6.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Icon(imageVector = draftNavIcons[index], contentDescription = label, tint = if (isSelected) Purple else WhiteDim, modifier = Modifier.size(22.dp))
                    Text(text = label, color = if (isSelected) Purple else WhiteDim, fontSize = 10.sp)
                }
            }
        }
    }
}

// ─────────────────────────────────────────────
// Preview
// ─────────────────────────────────────────────
@Preview
@Composable
fun DraftPreviewScreen() {
    var currentScreen by remember { mutableStateOf("list") }
    var selectedDraft by remember { mutableStateOf(sampleDrafts[0]) }

    if (currentScreen == "list") {
        // Preview uses sample data directly (no ViewModel needed)
        Box(modifier = Modifier.fillMaxSize().background(bgGradient)) {
            LazyColumn(modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(bottom = 100.dp)) {
                item { DraftHeader(totalCount = sampleDrafts.size, readyCount = sampleDrafts.count { it.status == DraftStatus.READY }) }
                items(sampleDrafts, key = { it.id }) { draft ->
                    DraftCard(draft = draft, onClick = { selectedDraft = draft; currentScreen = "detail" })
                }
            }
        }
    } else {
        DraftDetailScreen(draft = selectedDraft, analysis = sampleAnalysis, onBack = { currentScreen = "list" })
    }
}