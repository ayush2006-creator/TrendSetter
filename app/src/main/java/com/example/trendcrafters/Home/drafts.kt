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
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController

// ─────────────────────────────────────────────
//  Data Model
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
//  Sample Data
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

// ─────────────────────────────────────────────
//  Colors & Theme
// ─────────────────────────────────────────────

private val BgDark        = Color(0xFF08000F)
private val BgTop         = Color(0xFF1E0035)
private val BgMid         = Color(0xFF0F001F)
private val Purple        = Color(0xFFC060FF)
private val PurpleDim     = Color(0xFF7C3AED)
private val White82       = Color(0xD1FFFFFF)
private val WhiteDim      = Color(0x59FFFFFF)
private val PurpleMuted   = Color(0x80C896FF)
private val NavBg         = Color(0xF5080010)
private val CardBg        = Color(0x0DFFFFFF)
private val CardBorder    = Color(0x1AC060FF)

private val statusColors = mapOf(
    DraftStatus.READY        to Color(0xFF6EE7B7),
    DraftStatus.IN_PROGRESS  to Color(0xFFFBBF24),
    DraftStatus.NEEDS_REVIEW to Color(0xFFFF7B7B)
)

private val statusLabels = mapOf(
    DraftStatus.READY        to "✅ Ready",
    DraftStatus.IN_PROGRESS  to "✏️ In Progress",
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
//  DraftScreen
// ─────────────────────────────────────────────

@Composable
fun DraftScreen(
    navHostController: NavHostController,
    drafts: List<Draft> = sampleDrafts,
    onDraftClick: (Draft) -> Unit = {},
    onNewDraft: () -> Unit = {}
) {
    var selectedFilter by remember { mutableStateOf<DraftStatus?>(null) }

    val filteredDrafts = remember(selectedFilter, drafts) {
        if (selectedFilter == null) drafts else drafts.filter { it.status == selectedFilter }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // Background
        Box(modifier = Modifier.fillMaxSize().background(bgGradient))

        // Glow top-left
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
            bottomBar = {
                DraftBottomNavBar(selectedTab = 1, onTabSelected = {})
            },
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
                    Icon(Icons.Rounded.Add, contentDescription = "New Draft",
                        tint = Color.White, modifier = Modifier.size(26.dp))
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
                // Header
                item {
                    DraftHeader(
                        totalCount = drafts.size,
                        readyCount = drafts.count { it.status == DraftStatus.READY }
                    )
                }

                // Filter chips
                item {
                    FilterRow(
                        selectedFilter = selectedFilter,
                        onFilterSelected = { selectedFilter = if (selectedFilter == it) null else it }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }

                // Draft cards
                items(filteredDrafts, key = { it.id }) { draft ->
                    DraftCard(
                        draft = draft,
                        onClick = { onDraftClick(draft) }
                    )
                }

                // Empty state
                if (filteredDrafts.isEmpty()) {
                    item { EmptyState() }
                }
            }
        }
    }
}

// ─────────────────────────────────────────────
//  Header
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
            // Sort button
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
                    Icon(Icons.Rounded.Sort, contentDescription = "Sort",
                        tint = PurpleMuted, modifier = Modifier.size(15.dp))
                    Text(text = "Sort", color = PurpleMuted, fontSize = 12.sp)
                }
            }
        }
    }
}

// ─────────────────────────────────────────────
//  Filter Row
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
//  Draft Card  ← MAIN COMPONENT
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
        // Left accent bar
        Box(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .width(3.dp)
                .fillMaxHeight()
                .clip(RoundedCornerShape(topStart = 20.dp, bottomStart = 20.dp))
                .background(
                    Brush.verticalGradient(
                        listOf(Purple, PurpleDim)
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 14.dp)
        ) {
            // ── Row 1: Platform + Status badge
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
                    Text(
                        text = draft.platform,
                        color = PurpleMuted,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                // Status badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50.dp))
                        .background(statusColor.copy(alpha = 0.12f))
                        .border(1.dp, statusColor.copy(alpha = 0.35f), RoundedCornerShape(50.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = statusLabel,
                        color = statusColor,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // ── Row 2: Topic title
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

            // ── Row 3: Description
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

            // ── Row 4: Tags
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                draft.tags.forEach { tag ->
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(50.dp))
                            .background(Color(0x0FC060FF))
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = tag,
                            color = Purple.copy(alpha = 0.75f),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // ── Divider
            HorizontalDivider(
                color = Color(0x12C060FF),
                thickness = 1.dp
            )

            Spacer(modifier = Modifier.height(10.dp))

            // ── Row 5: Dates + Actions
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Dates column
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    DateRow(icon = Icons.Rounded.CalendarToday, label = "Created", date = draft.createdDate)
                    DateRow(icon = Icons.Rounded.Update, label = "Updated", date = draft.updatedDate)
                }

                // Action buttons
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    // Edit
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(Color(0x14C060FF))
                            .border(1.dp, Color(0x2EC060FF), CircleShape)
                            .clickable { },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Rounded.Edit, contentDescription = "Edit",
                            tint = PurpleMuted, modifier = Modifier.size(15.dp))
                    }
                    // Delete
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(Color(0x14FF5555))
                            .border(1.dp, Color(0x2EFF5555), CircleShape)
                            .clickable { },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Rounded.Delete, contentDescription = "Delete",
                            tint = Color(0xFFFF7B7B), modifier = Modifier.size(15.dp))
                    }
                    // Share / Post
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(50.dp))
                            .background(
                                Brush.linearGradient(listOf(Purple, PurpleDim))
                            )
                            .clickable { }
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(Icons.Rounded.Send, contentDescription = "Post",
                                tint = Color.White, modifier = Modifier.size(13.dp))
                            Text(text = "Post", color = Color.White,
                                fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }
        }
    }
}

// ─────────────────────────────────────────────
//  Date Row helper
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
        Icon(icon, contentDescription = null,
            tint = PurpleMuted, modifier = Modifier.size(10.dp))
        Text(
            text = "$label  ",
            color = PurpleMuted.copy(alpha = 0.6f),
            fontSize = 10.sp,
            fontWeight = FontWeight.Normal
        )
        Text(
            text = date,
            color = White82.copy(alpha = 0.6f),
            fontSize = 10.sp,
            fontWeight = FontWeight.Medium
        )
    }
}
@Composable
fun DraftContent(
    modifier: Modifier = Modifier,
    drafts: List<Draft> = sampleDrafts,
    onDraftClick: (Draft) -> Unit = {},
    onNewDraft: () -> Unit = {}
) {
    var selectedFilter by remember { mutableStateOf<DraftStatus?>(null) }

    val filteredDrafts = remember(selectedFilter, drafts) {
        if (selectedFilter == null) drafts else drafts.filter { it.status == selectedFilter }
    }

    Box(modifier = modifier.fillMaxSize()) {
        // Glow top-left
        Box(
            modifier = Modifier
                .size(220.dp)
                .offset(x = (-40).dp, y = (-40).dp)
                .background(
                    Brush.radialGradient(listOf(Color(0x22C060FF), Color.Transparent)),
                    CircleShape
                )
        )

        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.BottomEnd
        ) {
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

            // FAB sitting above bottom nav
            Box(
                modifier = Modifier
                    .padding(end = 16.dp, bottom = 16.dp)
                    .size(54.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(listOf(Color(0xFFC060FF), Color(0xFF7C3AED)))
                    )
                    .clickable(onClick = onNewDraft),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Rounded.Add, contentDescription = "New Draft",
                    tint = Color.White, modifier = Modifier.size(26.dp)
                )
            }
        }
    }
}

// ─────────────────────────────────────────────
//  Empty State
// ─────────────────────────────────────────────

@Composable
private fun EmptyState() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 60.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(text = "📭", fontSize = 48.sp)
        Text(text = "No drafts here", color = Color.White,
            fontSize = 16.sp, fontWeight = FontWeight.Bold)
        Text(text = "Tap + to start your first draft",
            color = PurpleMuted, fontSize = 13.sp)
    }
}

// ─────────────────────────────────────────────
//  Bottom Nav (shared theme)
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
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 8.dp),
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
                    Text(
                        text = label,
                        color = if (isSelected) Purple else WhiteDim,
                        fontSize = 10.sp
                    )
                }
            }
        }
    }
}

// ─────────────────────────────────────────────
//  Preview
// ─────────────────────────────────────────────

