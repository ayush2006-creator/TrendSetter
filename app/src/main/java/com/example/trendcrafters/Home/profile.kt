package com.example.trendcrafters.Home

import androidx.compose.foundation.*
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.PaintingStyle.Companion.Stroke
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel

// ─────────────────────────────────────────────
// DATA MODEL
// ─────────────────────────────────────────────

data class OnboardingProfile(
    val display_name: String,
    val creator_type: String,
    val organization_type: String?,
    val experience_level: String?,
    val platform: String,
    val audience_type: String,
    val goals: String?,
    val niches: List<String>
)

data class StatItem(val value: String, val label: String)

// ─────────────────────────────────────────────
// THEME COLORS
// ─────────────────────────────────────────────

private val Purple        = Color(0xFFC060FF)
private val PurpleAlpha12 = Color(0x1FC060FF)
private val PurpleAlpha25 = Color(0x40C060FF)
private val White82       = Color(0xD1FFFFFF)
private val PurpleMuted   = Color(0x80C896FF)

private val avatarRingBrush = Brush.linearGradient(
    colors = listOf(Color(0xFFC060FF), Color(0xFF7C3AED), Color(0xFFFF6AB0))
)

// ─────────────────────────────────────────────
// MAIN SCREEN
// ─────────────────────────────────────────────

@Composable
fun ProfileContent(
    modifier: Modifier = Modifier,
    onBack: () -> Unit = {},
    onEdit: () -> Unit = {},
    profileViewModel: ProfileViewModel = viewModel()
) {
    val uiState by profileViewModel.uiState.collectAsState()

    // Trigger profile load on entry
    LaunchedEffect(Unit) {
        profileViewModel.loadProfile()
    }

    // Determine what to display. If the backend 500s, uiState.onboardingProfile will be null.
    // We fallback to sampleProfile to keep the UI from crashing.
    val profile = uiState.onboardingProfile ?: sampleProfile

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
            .background(Color(0xFF0F001C)) // Dark background for the theme
    ) {

        // Error message handling (Visible if 500 error occurs)




        profile.experience_level?.let {
            AvatarSection(
                name = profile.display_name,
                creatorType = profile.creator_type,
                experienceLevel = it
            )
        }

        Spacer(Modifier.height(24.dp))

        profile.experience_level?.let {
            StatsRow(
                stats = listOf(
                    StatItem(it, "Experience"),
                    StatItem(profile.audience_type, "Audience"),
                    StatItem(profile.platform, "Platform")
                )
            )
        }

        Spacer(Modifier.height(24.dp))

        // Only show Organization if it's not null/blank
        if (!profile.organization_type.isNullOrBlank()) {
            SectionLabel("ORGANIZATION")
            Spacer(Modifier.height(6.dp))
            InfoCard(label = "Managed By", value = profile.organization_type)
            Spacer(Modifier.height(16.dp))
        }

        // Only show Goals if it's not null/blank
        if (!profile.goals.isNullOrBlank()) {
            SectionLabel("GOALS")
            Spacer(Modifier.height(8.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0x0AFFFFFF))
                    .padding(12.dp)
            ) {
                Text(
                    text = profile.goals,
                    fontSize = 14.sp,
                    color = White82,
                    lineHeight = 20.sp
                )
            }
            Spacer(Modifier.height(16.dp))
        }

        if (profile.niches.isNotEmpty()) {
            SectionLabel("NICHES")
            Spacer(Modifier.height(8.dp))
            HashtagStrip(profile.niches.map { "#${it.replace(" ", "")}" })
            Spacer(Modifier.height(16.dp))
        }

        Spacer(Modifier.height(40.dp))
    }
}

// ─────────────────────────────────────────────
// UI COMPONENTS (Header, Avatar, Stats, etc.)
// ─────────────────────────────────────────────

@Composable
private fun ProfileHeader(onBack: () -> Unit, onEdit: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 10.dp, start = 8.dp, end = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onBack) {
            Icon(Icons.Rounded.ArrowBackIos, null, tint = White82, modifier = Modifier.size(20.dp))
        }

        Text("Profile", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)

        IconButton(onClick = onEdit) {
            Icon(Icons.Rounded.Edit, null, tint = White82, modifier = Modifier.size(20.dp))
        }
    }
}

@Composable
private fun AvatarSection(name: String, creatorType: String, experienceLevel: String) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(100.dp)
                .drawBehind {
                    drawCircle(brush = avatarRingBrush, style = Stroke(width = 8f))
                }
                .padding(8.dp)
                .clip(CircleShape)
                .background(Color(0xFF1A0030)),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Rounded.Person, null, tint = Purple, modifier = Modifier.size(50.dp))
        }

        Spacer(Modifier.height(16.dp))
        Text(name, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color.White)
        Text(creatorType, color = PurpleMuted, fontSize = 14.sp)
        Spacer(Modifier.height(4.dp))
        Text(experienceLevel, color = Purple, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
private fun StatsRow(stats: List<StatItem>) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        stats.forEach { stat ->
            Box(
                Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0x0AFFFFFF))
                    .border(BorderStroke(1.dp, PurpleAlpha12), RoundedCornerShape(16.dp))
                    .padding(14.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(stat.value, fontWeight = FontWeight.Bold, color = Color.White, fontSize = 13.sp, textAlign = TextAlign.Center)
                    Spacer(Modifier.height(4.dp))
                    Text(stat.label, fontSize = 10.sp, color = PurpleMuted, fontWeight = FontWeight.Medium)
                }
            }
        }
    }
}

@Composable
private fun InfoCard(label: String, value: String) {
    Box(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0x0AFFFFFF))
            .border(BorderStroke(1.dp, PurpleAlpha25), RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Column {
            Text(label, fontSize = 10.sp, color = PurpleMuted, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(4.dp))
            Text(value, fontWeight = FontWeight.Medium, color = Color.White)
        }
    }
}

@Composable
private fun HashtagStrip(tags: List<String>) {
    Row(
        Modifier
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        tags.forEach { tag ->
            Box(
                Modifier
                    .clip(RoundedCornerShape(50.dp))
                    .background(Color(0x12B450FF))
                    .border(BorderStroke(1.dp, PurpleAlpha12), RoundedCornerShape(50.dp))
                    .padding(horizontal = 14.dp, vertical = 8.dp)
            ) {
                Text(tag, fontSize = 12.sp, color = Purple, fontWeight = FontWeight.Medium)
            }
        }
    }
}

@Composable
private fun SectionLabel(text: String) {
    Text(
        text = text,
        modifier = Modifier.padding(horizontal = 16.dp),
        fontSize = 12.sp,
        fontWeight = FontWeight.ExtraBold,
        color = PurpleMuted,
        letterSpacing = 1.sp
    )
}

// ─────────────────────────────────────────────
// SAMPLE DATA
// ─────────────────────────────────────────────

val sampleProfile = OnboardingProfile(
    display_name = "Ayush",
    creator_type = "Gaming",
    organization_type = null,
    experience_level = "1–2 years",
    audience_type = "Students",
    platform = "Instagram",
    goals = "Build a strong AI personal brand.",
    niches = listOf("AI", "Startups", "Tech")
)