package com.example.fspacebooking.ui.screens.admin

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fspacebooking.ui.components.FCard
import com.example.fspacebooking.ui.viewmodels.AdminViewModel
import com.example.fspacebooking.utils.Resource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatisticsScreen(
    viewModel: AdminViewModel,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val statsState by viewModel.utilizationStats.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadStats()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        text = "BÁO CÁO HIỆU SUẤT SỬ DỤNG PHÒNG", 
                        fontWeight = FontWeight.Bold, 
                        color = AdminTextPrimary,
                        fontSize = 15.sp
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
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(AdminBg)
                .padding(innerPadding)
        ) {
            when (val state = statsState) {
                is Resource.Loading -> {
                    CircularProgressIndicator(color = AdminPrimary, modifier = Modifier.align(Alignment.Center))
                }
                is Resource.Error -> {
                    Text(
                        text = "Không thể tải báo cáo hiệu suất sử dụng: ${state.message}",
                        color = Color(0xFFEF4444),
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                is Resource.Success -> {
                    val stats = state.data ?: emptyMap()
                    
                    LazyColumn(
                        contentPadding = PaddingValues(start = 20.dp, end = 20.dp, bottom = 32.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        // Section 1: Peak Booking Workspace Highlight Card
                        item {
                            Card(
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = AdminSurface),
                                border = BorderStroke(1.dp, AdminBorder),
                                modifier = Modifier.fillMaxWidth().padding(top = 16.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(50.dp)
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(AdminPrimary.copy(alpha = 0.15f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.AutoMirrored.Filled.TrendingUp,
                                            contentDescription = null,
                                            tint = AdminPrimary
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(16.dp))
                                    Column {
                                        Text(
                                            text = "Gosling Boardroom (F-Town 3)",
                                            fontWeight = FontWeight.ExtraBold,
                                            style = MaterialTheme.typography.titleMedium,
                                            color = AdminTextPrimary
                                        )
                                        Text(
                                            text = "Tần suất sử dụng đạt đỉnh tuần này (92% tỷ lệ lấp đầy).",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = AdminTextSecondary
                                        )
                                    }
                                }
                            }
                        }

                        // Section 2: Building Average Occupancy
                        item {
                            Text(
                                text = "TỶ LỆ LẤP ĐẦY TRUNG BÌNH THEO TÒA NHÀ",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = AdminPrimary,
                                letterSpacing = 1.5.sp,
                                modifier = Modifier.padding(top = 8.dp)
                            )
                        }

                        item {
                            Card(
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = AdminSurface),
                                border = BorderStroke(0.5.dp, AdminBorder),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                                    BuildingStatRow(buildingName = "F-Town 1 Campus", rate = 72f, progressColor = AdminPrimary)
                                    BuildingStatRow(buildingName = "F-Town 2 Campus", rate = 45f, progressColor = AdminSecondary)
                                    BuildingStatRow(buildingName = "F-Town 3 Campus", rate = 88f, progressColor = AdminAccent)
                                }
                            }
                        }

                        // Section 3: Peak Reservation Hours list
                        item {
                            Text(
                                text = "KHUNG GIỜ CAO ĐIỂM ĐẶT CHỖ",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = AdminPrimary,
                                letterSpacing = 1.5.sp,
                                modifier = Modifier.padding(top = 8.dp)
                            )
                        }

                        item {
                            Card(
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = AdminSurface),
                                border = BorderStroke(0.5.dp, AdminBorder),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                    PeakHourRow(time = "09:00 - 11:00", rate = 88, status = "Cực kỳ bận")
                                    PeakHourRow(time = "14:00 - 16:00", rate = 74, status = "Bận vừa phải")
                                    PeakHourRow(time = "11:00 - 13:00", rate = 32, status = "Trống / Giờ nghỉ trưa")
                                }
                            }
                        }

                        // Section 4: Individual Space Occupancy Frequency list
                        item {
                            Text(
                                text = "TẦN SUẤT SỬ DỤNG TỪNG PHÒNG",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = AdminPrimary,
                                letterSpacing = 1.5.sp,
                                modifier = Modifier.padding(top = 8.dp)
                            )
                        }

                        items(stats.toList()) { (spaceName, rate) ->
                            Card(
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = AdminSurface),
                                border = BorderStroke(0.5.dp, AdminBorder),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = spaceName,
                                            fontWeight = FontWeight.Bold,
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = AdminTextPrimary
                                        )
                                        Text(
                                            text = "${rate.toInt()}%",
                                            fontWeight = FontWeight.Black,
                                            style = MaterialTheme.typography.titleSmall,
                                            color = when {
                                                rate >= 80f -> AdminPrimary
                                                rate < 30f -> AdminTextSecondary
                                                else -> AdminSecondary
                                            }
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(10.dp))

                                    // Progress Bar
                                    val progressColor = when {
                                        rate >= 80f -> AdminPrimary
                                        rate < 30f -> AdminTextSecondary
                                        else -> AdminSecondary
                                    }
                                    LinearProgressIndicator(
                                        progress = { rate / 100f },
                                        color = progressColor,
                                        trackColor = AdminBg,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(6.dp)
                                            .clip(RoundedCornerShape(3.dp))
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

@Composable
fun BuildingStatRow(buildingName: String, rate: Float, progressColor: Color) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(text = buildingName, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = AdminTextPrimary)
            Text(text = "${rate.toInt()}% TB", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = progressColor)
        }
        Spacer(modifier = Modifier.height(6.dp))
        LinearProgressIndicator(
            progress = { rate / 100f },
            color = progressColor,
            trackColor = AdminBg,
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp))
        )
    }
}

@Composable
fun PeakHourRow(time: String, rate: Int, status: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(imageVector = Icons.Default.Schedule, contentDescription = null, tint = AdminTextSecondary, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(text = time, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = AdminTextPrimary)
                Text(text = status, fontSize = 10.sp, color = AdminTextSecondary)
            }
        }
        Text(
            text = "$rate% Tải",
            fontWeight = FontWeight.Black,
            fontSize = 12.sp,
            color = if (rate >= 80) Color(0xFFEF4444) else AdminSecondary
        )
    }
}
