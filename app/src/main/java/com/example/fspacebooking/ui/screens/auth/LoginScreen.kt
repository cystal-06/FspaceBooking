package com.example.fspacebooking.ui.screens.auth

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Login
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fspacebooking.domain.model.User
import com.example.fspacebooking.ui.components.FButton
import com.example.fspacebooking.ui.components.FTextField
import com.example.fspacebooking.ui.theme.AppBackground
import com.example.fspacebooking.ui.theme.AppBorder
import com.example.fspacebooking.ui.theme.AppCardSurface
import com.example.fspacebooking.ui.theme.AppTextMuted
import com.example.fspacebooking.ui.theme.AppTextPrimary
import com.example.fspacebooking.ui.theme.AppTextSecondary
import com.example.fspacebooking.ui.theme.FptBlue
import com.example.fspacebooking.ui.theme.FptOrange
import com.example.fspacebooking.ui.theme.FptRose
import com.example.fspacebooking.ui.theme.FptRoseLight
import com.example.fspacebooking.ui.viewmodels.AuthViewModel
import com.example.fspacebooking.utils.Resource

@Composable
fun LoginScreen(
    viewModel: AuthViewModel,
    onLoginSuccess: (User) -> Unit,
    modifier: Modifier = Modifier
) {
    val username by viewModel.username
    val password by viewModel.password
    val loginState by viewModel.loginState

    var isPasswordVisible by remember { mutableStateOf(false) }
    var rememberMe by remember { mutableStateOf(true) }
    val scrollState = rememberScrollState()

    // Elegant corporate background gradient
    val backgroundBrush = Brush.verticalGradient(
        colors = listOf(
            Color(0xFFEFF6FF), // Soft azure
            Color(0xFFF8FAFC), // Slate background
            Color(0xFFF8FAFC)
        )
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(backgroundBrush)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 24.dp, vertical = 36.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(modifier = Modifier.height(12.dp))

            // 1. Corporate FPT Logo Emblem
            Box(
                modifier = Modifier
                    .size(68.dp)
                    .shadow(elevation = 8.dp, shape = RoundedCornerShape(20.dp), spotColor = FptBlue.copy(alpha = 0.25f))
                    .clip(RoundedCornerShape(20.dp))
                    .background(
                        Brush.linearGradient(
                            listOf(Color(0xFF2563EB), Color(0xFF1D4ED8), Color(0xFF1E3A8A))
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "F",
                        color = Color.White,
                        style = MaterialTheme.typography.displaySmall.copy(
                            fontWeight = FontWeight.Black,
                            fontSize = 34.sp
                        )
                    )
                    Box(
                        modifier = Modifier
                            .padding(bottom = 12.dp, start = 2.dp)
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(FptOrange)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 2. App Branding Title & Subtitle
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "F-Space ",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 24.sp,
                        letterSpacing = (-0.5).sp
                    ),
                    color = AppTextPrimary
                )
                Text(
                    text = "Booking",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 24.sp,
                        letterSpacing = (-0.5).sp
                    ),
                    color = FptBlue
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Hệ thống Quản lý Không gian & Đặt chỗ FPT",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = AppTextSecondary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Normal
                )
            )

            Spacer(modifier = Modifier.height(28.dp))

            // 3. Login Card Surface
            Card(
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = AppCardSurface),
                border = BorderStroke(1.dp, AppBorder),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp)
                ) {
                    Text(
                        text = "Đăng nhập",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            color = AppTextPrimary
                        )
                    )

                    Text(
                        text = "Sử dụng tài khoản nhân viên để tiếp tục",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = AppTextSecondary,
                            fontSize = 13.sp
                        ),
                        modifier = Modifier.padding(top = 4.dp, bottom = 22.dp)
                    )

                    // Username / Email Input
                    FTextField(
                        value = username,
                        onValueChange = viewModel::onUsernameChange,
                        label = "Tên đăng nhập hoặc Email",
                        placeholder = "Ví dụ: employee hoặc namtv@fpt.com",
                        leadingIcon = Icons.Default.Person,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Password Input
                    FTextField(
                        value = password,
                        onValueChange = viewModel::onPasswordChange,
                        label = "Mật khẩu",
                        placeholder = "Nhập mật khẩu",
                        leadingIcon = Icons.Default.Lock,
                        visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        trailingIcon = {
                            IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                                Icon(
                                    imageVector = if (isPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                    contentDescription = null,
                                    tint = AppTextMuted,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Remember Me & Forgot Password
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .clickable { rememberMe = !rememberMe }
                                .padding(vertical = 4.dp, horizontal = 2.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(18.dp)
                                    .clip(RoundedCornerShape(5.dp))
                                    .background(if (rememberMe) FptBlue else Color.Transparent)
                                    .border(1.dp, if (rememberMe) FptBlue else AppBorder, RoundedCornerShape(5.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                if (rememberMe) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(12.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Ghi nhớ",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontSize = 12.sp,
                                    color = AppTextSecondary
                                )
                            )
                        }

                        Text(
                            text = "Quên mật khẩu?",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = FptBlue
                            ),
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .clickable {
                                    viewModel.onUsernameChange("employee")
                                    viewModel.onPasswordChange("emp123")
                                }
                                .padding(4.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Error Message Banner (if any)
                    val isError = loginState is Resource.Error
                    AnimatedVisibility(
                        visible = isError,
                        enter = fadeIn(),
                        exit = fadeOut()
                    ) {
                        Column {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(FptRoseLight)
                                    .border(1.dp, FptRose.copy(alpha = 0.2f), RoundedCornerShape(10.dp))
                                    .padding(horizontal = 12.dp, vertical = 9.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Error,
                                    contentDescription = null,
                                    tint = FptRose,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = (loginState as? Resource.Error)?.message ?: "Đăng nhập thất bại. Vui lòng kiểm tra lại.",
                                    color = FptRose,
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontWeight = FontWeight.Medium,
                                        fontSize = 12.sp
                                    )
                                )
                            }
                            Spacer(modifier = Modifier.height(14.dp))
                        }
                    }

                    // Primary Login Button
                    FButton(
                        text = "Đăng nhập",
                        onClick = { viewModel.login(onLoginSuccess) },
                        isLoading = loginState is Resource.Loading,
                        leadingIcon = Icons.AutoMirrored.Filled.Login,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // FPT Corporate SSO Button
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(Color(0xFFF1F5F9))
                            .clickable {
                                viewModel.onUsernameChange("employee")
                                viewModel.onPasswordChange("emp123")
                                viewModel.login(onLoginSuccess)
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Business,
                                contentDescription = null,
                                tint = Color(0xFF334155),
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Đăng nhập nhanh với FPT SSO",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 13.sp,
                                    color = Color(0xFF334155)
                                )
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 4. Subtle, Professional Quick Account Chips (Discreet testing toolbar)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 8.dp)
            ) {
                Text(
                    text = "Tài khoản mẫu:",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = AppTextSecondary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                )
                Spacer(modifier = Modifier.width(8.dp))

                // Employee NamTV chip
                QuickAccountChip(
                    label = "Nhân viên (NamTV)",
                    color = FptBlue,
                    onClick = {
                        viewModel.onUsernameChange("employee")
                        viewModel.onPasswordChange("emp123")
                    }
                )

                Spacer(modifier = Modifier.width(6.dp))

                // Admin chip
                QuickAccountChip(
                    label = "Quản trị viên",
                    color = FptOrange,
                    onClick = {
                        viewModel.onUsernameChange("admin")
                        viewModel.onPasswordChange("admin123")
                    }
                )

                Spacer(modifier = Modifier.width(6.dp))

                // Locked chip
                QuickAccountChip(
                    label = "Khóa (DucBT)",
                    color = FptRose,
                    onClick = {
                        viewModel.onUsernameChange("ducbt")
                        viewModel.onPasswordChange("123456")
                    }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 5. Minimalist Footer
            Text(
                text = "FPT Software © 2026 • F-Town Smart Campus",
                style = MaterialTheme.typography.labelSmall.copy(
                    color = AppTextMuted,
                    fontSize = 11.sp
                )
            )

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun QuickAccountChip(
    label: String,
    color: Color,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(color.copy(alpha = 0.08f))
            .border(1.dp, color.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
            .clickable { onClick() }
            .padding(horizontal = 8.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            color = color
        )
    }
}
