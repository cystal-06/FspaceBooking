package com.example.fspacebooking.ui.screens.home

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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.LaptopMac
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MeetingRoom
import androidx.compose.material.icons.filled.People
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fspacebooking.domain.model.ResourceType
import com.example.fspacebooking.domain.model.UserRole
import com.example.fspacebooking.ui.components.AmenityChip
import com.example.fspacebooking.ui.components.BookingStatusBadge
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
import com.example.fspacebooking.ui.theme.FptBlue
import com.example.fspacebooking.ui.theme.FptBlueLight
import com.example.fspacebooking.ui.theme.FptEmerald
import com.example.fspacebooking.ui.theme.FptOrange
import com.example.fspacebooking.ui.theme.FptOrangeLight
import com.example.fspacebooking.ui.theme.FptRose
import com.example.fspacebooking.ui.viewmodels.HomeViewModel
import com.example.fspacebooking.utils.Resource

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onNavigateToSearch: (ResourceType?) -> Unit,
    onNavigateToMyBookings: () -> Unit,
    onNavigateToAdmin: () -> Unit,
    onNavigateToBooking: (String) -> Unit,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier
) {
    val currentUser by viewModel.currentUser.collectAsState()
    val todayBookingsState by viewModel.todayBookings.collectAsState()
    val favorites by viewModel.favorites.collectAsState()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(AppBackground)
    ) {
        LazyColumn(
            contentPadding = PaddingValues(bottom = 28.dp),
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp)
        ) {
            // Enterprise Header: Avatar + User Info + Role + Logout
            item {
                Spacer(modifier = Modifier.height(24.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        // User Avatar with status dot
                        Box(contentAlignment = Alignment.BottomEnd) {
                            val initial = currentUser?.name?.take(1)?.uppercase() ?: "Đ"
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(CircleShape)
                                    .background(
                                        Brush.linearGradient(
                                            listOf(FptBlue, Color(0xFF60A5FA))
                                        )
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = initial,
                                    color = Color.White,
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            // Active status dot
                            Box(
                                modifier = Modifier
                                    .size(13.dp)
                                    .clip(CircleShape)
                                    .background(Color.White)
                                    .padding(2.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .clip(CircleShape)
                                        .background(FptEmerald)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            val greetingText = when (currentUser?.role) {
                                UserRole.ADMIN -> "Xin chào, Quản trị viên"
                                else -> "Chào buổi sáng"
                            }
                            Text(
                                text = greetingText,
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Medium,
                                    color = AppTextSecondary,
                                    fontSize = 12.sp
                                )
                            )
                            Text(
                                text = "${currentUser?.name ?: "Đức Nguyễn"} 👋",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 18.sp,
                                    color = AppTextPrimary
                                )
                            )
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(top = 2.dp)
                            ) {
                                Text(
                                    text = "FPT Campus • ${currentUser?.employeeId ?: "EMP888"}",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = AppTextSecondary,
                                        fontSize = 11.sp
                                    )
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(FptBlueLight)
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    val roleLabel = when (currentUser?.role) {
                                        UserRole.ADMIN -> "Quản trị viên"
                                        else -> "Nhân viên"
                                    }
                                    Text(
                                        text = roleLabel,
                                        color = FptBlue,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }

                    // Logout Icon Button
                    IconButton(
                        onClick = onLogout,
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(AppCardSurface)
                            .border(1.dp, AppBorder, RoundedCornerShape(12.dp))
                            .size(42.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Logout,
                            contentDescription = "Logout",
                            tint = FptRose,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(20.dp))
            }

            // Hero Card (Upcoming Booking or Quick CTA)
            item {
                val activeBookings = (todayBookingsState as? Resource.Success)?.data ?: emptyList()
                val upcomingBooking = activeBookings.firstOrNull()

                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = FptBlue),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 24.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(FptBlue, Color(0xFF1E40AF))
                                )
                            )
                            .fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            if (upcomingBooking != null) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(Color.White.copy(alpha = 0.2f))
                                            .padding(horizontal = 8.dp, vertical = 3.dp)
                                    ) {
                                        Text(
                                            text = "LỊCH HỌP SẮP DIỄN RA",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontWeight = FontWeight.ExtraBold,
                                                letterSpacing = 0.8.sp,
                                                fontSize = 10.sp
                                            ),
                                            color = Color.White
                                        )
                                    }
                                    Text(
                                        text = "Hôm nay",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = Color.White.copy(alpha = 0.8f),
                                            fontWeight = FontWeight.Medium
                                        )
                                    )
                                }
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    text = upcomingBooking.resource.name,
                                    style = MaterialTheme.typography.titleLarge.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 19.sp,
                                        lineHeight = 24.sp
                                    ),
                                    color = Color.White
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.LocationOn,
                                        contentDescription = null,
                                        tint = Color.White.copy(alpha = 0.85f),
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "${upcomingBooking.resource.building} • ${upcomingBooking.resource.floor}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color.White.copy(alpha = 0.9f)
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.AccessTime,
                                        contentDescription = null,
                                        tint = Color.White.copy(alpha = 0.85f),
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "${upcomingBooking.timeSlot.startTime} - ${upcomingBooking.timeSlot.endTime}",
                                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                                        color = Color.White
                                    )
                                }
                                Spacer(modifier = Modifier.height(18.dp))
                                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(Color.White)
                                            .clickable { onNavigateToBooking(upcomingBooking.resource.id) }
                                            .padding(horizontal = 16.dp, vertical = 9.dp)
                                    ) {
                                        Text(
                                            text = "Xem chi tiết",
                                            color = FptBlue,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(12.dp))
                                            .border(1.dp, Color.White.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                                            .clickable { viewModel.cancelBooking(upcomingBooking.id) }
                                            .padding(horizontal = 16.dp, vertical = 9.dp)
                                    ) {
                                        Text(
                                            text = "Hủy lịch",
                                            color = Color.White,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            } else {
                                // Elegant empty state
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(Color.White.copy(alpha = 0.2f))
                                        .padding(horizontal = 8.dp, vertical = 3.dp)
                                ) {
                                    Text(
                                        text = "KHÔNG CÓ LỊCH HỌP SẮP TỚI",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = FontWeight.ExtraBold,
                                            letterSpacing = 0.8.sp,
                                            fontSize = 10.sp
                                        ),
                                        color = Color.White
                                    )
                                }
                                Spacer(modifier = Modifier.height(10.dp))
                                Text(
                                    text = "Bạn chưa có lịch đặt chỗ hôm nay",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 17.sp
                                    ),
                                    color = Color.White
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Khám phá phòng họp hoặc bàn làm việc linh hoạt có sẵn tại Campus.",
                                    style = MaterialTheme.typography.bodySmall.copy(lineHeight = 18.sp),
                                    color = Color.White.copy(alpha = 0.85f)
                                )
                                Spacer(modifier = Modifier.height(18.dp))
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(Color.White)
                                        .clickable { onNavigateToSearch(null) }
                                        .padding(horizontal = 18.dp, vertical = 10.dp)
                                ) {
                                    Text(
                                        text = "Đặt không gian ngay",
                                        color = FptBlue,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Quick Actions Section
            item {
                Text(
                    text = "Không Gian Làm Việc",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp,
                        color = AppTextPrimary
                    ),
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Meeting Room Card
                    WorkspaceActionCard(
                        title = "Phòng Họp",
                        subtitle = "Trang bị màn hình 4K, smartboard",
                        availability = "8 phòng trống",
                        icon = Icons.Default.MeetingRoom,
                        iconBg = FptBlueLight,
                        iconTint = FptBlue,
                        onClick = { onNavigateToSearch(ResourceType.ROOM) },
                        modifier = Modifier.weight(1f)
                    )

                    // Hot-desk Card
                    WorkspaceActionCard(
                        title = "Bàn Làm Việc",
                        subtitle = "Ghế công thái học, Wifi 6",
                        availability = "24 bàn trống",
                        icon = Icons.Default.LaptopMac,
                        iconBg = FptOrangeLight,
                        iconTint = FptOrange,
                        onClick = { onNavigateToSearch(ResourceType.DESK) },
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // My Schedule Quick Button
                    MinimalActionButton(
                        title = "Lịch trình cá nhân",
                        icon = Icons.Default.CalendarToday,
                        onClick = onNavigateToMyBookings,
                        modifier = Modifier.weight(1f)
                    )

                    // Admin Console Quick Button
                    if (currentUser?.role == UserRole.ADMIN) {
                        MinimalActionButton(
                            title = "Bảng điều khiển Admin",
                            icon = Icons.Default.AdminPanelSettings,
                            onClick = onNavigateToAdmin,
                            modifier = Modifier.weight(1f),
                            isDanger = true
                        )
                    }
                }

                Spacer(modifier = Modifier.height(28.dp))
            }

            // Today's Bookings Section
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Lịch trình hôm nay",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp,
                            color = AppTextPrimary
                        )
                    )
                    Text(
                        text = "Xem tất cả",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = FptBlue
                        ),
                        modifier = Modifier.clickable { onNavigateToMyBookings() }
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
            }

            when (val bookingsRes = todayBookingsState) {
                is Resource.Loading -> {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(80.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = FptBlue, strokeWidth = 2.dp, modifier = Modifier.size(24.dp))
                        }
                    }
                }
                is Resource.Error -> {
                    item {
                        FCard(modifier = Modifier.fillMaxWidth()) {
                            Text("Không thể tải lịch trình.", color = MaterialTheme.colorScheme.error)
                        }
                    }
                }
                is Resource.Success -> {
                    val list = bookingsRes.data ?: emptyList()
                    if (list.isEmpty()) {
                        item {
                            Card(
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = AppCardSurface),
                                border = BorderStroke(1.dp, AppBorder),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(24.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = "Không có lịch đặt chỗ hôm nay",
                                        fontWeight = FontWeight.Bold,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = AppTextSecondary
                                    )
                                    Text(
                                        text = "Hãy chọn không gian làm việc trống để bắt đầu.",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = AppTextMuted,
                                        modifier = Modifier.padding(top = 4.dp)
                                    )
                                }
                            }
                        }
                    } else {
                        items(list) { booking ->
                            Card(
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = AppCardSurface),
                                border = BorderStroke(1.dp, AppBorder),
                                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 5.dp)
                                    .clickable { onNavigateToBooking(booking.resource.id) }
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(44.dp)
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(
                                                if (booking.resource.type == ResourceType.ROOM) FptBlueLight else FptOrangeLight
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = if (booking.resource.type == ResourceType.ROOM) Icons.Default.MeetingRoom else Icons.Default.LaptopMac,
                                            contentDescription = null,
                                            tint = if (booking.resource.type == ResourceType.ROOM) FptBlue else FptOrange,
                                            modifier = Modifier.size(22.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(14.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = booking.resource.name,
                                            fontWeight = FontWeight.Bold,
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = AppTextPrimary
                                        )
                                        Text(
                                            text = "${booking.resource.building} • ${booking.resource.floor}",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = AppTextSecondary,
                                            modifier = Modifier.padding(top = 2.dp)
                                        )
                                    }
                                    Column(horizontalAlignment = Alignment.End) {
                                        Text(
                                            text = "${booking.timeSlot.startTime} - ${booking.timeSlot.endTime}",
                                            fontWeight = FontWeight.ExtraBold,
                                            style = MaterialTheme.typography.labelMedium,
                                            color = FptBlue
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        BookingStatusBadge(status = booking.status)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Favorites section
            item {
                Spacer(modifier = Modifier.height(28.dp))
                Text(
                    text = "Không gian yêu thích",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp,
                        color = AppTextPrimary
                    ),
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                if (favorites.isEmpty()) {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = AppCardSurface),
                        border = BorderStroke(1.dp, AppBorder),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Danh sách yêu thích trống.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = AppTextSecondary
                            )
                        }
                    }
                } else {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(favorites) { resource ->
                            Card(
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = AppCardSurface),
                                border = BorderStroke(1.dp, AppBorder),
                                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                                modifier = Modifier
                                    .width(260.dp)
                                    .clickable { onNavigateToBooking(resource.id) }
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = resource.name,
                                            fontWeight = FontWeight.Bold,
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = AppTextPrimary,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis,
                                            modifier = Modifier.weight(1f)
                                        )
                                        IconButton(
                                            onClick = { viewModel.toggleFavorite(resource.id, false) },
                                            modifier = Modifier.size(24.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Favorite,
                                                contentDescription = null,
                                                tint = FptRose,
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }
                                    }
                                    Text(
                                        text = "${resource.building} • ${resource.floor}",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = AppTextSecondary,
                                        modifier = Modifier.padding(vertical = 4.dp)
                                    )
                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                                        modifier = Modifier.padding(vertical = 6.dp)
                                    ) {
                                        AmenityChip(
                                            name = "${resource.capacity} chỗ",
                                            icon = Icons.Default.People
                                        )
                                        resource.amenities.firstOrNull()?.let {
                                            AmenityChip(name = it)
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(10.dp))
                                    FButton(
                                        text = "Đặt phòng ngay",
                                        onClick = { onNavigateToBooking(resource.id) },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(44.dp)
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
fun WorkspaceActionCard(
    title: String,
    subtitle: String,
    availability: String,
    icon: ImageVector,
    iconBg: Color,
    iconTint: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = AppCardSurface),
        border = BorderStroke(width = 1.dp, color = AppBorder),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        modifier = modifier
            .height(145.dp)
            .clickable { onClick() }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(iconBg),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = iconTint,
                        modifier = Modifier.size(22.dp)
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(iconBg)
                        .padding(horizontal = 6.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = availability,
                        color = iconTint,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Column {
                Text(
                    text = title,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.bodyMedium.copy(fontSize = 15.sp),
                    color = AppTextPrimary
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.labelSmall,
                    color = AppTextSecondary,
                    fontSize = 11.sp,
                    lineHeight = 14.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
fun MinimalActionButton(
    title: String,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isDanger: Boolean = false
) {
    val containerColor = if (isDanger) Color(0xFFFEF2F2) else AppCardSurface
    val contentColor = if (isDanger) FptRose else AppTextPrimary
    val borderColor = if (isDanger) Color(0xFFFCA5A5) else AppBorder

    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        border = BorderStroke(1.dp, borderColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        modifier = modifier
            .height(48.dp)
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = contentColor,
                modifier = Modifier.size(17.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.labelMedium.copy(fontSize = 12.sp),
                color = contentColor
            )
        }
    }
}
