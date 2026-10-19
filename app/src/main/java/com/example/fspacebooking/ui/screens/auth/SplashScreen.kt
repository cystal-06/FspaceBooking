package com.example.fspacebooking.ui.screens.auth

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fspacebooking.ui.viewmodels.AuthViewModel
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    viewModel: AuthViewModel,
    onRedirectToLogin: () -> Unit,
    onRedirectToMain: () -> Unit,
    modifier: Modifier = Modifier
) {
    val alphaAnim = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        // Run logo fade-in animation
        alphaAnim.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 900)
        )
        delay(1000) // Keep visible

        // Silent login check
        val currentUser = viewModel.currentUser.value
        if (currentUser != null) {
            onRedirectToMain()
        } else {
            onRedirectToLogin()
        }
    }

    // Gradient background: Dark Slate to Deep Blue
    val splashGradient = Brush.verticalGradient(
        colors = listOf(
            Color(0xFF0B0F19), // Rich Obsidian Dark
            Color(0xFF111827), // Deep Gray-Blue
            Color(0xFF1E3A8A)  // Premium Royal Blue
        )
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(splashGradient),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.alpha(alphaAnim.value)
        ) {
            // Elegant Glassmorphic App Logo icon
            Box(
                modifier = Modifier
                    .size(96.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(Color.White.copy(alpha = 0.07f))
                    .border(1.dp, Color.White.copy(alpha = 0.2f), RoundedCornerShape(24.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "F",
                    color = Color.White,
                    style = MaterialTheme.typography.displayMedium.copy(
                        fontWeight = FontWeight.Black,
                        fontSize = 48.sp
                    )
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "F-Space Booking",
                color = Color.White,
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = (-0.5).sp
                )
            )

            Text(
                text = "Hệ thống Đặt tài nguyên Văn phòng FPT",
                color = Color.White.copy(alpha = 0.5f),
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Bold
                )
            )
        }
    }
}
