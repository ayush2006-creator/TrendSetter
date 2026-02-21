package com.example.trendcrafters.Home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController

// ─────────────────────────────────────────────
//  Data Models
// ─────────────────────────────────────────────

data class OnboardingProfile(
    val creatingFor: List<String>,   // Q1
    val platforms: List<String>,     // Q2
    val experience: String,          // Q3
    val followerRange: String,       // Q4
    val maxViews: String,            // Q5
    val breakthrough: String         // Q6
)

data class LevelBadge(val label: String, val color: Color)
data class BreakthroughInfo(val icon: String, val text: String)
data class PlatformInfo(val icon: String, val color: Color)
data class StatItem(val value: String, val label: String)
data class MenuItem(val label: String, val icon: String)

// ─────────────────────────────────────────────
//  Mappings  (mirrors the JS maps)
// ─────────────────────────────────────────────

private val followerBadgeMap = mapOf(
    "0 – 1K"      to LevelBadge("🐣 Rising",       Color(0xFF6EE7B7)),
    "1K – 10K"    to LevelBadge("📊 Growing",      Color(0xFF60A5FA)),
    "10K – 50K"   to LevelBadge("🔥 Heating Up",   Color(0xFFFB923C)),
    "50K – 100K"  to LevelBadge("🚀 Established",  Color(0xFFA78BFA)),
    "100K+"       to LevelBadge("👑 Pro Creator",   Color(0xFFFBBF24))
)

private val breakthroughMap = mapOf(
    "Yes — consistently"  to BreakthroughInfo("🏆", "Consistent Hits"),
    "Sometimes it works"  to BreakthroughInfo("🤞", "Hit or Miss"),
    "Not yet"             to BreakthroughInfo("🔍", "Still Searching"),
    "Still experimenting" to BreakthroughInfo("🧪", "Experimenting")
)

private val experienceIconMap = mapOf(
    "Just starting" to "🌱",
    "< 6 months"    to "⏳",
    "6–12 months"   to "📆",
    "1–2 years"     to "📈",
    "2+ years"      to "🏆"
)

private val platformInfoMap = mapOf(
    "Instagram Reels"    to PlatformInfo("📸", Color(0xFFE1306C)),
    "YouTube Shorts"     to PlatformInfo("▶️", Color(0xFFFF4444)),
    "Snapchat Spotlight" to PlatformInfo("👻", Color(0xFFD4B800)),
    "Facebook Reels"     to PlatformInfo("📘", Color(0xFF1877F2)),
    "Multiple Platforms" to PlatformInfo("🌐", Color(0xFFA78BFA))
)

// ─────────────────────────────────────────────
//  Theme colors
// ─────────────────────────────────────────────

private val BgDark        = Color(0xFF08000F)
private val BgTop         = Color(0xFF1E0035)
private val BgMid         = Color(0xFF0F001F)
private val BgBottom      = Color(0xFF1A0030)
private val Purple        = Color(0xFFC060FF)
private val PurpleDim     = Color(0xFF7C3AED)
private val PurpleAlpha12 = Color(0x1FC060FF)
private val PurpleAlpha25 = Color(0x40C060FF)
private val White82       = Color(0xD1FFFFFF)
private val WhiteDim      = Color(0x59FFFFFF)
private val PurpleMuted   = Color(0x80C896FF)
private val NavBg         = Color(0xF5080010)

private val bgGradient = Brush.linearGradient(
    colors = listOf(BgTop, BgMid, BgBottom),
    start = Offset(0f, 0f),
    end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
)

private val avatarRingBrush = Brush.linearGradient(
    colors = listOf(Color(0xFFC060FF), Color(0xFF7C3AED), Color(0xFFFF6AB0))
)

private val fabBrush = Brush.linearGradient(
    colors = listOf(Color(0xFFC060FF), Color(0xFF7C3AED))
)

// ─────────────────────────────────────────────
//  ProfileScreen  (main entry)
// ─────────────────────────────────────────────

@Composable
fun ProfileScreen(
    navHostController: NavHostController,
    profile: OnboardingProfile = sampleProfile,
    onBack: () -> Unit = {},
    onEdit: () -> Unit = {}
) {
    val badge        = followerBadgeMap[profile.followerRange] ?: LevelBadge("🐣 Rising", Color(0xFF6EE7B7))
    val breakthrough = breakthroughMap[profile.breakthrough]  ?: BreakthroughInfo("🔍", "Still Searching")
    val expIcon      = experienceIconMap[profile.experience]  ?: "📈"
    var selectedNavIndex by remember { mutableStateOf(3) }

    val stats = listOf(
        StatItem(profile.followerRange, "Followers"),
        StatItem(profile.maxViews,      "Max Views"),
        StatItem(profile.experience,    "Experience")
    )

    val hashtags = buildList {
        profile.platforms.forEach { p ->
            add("#" + p.replace(" Reels", "").replace(" Shorts", "Shorts").replace(" ", ""))
        }
        profile.creatingFor.forEach { c ->
            add("#" + c.removePrefix("Myself (").removeSuffix(")").replace(" ", ""))
        }
        add("#ContentCreator"); add("#Trending"); add("#ViralVideo")
    }

    val accountMenu = listOf(
        MenuItem("Subscription",         "⚡"),
        MenuItem("Restore Subscription", "🔄"),
        MenuItem("Terms of use",         "📄"),
        MenuItem("Privacy policy",       "🔒")
    )
    val moreMenu = listOf(
        MenuItem("Rate app",    "⭐"),
        MenuItem("Contact us",  "💬"),
        MenuItem("Log out",     "🚪")
    )

    var selectedTab by remember { mutableStateOf(3) } // 0=Home 1=Drafts 2=Chat 3=Profile

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BgDark)
    ) {
        // ── Background gradient
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(bgGradient)
        )

        // ── Glow orbs
        Box(
            modifier = Modifier
                .size(260.dp)
                .offset(x = 60.dp, y = (-60).dp)
                .align(Alignment.TopCenter)
                .background(
                    Brush.radialGradient(
                        colors = listOf(Color(0x29A03CFF), Color.Transparent)
                    ),
                    CircleShape
                )
        )
        Box(
            modifier = Modifier
                .size(180.dp)
                .align(Alignment.BottomEnd)
                .offset(x = 40.dp, y = (-80).dp)
                .background(
                    Brush.radialGradient(
                        colors = listOf(Color(0x1A7828DC), Color.Transparent)
                    ),
                    CircleShape
                )
        )


        // ── Main layout
        Scaffold(
            containerColor = Color.Transparent,
            bottomBar = {
                CustomBottomNavBar(
                    selectedIndex = selectedNavIndex,
                    onItemSelected = { selectedNavIndex = it }
                )
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState())
            ) {
                // Header
                ProfileHeader(onBack = onBack, onEdit = onEdit)

                // Avatar + identity
                AvatarSection(
                    name = "Ms.Charlotte",
                    creatingFor = profile.creatingFor,
                    badge = badge
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Stats row
                StatsRow(stats = stats)

                Spacer(modifier = Modifier.height(14.dp))

                // Platforms
                SectionLabel(text = "ACTIVE PLATFORMS", modifier = Modifier.padding(horizontal = 16.dp))
                Spacer(modifier = Modifier.height(8.dp))
                PlatformsRow(platforms = profile.platforms)

                Spacer(modifier = Modifier.height(10.dp))

                // Info cards row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    InfoCard(
                        label = "CONTENT STATUS",
                        value = "${breakthrough.icon} ${breakthrough.text}",
                        modifier = Modifier.weight(1f)
                    )
                    InfoCard(
                        label = "Drafts",
                        value = "10",
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Hashtag strip
                HashtagStrip(hashtags = hashtags)

                Spacer(modifier = Modifier.height(14.dp))

                // Menu groups
                MenuGroup(title = "ACCOUNT", items = accountMenu)
                Spacer(modifier = Modifier.height(12.dp))
                MenuGroup(title = "MORE", items = moreMenu)
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}
@Composable
fun ProfileContent(
    modifier: Modifier = Modifier,
    onBack: () -> Unit = {},
    onEdit: () -> Unit = {},
    profileViewModel: ProfileViewModel = viewModel()       // <-- inject ViewModel
) {
    val uiState by profileViewModel.uiState.collectAsState()

    // Fetch profile when this composable first enters composition
    LaunchedEffect(Unit) {
        profileViewModel.loadProfile()
    }

    // Fall back to sampleProfile while loading or on error
    val profile      = uiState.onboardingProfile ?: sampleProfile
    val displayName  = uiState.profile?.displayName ?: "Ms.Charlotte"

    val badge        = followerBadgeMap[profile.followerRange] ?: LevelBadge("🐣 Rising", Color(0xFF6EE7B7))
    val breakthrough = breakthroughMap[profile.breakthrough]   ?: BreakthroughInfo("🔍", "Still Searching")

    val stats = listOf(
        StatItem(profile.followerRange, "Followers"),
        StatItem(profile.maxViews,      "Max Views"),
        StatItem(profile.experience,    "Experience")
    )

    // Use real niches from API, fall back to computed hashtags
    val hashtags = if (uiState.profile?.niches?.isNotEmpty() == true) {
        uiState.profile!!.niches.map { "#${it.name.replace(" ", "")}" } +
                listOf("#ContentCreator", "#Trending", "#ViralVideo")
    } else {
        buildList {
            profile.platforms.forEach { p ->
                add("#" + p.replace(" Reels", "").replace(" Shorts", "Shorts").replace(" ", ""))
            }
            profile.creatingFor.forEach { c ->
                add("#" + c.removePrefix("Myself (").removeSuffix(")").replace(" ", ""))
            }
            add("#ContentCreator"); add("#Trending"); add("#ViralVideo")
        }
    }

    val accountMenu = listOf(
        MenuItem("Subscription",         "⚡"),
        MenuItem("Restore Subscription", "🔄"),
        MenuItem("Terms of use",         "📄"),
        MenuItem("Privacy policy",       "🔒")
    )
    val moreMenu = listOf(
        MenuItem("Rate app",   "⭐"),
        MenuItem("Contact us", "💬"),
        MenuItem("Log out",    "🚪")
    )

    // ── Loading state ──────────────────────────────────────────────────────────
    if (uiState.isLoading) {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = Purple)
        }
        return
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {

        // ── Error banner ───────────────────────────────────────────────────────
        uiState.errorMessage?.let { msg ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0x33FF5370))
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = msg,
                    color = Color(0xFFFF5370),
                    fontSize = 13.sp,
                    textAlign = TextAlign.Center
                )
            }
        }

        ProfileHeader(onBack = onBack, onEdit = onEdit)

        AvatarSection(
            name        = displayName,
            creatingFor = profile.creatingFor,
            badge       = badge
        )

        Spacer(modifier = Modifier.height(16.dp))
        StatsRow(stats = stats)
        Spacer(modifier = Modifier.height(14.dp))

        SectionLabel(
            text     = "ACTIVE PLATFORMS",
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        PlatformsRow(platforms = profile.platforms)
        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            InfoCard(
                label    = "CONTENT STATUS",
                value    = "${breakthrough.icon} ${breakthrough.text}",
                modifier = Modifier.weight(1f)
            )
            // Show real niche count from API, fall back to "Drafts"
            val nichesCount = uiState.profile?.niches?.size
            InfoCard(
                label    = if (nichesCount != null) "NICHES" else "Drafts",
                value    = if (nichesCount != null) "$nichesCount Active" else "10",
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(14.dp))
        HashtagStrip(hashtags = hashtags)
        Spacer(modifier = Modifier.height(14.dp))

        MenuGroup(title = "ACCOUNT", items = accountMenu)
        Spacer(modifier = Modifier.height(12.dp))
        MenuGroup(title = "MORE", items = moreMenu)
        Spacer(modifier = Modifier.height(24.dp))
    }
}

// ─────────────────────────────────────────────
//  Header
// ─────────────────────────────────────────────

@Composable
private fun ProfileHeader(onBack: () -> Unit, onEdit: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        IconButtonSquare(onClick = onBack) {
            Icon(Icons.Rounded.ArrowBackIos, contentDescription = "Back",
                tint = White82, modifier = Modifier.size(18.dp))
        }
        Text(
            text = "Profile",
            color = Color.White,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.3.sp
        )
        IconButtonSquare(onClick = onEdit) {
            Icon(Icons.Rounded.Edit, contentDescription = "Edit",
                tint = White82, modifier = Modifier.size(18.dp))
        }
    }
}

@Composable
private fun IconButtonSquare(onClick: () -> Unit, content: @Composable () -> Unit) {
    Box(
        modifier = Modifier
            .size(36.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0x0DFFFFFF))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) { content() }
}

// ─────────────────────────────────────────────
//  Avatar section
// ─────────────────────────────────────────────

@Composable
private fun AvatarSection(
    name: String,
    creatingFor: List<String>,
    badge: LevelBadge
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Avatar with gradient ring + vinyl badge
        Box(contentAlignment = Alignment.BottomEnd) {
            // Gradient ring
            Box(
                modifier = Modifier
                    .size(90.dp)
                    .drawBehind {
                        drawCircle(brush = avatarRingBrush)
                    }
                    .padding(3.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF1A0030)),
                contentAlignment = Alignment.Center
            ) {
                // Placeholder user icon
                Icon(
                    imageVector = Icons.Rounded.Person,
                    contentDescription = "Avatar",
                    tint = Color(0x80B464FF),
                    modifier = Modifier.size(48.dp)
                )
            }

            // Vinyl badge
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .offset(x = 4.dp, y = 4.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF1E0035))
                    .drawBehind {
                        drawCircle(
                            color = Purple,
                            radius = size.minDimension / 2,
                            style = Stroke(width = 2.dp.toPx())
                        )
                    },
                contentAlignment = Alignment.Center
            ) {
                Text(text = "🎵", fontSize = 14.sp)
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Name
        Text(
            text = name,
            color = Color.White,
            fontSize = 21.sp,
            fontWeight = FontWeight.ExtraBold,
            letterSpacing = (-0.3).sp
        )

        Spacer(modifier = Modifier.height(7.dp))

        // Role chips (Q1)
        Row(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier.padding(horizontal = 30.dp)
        ) {
            creatingFor.forEach { role ->
                RoleChip(text = role)
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Level badge (Q4)
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(50.dp))
                .background(badge.color.copy(alpha = 0.09f))
                .border(border = BorderStroke(1.5.dp, badge.color.copy(alpha = 0.33f)), shape = RoundedCornerShape(50.dp))
                .padding(horizontal = 16.dp, vertical = 5.dp)
        ) {
            Text(
                text = badge.label,
                color = badge.color,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun RoleChip(text: String) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(50.dp))
            .background(Color(0x1AB450FF))
            .border(BorderStroke(1.dp, Color(0x47B464FF)), RoundedCornerShape(50.dp))
            .padding(horizontal = 10.dp, vertical = 3.dp)
    ) {
        Text(text = text, color = Color(0xE6D2A0FF), fontSize = 11.sp, fontWeight = FontWeight.Medium)
    }
}

// ─────────────────────────────────────────────
//  Stats Row
// ─────────────────────────────────────────────

@Composable
private fun StatsRow(stats: List<StatItem>) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        stats.forEach { stat ->
            StatCard(stat = stat, modifier = Modifier.weight(1f))
        }
    }
}

@Composable
private fun StatCard(stat: StatItem, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(Color(0x0AFFFFFF))
            .border(BorderStroke(1.dp, PurpleAlpha12), RoundedCornerShape(14.dp))
            .padding(vertical = 13.dp, horizontal = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = stat.value,
                color = Color.White,
                fontSize = 13.sp,
                fontWeight = FontWeight.ExtraBold,
                textAlign = TextAlign.Center,
                lineHeight = 16.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = stat.label,
                color = PurpleMuted,
                fontSize = 10.sp,
                fontWeight = FontWeight.Normal,
                textAlign = TextAlign.Center
            )
        }
    }
}

// ─────────────────────────────────────────────
//  Platforms Row
// ─────────────────────────────────────────────

@Composable
private fun PlatformsRow(platforms: List<String>) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(7.dp)
    ) {
        platforms.forEach { platform ->
            val info = platformInfoMap[platform] ?: PlatformInfo("🌐", Color(0xFFA78BFA))
            PlatformChip(icon = info.icon, label = platform, color = info.color)
        }
    }
}

@Composable
private fun PlatformChip(icon: String, label: String, color: Color) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(50.dp))
            .background(color.copy(alpha = 0.08f))
            .border(BorderStroke(1.5.dp, color.copy(alpha = 0.33f)), RoundedCornerShape(50.dp))
            .padding(horizontal = 13.dp, vertical = 7.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Text(text = icon, fontSize = 13.sp)
        Text(text = label, color = White82, fontSize = 12.sp, fontWeight = FontWeight.Normal)
    }
}

// ─────────────────────────────────────────────
//  Info Cards
// ─────────────────────────────────────────────

@Composable
private fun InfoCard(label: String, value: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(Color(0x0AFFFFFF))
            .border(BorderStroke(1.dp, PurpleAlpha12), RoundedCornerShape(14.dp))
            .padding(12.dp)
    ) {
        Column {
            Text(
                text = label,
                color = PurpleMuted.copy(alpha = 0.5f),
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.2.sp
            )
            Spacer(modifier = Modifier.height(5.dp))
            Text(
                text = value,
                color = White82,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

// ─────────────────────────────────────────────
//  Hashtag Strip
// ─────────────────────────────────────────────

@Composable
private fun HashtagStrip(hashtags: List<String>) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(7.dp)
    ) {
        hashtags.forEach { tag ->
            HashChip(text = tag)
        }
    }
}

@Composable
private fun HashChip(text: String) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(50.dp))
            .background(Color(0x12B450FF))
            .border(BorderStroke(1.dp, Color(0x38B464FF)), RoundedCornerShape(50.dp))
            .padding(horizontal = 11.dp, vertical = 5.dp)
    ) {
        Text(
            text = text,
            color = Color(0xD9C896FF),
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

// ─────────────────────────────────────────────
//  Section Label
// ─────────────────────────────────────────────

@Composable
private fun SectionLabel(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text,
        color = PurpleMuted.copy(alpha = 0.5f),
        fontSize = 10.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = 1.5.sp,
        modifier = modifier
    )
}

// ─────────────────────────────────────────────
//  Menu Group
// ─────────────────────────────────────────────

@Composable
private fun MenuGroup(title: String, items: List<MenuItem>) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(Color(0x09FFFFFF))
            .border(BorderStroke(1.dp, Color(0x17B464FF)), RoundedCornerShape(18.dp))
    ) {
        Text(
            text = title,
            color = PurpleMuted.copy(alpha = 0.45f),
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.5.sp,
            modifier = Modifier.padding(start = 18.dp, top = 12.dp, bottom = 4.dp)
        )
        items.forEachIndexed { index, item ->
            MenuRow(item = item)
            if (index < items.lastIndex) {
                HorizontalDivider(
                    color = Color(0x12B464FF),
                    thickness = 1.dp,
                    modifier = Modifier.padding(horizontal = 18.dp)
                )
            }
        }
    }
}

@Composable
private fun MenuRow(item: MenuItem) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { }
            .padding(horizontal = 18.dp, vertical = 13.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0x1AB050FF)),
                contentAlignment = Alignment.Center
            ) {
                Text(text = item.icon, fontSize = 14.sp)
            }
            Text(text = item.label, color = White82, fontSize = 14.sp)
        }
        Icon(
            imageVector = Icons.Rounded.ChevronRight,
            contentDescription = null,
            tint = Purple.copy(alpha = 0.5f),
            modifier = Modifier.size(18.dp)
        )
    }
}

// ─────────────────────────────────────────────
//  Bottom Nav Bar
// ─────────────────────────────────────────────

private val navItems = listOf("Home", "Drafts", "Chat", "Profile")
private val navIcons = listOf(
    Icons.Rounded.Home,
    Icons.Rounded.Edit,
    Icons.Rounded.MailOutline,
    Icons.Rounded.Person
)

@Composable
private fun BottomNavBar(selectedTab: Int, onTabSelected: (Int) -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
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
                .padding(bottom = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            navItems.forEachIndexed { index, label ->
                val isSelected = selectedTab == index

                // Insert FAB in the middle (between index 1 and 2)
                if (index == 2) { FabButton() }

                Column(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isSelected) Color(0x1FC060FF) else Color.Transparent)
                        .clickable { onTabSelected(index) }
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Icon(
                        imageVector = navIcons[index],
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

@Composable
private fun FabButton() {
    Box(
        modifier = Modifier
            .size(50.dp)
            .clip(CircleShape)
            .background(fabBrush)
            .clickable { },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Rounded.Add,
            contentDescription = "Create",
            tint = Color.White,
            modifier = Modifier.size(26.dp)
        )
    }
}

// ─────────────────────────────────────────────
//  Extension for border (without Material3 import conflict)
// ─────────────────────────────────────────────

private fun Modifier.border(border: BorderStroke, shape: Shape): Modifier =
    this.then(
        Modifier.drawBehind {
            val strokeWidth = border.width.toPx()
            drawRoundRect(
                brush = border.brush,
                size = size,
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(
                    (size.minDimension / 2).coerceAtMost(50.dp.toPx())
                ),
                style = Stroke(width = strokeWidth)
            )
        }
    )

// ─────────────────────────────────────────────
//  Sample data (replace with real ViewModel)
// ─────────────────────────────────────────────

val sampleProfile = OnboardingProfile(
    creatingFor  = listOf("Myself (Personal Brand)", "Influencer"),
    platforms    = listOf("Instagram Reels", "YouTube Shorts"),
    experience   = "1–2 years",
    followerRange = "10K – 50K",
    maxViews     = "10K – 100K",
    breakthrough = "Sometimes it works"
)

// ─────────────────────────────────────────────
//  Preview
// ─────────────────────────────────────────────

