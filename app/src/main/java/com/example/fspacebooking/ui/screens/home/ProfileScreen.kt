package com.example.fspacebooking.ui.screens.home

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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fspacebooking.domain.model.UserRole
import com.example.fspacebooking.ui.components.FButton
import com.example.fspacebooking.ui.components.FCard
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
import com.example.fspacebooking.ui.theme.FptRose
import com.example.fspacebooking.ui.theme.FptRoseLight
import com.example.fspacebooking.ui.viewmodels.AuthViewModel

@Composable
fun ProfileScreen(
    viewModel: AuthViewModel,
    onNavigateToAdmin: () -> Unit,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier
) {
    val currentUser by viewModel.currentUser.collectAsState()

    var isVietnamese by remember { mutableStateOf(true) }
    var isNotificationEnabled by remember { mutableStateOf(true) }
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(AppBackground)
            .verticalScroll(scrollState)
            .padding(horizontal = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(28.dp))

        // Profile Avatar with Initial & Badge
        Box(
            contentAlignment = Alignment.BottomEnd
        ) {
            Box(
                modifier = Modifier
                    .size(96.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(
                            colors = listOf(FptBlue, Color(0xFF60A5FA))
                        )
                    )
                    .border(3.dp, Color.White, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                val initial = currentUser?.name?.take(1)?.uppercase() ?: "D"
                Text(
                    text = initial,
                    color = Color.White,
                    fontSize = 38.sp,
                    fontWeight = FontWeight.Black
                )
            }

            // Role indicator badge dot
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(if (currentUser?.role == UserRole.ADMIN) FptOrange else FptEmerald)
                    .border(2.dp, Color.White, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (currentUser?.role == UserRole.ADMIN) Icons.Default.Security else Icons.Default.Badge,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(15.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // User Metadata Info
        Text(
            text = currentUser?.name ?: "Đức Nguyễn",
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold,
                color = AppTextPrimary
            )
        )

        Spacer(modifier = Modifier.height(4.dp))

        // Employee ID Pill
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(FptBlueLight)
                .padding(horizontal = 10.dp, vertical = 3.dp)
        ) {
            Text(
                text = "Mã NV: ${currentUser?.employeeId ?: "EMP999"}",
                style = MaterialTheme.typography.labelMedium.copy(
                    color = FptBlueDark,
                    fontWeight = FontWeight.SemiBold
                )
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "•",
                color = AppTextMuted,
                fontSize = 10.sp
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = currentUser?.fsu ?: "FSU1.BU2",
                style = MaterialTheme.typography.labelMedium.copy(
                    color = AppTextSecondary,
                    fontWeight = FontWeight.Medium
                )
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Compliance & Account Status Card (UC12)
        val violations = currentUser?.violations ?: 0
        val isLocked = currentUser?.userStatus == "ĐÃ KHÓA" || violations >= 3
        val statusText = currentUser?.userStatus ?: "ĐANG HOẠT ĐỘNG"
        val statusColor = when {
            isLocked -> FptRose
            violations > 0 -> FptAmber
            else -> FptEmerald
        }
        val statusBg = when {
            isLocked -> FptRoseLight
            violations > 0 -> FptAmberLight
            else -> FptEmeraldLight
        }

        FCard(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Security,
                            contentDescription = null,
                            tint = FptBlue,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Tuân thủ & No-Show (UC12)",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = AppTextPrimary
                        )
                    }

                    // Status chip
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(statusBg)
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = statusText,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = statusColor
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))
                HorizontalDivider(color = AppBorderSubtle, thickness = 1.dp)
                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Trạng thái đặt chỗ", fontSize = 11.sp, color = AppTextSecondary)
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = if (isLocked) Icons.Default.Error else Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = statusColor,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (isLocked) "Tạm khóa" else "Khả dụng",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = statusColor
                            )
                        }
                    }

                    Box(modifier = Modifier.width(1.dp).height(32.dp).background(AppBorder))

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Vi phạm No-Show", fontSize = 11.sp, color = AppTextSecondary)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "$violations / 3 lần",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 14.sp,
                            color = statusColor
                        )
                    }

                    Box(modifier = Modifier.width(1.dp).height(32.dp).background(AppBorder))

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Bộ phận FSU", fontSize = 11.sp, color = AppTextSecondary)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = currentUser?.fsu ?: "FSU1.BU2",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = AppTextPrimary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Explanatory note
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isLocked) FptRoseLight else AppBackground)
                        .padding(horizontal = 10.dp, vertical = 8.dp)
                ) {
                    Row(verticalAlignment = Alignment.Top) {
                        Icon(
                            imageVector = if (isLocked) Icons.Default.Warning else Icons.Default.Info,
                            contentDescription = null,
                            tint = if (isLocked) FptRose else AppTextMuted,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isLocked) {
                                "Bạn đã vắng mặt quá 3 lần mà không check-in QR. Vui lòng liên hệ Admin để được hỗ trợ mở khóa tài khoản."
                            } else {
                                "Quy tắc FPT: Đặt phòng/chỗ ngồi cần quét mã QR check-in trong vòng 15 phút. Vắng mặt 3 lần sẽ bị khóa quyền đặt chỗ."
                            },
                            fontSize = 11.sp,
                            color = if (isLocked) FptRose else AppTextSecondary,
                            lineHeight = 15.sp
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Settings Section
        Text(
            text = "Cài đặt & Tùy chọn",
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                color = AppTextPrimary
            ),
            modifier = Modifier.align(Alignment.Start)
        )

        Spacer(modifier = Modifier.height(10.dp))

        FCard(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                // Language settings row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Language,
                            contentDescription = null,
                            tint = FptBlue,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Ngôn ngữ hiển thị",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.SemiBold,
                                    color = AppTextPrimary
                                )
                            )
                            Text(
                                text = if (isVietnamese) "Tiếng Việt (Mặc định)" else "English",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = AppTextSecondary
                                )
                            )
                        }
                    }

                    // Compact Pill Toggle
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(AppBackground)
                            .border(1.dp, AppBorder, RoundedCornerShape(20.dp))
                            .clickable { isVietnamese = !isVietnamese }
                            .padding(3.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .background(if (isVietnamese) FptBlue else Color.Transparent)
                                .padding(horizontal = 12.dp, vertical = 5.dp)
                        ) {
                            Text(
                                text = "VI",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isVietnamese) Color.White else AppTextSecondary
                            )
                        }
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .background(if (!isVietnamese) FptBlue else Color.Transparent)
                                .padding(horizontal = 12.dp, vertical = 5.dp)
                        ) {
                            Text(
                                text = "EN",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (!isVietnamese) Color.White else AppTextSecondary
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))
                HorizontalDivider(color = AppBorderSubtle, thickness = 1.dp)
                Spacer(modifier = Modifier.height(14.dp))

                // Push notifications preference switch
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = null,
                            tint = FptBlue,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Thông báo nhắc lịch",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.SemiBold,
                                    color = AppTextPrimary
                                )
                            )
                            Text(
                                text = "Nhắc nhở quét mã check-in trước 15p",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = AppTextSecondary
                                )
                            )
                        }
                    }

                    Switch(
                        checked = isNotificationEnabled,
                        onCheckedChange = { isNotificationEnabled = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = FptBlue,
                            uncheckedThumbColor = AppTextMuted,
                            uncheckedTrackColor = AppBorder
                        )
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Admin Console authorization switch button (only visible to admins)
        if (currentUser?.role == UserRole.ADMIN) {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
                border = BorderStroke(width = 1.dp, color = Color(0xFF1E293B)),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onNavigateToAdmin() }
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(FptOrange.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.AdminPanelSettings,
                                contentDescription = null,
                                tint = FptOrange,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(14.dp))
                        Column {
                            Text(
                                text = "Bảng Điều Khiển Quản Trị",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            )
                            Text(
                                text = "Quản lý phòng họp, chỗ ngồi & No-Show",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = Color(0xFF94A3B8)
                                )
                            )
                        }
                    }

                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                        contentDescription = null,
                        tint = Color(0xFF94A3B8),
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Logout action button
        FButton(
            text = "Đăng xuất tài khoản",
            onClick = onLogout,
            isSecondary = true,
            leadingIcon = Icons.AutoMirrored.Filled.Logout,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // App Footer & Version info
        Text(
            text = "F-Space Booking v2.4.0 • FPT Software Campus",
            style = MaterialTheme.typography.labelSmall.copy(
                color = AppTextMuted,
                fontSize = 11.sp
            )
        )

        Spacer(modifier = Modifier.height(32.dp))
    }
}
