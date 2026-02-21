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

// --- Theme Colors (Consistent with Auth) ---
val NeonPurple = Color(0xFF9D4EDD)
val NeonBlue = Color(0xFF4CC9F0)
val DarkBackgroundStart = Color(0xFF1E0A25)
val DarkBackgroundEnd = Color(0xFF000000)
val CardBackground = Color(0xFF1A1A1A)
val TextWhite = Color(0xFFFFFFFF)
val TextGray = Color(0xFFAAAAAA)

// --- Data Models ---
data class ProfileOption(
    val id: String,
    val text: String,
    val emoji: String
)

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
        questionText = "Who are you creating content for?",
        options = listOf(
            ProfileOption("1", "Myself (Personal Brand)", "🧑‍💼"),
            ProfileOption("2", "College Club", "🎓"),
            ProfileOption("3", "Startup", "🚀"),
            ProfileOption("4", "Business", "🏢"),
            ProfileOption("5", "Agency", "📊"),
            ProfileOption("6", "Influencer", "🌟"),
            ProfileOption("7", "Event/Fest", "🎉"),
            ProfileOption("8", "Community Page", "👥")
        ),
        true
    ),

    OnboardingQuestion(
        id = 2,
        questionText = "Where do you mainly post?",
        options = listOf(
            ProfileOption("1", "Instagram Reels", "📸"),
            ProfileOption("2", "YouTube Shorts", "▶️"),
            ProfileOption("3", "Snapchat Spotlight", "👻"),
            ProfileOption("4", "Facebook Reels", "📘"),
            ProfileOption("5", "Multiple Platforms", "🌐")
        ),
        true
    ),

    OnboardingQuestion(
        id = 3,
        questionText = "How long have you been creating reels?",
        options = listOf(
            ProfileOption("1", "Just starting", "🌱"),
            ProfileOption("2", "< 6 months", "⏳"),
            ProfileOption("3", "6–12 months", "📆"),
            ProfileOption("4", "1–2 years", "📈"),
            ProfileOption("5", "2+ years", "🏆")
        )
    ),

    OnboardingQuestion(
        id = 4,
        questionText = "What is your current follower/subscriber range?",
        options = listOf(
            ProfileOption("1", "0 – 1K", "🐣"),
            ProfileOption("2", "1K – 10K", "📊"),
            ProfileOption("3", "10K – 50K", "🔥"),
            ProfileOption("4", "50K – 100K", "🚀"),
            ProfileOption("5", "100K+", "👑")
        )
    ),

    OnboardingQuestion(
        id = 5,
        questionText = "What’s your typical max views on a reel?",
        options = listOf(
            ProfileOption("1", "< 1K", "👀"),
            ProfileOption("2", "1K – 10K", "📈"),
            ProfileOption("3", "10K – 100K", "🔥"),
            ProfileOption("4", "100K – 1M", "🚀"),
            ProfileOption("5", "1M+", "💥")
        )
    ),

    OnboardingQuestion(
        id = 6,
        questionText = "Have you found any breakthrough content?",
        options = listOf(
            ProfileOption("1", "Yes — consistently", "🏆"),
            ProfileOption("2", "Sometimes it works", "🤞"),
            ProfileOption("3", "Not yet", "🔍"),
            ProfileOption("4", "Still experimenting", "🧪")
        )
    )
)

@Preview
@Composable
fun QuestionnaireHandler() {
    // Stores answers as Map<QuestionId, List<OptionId>>
    val answers = remember { mutableStateMapOf<Int, List<String>>() }
    var currentQuestionIndex by remember { mutableIntStateOf(0) }
    var isComplete by remember { mutableStateOf(false) }

    // New states for the text input step
    var showContentInput by remember { mutableStateOf(false) }
    var contentDescription by remember { mutableStateOf("") }

    if (isComplete) {
        // Show completion screen with all data
        CompletionScreen(answers = answers, contentDescription = contentDescription)
    } else if (showContentInput) {
        // Show the text input screen
        ContentDescriptionScreen(
            onSubmit = { description ->
                contentDescription = description
                isComplete = true
            },
            onBack = {
                showContentInput = false
            }
        )
    } else {
        QuestionnaireScreen(
            question = onboardingQuestions[currentQuestionIndex],
            totalQuestions = onboardingQuestions.size,
            currentProgress = currentQuestionIndex + 1,
            selectedOptions = answers[onboardingQuestions[currentQuestionIndex].id] ?: emptyList(),
            onOptionSelected = { questionId, optionId, isMultiSelect ->
                val currentSelection = answers[questionId] ?: emptyList()
                if (isMultiSelect) {
                    if (currentSelection.contains(optionId)) {
                        answers[questionId] = currentSelection - optionId
                    } else {
                        answers[questionId] = currentSelection + optionId
                    }
                } else {
                    answers[questionId] = listOf(optionId)
                }
            },
            onNext = {
                if (currentQuestionIndex < onboardingQuestions.size - 1) {
                    currentQuestionIndex++
                } else {
                    // Instead of completing immediately, go to the content description step
                    showContentInput = true
                }
            },
            onBack = {
                if (currentQuestionIndex > 0) {
                    currentQuestionIndex--
                }
            }
        )
    }
}

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
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(DarkBackgroundStart, DarkBackgroundEnd)
                )
            )
            .padding(24.dp)
            .systemBarsPadding()
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Header: Back Button & Progress
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = TextWhite
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Progress Bar
            CustomProgressBar(
                currentStep = currentProgress,
                totalSteps = totalQuestions
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Question Text
            Text(
                text = question.questionText,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = TextWhite,
                lineHeight = 36.sp
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Options List
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.weight(1f) // Takes remaining space
            ) {
                items(question.options) { option ->
                    OptionCard(
                        option = option,
                        isSelected = selectedOptions.contains(option.id),
                        onClick = {
                            onOptionSelected(question.id, option.id, question.allowMultiSelect)
                        }
                    )
                }
                // Add bottom padding for the FAB space
                item { Spacer(modifier = Modifier.height(100.dp)) }
            }
        }

        // Floating Action Button (Next)
        // Only show if an option is selected
        if (selectedOptions.isNotEmpty()) {
            FloatingActionButton(
                onClick = onNext,
                containerColor = NeonPurple,
                contentColor = TextWhite,
                shape = CircleShape,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(bottom = 24.dp)
                    .size(64.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowForward,
                    contentDescription = "Next",
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

@Composable
fun ContentDescriptionScreen(
    onSubmit: (String) -> Unit,
    onBack: () -> Unit
) {
    var text by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(DarkBackgroundStart, DarkBackgroundEnd)
                )
            )
            .padding(24.dp)
            .systemBarsPadding()
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Header
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = TextWhite
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Content Strategy",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = TextWhite
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Describe the content which led to engagement or you think will get engagement.",
                fontSize = 16.sp,
                color = TextGray,
                lineHeight = 24.sp
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Styled Text Input
            OutlinedTextField(
                value = text,
                onValueChange = { text = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .background(CardBackground, RoundedCornerShape(16.dp)),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = NeonPurple,
                    unfocusedBorderColor = Color.Transparent,
                    focusedTextColor = TextWhite,
                    unfocusedTextColor = TextWhite,
                    cursorColor = NeonPurple,
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent
                ),
                shape = RoundedCornerShape(16.dp),
                placeholder = {
                    Text(
                        "E.g., Behind-the-scenes vlogs, educational tutorials about crypto, or funny skits related to coding...",
                        color = TextGray.copy(alpha = 0.5f)
                    )
                }
            )
        }

        // Finish Button
        FloatingActionButton(
            onClick = { onSubmit(text) },
            containerColor = NeonPurple,
            contentColor = TextWhite,
            shape = CircleShape,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 24.dp)
                .size(64.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = "Finish",
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
fun OptionCard(
    option: ProfileOption,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val borderColor = if (isSelected) NeonPurple else Color.White.copy(alpha = 0.1f)
    val backgroundColor = if (isSelected) NeonPurple.copy(alpha = 0.15f) else CardBackground

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .border(1.dp, borderColor, RoundedCornerShape(16.dp))
            .background(backgroundColor)
            .clickable { onClick() }
            .padding(horizontal = 20.dp, vertical = 20.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Emoji
        Text(
            text = option.emoji,
            fontSize = 24.sp
        )

        // Text
        Text(
            text = option.text,
            fontSize = 16.sp,
            color = if (isSelected) TextWhite else TextGray,
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
            modifier = Modifier.weight(1f)
        )

        // Checkmark (only visible when selected)
        if (isSelected) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = "Selected",
                tint = NeonPurple,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
fun CustomProgressBar(currentStep: Int, totalSteps: Int) {
    val progress = currentStep.toFloat() / totalSteps.toFloat()
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
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(NeonPurple, NeonBlue)
                    )
                )
        )
    }
}

@Composable
fun CompletionScreen(answers: Map<Int, List<String>>, contentDescription: String) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(DarkBackgroundStart, DeepPurple)
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(24.dp)
        ) {
            Text(
                text = "✨",
                fontSize = 64.sp
            )
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "Profile Ready!",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = TextWhite
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "We've personalized your feed based on your interests.",
                color = TextGray,
                fontSize = 16.sp,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(32.dp))

            // For debugging visualization of stored answers
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.05f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Debug: Profile Preferences", color = NeonBlue, fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    answers.forEach { (qId, aIds) ->
                        Text("Q$qId: Selection $aIds", color = TextGray, fontSize = 12.sp)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Content Strategy:", color = NeonBlue, fontSize = 12.sp)
                    Text(contentDescription, color = TextGray, fontSize = 12.sp)
                }
            }
        }
    }
}