package com.example.fspacebooking.ui.screens.admin

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Mail
import androidx.compose.material.icons.filled.People
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.animation.core.tween
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fspacebooking.ui.components.FButton
import com.example.fspacebooking.ui.viewmodels.AdminViewModel

@Composable
fun AdminTabContainerScreen(
    viewModel: AdminViewModel,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var activeTabIndex by rememberSaveable { mutableIntStateOf(0) }

    val adminTabs = listOf(
        AdminTabItem("Tổng quan", Icons.Default.Dashboard),
        AdminTabItem("Thống kê", Icons.Default.Analytics),
        AdminTabItem("Tài nguyên", Icons.Default.Build),
        AdminTabItem("Vi phạm", Icons.Default.Gavel)
    )

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = AdminSurface,
                tonalElevation = 8.dp
            ) {
                adminTabs.forEachIndexed { index, tab ->
                    NavigationBarItem(
                        selected = activeTabIndex == index,
                        onClick = { activeTabIndex = index },
                        icon = { 
                            Icon(
                                imageVector = tab.icon, 
                                contentDescription = tab.label
                            ) 
                        },
                        label = { 
                            Text(
                                text = tab.label,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            ) 
                        },
                        colors = androidx.compose.material3.NavigationBarItemDefaults.colors(
                            selectedIconColor = Color.Black,
                            selectedTextColor = AdminPrimary,
                            indicatorColor = AdminPrimary,
                            unselectedIconColor = AdminTextSecondary,
                            unselectedTextColor = AdminTextSecondary
                        )
                    )
                }
            }
        },
        modifier = modifier
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(AdminBg)
                .padding(innerPadding)
        ) {
            AnimatedContent(
                targetState = activeTabIndex,
                transitionSpec = {
                    fadeIn(animationSpec = tween(220)) togetherWith fadeOut(animationSpec = tween(220))
                },
                label = "AdminTabTransition",
                modifier = Modifier.fillMaxSize()
            ) { targetIndex ->
                when (targetIndex) {
                    0 -> AdminDashboardScreen(
                        viewModel = viewModel,
                        onNavigateBack = onNavigateBack,
                        onNavigateToCatalog = { activeTabIndex = 2 },
                        onNavigateToStats = { activeTabIndex = 1 }
                    )
                    1 -> StatisticsScreen(
                        viewModel = viewModel,
                        onNavigateBack = onNavigateBack
                    )
                    2 -> ResourceManagementScreen(
                        viewModel = viewModel,
                        onNavigateBack = onNavigateBack
                    )
                    3 -> AdminBlacklistUsersScreen(
                        viewModel = viewModel,
                        onNavigateBack = onNavigateBack
                    )
                }
            }
        }
    }
}

@Composable
fun AdminBlacklistUsersScreen(
    viewModel: AdminViewModel,
    onNavigateBack: () -> Unit
) {
    val usersList by viewModel.violatorsList.collectAsState()
    var snackbarMessage by rememberSaveable { mutableStateOf<String?>(null) }

    val employees = remember(usersList) {
        usersList.filter { it.role == com.example.fspacebooking.domain.model.UserRole.EMPLOYEE }
    }

    Scaffold(
        snackbarHost = {
            snackbarMessage?.let { msg ->
                androidx.compose.material3.Snackbar(
                    containerColor = AdminSurface,
                    contentColor = AdminTextPrimary,
                    modifier = Modifier.padding(16.dp),
                    action = {
                        androidx.compose.material3.TextButton(onClick = { snackbarMessage = null }) {
                            Text("Đóng", color = AdminPrimary)
                        }
                    }
                ) {
                    Text(msg)
                }
            }
        },
        containerColor = AdminBg
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(AdminBg)
                .padding(innerPadding)
        ) {
            // Custom Top Bar for Dark console aesthetics
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onNavigateBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = null,
                        tint = AdminTextPrimary
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "DANH SÁCH VI PHẠM CHECK-IN",
                    fontWeight = FontWeight.Black,
                    fontSize = 16.sp,
                    letterSpacing = 1.sp,
                    color = AdminTextPrimary
                )
            }

            LazyColumn(
                contentPadding = PaddingValues(start = 20.dp, end = 20.dp, bottom = 32.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                item {
                    Text(
                        text = "DANH SÁCH NHÂN VIÊN VI PHẠM (NO-SHOW)",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = AdminPrimary,
                        letterSpacing = 1.5.sp,
                        modifier = Modifier.padding(top = 16.dp, bottom = 4.dp)
                    )
                    Text(
                        text = "Nhân viên đặt phòng họp mà không quét QR check-in quá 3 lần sẽ bị khóa chức năng đặt chỗ tạm thời.",
                        fontSize = 10.sp,
                        color = AdminTextSecondary,
                        lineHeight = 14.sp,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                }

                items(employees) { user ->
                    val statusColor = when (user.userStatus) {
                        "ĐÃ KHÓA" -> Color(0xFFEF4444)
                        "CẢNH BÁO" -> Color(0xFFF59E0B)
                        "THEO DÕI" -> Color(0xFF3B82F6)
                        else -> Color(0xFF10B981)
                    }

                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = AdminSurface),
                        border = BorderStroke(1.dp, AdminBorder),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(38.dp)
                                            .clip(CircleShape)
                                            .background(statusColor.copy(alpha = 0.15f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.People,
                                            contentDescription = null,
                                            tint = statusColor,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text(
                                            text = user.name,
                                            fontWeight = FontWeight.Bold,
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = AdminTextPrimary
                                        )
                                        Text(
                                            text = "ID: ${user.employeeId} • ${user.fsu} • ${user.violations}/3 lần vi phạm",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = AdminTextSecondary
                                        )
                                    }
                                }

                                // Status badge
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(statusColor.copy(alpha = 0.12f))
                                        .border(0.5.dp, statusColor, RoundedCornerShape(6.dp))
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                    ) {
                                    Text(
                                        text = user.userStatus,
                                        color = statusColor,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            // Controls action row
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Send Warning Mail action button
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(12.dp))
                                        .border(0.5.dp, AdminBorder, RoundedCornerShape(12.dp))
                                        .clickable {
                                            viewModel.sendWarningEmail(user.id)
                                            snackbarMessage = "Đã gửi email nhắc nhở vi phạm tới ${user.email}!"
                                        }
                                        .padding(horizontal = 12.dp, vertical = 6.dp)
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(imageVector = Icons.Default.Mail, contentDescription = null, tint = AdminTextSecondary, modifier = Modifier.size(12.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("Gửi nhắc nhở", color = AdminTextSecondary, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                    }
                                }

                                Spacer(modifier = Modifier.width(8.dp))

                                // Lock / Unlock account action button
                                val isLocked = user.userStatus == "ĐÃ KHÓA"
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(if (isLocked) AdminPrimary else Color.Transparent)
                                        .border(0.5.dp, if (isLocked) Color.Transparent else AdminBorder, RoundedCornerShape(12.dp))
                                        .clickable {
                                            if (isLocked) {
                                                viewModel.unlockUser(user.id)
                                                snackbarMessage = "Đã mở khóa đặt phòng cho nhân viên ${user.name}!"
                                            } else {
                                                viewModel.lockUser(user.id)
                                                snackbarMessage = "Đã khóa quyền đặt phòng của ${user.name}!"
                                            }
                                        }
                                        .padding(horizontal = 12.dp, vertical = 6.dp)
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = if (isLocked) Icons.Default.LockOpen else Icons.Default.Lock,
                                            contentDescription = null,
                                            tint = if (isLocked) Color.Black else AdminTextSecondary,
                                            modifier = Modifier.size(12.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = if (isLocked) "Mở khóa tài khoản" else "Khóa đặt phòng",
                                            color = if (isLocked) Color.Black else AdminTextSecondary,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

private data class BlacklistUser(
    val name: String,
    val empId: String,
    val violations: Int,
    val status: String,
    val statusColor: Color
)

private data class AdminTabItem(
    val label: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector
)
