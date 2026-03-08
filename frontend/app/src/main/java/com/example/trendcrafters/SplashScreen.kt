package com.example.trendcrafters

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    onSplashComplete: () -> Unit
) {
    var showContent by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(500)
        showContent = true
        delay(3500) // Total splash duration
        onSplashComplete()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),
        contentAlignment = Alignment.Center
    ) {
        // Animated gradient orbs
        AnimatedGradientOrbs()

        // Main content
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(horizontal = 24.dp)
        ) {
            // Logo with orbiting icons
            AnimatedLogo()

            Spacer(modifier = Modifier.height(48.dp))

            // App name with animation
            androidx.compose.animation.AnimatedVisibility(
                visible = showContent,
                enter = androidx.compose.animation.fadeIn(
                    animationSpec = tween(800, delayMillis = 600)
                ) + androidx.compose.animation.slideInVertically(
                    animationSpec = tween(800, delayMillis = 600),
                    initialOffsetY = { 20 }
                )
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "TrendSetter",
                        fontSize = 48.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    AnimatedDivider()
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Tagline
            androidx.compose.animation.AnimatedVisibility(
                visible = showContent,
                enter = androidx.compose.animation.fadeIn(
                    animationSpec = tween(800, delayMillis = 900)
                ) + androidx.compose.animation.slideInVertically(
                    animationSpec = tween(800, delayMillis = 900),
                    initialOffsetY = { 20 }
                )
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "AI-Powered Script Writing",
                        fontSize = 18.sp,
                        color = Color(0xFFB0B0B0)
                    )
                    Text(
                        text = "Made Simple",
                        fontSize = 18.sp,
                        color = Color(0xFFA78BFA)
                    )
                }
            }

            Spacer(modifier = Modifier.height(64.dp))

            // Loading indicator
            androidx.compose.animation.AnimatedVisibility(
                visible = showContent,
                enter = androidx.compose.animation.fadeIn(
                    animationSpec = tween(600, delayMillis = 1200)
                )
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    LoadingDots()

                    Spacer(modifier = Modifier.height(12.dp))

                    PulsingText(text = "Loading your creative space...")
                }
            }
        }

        // Bottom decorative line
        BottomGradientLine()
    }
}

@Composable
fun AnimatedGradientOrbs() {
    val infiniteTransition = rememberInfiniteTransition(label = "orbs")

    // First orb animation
    val scale1 by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale1"
    )

    val alpha1 by infiniteTransition.animateFloat(
        initialValue = 0.2f,
        targetValue = 0.3f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha1"
    )

    // Second orb animation
    val scale2 by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.3f,
        animationSpec = infiniteRepeatable(
            animation = tween(5000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale2"
    )

    val alpha2 by infiniteTransition.animateFloat(
        initialValue = 0.2f,
        targetValue = 0.35f,
        animationSpec = infiniteRepeatable(
            animation = tween(5000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha2"
    )

    // First gradient orb
    Box(
        modifier = Modifier
            .offset(x = (-100).dp, y = (-200).dp)
            .size(256.dp)
            .scale(scale1)
            .blur(80.dp)
            .background(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0xFF667eea).copy(alpha = alpha1),
                        Color(0xFF764ba2).copy(alpha = alpha1 * 0.5f),
                        Color.Transparent
                    )
                ),
                shape = CircleShape
            )
    )

    // Second gradient orb
    Box(
        modifier = Modifier
            .offset(x = 100.dp, y = 200.dp)
            .size(256.dp)
            .scale(scale2)
            .blur(80.dp)
            .background(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color(0xFFf093fb).copy(alpha = alpha2),
                        Color(0xFFf5576c).copy(alpha = alpha2 * 0.5f),
                        Color.Transparent
                    )
                ),
                shape = CircleShape
            )
    )
}

@Composable
fun AnimatedLogo() {
    val infiniteTransition = rememberInfiniteTransition(label = "logo")

    val rotation1 by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation1"
    )

    val rotation2 by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = -360f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation2"
    )

    val sparkleScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "sparkleScale"
    )

    val zapScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.3f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "zapScale"
    )

    Box(
        modifier = Modifier.size(128.dp),
        contentAlignment = Alignment.Center
    ) {
        // Main logo circle
        Box(
            modifier = Modifier
                .size(128.dp)
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            Color(0xFF667eea),
                            Color(0xFF764ba2),
                            Color(0xFFf093fb)
                        )
                    ),
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            // File icon (simplified representation)
            Text(
                text = "📄",
                fontSize = 64.sp
            )
        }

        // Orbiting sparkle (top-right)
        Box(
            modifier = Modifier
                .offset(x = 48.dp, y = (-48).dp)
                .rotate(rotation1)
                .offset(x = 8.dp, y = 8.dp)
        ) {
            Text(
                text = "✨",
                fontSize = (24 * sparkleScale).sp
            )
        }

        // Orbiting zap (bottom-left)
        Box(
            modifier = Modifier
                .offset(x = (-48).dp, y = 48.dp)
                .rotate(rotation2)
                .offset(x = (-8).dp, y = (-8).dp)
        ) {
            Text(
                text = "⚡",
                fontSize = (20 * zapScale).sp
            )
        }
    }
}

@Composable
fun AnimatedDivider() {
    val width = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        delay(800) // match the text's delayMillis
        width.animateTo(
            targetValue = 120f,
            animationSpec = tween(800, easing = FastOutSlowInEasing)
        )
    }

    Box(
        modifier = Modifier
            .width(width.value.dp)
            .height(1.dp)
            .background(
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        Color.Transparent,
                        Color(0xFFA78BFA),
                        Color.Transparent
                    )
                )
            )
    )
}
@Composable
fun LoadingDots() {
    val infiniteTransition = rememberInfiniteTransition(label = "dots")

    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        repeat(3) { index ->
            val scale by infiniteTransition.animateFloat(
                initialValue = 1f,
                targetValue = 1.5f,
                animationSpec = infiniteRepeatable(
                    animation = keyframes {
                        durationMillis = 1500
                        1f at (index * 200)
                        1.5f at (index * 200 + 400)
                        1f at (index * 200 + 800)
                        1f at 1500
                    },
                    repeatMode = RepeatMode.Restart
                ),
                label = "dotScale$index"
            )

            val alpha by infiniteTransition.animateFloat(
                initialValue = 0.5f,
                targetValue = 1f,
                animationSpec = infiniteRepeatable(
                    animation = keyframes {
                        durationMillis = 1500
                        0.5f at (index * 200)
                        1f at (index * 200 + 400)
                        0.5f at (index * 200 + 800)
                        0.5f at 1500
                    },
                    repeatMode = RepeatMode.Restart
                ),
                label = "dotAlpha$index"
            )

            Box(
                modifier = Modifier
                    .size(8.dp)
                    .scale(scale)
                    .background(
                        color = Color(0xFFA78BFA).copy(alpha = alpha),
                        shape = CircleShape
                    )
            )
        }
    }
}
@Composable
fun PulsingText(text: String) {
    val infiniteTransition = rememberInfiniteTransition(label = "text")

    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "textAlpha"
    )

    Text(
        text = text,
        fontSize = 14.sp,
        color = Color(0xFF808080).copy(alpha = alpha)
    )
}

@Composable
fun BottomGradientLine() {
    val infiniteTransition = rememberInfiniteTransition(label = "bottomLine")

    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.6f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "lineAlpha"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(4.dp)
            .background(
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        Color.Transparent,
                        Color(0xFFA855F7).copy(alpha = alpha),
                        Color.Transparent
                    )
                )
            )
    )
}
