package com.example.fspacebooking.ui.screens.auth

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
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Apartment
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
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
import com.example.fspacebooking.ui.components.FCard
import com.example.fspacebooking.ui.components.FTextField
import com.example.fspacebooking.ui.theme.AppBackground
import com.example.fspacebooking.ui.theme.AppBorder
import com.example.fspacebooking.ui.theme.AppBorderSubtle
import com.example.fspacebooking.ui.theme.AppCardSurface
import com.example.fspacebooking.ui.theme.AppTextMuted
import com.example.fspacebooking.ui.theme.AppTextPrimary
import com.example.fspacebooking.ui.theme.AppTextSecondary
import com.example.fspacebooking.ui.theme.FptAmber
import com.example.fspacebooking.ui.theme.FptAmberLight
import com.example.fspacebooking.ui.theme.FptBlue
import com.example.fspacebooking.ui.theme.FptBlueDark
import com.example.fspacebooking.ui.theme.FptBlueLight
import com.example.fspacebooking.ui.theme.FptEmerald
import com.example.fspacebooking.ui.theme.FptEmeraldLight
import com.example.fspacebooking.ui.theme.FptOrange
import com.example.fspacebooking.ui.theme.FptOrangeLight
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
    val scrollState = rememberScrollState()

    // Smooth enterprise gradient background
    val gradient = Brush.verticalGradient(
        colors = listOf(
            FptBlue.copy(alpha = 0.08f),
            AppBackground,
            AppBackground
        )
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(gradient)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 24.dp, vertical = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // FPT Corporate App Logo with multi-color gradient
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(
                        Brush.linearGradient(
                            listOf(FptOrange, FptBlue)
                        )
                    )
                    .border(2.dp, Color.White, RoundedCornerShape(20.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "F",
                    color = Color.White,
                    style = MaterialTheme.typography.displaySmall.copy(
                        fontWeight = FontWeight.Black,
                        fontSize = 38.sp
                    )
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // App title with highlight
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "F-Space ",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Black,
                        letterSpacing = (-0.5).sp
                    ),
                    color = AppTextPrimary
                )
                Text(
                    text = "Booking",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Black,
                        letterSpacing = (-0.5).sp
                    ),
                    color = FptBlue
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Hệ thống Quản lý & Đặt chỗ FPT Software",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = AppTextSecondary,
                    fontWeight = FontWeight.Medium
                )
            )

            Spacer(modifier = Modifier.height(28.dp))

            // Login Card Form
            FCard(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Đăng Nhập",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.ExtraBold,
                            color = AppTextPrimary
                        ),
                        modifier = Modifier.align(Alignment.Start)
                    )

                    Text(
                        text = "Đăng nhập tài khoản nội bộ FPT để tiếp tục",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = AppTextSecondary
                        ),
                        modifier = Modifier
                            .align(Alignment.Start)
                            .padding(top = 4.dp, bottom = 20.dp)
                    )

                    FTextField(
                        value = username,
                        onValueChange = viewModel::onUsernameChange,
                        label = "Tên đăng nhập hoặc Email FPT",
                        leadingIcon = Icons.Default.Person,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    FTextField(
                        value = password,
                        onValueChange = viewModel::onPasswordChange,
                        label = "Mật khẩu",
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

                    Spacer(modifier = Modifier.height(18.dp))

                    val isError = loginState is Resource.Error
                    if (isError) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(FptRoseLight)
                                .padding(horizontal = 12.dp, vertical = 8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Error,
                                contentDescription = null,
                                tint = FptRose,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = (loginState as Resource.Error).message ?: "Đăng nhập thất bại",
                                color = FptRose,
                                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold)
                            )
                        }
                        Spacer(modifier = Modifier.height(14.dp))
                    }

                    FButton(
                        text = "Đăng nhập",
                        onClick = { viewModel.login(onLoginSuccess) },
                        isLoading = loginState is Resource.Loading,
                        leadingIcon = Icons.AutoMirrored.Filled.Login,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    FButton(
                        text = "Đăng nhập nhanh với FPT Mail",
                        onClick = {
                            viewModel.onUsernameChange("employee")
                            viewModel.onPasswordChange("emp123")
                            viewModel.login(onLoginSuccess)
                        },
                        isSecondary = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(20.dp))
                    HorizontalDivider(color = AppBorderSubtle, thickness = 1.dp)
                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Tài khoản kiểm thử nhanh (Demo):",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = AppTextSecondary,
                            fontWeight = FontWeight.Bold
                        ),
                        modifier = Modifier.align(Alignment.Start)
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Admin Demo Selector Chip
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .background(FptBlueLight)
                                .border(1.dp, FptBlue.copy(alpha = 0.25f), RoundedCornerShape(12.dp))
                                .clickable {
                                    viewModel.onUsernameChange("admin")
                                    viewModel.onPasswordChange("admin123")
                                }
                                .padding(horizontal = 8.dp, vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.AdminPanelSettings,
                                        contentDescription = null,
                                        tint = FptBlue,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Admin", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = FptBlue)
                                }
                                Spacer(modifier = Modifier.height(2.dp))
                                Text("Quản trị viên", fontSize = 9.sp, color = AppTextSecondary)
                            }
                        }

                        // Employee NamTV Demo Selector Chip
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .background(FptEmeraldLight)
                                .border(1.dp, FptEmerald.copy(alpha = 0.25f), RoundedCornerShape(12.dp))
                                .clickable {
                                    viewModel.onUsernameChange("employee")
                                    viewModel.onPasswordChange("emp123")
                                }
                                .padding(horizontal = 8.dp, vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = null,
                                        tint = FptEmerald,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("NamTV", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = FptEmerald)
                                }
                                Spacer(modifier = Modifier.height(2.dp))
                                Text("Nhân viên (Hợp lệ)", fontSize = 9.sp, color = AppTextSecondary)
                            }
                        }

                        // Locked DucBT Demo Selector Chip
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .background(FptRoseLight)
                                .border(1.dp, FptRose.copy(alpha = 0.25f), RoundedCornerShape(12.dp))
                                .clickable {
                                    viewModel.onUsernameChange("ducbt")
                                    viewModel.onPasswordChange("123456")
                                }
                                .padding(horizontal = 8.dp, vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Block,
                                        contentDescription = null,
                                        tint = FptRose,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("DucBT", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = FptRose)
                                }
                                Spacer(modifier = Modifier.height(2.dp))
                                Text("Bị khóa (UC12)", fontSize = 9.sp, color = FptRose)
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Footer
            Text(
                text = "FPT Software © 2026 • Campus Booking Solution",
                style = MaterialTheme.typography.labelSmall.copy(
                    color = AppTextMuted,
                    fontSize = 11.sp
                )
            )

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
