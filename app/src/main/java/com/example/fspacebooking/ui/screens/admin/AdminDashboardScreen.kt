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
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.LaptopMac
import androidx.compose.material.icons.filled.MeetingRoom
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fspacebooking.domain.model.BookingStatus
import com.example.fspacebooking.domain.model.ResourceType
import com.example.fspacebooking.ui.components.FButton
import com.example.fspacebooking.ui.viewmodels.AdminViewModel
import com.example.fspacebooking.utils.Resource

// High Tech Enterprise Dark Theme Colors
val AdminBg = Color(0xFF0B0F19)
val AdminSurface = Color(0xFF151D30)
val AdminBorder = Color(0xFF263554)
val AdminPrimary = Color(0xFF38BDF8) // Bright Sky Blue
val AdminSecondary = Color(0xFF2DD4BF) // Mint green
val AdminAccent = Color(0xFFA78BFA) // Lavender
val AdminTextPrimary = Color(0xFFF1F5F9)
val AdminTextSecondary = Color(0xFF94A3B8)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(
    viewModel: AdminViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToCatalog: () -> Unit,
    onNavigateToStats: () -> Unit,
    modifier: Modifier = Modifier
) {
    val bookingsState by viewModel.allBookings.collectAsState()
    val resourcesState by viewModel.allResources.collectAsState()
    val auditLogs by viewModel.auditLogs.collectAsState()

    var activeBuildingFilter by remember { mutableStateOf("Tất cả tòa nhà") }
    val buildings = listOf("Tất cả tòa nhà", "F-Town 1", "F-Town 2", "F-Town 3")

    LaunchedEffect(Unit) {
        viewModel.loadDashboardData()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        text = "TRUNG TÂM KIỂM SOÁT HỆ THỐNG", 
                        fontWeight = FontWeight.Black, 
                        letterSpacing = 1.sp,
                        fontSize = 16.sp,
                        color = AdminTextPrimary
                    ) 
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack, 
                            contentDescription = "Quay lại",
                            tint = AdminTextPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = AdminBg)
            )
        },
        modifier = modifier
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(AdminBg)
                .padding(innerPadding),
            contentPadding = PaddingValues(start = 20.dp, end = 20.dp, bottom = 32.dp)
        ) {
            val bookings = (bookingsState as? Resource.Success)?.data ?: emptyList()
            val resources = (resourcesState as? Resource.Success)?.data ?: emptyList()
            
            val totalReservations = bookings.filter { it.status == BookingStatus.CONFIRMED }.size
            val totalMaint = resources.filter { it.status == com.example.fspacebooking.domain.model.ResourceStatus.MAINTENANCE }.size
            val activeSpaces = resources.size - totalMaint

            // Summary statistics grid section
            item {
                Text(
                    text = "CHỈ SỐ THỐNG KÊ HỆ THỐNG",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = AdminPrimary,
                    letterSpacing = 1.5.sp,
                    modifier = Modifier.padding(top = 16.dp, bottom = 12.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OverviewStatCard(
                        title = "Không gian khả dụng",
                        value = "$activeSpaces",
                        icon = Icons.Default.MeetingRoom,
                        color = AdminSecondary,
                        modifier = Modifier.weight(1f)
                    )

                    OverviewStatCard(
                        title = "Đã đặt hôm nay",
                        value = "$totalReservations",
                        icon = Icons.Default.CalendarToday,
                        color = AdminPrimary,
                        modifier = Modifier.weight(1f)
                    )

                    OverviewStatCard(
                        title = "Đang bảo trì",
                        value = "$totalMaint",
                        icon = Icons.Default.Build,
                        color = Color(0xFFEF4444),
                        modifier = Modifier.weight(1f)
                    )
                }
                Spacer(modifier = Modifier.height(20.dp))
            }

            // Quick navigation panel card
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = AdminSurface),
                    border = BorderStroke(1.dp, AdminBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "QUẢN LÝ & PHÂN TÍCH HIỆU SUẤT",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = AdminAccent,
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            FButton(
                                text = "Danh mục tài nguyên",
                                onClick = onNavigateToCatalog,
                                modifier = Modifier.weight(1f)
                            )
                            FButton(
                                text = "Báo cáo sử dụng",
                                onClick = onNavigateToStats,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
            }

            // Horizontal Building Filter chips
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(imageVector = Icons.Default.FilterList, contentDescription = null, tint = AdminTextSecondary, modifier = Modifier.size(16.dp))
                    Text(
                        text = " LỌC THEO VỊ TRÍ TOÀ NHÀ",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = AdminTextSecondary,
                        letterSpacing = 1.sp,
                        modifier = Modifier.padding(vertical = 12.dp)
                    )
                }
                
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    buildings.forEach { bld ->
                        val isSelected = bld == activeBuildingFilter
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) AdminPrimary else AdminSurface)
                                .border(1.dp, if (isSelected) Color.Transparent else AdminBorder, RoundedCornerShape(8.dp))
                                .clickable { activeBuildingFilter = bld }
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = bld,
                                color = if (isSelected) Color.Black else AdminTextSecondary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            // Bookings and Schedules Section
            item {
                Text(
                    text = "DANH SÁCH LỊCH ĐẶT HOẠT ĐỘNG",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = AdminPrimary,
                    letterSpacing = 1.5.sp,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
            }

            when (val bookingsRes = bookingsState) {
                is Resource.Loading -> {
                    item {
                        Box(modifier = Modifier.fillMaxWidth().height(100.dp), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(strokeWidth = 2.dp, color = AdminPrimary, modifier = Modifier.size(28.dp))
                        }
                    }
                }
                is Resource.Error -> {
                    item {
                        Box(modifier = Modifier.fillMaxWidth().height(100.dp), contentAlignment = Alignment.Center) {
                            Text("Không thể tải danh sách đặt phòng.", color = Color(0xFFEF4444))
                        }
                    }
                }
                is Resource.Success -> {
                    var filteredList = bookingsRes.data ?: emptyList()
                    if (activeBuildingFilter != "Tất cả tòa nhà") {
                        filteredList = filteredList.filter { it.resource.building.equals(activeBuildingFilter, ignoreCase = true) }
                    }

                    if (filteredList.isEmpty()) {
                        item {
                            Card(
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = AdminSurface),
                                border = BorderStroke(1.dp, AdminBorder),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Box(modifier = Modifier.fillMaxWidth().padding(24.dp), contentAlignment = Alignment.Center) {
                                    Text("Không có lịch đặt phòng nào tại địa điểm này.", color = AdminTextSecondary)
                                }
                            }
                        }
                    } else {
                        items(filteredList) { booking ->
                            Card(
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = AdminSurface),
                                border = BorderStroke(0.5.dp, AdminBorder),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(14.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(40.dp)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(AdminBg),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = if (booking.resource.type == ResourceType.ROOM) Icons.Default.MeetingRoom else Icons.Default.LaptopMac,
                                            contentDescription = null,
                                            tint = if (booking.status == BookingStatus.CANCELLED) AdminTextSecondary else AdminPrimary,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(14.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = booking.resource.name,
                                            fontWeight = FontWeight.Bold,
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = if (booking.status == BookingStatus.CANCELLED) AdminTextSecondary else AdminTextPrimary
                                        )
                                        Text(
                                            text = "Nhân viên: ${booking.username} • ${booking.date}",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = AdminTextSecondary
                                        )
                                    }
                                    Column(horizontalAlignment = Alignment.End) {
                                        Text(
                                            text = booking.timeSlot.startTime,
                                            fontWeight = FontWeight.Bold,
                                            style = MaterialTheme.typography.labelSmall,
                                            color = if (booking.status == BookingStatus.CANCELLED) AdminTextSecondary else AdminSecondary
                                        )
                                        Text(
                                            text = if (booking.status == BookingStatus.CANCELLED) "Đã hủy" else "Hoạt động",
                                            style = MaterialTheme.typography.labelSmall,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (booking.status == BookingStatus.CANCELLED) Color.Gray else AdminSecondary
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Realtime Audit Terminal logs section (High Tech vibes)
            item {
                Spacer(modifier = Modifier.height(28.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Terminal, contentDescription = null, tint = AdminPrimary, modifier = Modifier.size(16.dp))
                    Text(
                        text = " LỊCH SỬ HỆ THỐNG THỜI GIAN THỰC",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = AdminPrimary,
                        letterSpacing = 1.5.sp
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.Black),
                    border = BorderStroke(1.dp, AdminBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        if (auditLogs.isEmpty()) {
                            TerminalLogLine(timestamp = "09:00:00", log = "SYS: Hệ thống F-Space đã khởi động và sẵn sàng hoạt động.")
                        } else {
                            auditLogs.take(6).forEach { entry ->
                                TerminalLogLine(timestamp = entry.timestamp, log = "${entry.category}: ${entry.message}")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TerminalLogLine(timestamp: String, log: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp)
    ) {
        Text(
            text = "[$timestamp]",
            color = AdminSecondary,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = " $log",
            color = Color(0xFF10B981), // Matrix Green
            fontSize = 10.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun OverviewStatCard(
    title: String,
    value: String,
    icon: ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = AdminSurface),
        border = BorderStroke(1.dp, AdminBorder),
        modifier = modifier.height(105.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(20.dp)
            )
            Column {
                Text(
                    text = value,
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold),
                    color = color
                )
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelSmall,
                    color = AdminTextSecondary,
                    maxLines = 1,
                    fontSize = 9.sp
                )
            }
        }
    }
}
