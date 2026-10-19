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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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

    val primaryColor = Color(0xFF2563EB)
    val backgroundLight = Color(0xFFF8FAFC)
    val cardSurface = Color(0xFFFFFFFF)

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(backgroundLight)
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(40.dp))

        // Large Profile Avatar Graphic block
        Box(
            modifier = Modifier
                .size(108.dp)
                .clip(CircleShape)
                .background(
                    Brush.linearGradient(
                        colors = listOf(primaryColor, Color(0xFF60A5FA))
                    )
                )
                .border(4.dp, Color.White, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            val initial = currentUser?.name?.take(1)?.uppercase() ?: "D"
            Text(
                text = initial,
                color = Color.White,
                fontSize = 44.sp,
                fontWeight = FontWeight.Black
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // User Metadata Profile Info details
        Text(
            text = currentUser?.name ?: "Đức Nguyễn",
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1E293B)
            )
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "Mã nhân viên: ${currentUser?.employeeId ?: "EMP999"}",
            style = MaterialTheme.typography.bodyMedium.copy(
                color = Color(0xFF64748B),
                fontWeight = FontWeight.Medium
            )
        )

        Text(
            text = "Phòng ban: FPT Software • ${currentUser?.fsu ?: "FSU1.BU2"}",
            style = MaterialTheme.typography.bodySmall.copy(
                color = Color(0xFF94A3B8)
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Compliance & Account Status Card (UC12)
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = cardSurface),
            border = BorderStroke(width = 1.dp, color = Color(0xFFE2E8F0)),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Trạng thái", fontSize = 11.sp, color = Color(0xFF64748B))
                    val st = currentUser?.userStatus ?: "ĐANG HOẠT ĐỘNG"
                    val stColor = when (st) {
                        "ĐÃ KHÓA" -> Color(0xFFEF4444)
                        "CẢNH BÁO" -> Color(0xFFF59E0B)
                        else -> Color(0xFF10B981)
                    }
                    Text(st, fontWeight = FontWeight.ExtraBold, fontSize = 12.sp, color = stColor, modifier = Modifier.padding(top = 4.dp))
                }

                Box(modifier = Modifier.width(1.dp).height(28.dp).background(Color(0xFFE2E8F0)))

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Vi phạm No-show", fontSize = 11.sp, color = Color(0xFF64748B))
                    val vCount = currentUser?.violations ?: 0
                    val vColor = if (vCount >= 3) Color(0xFFEF4444) else if (vCount > 0) Color(0xFFF59E0B) else Color(0xFF10B981)
                    Text("$vCount / 3", fontWeight = FontWeight.ExtraBold, fontSize = 13.sp, color = vColor, modifier = Modifier.padding(top = 4.dp))
                }

                Box(modifier = Modifier.width(1.dp).height(28.dp).background(Color(0xFFE2E8F0)))

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Bộ phận FSU", fontSize = 11.sp, color = Color(0xFF64748B))
                    Text(currentUser?.fsu ?: "FSU1.BU2", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color(0xFF1E293B), modifier = Modifier.padding(top = 4.dp))
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Settings list header
        Text(
            text = "Cài đặt & Tùy chọn",
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1E293B)
            ),
            modifier = Modifier.align(Alignment.Start)
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Languages preference configuration row in a Card
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = cardSurface),
            border = BorderStroke(width = 1.dp, color = Color(0xFFF1F5F9)),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
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
                            tint = primaryColor,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(14.dp))
                        Text(
                            text = "Ngôn ngữ",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1E293B)
                            )
                        )
                    }

                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(Color(0xFFF1F5F9))
                            .clickable { isVietnamese = !isVietnamese }
                            .padding(4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .background(if (isVietnamese) primaryColor else Color.Transparent)
                                .padding(horizontal = 14.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = "VI",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isVietnamese) Color.White else Color(0xFF64748B)
                            )
                        }
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .background(if (!isVietnamese) primaryColor else Color.Transparent)
                                .padding(horizontal = 14.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = "EN",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (!isVietnamese) Color.White else Color(0xFF64748B)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

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
                            tint = primaryColor,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(14.dp))
                        Text(
                            text = "Thông báo đẩy",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1E293B)
                            )
                        )
                    }

                    Switch(
                        checked = isNotificationEnabled,
                        onCheckedChange = { isNotificationEnabled = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = primaryColor,
                            uncheckedThumbColor = Color(0xFF94A3B8),
                            uncheckedTrackColor = Color(0xFFE2E8F0)
                        )
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        // Admin Console authorization switch button
        if (currentUser?.role == UserRole.ADMIN) {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFEF2F2)),
                border = BorderStroke(width = 1.dp, color = Color(0xFFFEE2E2)),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onNavigateToAdmin() }
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.AdminPanelSettings,
                        contentDescription = null,
                        tint = Color(0xFFEF4444),
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "CHUYỂN SANG GIAO DIỆN QUẢN TRỊ",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Black,
                            color = Color(0xFFEF4444)
                        )
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Logout action button
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .border(1.dp, Color(0xFFEF4444), RoundedCornerShape(16.dp))
                .clickable { onLogout() }
                .padding(vertical = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Logout,
                    contentDescription = null,
                    tint = Color(0xFFEF4444),
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "Đăng xuất",
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFEF4444),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}
