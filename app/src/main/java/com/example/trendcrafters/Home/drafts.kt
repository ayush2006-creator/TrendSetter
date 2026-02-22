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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
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
        description = "Deep dive into the routines that helped me grow from 0 to 50K followers. Covers sleep, content batching & mindset shifts.",
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
        description = "Editing workflow walkthrough using CapCut + Premiere. Template drops at the end for subscribers.",
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
        description = "Breaking down 7 hook structures that are dominating the algorithm right now. Examples included from top creators.",
        platform = "Instagram Reels",
        platformIcon = "📸",
        createdDate = "Feb 5, 2025",
        updatedDate = "Feb 14, 2025",
        status = DraftStatus.NEEDS_REVIEW,
        tags = listOf("#ViralHooks", "#Algorithm")
    ),
    Draft(
        id = "4",
        topic = "Brand Deal Red Flags (Don't Miss These!)",
        description = "What to look for before signing with any brand. Legal tips, rate card advice and real deal breakdowns.",
        platform = "YouTube Shorts",
        platformIcon = "▶️",
        createdDate = "Jan 28, 2025",
        updatedDate = "Feb 10, 2025",
        status = DraftStatus.IN_PROGRESS,
        tags = listOf("#BrandDeals", "#CreatorBusiness")
    ),
    Draft(
        id = "5",
        topic = "The Art of the Perfect Thumbnail",
        description = "Color theory, face expressions, and text hierarchy — everything behind my highest-CTR thumbnails dissected.",
        platform = "Instagram Reels",
        platformIcon = "📸",
        createdDate = "Jan 20, 2025",
        updatedDate = "Feb 1, 2025",
        status = DraftStatus.READY,
        tags = listOf("#Thumbnail", "#DesignTips")
    )
)

val sampleAnalysis = DraftAnalysis(
    performanceDrivers = listOf(
        "Immediate relatability through everyday student struggles",
        "Hook lines that mirror internal thoughts viewers already have",
        "Fast pacing with quick punchlines every 2–3 seconds",
        "Use of POV framing to simulate viewer experience",
        "Humor rooted in shared academic stress and procrastination"
    ),
    engagementTriggers = listOf(
        "Validation of student frustration",
        "Nostalgic recognition of college routines",
        "Taggable moments friends relate to",
        "Short tension → quick payoff structure"
    ),
    patterns = listOf(
        "POV format placing viewer inside the situation",
        "Hook begins with a thought students never say aloud",
        "Escalating micro-problems (assignment → attendance → surprise test)",
        "Use of silence + reaction shot for punchline",
        "Relatable academic pain points drive shares"
    ),
    ideas = listOf(
        ContentIdea(
            concept = "POV: You open your laptop to study and every distraction attacks at once",
            hook = "POV: you finally decide to study…",
            structure = listOf(
                "Open laptop with determined face",
                "Phone buzzes → reels notification",
                "Friend: 'chai peene chal?'",
                "Laptop updates for 37 minutes",
                "Cut to you scrolling memes instead"
            ),
            emotion = "Relatable frustration mixed with humor",
            whyItWorks = "Uses POV format and escalating micro-distractions — a pattern seen in high-performing student reels that drive shares among peers."
        ),
        ContentIdea(
            concept = "When the professor says 'surprise test' and your soul leaves your body",
            hook = "That moment a professor says 'take out a sheet…'",
            structure = listOf(
                "Professor voice in background",
                "Zoom on frozen face",
                "Heartbeat sound effect",
                "Flash cuts of blank notebook pages",
                "Final shot: writing name slowly in defeat"
            ),
            emotion = "Anxiety validation + comedic despair",
            whyItWorks = "Mirrors internal panic students experience, creating strong emotional recognition — a key engagement driver in viral campus reels."
        ),
        ContentIdea(
            concept = "Hostel nights be like: we plan to sleep early but end up solving life at 3 AM",
            hook = "We said 'sleep by 11 tonight'…",
            structure = listOf(
                "Clock shows 10:58 PM",
                "Friends discussing random topic",
                "Deep philosophical debate at 2 AM",
                "Maggi cooking montage",
                "Sunrise shot with regret"
            ),
            emotion = "Nostalgia & social bonding",
            whyItWorks = "Late-night hostel bonding moments are highly taggable and nostalgia-driven — a pattern that boosts shares and comments."
        )
    ),
    bestFitIndex = 2,
    bestFitReason = "Hostel bonding nostalgia content performs best for college pages because it encourages tagging friends and comment engagement.",
    optimizationSuggestion = "Replace comedic sound effects with heartbeat + silence, add close-up breathing audio → increases emotional intensity and viewer immersion."
)

// ─────────────────────────────────────────────
// Colors & Theme
// ─────────────────────────────────────────────
private val BgDark = Color(0xFF08000F)
private val BgTop = Color(0xFF1E0035)
private val BgMid = Color(0xFF0F001F)
private val Purple = Color(0xFFC060FF)
private val PurpleDim = Color(0xFF7C3AED)
private val White82 = Color(0xD1FFFFFF)
private val WhiteDim = Color(0x59FFFFFF)
private val PurpleMuted = Color(0x80C896FF)
private val NavBg = Color(0xF5080010)
private val CardBg = Color(0x0DFFFFFF)
private val CardBorder = Color(0x1AC060FF)
private val GreenAccent = Color(0xFF6EE7B7)
private val YellowAccent = Color(0xFFFBBF24)
private val RedAccent = Color(0xFFFF7B7B)

private val statusColors = mapOf(
    DraftStatus.READY to GreenAccent,
    DraftStatus.IN_PROGRESS to YellowAccent,
    DraftStatus.NEEDS_REVIEW to RedAccent
)
private val statusLabels = mapOf(
    DraftStatus.READY to "✅ Ready",
    DraftStatus.IN_PROGRESS to "✏️ In Progress",
    DraftStatus.NEEDS_REVIEW to "🔍 Needs Review"
)
private val bgGradient = Brush.linearGradient(
    colors = listOf(BgTop, BgMid, BgMid),
    start = Offset(0f, 0f),
    end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
)
private val fabBrush = Brush.linearGradient(
    colors = listOf(Color(0xFFC060FF), Color(0xFF7C3AED))
)

// ─────────────────────────────────────────────
// DraftScreen — Entry Point
// ─────────────────────────────────────────────


// ─────────────────────────────────────────────
// Draft List Content
// ─────────────────────────────────────────────
@Composable
private fun DraftListContent(
    drafts: List<Draft>,
    onDraftClick: (Draft) -> Unit,
    onNewDraft: () -> Unit
) {
    var selectedFilter by remember { mutableStateOf<DraftStatus?>(null) }
    val filteredDrafts = remember(selectedFilter, drafts) {
        if (selectedFilter == null) drafts else drafts.filter { it.status == selectedFilter }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Box(modifier = Modifier.fillMaxSize().background(bgGradient))
        Box(
            modifier = Modifier
                .size(220.dp)
                .offset(x = (-40).dp, y = (-40).dp)
                .background(
                    Brush.radialGradient(listOf(Color(0x22C060FF), Color.Transparent)),
                    CircleShape
                )
        )
        Scaffold(
            containerColor = Color.Transparent,
            bottomBar = { DraftBottomNavBar(selectedTab = 1, onTabSelected = {}) },
            floatingActionButton = {
                Box(
                    modifier = Modifier
                        .padding(bottom = 90.dp)
                        .size(54.dp)
                        .clip(CircleShape)
                        .background(fabBrush)
                        .clickable(onClick = onNewDraft),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Rounded.Add, contentDescription = "New Draft", tint = Color.White, modifier = Modifier.size(26.dp))
                }
            },
            floatingActionButtonPosition = FabPosition.End
        ) { innerPadding ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentPadding = PaddingValues(bottom = 24.dp)
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
                    DraftCard(draft = draft, onClick = { onDraftClick(draft) })
                }
                if (filteredDrafts.isEmpty()) {
                    item { EmptyState() }
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
        // Glow
        Box(
            modifier = Modifier
                .size(260.dp)
                .offset(x = (-50).dp, y = (-50).dp)
                .background(
                    Brush.radialGradient(listOf(Color(0x22C060FF), Color.Transparent)),
                    CircleShape
                )
        )

        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                // Top bar with back + chat
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(NavBg)
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    // Back button
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
                        Icon(
                            Icons.Rounded.ArrowBack,
                            contentDescription = "Back",
                            tint = PurpleMuted,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    // Title
                    Text(
                        text = "Draft Details",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.align(Alignment.Center)
                    )

                    // Chat icon button
                    Box(
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(listOf(Purple, PurpleDim))
                            )
                            .clickable(onClick = onChatClick),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Rounded.Chat,
                            contentDescription = "Chat",
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            },
        ) { innerPadding ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
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
                                // Platform chip
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
                                // Status badge
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
                            Text(
                                text = draft.topic,
                                color = Color.White,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.ExtraBold,
                                letterSpacing = (-0.3).sp
                            )
                            Text(
                                text = draft.description,
                                color = White82.copy(alpha = 0.65f),
                                fontSize = 13.sp,
                                lineHeight = 19.sp
                            )
                            // Tags
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
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(4.dp, 20.dp)
                                .clip(RoundedCornerShape(2.dp))
                                .background(Brush.verticalGradient(listOf(Purple, PurpleDim)))
                        )
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

                // ── Performance Drivers
                item {
                    AnalysisSectionCard(
                        title = "🚀 Performance Drivers",
                        accentColor = Purple
                    ) {
                        analysis.performanceDrivers.forEachIndexed { i, driver ->
                            BulletItem(text = driver, index = i + 1)
                        }
                    }
                }

                // ── Engagement Triggers
                item {
                    AnalysisSectionCard(
                        title = "⚡ Engagement Triggers",
                        accentColor = YellowAccent
                    ) {
                        analysis.engagementTriggers.forEach { trigger ->
                            TagItem(text = trigger, color = YellowAccent)
                        }
                    }
                }

                // ── Patterns
                item {
                    AnalysisSectionCard(
                        title = "🔁 Patterns",
                        accentColor = GreenAccent
                    ) {
                        analysis.patterns.forEachIndexed { i, pattern ->
                            BulletItem(text = pattern, index = i + 1, accentColor = GreenAccent)
                        }
                    }
                }

                // ── Content Ideas
                item {
                    Text(text = "💡 Content Ideas", color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                }

                items(analysis.ideas.indices.toList()) { index ->
                    val idea = analysis.ideas[index]
                    val isBestFit = index == analysis.bestFitIndex
                    ContentIdeaCard(idea = idea, isBestFit = isBestFit)
                }

                // ── Best Fit Recommendation
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
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

                // ── Optimization Suggestion
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
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

                // ── Bottom Chat CTA
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(Brush.linearGradient(listOf(Purple.copy(alpha = 0.15f), PurpleDim.copy(alpha = 0.10f))))
                            .border(1.dp, CardBorder, RoundedCornerShape(16.dp))
                            .clickable(onClick = onChatClick)
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(Brush.linearGradient(listOf(Purple, PurpleDim))),
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
private fun AnalysisSectionCard(
    title: String,
    accentColor: Color = Purple,
    content: @Composable ColumnScope.() -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(CardBg)
            .border(1.dp, accentColor.copy(alpha = 0.18f), RoundedCornerShape(16.dp))
            .padding(14.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(text = title, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
            content()
        }
    }
}

@Composable
private fun BulletItem(text: String, index: Int, accentColor: Color = Purple) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .padding(top = 3.dp)
                .size(18.dp)
                .clip(CircleShape)
                .background(accentColor.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "$index", color = accentColor, fontSize = 9.sp, fontWeight = FontWeight.Bold)
        }
        Text(
            text = text,
            color = White82.copy(alpha = 0.75f),
            fontSize = 12.sp,
            lineHeight = 17.sp,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun TagItem(text: String, color: Color = Purple) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(color.copy(alpha = 0.10f))
            .border(1.dp, color.copy(alpha = 0.25f), RoundedCornerShape(8.dp))
            .padding(horizontal = 10.dp, vertical = 5.dp)
    ) {
        Text(text = "• $text", color = color.copy(alpha = 0.85f), fontSize = 11.sp, lineHeight = 16.sp)
    }
}

@Composable
private fun ContentIdeaCard(idea: ContentIdea, isBestFit: Boolean) {
    val borderColor = if (isBestFit) GreenAccent.copy(alpha = 0.40f) else CardBorder
    val bgColor = if (isBestFit) GreenAccent.copy(alpha = 0.06f) else CardBg

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(bgColor)
            .border(1.dp, borderColor, RoundedCornerShape(16.dp))
            .padding(14.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Text(
                    text = idea.concept,
                    color = Color.White,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    lineHeight = 18.sp,
                    modifier = Modifier.weight(1f)
                )
                if (isBestFit) {
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(50.dp))
                            .background(GreenAccent.copy(alpha = 0.15f))
                            .border(1.dp, GreenAccent.copy(alpha = 0.4f), RoundedCornerShape(50.dp))
                            .padding(horizontal = 7.dp, vertical = 3.dp)
                    ) {
                        Text(text = "Best Fit", color = GreenAccent, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            // Hook
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(Purple.copy(alpha = 0.08f))
                    .padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
                Icon(Icons.Rounded.FlashOn, contentDescription = null, tint = Purple, modifier = Modifier.size(12.dp))
                Text(text = idea.hook, color = PurpleMuted, fontSize = 11.sp, fontStyle = androidx.compose.ui.text.font.FontStyle.Italic)
            }

            // Structure
            Text(text = "Structure", color = PurpleMuted, fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
            idea.structure.forEachIndexed { i, step ->
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "${i + 1}.", color = PurpleMuted, fontSize = 10.sp)
                    Text(text = step, color = White82.copy(alpha = 0.70f), fontSize = 11.sp)
                }
            }

            HorizontalDivider(color = Color(0x10C060FF), thickness = 1.dp)

            // Emotion
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "😊", fontSize = 11.sp)
                Text(text = idea.emotion, color = YellowAccent.copy(alpha = 0.85f), fontSize = 11.sp)
            }

            // Why it works
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.Top
            ) {
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
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Drafts",
                    color = Color.White,
                    fontSize = 26.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = (-0.5).sp
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "$totalCount total · $readyCount ready to post",
                    color = PurpleMuted,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Normal
                )
            }
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(CardBg)
                    .border(1.dp, CardBorder, RoundedCornerShape(12.dp))
                    .clickable { }
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
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
private fun FilterRow(
    selectedFilter: DraftStatus?,
    onFilterSelected: (DraftStatus) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        DraftStatus.entries.forEach { status ->
            val isSelected = selectedFilter == status
            val color = statusColors[status] ?: Purple
            val label = statusLabels[status] ?: status.name
            val bgColor by animateColorAsState(
                targetValue = if (isSelected) color.copy(alpha = 0.18f) else Color(0x0AFFFFFF),
                animationSpec = tween(200), label = "filter_bg"
            )
            val borderColor by animateColorAsState(
                targetValue = if (isSelected) color.copy(alpha = 0.55f) else Color(0x1AC060FF),
                animationSpec = tween(200), label = "filter_border"
            )
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(50.dp))
                    .background(bgColor)
                    .border(1.dp, borderColor, RoundedCornerShape(50.dp))
                    .clickable { onFilterSelected(status) }
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text(
                    text = label,
                    color = if (isSelected) color else PurpleMuted,
                    fontSize = 11.sp,
                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                )
            }
        }
    }
}

// ─────────────────────────────────────────────
// DraftContent (standalone, no Scaffold — embed in pager/tab)
// ─────────────────────────────────────────────
@Composable
fun DraftContent(
    viewModel: DraftViewModel,

    onDraftClick: (Draft) -> Unit = {},
    onNewDraft: () -> Unit = {}
) {
    val drafts by viewModel.drafts.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    // Fetch data when the screen loads
    LaunchedEffect(Unit) {
        viewModel.loadDrafts()
    }

    Box(modifier = Modifier.fillMaxSize().background(bgGradient)) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center),
                color = Purple
            )
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 80.dp)
            ) {
                item {
                    DraftHeader(
                        totalCount = drafts.size,
                        readyCount = drafts.count { it.status == DraftStatus.READY }
                    )
                }

                items(drafts, key = { it.id }) { draft ->
                    DraftCard(
                        draft = draft,
                        onClick = { onDraftClick(draft) }
                    )
                }

                if (drafts.isEmpty()) {
                    item { EmptyState() }
                }
            }
        }

        // FAB for New Draft

    }
}

// ─────────────────────────────────────────────
// Draft Card
// ─────────────────────────────────────────────
@Composable
fun DraftCard(draft: Draft, onClick: () -> Unit = {}) {
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
            Text(
                text = draft.description,
                color = White82.copy(alpha = 0.55f),
                fontSize = 12.sp,
                fontWeight = FontWeight.Normal,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                lineHeight = 17.sp
            )
            Spacer(modifier = Modifier.height(12.dp))
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
                    Box(
                        modifier = Modifier.size(32.dp).clip(CircleShape)
                            .background(Color(0x14C060FF))
                            .border(1.dp, Color(0x2EC060FF), CircleShape)
                            .clickable { },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Rounded.Edit, contentDescription = "Edit", tint = PurpleMuted, modifier = Modifier.size(15.dp))
                    }
                    Box(
                        modifier = Modifier.size(32.dp).clip(CircleShape)
                            .background(Color(0x14FF5555))
                            .border(1.dp, Color(0x2EFF5555), CircleShape)
                            .clickable { },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Rounded.Delete, contentDescription = "Delete", tint = Color(0xFFFF7B7B), modifier = Modifier.size(15.dp))
                    }
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
                            Text(text = "Post", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }
        }
    }
}

// ─────────────────────────────────────────────
// Date Row helper
// ─────────────────────────────────────────────
@Composable
private fun DateRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    date: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(icon, contentDescription = null, tint = PurpleMuted, modifier = Modifier.size(10.dp))
        Text(text = "$label ", color = PurpleMuted.copy(alpha = 0.6f), fontSize = 10.sp, fontWeight = FontWeight.Normal)
        Text(text = date, color = White82.copy(alpha = 0.6f), fontSize = 10.sp, fontWeight = FontWeight.Medium)
    }
}

// ─────────────────────────────────────────────
// Empty State
// ─────────────────────────────────────────────
@Composable
private fun EmptyState() {
    Column(
        modifier = Modifier.fillMaxWidth().padding(top = 60.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(text = "📭", fontSize = 48.sp)
        Text(text = "No drafts here", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        Text(text = "Tap + to start your first draft", color = PurpleMuted, fontSize = 13.sp)
    }
}

// ─────────────────────────────────────────────
// Bottom Nav
// ─────────────────────────────────────────────
private val draftNavItems = listOf("Home", "Drafts", "Chat", "Profile")
private val draftNavIcons = listOf(
    Icons.Rounded.Home,
    Icons.Rounded.Edit,
    Icons.Rounded.MailOutline,
    Icons.Rounded.Person
)

@Composable
private fun DraftBottomNavBar(selectedTab: Int, onTabSelected: (Int) -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(72.dp)
            .background(NavBg)
            .drawBehind {
                drawLine(
                    color = Color(0x1AC060FF),
                    start = Offset(0f, 0f),
                    end = Offset(size.width, 0f),
                    strokeWidth = 1.dp.toPx()
                )
            }
    ) {
        Row(
            modifier = Modifier.fillMaxSize().padding(bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            draftNavItems.forEachIndexed { index, label ->
                val isSelected = selectedTab == index
                Column(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isSelected) Color(0x1FC060FF) else Color.Transparent)
                        .clickable { onTabSelected(index) }
                        .padding(horizontal = 14.dp, vertical = 6.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Icon(
                        imageVector = draftNavIcons[index],
                        contentDescription = label,
                        tint = if (isSelected) Purple else WhiteDim,
                        modifier = Modifier.size(22.dp)
                    )
                    Text(text = label, color = if (isSelected) Purple else WhiteDim, fontSize = 10.sp)
                }
            }
        }
    }
}