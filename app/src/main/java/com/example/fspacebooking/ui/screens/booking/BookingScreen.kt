package com.example.fspacebooking.ui.screens.booking

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LaptopMac
import androidx.compose.material.icons.filled.LocalCafe
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MeetingRoom
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.fspacebooking.domain.model.ResourceType
import com.example.fspacebooking.domain.model.ResourceStatus
import com.example.fspacebooking.domain.model.TimeSlot
import com.example.fspacebooking.domain.model.WorkspaceResource
import com.example.fspacebooking.ui.components.AmenityChip
import com.example.fspacebooking.ui.components.FButton
import com.example.fspacebooking.ui.components.FCard
import com.example.fspacebooking.ui.components.FTextField
import com.example.fspacebooking.ui.components.ModernSegmentedControl
import com.example.fspacebooking.ui.components.StatusBadge
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
import com.example.fspacebooking.ui.viewmodels.BookingViewModel
import com.example.fspacebooking.utils.Resource
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Composable
fun RoomShowcaseHeader(
    resource: WorkspaceResource,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isFavorite by remember { mutableStateOf(resource.isFavorite) }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(210.dp)
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF0F172A),
                        Color(0xFF1E293B),
                        Color(0xFF1E3A8A)
                    )
                )
            )
    ) {
        // Architectural grid pattern background
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height
            
            // Soft atmospheric circles
            drawCircle(
                color = FptBlue.copy(alpha = 0.22f),
                center = Offset(w * 0.85f, h * 0.2f),
                radius = 110f
            )
            drawCircle(
                color = FptOrange.copy(alpha = 0.15f),
                center = Offset(w * 0.12f, h * 0.8f),
                radius = 80f
            )
        }

        // Top Navigation Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(
                onClick = onNavigateBack,
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.2f))
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Quay lại",
                    tint = Color.White
                )
            }

            // Resource Type Capsule
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color.White.copy(alpha = 0.18f))
                    .border(0.5.dp, Color.White.copy(alpha = 0.3f), RoundedCornerShape(20.dp))
                    .padding(horizontal = 14.dp, vertical = 6.dp)
            ) {
                Text(
                    text = if (resource.type == ResourceType.ROOM) "PHÒNG HỌP HIỆN ĐẠI" else "CHỖ NGỒI LINH HOẠT",
                    color = Color.White,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.6.sp
                )
            }

            // Favorite Button
            IconButton(
                onClick = { isFavorite = !isFavorite },
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.2f))
            ) {
                Icon(
                    imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = "Yêu thích",
                    tint = if (isFavorite) FptRose else Color.White
                )
            }
        }

        // Title and location banner at bottom of header
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(horizontal = 20.dp, vertical = 18.dp)
        ) {
            Text(
                text = resource.name,
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Black,
                    fontSize = 22.sp
                ),
                color = Color.White
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = null,
                    tint = Color.White.copy(alpha = 0.85f),
                    modifier = Modifier.size(15.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "${resource.building} • ${resource.floor}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.9f),
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun BookingScreen(
    viewModel: BookingViewModel,
    resourceId: String,
    onNavigateBack: () -> Unit,
    onBookingSuccess: () -> Unit,
    modifier: Modifier = Modifier
) {
    val resourceState by viewModel.resourceState
    val timeSlotsState by viewModel.timeSlotsState
    val bookingResult by viewModel.bookingResultState
    val selectedDate by viewModel.selectedDate
    val selectedSlotId by viewModel.selectedSlotId
    val currentUser by viewModel.currentUser.collectAsState()

    val violations = currentUser?.violations ?: 0
    val isLocked = currentUser?.userStatus == "ĐÃ KHÓA" || violations >= 3

    var showConflictDialog by remember { mutableStateOf(false) }
    var conflictMessage by remember { mutableStateOf("") }
    var showSuccessDialog by remember { mutableStateOf(false) }
    var showLockedDialog by remember { mutableStateOf(false) }
    var lockedDialogMessage by remember { mutableStateOf("") }
    
    var showSummarySheet by remember { mutableStateOf(false) }
    var meetingPurpose by remember { mutableStateOf("") }

    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var selectedTab by remember { mutableIntStateOf(0) }
    val tabsList = listOf("Đặt lịch họp", "Tiện nghi & Sơ đồ", "Đánh giá (32)")

    LaunchedEffect(resourceId) {
        viewModel.loadResourceDetails(resourceId)
    }

    LaunchedEffect(bookingResult) {
        when (bookingResult) {
            is Resource.Success -> {
                showSuccessDialog = true
            }
            is Resource.Error -> {
                val errMsg = bookingResult?.message ?: "Trùng lịch đặt chỗ!"
                if (errMsg.contains("KHÓA") || errMsg.contains("khóa") || errMsg.contains("No-show")) {
                    lockedDialogMessage = errMsg
                    showLockedDialog = true
                } else if (errMsg.contains("409") || errMsg.contains("Conflict") || errMsg.contains("đặt") || errMsg.contains("trùng")) {
                    conflictMessage = errMsg
                    showConflictDialog = true
                } else {
                    coroutineScope.launch {
                        snackbarHostState.showSnackbar(errMsg)
                    }
                }
            }
            else -> {}
        }
    }

    // Scrollable Calendar Days (7 days from today)
    val calendarDays = remember {
        val list = mutableListOf<Date>()
        val cal = Calendar.getInstance()
        for (i in 0..6) {
            list.add(cal.time)
            cal.add(Calendar.DAY_OF_YEAR, 1)
        }
        list
    }
    val sdfDateKey = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val sdfDayLabel = SimpleDateFormat("EEE", Locale.forLanguageTag("vi-VN"))
    val sdfDayNum = SimpleDateFormat("dd", Locale.getDefault())

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        containerColor = AppBackground,
        modifier = modifier
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (val res = resourceState) {
                is Resource.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = FptBlue, strokeWidth = 2.dp, modifier = Modifier.size(36.dp))
                    }
                }
                is Resource.Error -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(
                            text = res.message ?: "Không thể tải thông tin không gian.",
                            color = FptRose,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                is Resource.Success -> {
                    val resource = res.data!!
                    val slotsList = (timeSlotsState as? Resource.Success)?.data ?: emptyList()
                    val selectedSlot = slotsList.find { it.id == selectedSlotId }

                    LazyColumn(
                        contentPadding = PaddingValues(bottom = 110.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        // Header Showcase Banner
                        item {
                            RoomShowcaseHeader(resource = resource, onNavigateBack = onNavigateBack)
                        }

                        // Space Specs Bar Card
                        item {
                            Spacer(modifier = Modifier.height(14.dp))
                            FCard(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 20.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(14.dp),
                                    horizontalArrangement = Arrangement.SpaceAround,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(imageVector = Icons.Default.People, contentDescription = null, tint = FptBlue, modifier = Modifier.size(18.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(text = "${resource.capacity} Chỗ ngồi", style = MaterialTheme.typography.labelMedium, color = AppTextPrimary, fontWeight = FontWeight.Bold)
                                    }
                                    Box(modifier = Modifier.width(1.dp).height(20.dp).background(AppBorder))
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(imageVector = Icons.Default.Star, contentDescription = null, tint = FptAmber, modifier = Modifier.size(18.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(text = "4.9 (32 đánh giá)", style = MaterialTheme.typography.labelMedium, color = AppTextPrimary, fontWeight = FontWeight.Bold)
                                    }
                                    Box(modifier = Modifier.width(1.dp).height(20.dp).background(AppBorder))
                                    StatusBadge(status = resource.status)
                                }
                            }
                        }

                        // Account Status / Violation Banner if applicable
                        if (isLocked || violations > 0) {
                            item {
                                Spacer(modifier = Modifier.height(10.dp))
                                Card(
                                    shape = RoundedCornerShape(14.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = if (isLocked) FptRoseLight else FptAmberLight
                                    ),
                                    border = BorderStroke(1.dp, if (isLocked) FptRose.copy(alpha = 0.4f) else FptAmber.copy(alpha = 0.4f)),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 20.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(14.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Warning,
                                            contentDescription = null,
                                            tint = if (isLocked) FptRose else FptAmber,
                                            modifier = Modifier.size(24.dp)
                                        )
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Column {
                                            Text(
                                                text = if (isLocked) "Tài khoản đang bị KHÓA đặt chỗ (UC12)" else "Cảnh báo vi phạm No-Show ($violations/3)",
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 13.sp,
                                                color = if (isLocked) FptRose else Color(0xFF92400E)
                                            )
                                            Text(
                                                text = if (isLocked) "Bạn đã vắng mặt quá 3 lần mà không check-in QR. Vui lòng liên hệ Admin để mở khóa." else "Quy chế FPT: Vắng mặt không quét QR quá 3 lần sẽ bị tự động khóa quyền đặt tài nguyên.",
                                                fontSize = 11.sp,
                                                color = if (isLocked) FptRose else Color(0xFFB45309),
                                                lineHeight = 15.sp
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        // Modern Segmented Control for Body Tabs
                        item {
                            ModernSegmentedControl(
                                items = tabsList,
                                selectedIndex = selectedTab,
                                onSelect = { selectedTab = it },
                                modifier = Modifier.padding(horizontal = 20.dp, vertical = 14.dp)
                            )
                        }

                        // Tab 0: Booking Flow (Calendar + Slots)
                        if (selectedTab == 0) {
                            item {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 20.dp)
                                ) {
                                    // 1. Date Selector
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.CalendarToday,
                                            contentDescription = null,
                                            tint = FptBlue,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = "1. Chọn ngày sử dụng",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.sp,
                                            color = AppTextPrimary
                                        )
                                        Spacer(modifier = Modifier.weight(1f))
                                        Text(
                                            text = "7 ngày tới",
                                            fontSize = 11.sp,
                                            color = AppTextSecondary
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(10.dp))

                                    // 7-day Pill Row
                                    LazyRow(
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        contentPadding = PaddingValues(vertical = 4.dp)
                                    ) {
                                        items(calendarDays) { date ->
                                            val dKey = sdfDateKey.format(date)
                                            val isSel = dKey == selectedDate
                                            val dayName = if (dKey == sdfDateKey.format(Date())) "Hôm nay" else sdfDayLabel.format(date).uppercase()
                                            val dayNum = sdfDayNum.format(date)

                                            Column(
                                                horizontalAlignment = Alignment.CenterHorizontally,
                                                modifier = Modifier
                                                    .width(62.dp)
                                                    .clip(RoundedCornerShape(16.dp))
                                                    .background(if (isSel) FptBlue else AppCardSurface)
                                                    .border(
                                                        width = 1.dp,
                                                        color = if (isSel) Color.Transparent else AppBorder,
                                                        shape = RoundedCornerShape(16.dp)
                                                    )
                                                    .clickable { viewModel.selectDate(dKey) }
                                                    .padding(vertical = 12.dp)
                                            ) {
                                                Text(
                                                    text = dayName,
                                                    color = if (isSel) Color.White.copy(alpha = 0.85f) else AppTextMuted,
                                                    fontSize = 10.sp,
                                                    fontWeight = FontWeight.Bold
                                                )
                                                Spacer(modifier = Modifier.height(4.dp))
                                                Text(
                                                    text = dayNum,
                                                    color = if (isSel) Color.White else AppTextPrimary,
                                                    fontSize = 18.sp,
                                                    fontWeight = FontWeight.Black
                                                )
                                                if (isSel) {
                                                    Spacer(modifier = Modifier.height(4.dp))
                                                    Box(
                                                        modifier = Modifier
                                                            .size(4.dp)
                                                            .clip(CircleShape)
                                                            .background(Color.White)
                                                    )
                                                }
                                            }
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(20.dp))

                                    // 2. Time Slots Header
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.AccessTime,
                                            contentDescription = null,
                                            tint = FptBlue,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = "2. Chọn khung giờ họp",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.sp,
                                            color = AppTextPrimary
                                        )
                                        Spacer(modifier = Modifier.weight(1f))
                                        Text(
                                            text = "Ca 90 phút",
                                            fontSize = 11.sp,
                                            color = AppTextSecondary
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(10.dp))
                                }
                            }

                            // Dynamic slot grid (Rendered as 2-column or list)
                            when (val slotsRes = timeSlotsState) {
                                is Resource.Loading -> {
                                    item {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(80.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            CircularProgressIndicator(color = FptBlue, strokeWidth = 2.dp, modifier = Modifier.size(28.dp))
                                        }
                                    }
                                }
                                is Resource.Error -> {
                                    item {
                                        Text("Không thể tải khung giờ.", color = FptRose, modifier = Modifier.padding(horizontal = 20.dp))
                                    }
                                }
                                is Resource.Success -> {
                                    val slots = slotsRes.data ?: emptyList()
                                    
                                    // Pair slots in rows of 2 for clean 2-column grid
                                    val chunkedSlots = slots.chunked(2)
                                    items(chunkedSlots) { rowSlots ->
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(horizontal = 20.dp, vertical = 4.dp),
                                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                                        ) {
                                            rowSlots.forEach { slot ->
                                                val isBooked = slot.id == 3 // Mock booked state for slot 3
                                                val isSelected = selectedSlotId == slot.id

                                                val cardBg = when {
                                                    isBooked -> Color(0xFFF1F5F9)
                                                    isSelected -> FptBlueLight
                                                    else -> AppCardSurface
                                                }
                                                val borderColor = when {
                                                    isBooked -> AppBorderSubtle
                                                    isSelected -> FptBlue
                                                    else -> AppBorder
                                                }

                                                Card(
                                                    shape = RoundedCornerShape(14.dp),
                                                    colors = CardDefaults.cardColors(containerColor = cardBg),
                                                    border = BorderStroke(1.dp, borderColor),
                                                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                                                    modifier = Modifier
                                                        .weight(1f)
                                                        .clickable(enabled = !isBooked) {
                                                            viewModel.selectSlot(slot.id)
                                                        }
                                                ) {
                                                    Column(
                                                        modifier = Modifier.padding(12.dp)
                                                    ) {
                                                        Row(
                                                            verticalAlignment = Alignment.CenterVertically,
                                                            horizontalArrangement = Arrangement.SpaceBetween,
                                                            modifier = Modifier.fillMaxWidth()
                                                        ) {
                                                            Text(
                                                                text = "${slot.startTime} - ${slot.endTime}",
                                                                fontWeight = FontWeight.ExtraBold,
                                                                fontSize = 13.sp,
                                                                color = if (isBooked) AppTextMuted else if (isSelected) FptBlueDark else AppTextPrimary
                                                            )

                                                            if (isSelected) {
                                                                Icon(
                                                                    imageVector = Icons.Default.CheckCircle,
                                                                    contentDescription = null,
                                                                    tint = FptBlue,
                                                                    modifier = Modifier.size(16.dp)
                                                                )
                                                            } else if (isBooked) {
                                                                Icon(
                                                                    imageVector = Icons.Default.Lock,
                                                                    contentDescription = null,
                                                                    tint = AppTextMuted,
                                                                    modifier = Modifier.size(14.dp)
                                                                )
                                                            }
                                                        }

                                                        Spacer(modifier = Modifier.height(4.dp))

                                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                                            Box(
                                                                modifier = Modifier
                                                                    .size(6.dp)
                                                                    .clip(CircleShape)
                                                                    .background(
                                                                        if (isBooked) AppTextMuted else if (isSelected) FptBlue else FptEmerald
                                                                    )
                                                            )
                                                            Spacer(modifier = Modifier.width(6.dp))
                                                            Text(
                                                                text = if (isBooked) "Đã có người đặt" else if (isSelected) "Đang chọn" else "Còn trống",
                                                                fontSize = 11.sp,
                                                                fontWeight = FontWeight.Medium,
                                                                color = if (isBooked) AppTextMuted else if (isSelected) FptBlue else FptEmerald
                                                            )
                                                        }
                                                    }
                                                }
                                            }

                                            // If odd number in row, fill empty space
                                            if (rowSlots.size == 1) {
                                                Spacer(modifier = Modifier.weight(1f))
                                            }
                                        }
                                    }
                                }
                            }

                            // Policy reminder card
                            item {
                                Spacer(modifier = Modifier.height(16.dp))
                                FCard(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 20.dp)
                                ) {
                                    Column(modifier = Modifier.padding(16.dp)) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(
                                                imageVector = Icons.Default.Info,
                                                contentDescription = null,
                                                tint = FptBlue,
                                                modifier = Modifier.size(18.dp)
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(
                                                text = "Quy định đặt phòng FPT Software (UC05 & UC12)",
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 13.sp,
                                                color = AppTextPrimary
                                            )
                                        }
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text(
                                            text = "• Quét mã QR check-in tại cửa phòng trong vòng 15 phút đầu.\n• Nếu vắng mặt quá 15 phút, hệ thống tự động hủy phòng và ghi nhận 1 lần No-Show.\n• Nhân viên vi phạm No-Show 3 lần sẽ bị khóa chức năng đặt chỗ.",
                                            fontSize = 11.sp,
                                            color = AppTextSecondary,
                                            lineHeight = 16.sp
                                        )
                                    }
                                }
                            }
                        } else if (selectedTab == 1) {
                            // Tab 1: Amenities & Floor Blueprint
                            item {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 20.dp)
                                ) {
                                    Text(
                                        text = "Trang thiết bị & Tiện nghi",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp,
                                        color = AppTextPrimary
                                    )
                                    Spacer(modifier = Modifier.height(10.dp))

                                    FlowRow(
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        verticalArrangement = Arrangement.spacedBy(8.dp),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        AmenityChip(name = "${resource.capacity} Chỗ ngồi", icon = Icons.Default.People, highlight = true)
                                        AmenityChip(name = "Màn hình 4K", icon = Icons.Default.Tv)
                                        AmenityChip(name = "Họp trực tuyến", icon = Icons.Default.Videocam)
                                        AmenityChip(name = "Wifi 1Gbps FPT", icon = Icons.Default.Wifi)
                                        AmenityChip(name = "Trà & Cà phê", icon = Icons.Default.LocalCafe)
                                        resource.amenities.forEach { am ->
                                            AmenityChip(name = am)
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(24.dp))

                                    Text(
                                        text = "Sơ đồ mặt bằng vị trí",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp,
                                        color = AppTextPrimary
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = "${resource.building} • ${resource.floor} • Khu vực làm việc & phòng họp",
                                        fontSize = 12.sp,
                                        color = AppTextSecondary
                                    )
                                    Spacer(modifier = Modifier.height(12.dp))

                                    // Architectural Floor Plan Visual Card
                                    FCard(
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Column(modifier = Modifier.padding(16.dp)) {
                                            Canvas(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .height(180.dp)
                                                    .clip(RoundedCornerShape(12.dp))
                                                    .background(Color(0xFF0F172A))
                                            ) {
                                                val w = size.width
                                                val h = size.height

                                                // Background grid lines (CAD style)
                                                val gridStep = 30f
                                                var gx = 0f
                                                while (gx < w) {
                                                    drawLine(Color(0xFF1E293B), Offset(gx, 0f), Offset(gx, h), strokeWidth = 1f)
                                                    gx += gridStep
                                                }
                                                var gy = 0f
                                                while (gy < h) {
                                                    drawLine(Color(0xFF1E293B), Offset(0f, gy), Offset(w, gy), strokeWidth = 1f)
                                                    gy += gridStep
                                                }

                                                // Corridor
                                                drawLine(
                                                    color = Color(0xFF334155),
                                                    start = Offset(0f, h * 0.5f),
                                                    end = Offset(w, h * 0.5f),
                                                    strokeWidth = 3f,
                                                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f))
                                                )

                                                // Highlighted current room box (P.201)
                                                val roomW = w * 0.42f
                                                val roomH = h * 0.38f
                                                drawRoundRect(
                                                    color = FptBlue.copy(alpha = 0.25f),
                                                    topLeft = Offset(16f, 16f),
                                                    size = Size(roomW, roomH),
                                                    cornerRadius = CornerRadius(10f, 10f)
                                                )
                                                drawRoundRect(
                                                    color = FptBlue,
                                                    topLeft = Offset(16f, 16f),
                                                    size = Size(roomW, roomH),
                                                    cornerRadius = CornerRadius(10f, 10f),
                                                    style = Stroke(width = 2.5f)
                                                )

                                                // Hot desks zone (lower area)
                                                val deskY = h * 0.72f
                                                for (i in 0..5) {
                                                    val deskX = w * 0.1f + i * (w * 0.15f)
                                                    val isBooked = i % 2 == 1
                                                    drawCircle(
                                                        color = if (isBooked) FptRose else FptEmerald,
                                                        center = Offset(deskX, deskY),
                                                        radius = 11f
                                                    )
                                                }
                                            }

                                            Spacer(modifier = Modifier.height(12.dp))

                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceAround,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                    Box(modifier = Modifier.size(10.dp).background(FptBlue, CircleShape))
                                                    Spacer(modifier = Modifier.width(6.dp))
                                                    Text("Vị trí đang chọn (${resource.name})", fontSize = 11.sp, color = AppTextPrimary, fontWeight = FontWeight.SemiBold)
                                                }
                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                    Box(modifier = Modifier.size(10.dp).background(FptEmerald, CircleShape))
                                                    Spacer(modifier = Modifier.width(6.dp))
                                                    Text("Chỗ trống", fontSize = 11.sp, color = AppTextSecondary)
                                                }
                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                    Box(modifier = Modifier.size(10.dp).background(FptRose, CircleShape))
                                                    Spacer(modifier = Modifier.width(6.dp))
                                                    Text("Đã kín", fontSize = 11.sp, color = AppTextSecondary)
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        } else {
                            // Tab 2: Reviews
                            item {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 20.dp)
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text(
                                            text = "4.9",
                                            fontWeight = FontWeight.Black,
                                            fontSize = 32.sp,
                                            color = AppTextPrimary
                                        )
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Column {
                                            Row {
                                                repeat(5) {
                                                    Icon(imageVector = Icons.Default.Star, contentDescription = null, tint = FptAmber, modifier = Modifier.size(16.dp))
                                                }
                                            }
                                            Text("Dựa trên 32 lượt đánh giá nội bộ", fontSize = 11.sp, color = AppTextSecondary)
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(16.dp))

                                    EmployeeReviewRow(
                                        name = "Nguyễn Văn Tuấn",
                                        department = "FSU1.BU9",
                                        date = "Hôm qua",
                                        rating = 5,
                                        comment = "Phòng họp rất chuyên nghiệp, màn hình 4K sắc nét và hệ thống camera hội nghị tự động bắt cận mặt người nói rất xịn!"
                                    )
                                    Spacer(modifier = Modifier.height(10.dp))
                                    EmployeeReviewRow(
                                        name = "Trần Thị Mai",
                                        department = "FSU2.DX",
                                        date = "3 ngày trước",
                                        rating = 5,
                                        comment = "Hệ thống quét mã QR check-in tại cửa rất mượt mà. Đặt xong là vào họp ngay không cần chờ lễ tân."
                                    )
                                    Spacer(modifier = Modifier.height(10.dp))
                                    EmployeeReviewRow(
                                        name = "Lê Hoàng Nam",
                                        department = "QA Team",
                                        date = "Tuần trước",
                                        rating = 4,
                                        comment = "Không gian cách âm tốt, wifi 1Gbps kết nối máy chủ khách hàng không bị giật lag."
                                    )
                                }
                            }
                        }
                    }

                    // Sticky Bottom Bar with Selected Slot Summary & CTA
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .fillMaxWidth()
                            .background(AppCardSurface)
                            .drawBehind {
                                drawLine(
                                    color = AppBorder,
                                    start = Offset(0f, 0f),
                                    end = Offset(size.width, 0f),
                                    strokeWidth = 1.dp.toPx()
                                )
                            }
                            .padding(horizontal = 20.dp, vertical = 14.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = if (selectedSlot != null) "${selectedSlot.startTime} - ${selectedSlot.endTime}" else "Chưa chọn khung giờ",
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 15.sp,
                                    color = if (selectedSlot != null) FptBlue else AppTextMuted
                                )
                                Text(
                                    text = if (selectedSlot != null) "Ngày: $selectedDate (90p)" else "Vui lòng chọn 1 ca họp",
                                    fontSize = 11.sp,
                                    color = AppTextSecondary
                                )
                            }

                            FButton(
                                text = if (isLocked) "Tài khoản bị khóa" else "Đặt Chỗ Ngay",
                                onClick = {
                                    if (isLocked) {
                                        lockedDialogMessage = "Tài khoản của bạn (${currentUser?.name}) đã vi phạm quy chế No-Show quá 3 lần. Vui lòng liên hệ Admin FPT Software để mở khóa."
                                        showLockedDialog = true
                                    } else {
                                        showSummarySheet = true
                                    }
                                },
                                enabled = selectedSlotId != null && !isLocked,
                                isLoading = bookingResult is Resource.Loading,
                                leadingIcon = Icons.Default.CheckCircle,
                                modifier = Modifier.weight(1.2f)
                            )
                        }
                    }
                }
            }
        }

        // World-Class Confirmation BottomSheet (Ticket Boarding Pass Style)
        if (showSummarySheet && resourceState is Resource.Success) {
            val resourceData = (resourceState as Resource.Success).data!!
            val slotsList = (timeSlotsState as? Resource.Success)?.data ?: emptyList()
            val selectedSlot = slotsList.find { it.id == selectedSlotId }
            val timeLabel = selectedSlot?.let { "${it.startTime} - ${it.endTime}" } ?: "08:00 - 09:30"

            ModalBottomSheet(
                onDismissRequest = { showSummarySheet = false },
                sheetState = sheetState,
                containerColor = AppCardSurface,
                dragHandle = {
                    Box(
                        modifier = Modifier
                            .padding(vertical = 10.dp)
                            .size(width = 38.dp, height = 4.dp)
                            .clip(CircleShape)
                            .background(AppBorder)
                    )
                }
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 20.dp, end = 20.dp, bottom = 36.dp)
                ) {
                    // Sheet Header
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Xác Nhận Đặt Chỗ",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Black,
                                    color = AppTextPrimary,
                                    fontSize = 20.sp
                                )
                            )
                            Text(
                                text = "Vui lòng kiểm tra lại thông tin trước khi hoàn tất",
                                style = MaterialTheme.typography.bodySmall.copy(color = AppTextSecondary)
                            )
                        }
                        IconButton(
                            onClick = {
                                coroutineScope.launch { sheetState.hide() }.invokeOnCompletion {
                                    showSummarySheet = false
                                }
                            }
                        ) {
                            Icon(imageVector = Icons.Default.Close, contentDescription = "Đóng", tint = AppTextSecondary)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Premium Ticket Boarding Pass Card
                    Card(
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = FptBlueLight),
                        border = BorderStroke(1.dp, FptBlue.copy(alpha = 0.25f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            // Ticket top row
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(36.dp)
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(FptBlue),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = if (resourceData.type == ResourceType.ROOM) Icons.Default.MeetingRoom else Icons.Default.LaptopMac,
                                            contentDescription = null,
                                            tint = Color.White,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(
                                            text = resourceData.name,
                                            fontWeight = FontWeight.ExtraBold,
                                            fontSize = 16.sp,
                                            color = FptBlueDark
                                        )
                                        Text(
                                            text = "${resourceData.building} • ${resourceData.floor}",
                                            fontSize = 12.sp,
                                            color = AppTextSecondary
                                        )
                                    }
                                }

                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(Color.White)
                                        .border(0.5.dp, FptBlue.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                                        .padding(horizontal = 8.dp, vertical = 3.dp)
                                ) {
                                    Text(
                                        text = if (resourceData.type == ResourceType.ROOM) "PHÒNG HỌP" else "CHỖ NGỒI",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 10.sp,
                                        color = FptBlue
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))
                            HorizontalDivider(color = FptBlue.copy(alpha = 0.15f), thickness = 1.dp)
                            Spacer(modifier = Modifier.height(14.dp))

                            // Ticket Key Details Grid
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text("Thời gian sử dụng", fontSize = 11.sp, color = AppTextSecondary)
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = "$selectedDate • $timeLabel",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = AppTextPrimary
                                    )
                                }
                                Column(horizontalAlignment = Alignment.End) {
                                    Text("Người đặt chỗ", fontSize = 11.sp, color = AppTextSecondary)
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = currentUser?.name ?: "Đức Nguyễn",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = AppTextPrimary
                                    )
                                    Text(
                                        text = "${currentUser?.employeeId ?: "EMP999"} • ${currentUser?.fsu ?: "FSU1"}",
                                        fontSize = 10.sp,
                                        color = AppTextSecondary
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Policy reminder inside ticket
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color.White.copy(alpha = 0.8f))
                                    .padding(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Info,
                                    contentDescription = null,
                                    tint = FptAmber,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Quét mã QR tại cửa trong 15p đầu để check-in tránh No-Show.",
                                    fontSize = 11.sp,
                                    color = Color(0xFF92400E),
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // Meeting Purpose Input (UC04)
                    Text(
                        text = "Mục đích sử dụng (Purpose) *",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = AppTextPrimary
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    // 4 Quick suggestion chips
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        listOf(
                            "💼 Họp sprint / team",
                            "🤝 Tiếp khách hàng",
                            "🎯 Phỏng vấn ứng viên",
                            "💻 Thảo luận kỹ thuật"
                        ).forEach { suggestion ->
                            val cleanText = suggestion.substringAfter(" ")
                            val isSel = meetingPurpose == cleanText || meetingPurpose == suggestion
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (isSel) FptBlueLight else AppBackground)
                                    .border(1.dp, if (isSel) FptBlue else AppBorder, RoundedCornerShape(10.dp))
                                    .clickable { meetingPurpose = cleanText }
                                    .padding(horizontal = 12.dp, vertical = 7.dp)
                            ) {
                                Text(
                                    text = suggestion,
                                    fontSize = 11.sp,
                                    color = if (isSel) FptBlue else AppTextPrimary,
                                    fontWeight = if (isSel) FontWeight.Bold else FontWeight.Medium
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    FTextField(
                        value = meetingPurpose,
                        onValueChange = { meetingPurpose = it },
                        label = "Ghi chú mục đích họp",
                        placeholder = "VD: Họp demo sprint 14 dự án F-Space...",
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Action buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        FButton(
                            text = "Hủy bỏ",
                            onClick = {
                                coroutineScope.launch { sheetState.hide() }.invokeOnCompletion {
                                    showSummarySheet = false
                                }
                            },
                            isSecondary = true,
                            modifier = Modifier.weight(1f)
                        )
                        FButton(
                            text = "Xác Nhận & Giữ Chỗ",
                            onClick = {
                                coroutineScope.launch {
                                    sheetState.hide()
                                    showSummarySheet = false
                                    viewModel.submitBooking(resourceId, meetingPurpose.ifBlank { "Họp công việc nội bộ" })
                                }
                            },
                            isLoading = bookingResult is Resource.Loading,
                            leadingIcon = Icons.Default.CheckCircle,
                            modifier = Modifier.weight(1.8f)
                        )
                    }
                }
            }
        }

        // World-Class Success Ticket Dialog
        if (showSuccessDialog) {
            val resourceData = (resourceState as? Resource.Success)?.data
            val slotsList = (timeSlotsState as? Resource.Success)?.data ?: emptyList()
            val selectedSlot = slotsList.find { it.id == selectedSlotId }
            val timeLabel = selectedSlot?.let { "${it.startTime} - ${it.endTime}" } ?: "08:00 - 09:30"

            Dialog(onDismissRequest = {
                showSuccessDialog = false
                viewModel.resetBookingResult()
                onBookingSuccess()
            }) {
                Card(
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = AppCardSurface),
                    border = BorderStroke(width = 1.dp, color = AppBorder),
                    elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Green Glow Celebration Circle
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape)
                                .background(FptEmeraldLight),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = FptEmerald,
                                modifier = Modifier.size(36.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "ĐẶT CHỖ THÀNH CÔNG!",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Black,
                                fontSize = 18.sp,
                                letterSpacing = 0.5.sp
                            ),
                            color = FptEmerald
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = "Mã xác nhận: #FSB-${(10000..99999).random()}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = AppTextSecondary
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Mini Ticket Box
                        Card(
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = AppBackground),
                            border = BorderStroke(1.dp, AppBorder),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text(
                                    text = resourceData?.name ?: "Phòng Họp F-Space",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = AppTextPrimary
                                )
                                Text(
                                    text = "${resourceData?.building} • ${resourceData?.floor}",
                                    fontSize = 11.sp,
                                    color = AppTextSecondary
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(imageVector = Icons.Default.AccessTime, contentDescription = null, tint = FptBlue, modifier = Modifier.size(13.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "$selectedDate • $timeLabel",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = FptBlue
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Text(
                            text = "Mã QR check-in tại cửa đã sẵn sàng trong mục Lịch Trình. Vui lòng check-in đúng giờ theo quy định FPT.",
                            style = MaterialTheme.typography.bodySmall.copy(
                                textAlign = TextAlign.Center,
                                color = AppTextSecondary,
                                lineHeight = 16.sp
                            )
                        )

                        Spacer(modifier = Modifier.height(22.dp))

                        FButton(
                            text = "Xem Trong Lịch Trình",
                            onClick = {
                                showSuccessDialog = false
                                viewModel.resetBookingResult()
                                onBookingSuccess()
                            },
                            leadingIcon = Icons.Default.CalendarToday,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }

        // Conflict Dialog
        if (showConflictDialog) {
            Dialog(onDismissRequest = {
                showConflictDialog = false
                viewModel.resetBookingResult()
            }) {
                Card(
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = AppCardSurface),
                    border = BorderStroke(width = 1.dp, color = FptRose.copy(alpha = 0.3f)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape)
                                .background(FptRoseLight),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = "Warning",
                                tint = FptRose,
                                modifier = Modifier.size(30.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Trùng Lịch Đặt Chỗ!",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Black,
                                fontSize = 17.sp
                            ),
                            color = FptRose
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = conflictMessage.ifBlank { "Khung giờ này vừa có đồng nghiệp khác đặt chỗ thành công. Vui lòng chọn khung giờ khác." },
                            style = MaterialTheme.typography.bodySmall.copy(
                                textAlign = TextAlign.Center,
                                color = AppTextSecondary,
                                lineHeight = 16.sp
                            )
                        )
                        Spacer(modifier = Modifier.height(22.dp))
                        FButton(
                            text = "Chọn Khung Giờ Khác",
                            onClick = {
                                showConflictDialog = false
                                viewModel.resetBookingResult()
                            },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }

        // Account Locked Warning Dialog (UC12)
        if (showLockedDialog) {
            Dialog(onDismissRequest = {
                showLockedDialog = false
                viewModel.resetBookingResult()
            }) {
                Card(
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = AppCardSurface),
                    border = BorderStroke(width = 1.dp, color = FptRose.copy(alpha = 0.4f)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(60.dp)
                                .clip(CircleShape)
                                .background(FptRoseLight),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = "Tài khoản bị khóa",
                                tint = FptRose,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "TÀI KHOẢN BỊ KHÓA ĐẶT CHỖ",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Black,
                                fontSize = 16.sp
                            ),
                            color = FptRose
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = if (lockedDialogMessage.isNotBlank()) lockedDialogMessage else "Tài khoản của bạn đã vi phạm quy chế No-Show vượt quá giới hạn (3 lần). Hệ thống đã tự động khóa quyền đặt phòng theo quy định FPT Software. Vui lòng liên hệ Admin để mở khóa.",
                            style = MaterialTheme.typography.bodySmall.copy(
                                textAlign = TextAlign.Center,
                                color = AppTextSecondary,
                                lineHeight = 17.sp
                            )
                        )
                        Spacer(modifier = Modifier.height(22.dp))
                        FButton(
                            text = "Đã hiểu",
                            onClick = {
                                showLockedDialog = false
                                viewModel.resetBookingResult()
                            },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun EmployeeReviewRow(
    name: String,
    department: String,
    date: String,
    rating: Int,
    comment: String
) {
    FCard(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(FptBlueLight),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = name.take(1).uppercase(),
                            color = FptBlue,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(text = name, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium, color = AppTextPrimary)
                        Text(text = "$department • $date", style = MaterialTheme.typography.labelSmall, color = AppTextSecondary)
                    }
                }
                
                Row {
                    repeat(rating) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = FptAmber,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = comment,
                style = MaterialTheme.typography.bodySmall,
                color = AppTextSecondary,
                lineHeight = 17.sp
            )
        }
    }
}

