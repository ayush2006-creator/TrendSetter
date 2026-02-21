package com.example.trendcrafters.Auth

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.trendcrafters.navigationFiles.Screens

// Define Theme Colors
val NeonPurple = Color(0xFF9D4EDD)
val DeepPurple = Color(0xFF320b4d)
val LightPurple = Color(0xFFE0B0FF)
val TextWhite = Color(0xFFFFFFFF)
val TextGray = Color(0xFF888888)

// Gradient Colors
val BackgroundGradientStart = Color(0xFF1E0A25) // Deep purple-tinted black
val BackgroundGradientEnd = Color(0xFF000000)   // Pure black
val InputGradientStart = Color(0xFF2A2A2A)
val InputGradientEnd = Color(0xFF181818)
val SocialButtonGradientStart = Color(0xFF252525)
val SocialButtonGradientEnd = Color(0xFF121212)

@Composable
fun AuthScreenHandler(navController: NavController) {
    var currentScreen by remember { mutableStateOf("login") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(BackgroundGradientStart, BackgroundGradientEnd)
                )
            )
    ) {
        when (currentScreen) {
            "login" -> LoginScreen(
                onNavigateToSignup = { currentScreen = "signup" },navController
            )
            "signup" -> SignupScreen(
                onNavigateToLogin = { currentScreen = "login" },navController
            )
        }
    }
}

@Composable
fun LoginScreen(
    onNavigateToSignup: () -> Unit,
    navController: NavController,
    authViewModel: AuthViewModel = viewModel()          // <-- inject ViewModel
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) }

    val uiState by authViewModel.uiState.collectAsState()   // observe state

    // Navigate on successful login
    LaunchedEffect(uiState.loginSuccess) {
        if (uiState.loginSuccess != null) {
            navController.navigate(Screens.Home.route) {
                popUpTo(Screens.Auth.route) { inclusive = true }
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        AuthBackgroundGraphic()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
                .systemBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AuthHeader(
                title = "Welcome Back!",
                subtitle = "Sign in to access your personalized trend dashboard."
            )

            Spacer(modifier = Modifier.height(32.dp))

            AuthTextField(
                value = email,
                onValueChange = { email = it },
                label = "Email address",
                icon = Icons.Outlined.Email,
                placeholder = "example@gmail.com"
            )

            Spacer(modifier = Modifier.height(16.dp))

            AuthTextField(
                value = password,
                onValueChange = { password = it },
                label = "Password",
                icon = Icons.Outlined.Lock,
                placeholder = "Enter your password",
                isPassword = true,
                isPasswordVisible = isPasswordVisible,
                onVisibilityChange = { isPasswordVisible = !isPasswordVisible }
            )

            Spacer(modifier = Modifier.height(16.dp))

            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterEnd) {
                Text("Forgot Password?", color = LightPurple, fontSize = 14.sp,
                    fontWeight = FontWeight.Medium, modifier = Modifier.clickable { })
            }

            // ── Error message ──────────────────────────────────────────────
            uiState.errorMessage?.let { msg ->
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = msg,
                    color = Color(0xFFFF5370),
                    fontSize = 13.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // ── Loading indicator or button ────────────────────────────────
            if (uiState.isLoading) {
                CircularProgressIndicator(color = NeonPurple)
            } else {
                GradientButton(text = "Sign In", onClick = {
                    authViewModel.clearError()
                    authViewModel.login(email, password)
                })
            }

            Spacer(modifier = Modifier.weight(1f))

            Row(
                modifier = Modifier.padding(bottom = 24.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Don't have an account? ", color = TextGray, fontSize = 14.sp)
                Text("Sign up", color = LightPurple, fontWeight = FontWeight.Bold,
                    fontSize = 14.sp, modifier = Modifier.clickable { onNavigateToSignup() })
            }
        }
    }
}
@Composable
fun SignupScreen(
    onNavigateToLogin: () -> Unit,
    navController: NavController,
    authViewModel: AuthViewModel = viewModel()          // <-- inject ViewModel
) {
    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) }

    val uiState by authViewModel.uiState.collectAsState()

    // Navigate on successful signup
    LaunchedEffect(uiState.signupSuccess) {
        if (uiState.signupSuccess != null) {
            navController.navigate(Screens.Onboarding.route) {
                popUpTo(Screens.Auth.route) { inclusive = true }
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        AuthBackgroundGraphic()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
                .systemBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AuthHeader(
                title = "Create Account",
                subtitle = "Join us to explore exciting trends and adventures."
            )

            Spacer(modifier = Modifier.height(32.dp))

            AuthTextField(
                value = fullName,
                onValueChange = { fullName = it },
                label = "Full Name",
                icon = Icons.Outlined.Person,
                placeholder = "Alex Smith"
            )

            Spacer(modifier = Modifier.height(16.dp))

            AuthTextField(
                value = email,
                onValueChange = { email = it },
                label = "Email address",
                icon = Icons.Outlined.Email,
                placeholder = "example@gmail.com"
            )

            Spacer(modifier = Modifier.height(16.dp))

            AuthTextField(
                value = password,
                onValueChange = { password = it },
                label = "Password",
                icon = Icons.Outlined.Lock,
                placeholder = "Create a strong password",
                isPassword = true,
                isPasswordVisible = isPasswordVisible,
                onVisibilityChange = { isPasswordVisible = !isPasswordVisible }
            )

            // ── Error message ──────────────────────────────────────────────
            uiState.errorMessage?.let { msg ->
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = msg,
                    color = Color(0xFFFF5370),
                    fontSize = 13.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            if (uiState.isLoading) {
                CircularProgressIndicator(color = NeonPurple)
            } else {
                GradientButton(text = "Register", onClick = {
                    authViewModel.clearError()
                    authViewModel.signup(fullName, email, password)
                })
            }

            Spacer(modifier = Modifier.height(32.dp))

            Row(
                modifier = Modifier.padding(bottom = 24.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Already have an account? ", color = TextGray, fontSize = 14.sp)
                Text("Sign In", color = LightPurple, fontWeight = FontWeight.Bold,
                    fontSize = 14.sp, modifier = Modifier.clickable { onNavigateToLogin() })
            }
        }
    }
}

@Composable
fun AuthBackgroundGraphic() {
    Canvas(modifier = Modifier.fillMaxWidth().height(350.dp)) {
        val width = size.width
        val height = size.height

        // Draw the large purple semi-circle/arc at the top
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(DeepPurple, Color.Transparent),
                center = Offset(width / 2, height * 0.1f),
                radius = width * 0.9f
            ),
            radius = width * 0.9f,
            center = Offset(width / 2, height * 0.1f)
        )
    }
}

@Composable
fun AuthHeader(title: String, subtitle: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        // Back Button & Top Bar

        Spacer(modifier = Modifier.height(10.dp))

        // Glossy Sphere Logo
        Box(contentAlignment = Alignment.Center) {
            // Outer glow
            Canvas(modifier = Modifier.size(100.dp)) {
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(NeonPurple.copy(alpha = 0.2f), Color.Transparent),
                    )
                )
            }

            // The Sphere
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(Color.Black),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    // 1. Base Dark Sphere Gradient
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(Color(0xFF2D1B36), Color.Black),
                            center = center,
                            radius = size.width / 2
                        )
                    )

                    // 2. The Purple Ring Light Reflection
                    // Reduced opacity here as requested
                    drawCircle(
                        brush = Brush.sweepGradient(
                            colors = listOf(
                                NeonPurple.copy(alpha = 0.05f),
                                NeonPurple.copy(alpha = 0.4f), // Reduced from 1.0f to 0.4f
                                NeonPurple.copy(alpha = 0.05f)
                            )
                        ),
                        radius = size.width * 0.25f,
                        style = Stroke(width = 6.dp.toPx())
                    )

                    // 3. Top glossy highlight
                    drawCircle(
                        color = Color.White.copy(alpha = 0.15f),
                        radius = size.width * 0.2f,
                        center = Offset(center.x, center.y - 12f)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = title,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = TextWhite
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = subtitle,
            fontSize = 14.sp,
            color = TextGray,
            textAlign = TextAlign.Center,
            lineHeight = 20.sp,
            modifier = Modifier.width(280.dp)
        )
    }
}

@Composable
fun AuthTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    icon: ImageVector,
    placeholder: String,
    isPassword: Boolean = false,
    isPasswordVisible: Boolean = false,
    onVisibilityChange: () -> Unit = {}
) {
    Column {
        Text(
            text = label,
            color = TextWhite,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        // Gradient Background Box
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(InputGradientStart, InputGradientEnd)
                    )
                )
        ) {
            OutlinedTextField(
                value = value,
                onValueChange = onValueChange,
                placeholder = { Text(placeholder, color = TextGray) },
                leadingIcon = { Icon(icon, contentDescription = null, tint = TextGray) },
                trailingIcon = if (isPassword) {
                    {
                        IconButton(onClick = onVisibilityChange) {
                            Icon(
                                imageVector = if (isPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                contentDescription = "Toggle Password",
                                tint = TextGray
                            )
                        }
                    }
                } else null,
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = NeonPurple,
                    unfocusedBorderColor = Color.Transparent, // Transparent because we have a gradient background
                    focusedContainerColor = Color.Transparent, // Transparent to show gradient
                    unfocusedContainerColor = Color.Transparent,
                    cursorColor = NeonPurple,
                    focusedTextColor = TextWhite,
                    unfocusedTextColor = TextWhite
                ),
                shape = RoundedCornerShape(12.dp),
                singleLine = true,
                visualTransformation = if (isPassword && !isPasswordVisible) PasswordVisualTransformation() else VisualTransformation.None,
                keyboardOptions = if (isPassword) KeyboardOptions(keyboardType = KeyboardType.Password) else KeyboardOptions.Default
            )
        }
    }
}

@Composable
fun GradientButton(text: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(54.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
        contentPadding = PaddingValues(),
        shape = RoundedCornerShape(14.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(NeonPurple, Color(0xFF7B2CBF))
                    ),
                    shape = RoundedCornerShape(14.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = TextWhite
            )
        }
    }
}

@Composable
fun SocialLoginButtons() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        SocialButton(
            text = "Google",
            icon = "G", // Placeholder
            color = Color(0xFFDB4437),
            modifier = Modifier.weight(1f)
        )
        SocialButton(
            text = "Apple",
            icon = "", // Placeholder
            color = Color.White,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun SocialButton(
    text: String,
    icon: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = { },
        modifier = modifier.height(54.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent
        ),
        shape = RoundedCornerShape(14.dp),
        contentPadding = PaddingValues(0.dp) // Reset padding for Box
    ) {
        // Gradient Background for Social Button
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(SocialButtonGradientStart, SocialButtonGradientEnd)
                    ),
                    shape = RoundedCornerShape(14.dp)
                )
                .border(
                    width = 1.dp,
                    brush = Brush.linearGradient(
                        colors = listOf(Color.White.copy(alpha = 0.1f), Color.Transparent)
                    ),
                    shape = RoundedCornerShape(14.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = icon,
                    color = color,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = text,
                    color = TextWhite,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}