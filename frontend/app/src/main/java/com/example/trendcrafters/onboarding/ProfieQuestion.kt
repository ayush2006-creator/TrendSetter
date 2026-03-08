package com.example.trendcrafters.onboarding

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.trendcrafters.Auth.DeepPurple
import com.example.trendcrafters.Profile.ProfileCreateUpdateRequest
import com.example.trendcrafters.Profile.ProfileService
import com.example.trendcrafters.Auth.AuthResult
import kotlinx.coroutines.launch

// --- Theme Colors ---
val NeonPurple = Color(0xFF9D4EDD)
val NeonBlue = Color(0xFF4CC9F0)
val DarkBackgroundStart = Color(0xFF1E0A25)
val DarkBackgroundEnd = Color(0xFF000000)
val CardBackground = Color(0xFF1A1A1A)
val TextWhite = Color(0xFFFFFFFF)
val TextGray = Color(0xFFAAAAAA)

// --- Data Models ---
data class ProfileOption(val id: String, val text: String, val emoji: String)

data class OnboardingQuestion(
    val id: Int,
    val questionText: String,
    val options: List<ProfileOption>,
    val allowMultiSelect: Boolean = false
)

// --- Mock Data ---
val onboardingQuestions = listOf(
    OnboardingQuestion(
        id = 1,
        questionText = "What best describes your content?",
        options = listOf(
            ProfileOption("Campus / Student Life", "Campus / Student Life", "🎓"),
            ProfileOption("Fitness & Health", "Fitness & Health", "💪"),
            ProfileOption("Photography / Creative", "Photography / Creative", "📸"),
            ProfileOption("Content Creator / Influencer", "Content Creator / Influencer", "🎥"),
            ProfileOption("Startup / Business", "Startup / Business", "💼"),
            ProfileOption("Lifestyle & Aesthetic", "Lifestyle & Aesthetic", "🎨"),
            ProfileOption("Comedy / Relatable", "Comedy / Relatable", "😂"),
            ProfileOption("Gaming", "Gaming", "🎮"),
            ProfileOption("Food / Cooking", "Food / Cooking", "🍳"),
            ProfileOption("Travel", "Travel", "✈️"),
            ProfileOption("Music / Dance", "Music / Dance", "🎵"),
            ProfileOption("Other", "Other", "✍️")
        ),
        allowMultiSelect = true  // niches can be multiple
    ),
    OnboardingQuestion(
        id = 2,
        questionText = "Who are you creating for?",
        options = listOf(
            ProfileOption("Students", "Students", "🎓"),
            ProfileOption("Young professionals", "Young professionals", "💼"),
            ProfileOption("Fitness enthusiasts", "Fitness enthusiasts", "💪"),
            ProfileOption("Creatives & artists", "Creatives & artists", "🎨"),
            ProfileOption("Entrepreneurs", "Entrepreneurs", "🚀"),
            ProfileOption("General audience", "General audience", "🌍"),
            ProfileOption("Local community", "Local community", "📍"),
            ProfileOption("Brand customers", "Brand customers", "🛍")
        )
    ),
    OnboardingQuestion(
        id = 3,
        questionText = "What do you want this content to achieve?",
        options = listOf(
            ProfileOption("Reach & virality", "Reach & virality", "🚀"),
            ProfileOption("Grow followers", "Grow followers", "📈"),
            ProfileOption("Engagement", "Engagement", "💬"),
            ProfileOption("Build authority", "Build authority", "🧠"),
            ProfileOption("Promote product/service", "Promote product/service", "🛍"),
            ProfileOption("Event promotion", "Event promotion", "🎟"),
            ProfileOption("Community building", "Community building", "🤝")
        )
    ),
    OnboardingQuestion(
        id = 4,
        questionText = "Which reel styles do you prefer?",
        options = listOf(
            ProfileOption("POV storytelling", "POV storytelling", "🎭"),
            ProfileOption("Before & after transformation", "Before & after transformation", "✨"),
            ProfileOption("Cinematic aesthetic", "Cinematic aesthetic", "🎬"),
            ProfileOption("Educational quick tips", "Educational quick tips", "📚"),
            ProfileOption("Funny / relatable", "Funny / relatable", "😂"),
            ProfileOption("Motivational", "Motivational", "🔥"),
            ProfileOption("Reaction / trend participation", "Reaction / trend participation", "📱")
        )
    ),
    OnboardingQuestion(
        id = 5,
        questionText = "Pick the vibe that fits you",
        options = listOf(
            ProfileOption("Fun & humorous", "Fun & humorous", "😄"),
            ProfileOption("Inspirational", "Inspirational", "✨"),
            ProfileOption("Professional & informative", "Professional & informative", "📊"),
            ProfileOption("Emotional & storytelling", "Emotional & storytelling", "💔"),
            ProfileOption("Aesthetic & calm", "Aesthetic & calm", "🌿"),
            ProfileOption("Bold & energetic", "Bold & energetic", "⚡")
        )
    ),
    OnboardingQuestion(
        id = 6,
        questionText = "How experienced are you with reels?",
        options = listOf(
            ProfileOption("Beginner", "Beginner", "🌱"),
            ProfileOption("Intermediate", "Intermediate", "📈"),
            ProfileOption("Advanced", "Advanced", "🏆")
        )
    ),
    OnboardingQuestion(
        id = 7,
        questionText = "Where do you mainly post?",
        options = listOf(
            ProfileOption("Instagram Reels", "Instagram Reels", "📸"),
            ProfileOption("YouTube Shorts", "YouTube Shorts", "▶️"),
            ProfileOption("Both", "Both", "🔄"),
            ProfileOption("Exploring both", "Exploring both", "🧭")
        )
    )
)

// ─────────────────────────────────────────────────────────────────────────────
// QuestionnaireHandler
// • onProfileSaved  → called after a successful POST /profile (navigate to Home)
// • displayName     → passed in from wherever you collected the user's name;
//                     falls back to "Creator" if empty
// ─────────────────────────────────────────────────────────────────────────────
@Composable
fun QuestionnaireHandler(
    displayName: String = "Creator",
    onProfileSaved: () -> Unit = {}
) {
    // Map<QuestionId, List<OptionId>> — using option text as the id for easy mapping
    val answers = remember { mutableStateMapOf<Int, List<String>>() }
    var currentQuestionIndex by remember { mutableIntStateOf(0) }
    var showContentInput by remember { mutableStateOf(false) }
    var contentDescription by remember { mutableStateOf("") }
    var isComplete by remember { mutableStateOf(false) }
    var isSaving by remember { mutableStateOf(false) }
    var saveError by remember { mutableStateOf<String?>(null) }

    val scope = rememberCoroutineScope()

    // Helper: build ProfileCreateUpdateRequest from collected answers
    fun buildProfileRequest(contentDesc: String): ProfileCreateUpdateRequest {
        // Q1 → niches (multi-select, values are option texts)
        val niches = answers[1] ?: emptyList()

        // Q2 → audience_type (first selection)
        val audienceType = answers[2]?.firstOrNull() ?: ""

        // Q3 → goals (join multiple if needed)
        val goals = answers[3]?.joinToString(", ") ?: ""

        // Q4 → creator_type (reel style → maps to a creator archetype)
        val creatorType = answers[4]?.firstOrNull() ?: "Content Creator"

        // Q5 → organization_type (tone/vibe stored here as a "style" tag)
        val organizationType = answers[5]?.firstOrNull()

        // Q6 → experience_level
        val experienceLevel = answers[6]?.firstOrNull() ?: "Beginner"

        // Q7 → platform
        val platform = answers[7]?.firstOrNull() ?: "Instagram Reels"

        // content description goes into goals (append)
        val fullGoals = if (contentDesc.isBlank()) goals
        else if (goals.isBlank()) contentDesc
        else "$goals | $contentDesc"

        return ProfileCreateUpdateRequest(
            displayName      = displayName,
            creatorType      = creatorType,
            organizationType = organizationType,
            experienceLevel  = experienceLevel,
            audienceType     = audienceType,
            platform         = platform,
            goals            = fullGoals,
            niches           = niches
        )
    }

    if (isComplete) {
        CompletionScreen(answers = answers, contentDescription = contentDescription)
        return
    }

    if (showContentInput) {
        ContentDescriptionScreen(
            isSaving  = isSaving,
            saveError = saveError,
            onSubmit  = { description ->
                contentDescription = description
                isSaving   = true
                saveError  = null
                scope.launch {
                    val request = buildProfileRequest(description)
                    when (val result = ProfileService.saveProfile(request)) {
                        is AuthResult.Success -> {
                            isSaving   = false
                            isComplete = true
                            onProfileSaved()          // ← navigate away
                        }
                        is AuthResult.Error -> {
                            isSaving   = false
                            saveError  = result.message
                        }
                    }
                }
            },
            onBack = { showContentInput = false }
        )
        return
    }

    QuestionnaireScreen(
        question        = onboardingQuestions[currentQuestionIndex],
        totalQuestions  = onboardingQuestions.size,
        currentProgress = currentQuestionIndex + 1,
        selectedOptions = answers[onboardingQuestions[currentQuestionIndex].id] ?: emptyList(),
        onOptionSelected = { questionId, optionId, isMultiSelect ->
            val current = answers[questionId] ?: emptyList()
            answers[questionId] = if (isMultiSelect) {
                if (current.contains(optionId)) current - optionId else current + optionId
            } else {
                listOf(optionId)
            }
        },
        onNext = {
            if (currentQuestionIndex < onboardingQuestions.size - 1) {
                currentQuestionIndex++
            } else {
                showContentInput = true
            }
        },
        onBack = {
            if (currentQuestionIndex > 0) currentQuestionIndex--
        }
    )
}

// ─────────────────────────────────────────────────────────────────────────────
// QuestionnaireScreen  (unchanged layout, option ids are now the text strings)
// ─────────────────────────────────────────────────────────────────────────────
@Composable
fun QuestionnaireScreen(
    question: OnboardingQuestion,
    totalQuestions: Int,
    currentProgress: Int,
    selectedOptions: List<String>,
    onOptionSelected: (Int, String, Boolean) -> Unit,
    onNext: () -> Unit,
    onBack: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(DarkBackgroundStart, DarkBackgroundEnd)))
            .padding(24.dp)
            .systemBarsPadding()
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = TextWhite)
                }
            }

            Spacer(Modifier.height(16.dp))
            CustomProgressBar(currentStep = currentProgress, totalSteps = totalQuestions)
            Spacer(Modifier.height(32.dp))

            Text(
                text       = question.questionText,
                fontSize   = 28.sp,
                fontWeight = FontWeight.Bold,
                color      = TextWhite,
                lineHeight = 36.sp
            )

            Spacer(Modifier.height(32.dp))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(question.options) { option ->
                    OptionCard(
                        option     = option,
                        isSelected = selectedOptions.contains(option.id),
                        onClick    = { onOptionSelected(question.id, option.id, question.allowMultiSelect) }
                    )
                }
                item { Spacer(Modifier.height(100.dp)) }
            }
        }

        if (selectedOptions.isNotEmpty()) {
            FloatingActionButton(
                onClick        = onNext,
                containerColor = NeonPurple,
                contentColor   = TextWhite,
                shape          = CircleShape,
                modifier       = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(bottom = 24.dp)
                    .size(64.dp)
            ) {
                Icon(Icons.Default.ArrowForward, contentDescription = "Next", modifier = Modifier.size(24.dp))
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// ContentDescriptionScreen  — now shows a loading spinner & error banner
// ─────────────────────────────────────────────────────────────────────────────
@Composable
fun ContentDescriptionScreen(
    isSaving: Boolean  = false,
    saveError: String? = null,
    onSubmit: (String) -> Unit,
    onBack: () -> Unit
) {
    var text by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(DarkBackgroundStart, DarkBackgroundEnd)))
            .padding(24.dp)
            .systemBarsPadding()
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                IconButton(onClick = onBack, enabled = !isSaving) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = TextWhite)
                }
            }

            Spacer(Modifier.height(16.dp))

            Text("Content Strategy", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = TextWhite)
            Spacer(Modifier.height(8.dp))
            Text(
                text       = "Describe the content which led to engagement or you think will get engagement.",
                fontSize   = 16.sp,
                color      = TextGray,
                lineHeight = 24.sp
            )

            Spacer(Modifier.height(32.dp))

            OutlinedTextField(
                value         = text,
                onValueChange = { text = it },
                enabled       = !isSaving,
                modifier      = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .background(CardBackground, RoundedCornerShape(16.dp)),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor   = NeonPurple,
                    unfocusedBorderColor = Color.Transparent,
                    focusedTextColor     = TextWhite,
                    unfocusedTextColor   = TextWhite,
                    cursorColor          = NeonPurple,
                    focusedContainerColor   = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    disabledBorderColor  = Color.Transparent,
                    disabledTextColor    = TextWhite.copy(alpha = 0.5f)
                ),
                shape       = RoundedCornerShape(16.dp),
                placeholder = {
                    Text(
                        "E.g., Behind-the-scenes vlogs, educational tutorials about crypto…",
                        color = TextGray.copy(alpha = 0.5f)
                    )
                }
            )

            // Error banner
            if (saveError != null) {
                Spacer(Modifier.height(16.dp))
                Text(
                    text     = saveError,
                    color    = Color(0xFFFF6B6B),
                    fontSize = 14.sp
                )
            }
        }

        // FAB: spinner while saving, checkmark when idle
        FloatingActionButton(
            onClick        = { if (!isSaving) onSubmit(text) },
            containerColor = NeonPurple,
            contentColor   = TextWhite,
            shape          = CircleShape,
            modifier       = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 24.dp)
                .size(64.dp)
        ) {
            if (isSaving) {
                CircularProgressIndicator(
                    color     = TextWhite,
                    modifier  = Modifier.size(28.dp),
                    strokeWidth = 3.dp
                )
            } else {
                Icon(Icons.Default.Check, contentDescription = "Finish", modifier = Modifier.size(24.dp))
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Shared UI components (unchanged)
// ─────────────────────────────────────────────────────────────────────────────
@Composable
fun OptionCard(option: ProfileOption, isSelected: Boolean, onClick: () -> Unit) {
    val borderColor     = if (isSelected) NeonPurple else Color.White.copy(alpha = 0.1f)
    val backgroundColor = if (isSelected) NeonPurple.copy(alpha = 0.15f) else CardBackground

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .border(1.dp, borderColor, RoundedCornerShape(16.dp))
            .background(backgroundColor)
            .clickable { onClick() }
            .padding(horizontal = 20.dp, vertical = 20.dp),
        verticalAlignment   = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(text = option.emoji, fontSize = 24.sp)
        Text(
            text       = option.text,
            fontSize   = 16.sp,
            color      = if (isSelected) TextWhite else TextGray,
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
            modifier   = Modifier.weight(1f)
        )
        if (isSelected) {
            Icon(Icons.Default.Check, contentDescription = "Selected", tint = NeonPurple, modifier = Modifier.size(20.dp))
        }
    }
}

@Composable
fun CustomProgressBar(currentStep: Int, totalSteps: Int) {
    val progress          = currentStep.toFloat() / totalSteps.toFloat()
    val animatedProgress by animateFloatAsState(targetValue = progress, label = "progress")

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(6.dp)
            .clip(RoundedCornerShape(3.dp))
            .background(Color.White.copy(alpha = 0.1f))
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(animatedProgress)
                .fillMaxHeight()
                .clip(RoundedCornerShape(3.dp))
                .background(Brush.horizontalGradient(listOf(NeonPurple, NeonBlue)))
        )
    }
}

@Composable
fun CompletionScreen(answers: Map<Int, List<String>>, contentDescription: String) {
    Box(
        modifier          = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(DarkBackgroundStart, DeepPurple))),
        contentAlignment  = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(24.dp)) {
            Text("✨", fontSize = 64.sp)
            Spacer(Modifier.height(24.dp))
            Text("Profile Ready!", fontSize = 32.sp, fontWeight = FontWeight.Bold, color = TextWhite)
            Spacer(Modifier.height(16.dp))
            Text(
                text      = "We've personalized your experience based on your interests.",
                color     = TextGray,
                fontSize  = 16.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Preview
// ─────────────────────────────────────────────────────────────────────────────
@Preview
@Composable
fun QuestionnaireHandlerPreview() {
    QuestionnaireHandler(displayName = "Alex")
}