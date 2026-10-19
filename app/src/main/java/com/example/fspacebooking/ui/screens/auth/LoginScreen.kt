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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
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

    val primaryColor = Color(0xFF2563EB)
    val backgroundLight = Color(0xFFF8FAFC)
    val cardSurface = Color(0xFFFFFFFF)

    // Premium light gradient background
    val gradient = Brush.verticalGradient(
        colors = listOf(
            primaryColor.copy(alpha = 0.06f),
            backgroundLight
        )
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(gradient),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .padding(vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Elegant Monolithic App Logo
            Box(
                modifier = Modifier
                    .size(68.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(
                        Brush.linearGradient(
                            listOf(primaryColor, Color(0xFF3B82F6))
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "F",
                    color = Color.White,
                    style = MaterialTheme.typography.displaySmall.copy(
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 34.sp
                    )
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "F-Space Booking",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = (-0.5).sp
                ),
                color = Color(0xFF1E293B)
            )

            Text(
                text = "Hệ thống Đặt tài nguyên Văn phòng FPT",
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFF64748B)
            )

            Spacer(modifier = Modifier.height(32.dp))

            // White flat Card with thin border (Envoy Style)
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = cardSurface),
                border = BorderStroke(width = 1.dp, color = Color(0xFFE2E8F0)),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
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
                            color = Color(0xFF1E293B)
                        ),
                        modifier = Modifier.align(Alignment.Start)
                    )

                    Text(
                        text = "Nhập tài khoản FPT ('admin' hoặc 'employee')",
                        style = MaterialTheme.typography.bodySmall,
                        color = primaryColor,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .align(Alignment.Start)
                            .padding(top = 4.dp, bottom = 20.dp)
                    )

                    FTextField(
                        value = username,
                        onValueChange = viewModel::onUsernameChange,
                        label = "Tên đăng nhập hoặc Email",
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
                                    tint = Color(0xFF94A3B8),
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    val isError = loginState is Resource.Error
                    if (isError) {
                        Text(
                            text = (loginState as Resource.Error).message ?: "Đăng nhập thất bại",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                            modifier = Modifier.padding(bottom = 12.dp)
                        )
                    }

                    FButton(
                        text = "Đăng nhập",
                        onClick = { viewModel.login(onLoginSuccess) },
                        isLoading = loginState is Resource.Loading,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    FButton(
                        text = "Đăng nhập nhanh với SSO FPT",
                        onClick = {
                            viewModel.onUsernameChange("employee")
                            viewModel.onPasswordChange("emp123")
                            viewModel.login(onLoginSuccess)
                        },
                        isSecondary = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Text(
                        text = "Tài khoản kiểm thử nhanh (Demo):",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFF64748B),
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Admin chip
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color(0xFFEFF6FF))
                                .border(1.dp, Color(0xFFBFDBFE), RoundedCornerShape(10.dp))
                                .clickable {
                                    viewModel.onUsernameChange("admin")
                                    viewModel.onPasswordChange("admin123")
                                }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("Admin", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = primaryColor)
                                Text("Quản trị viên", fontSize = 9.sp, color = Color(0xFF64748B))
                            }
                        }

                        // Employee NamTV chip
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color(0xFFF0FDF4))
                                .border(1.dp, Color(0xFFBBF7D0), RoundedCornerShape(10.dp))
                                .clickable {
                                    viewModel.onUsernameChange("employee")
                                    viewModel.onPasswordChange("emp123")
                                }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("NamTV", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = Color(0xFF16A34A))
                                Text("Nhân viên", fontSize = 9.sp, color = Color(0xFF64748B))
                            }
                        }

                        // Locked user DucBT chip
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color(0xFFFEF2F2))
                                .border(1.dp, Color(0xFFFECACA), RoundedCornerShape(10.dp))
                                .clickable {
                                    viewModel.onUsernameChange("ducbt")
                                    viewModel.onPasswordChange("123456")
                                }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("DucBT", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = Color(0xFFDC2626))
                                Text("Bị khóa (4/3)", fontSize = 9.sp, color = Color(0xFFDC2626))
                            }
                        }
                    }
                }
            }
        }
    }
}
