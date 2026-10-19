package com.example.fspacebooking.ui.screens.booking

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fspacebooking.domain.model.ResourceType
import com.example.fspacebooking.domain.model.ResourceStatus
import com.example.fspacebooking.ui.components.FButton
import com.example.fspacebooking.ui.components.FCard
import com.example.fspacebooking.ui.components.FTextField
import com.example.fspacebooking.ui.components.StatusBadge
import com.example.fspacebooking.ui.viewmodels.BookingViewModel
import com.example.fspacebooking.utils.Resource
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Composable
fun WorkspaceMapsIllustration(
    type: ResourceType,
    modifier: Modifier = Modifier
) {
    val primaryColor = Color(0xFF2563EB)
    val secondaryColor = Color(0xFF3B82F6)
    val outlineColor = Color(0xFFE2E8F0)
    
    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(260.dp)
            .clip(RoundedCornerShape(bottomStart = 28.dp, bottomEnd = 28.dp))
            .background(primaryColor.copy(alpha = 0.05f))
    ) {
        val w = size.width
        val h = size.height

        if (type == ResourceType.ROOM) {
            drawRoundRect(
                color = Color.White,
                topLeft = Offset(w * 0.15f, h * 0.15f),
                size = Size(w * 0.7f, h * 0.65f),
                cornerRadius = CornerRadius(16f, 16f)
            )
            drawRoundRect(
                color = outlineColor,
                topLeft = Offset(w * 0.15f, h * 0.15f),
                size = Size(w * 0.7f, h * 0.65f),
                cornerRadius = CornerRadius(16f, 16f),
                style = Stroke(width = 2f)
            )
            drawLine(
                color = outlineColor.copy(alpha = 0.6f),
                start = Offset(w * 0.5f, h * 0.15f),
                end = Offset(w * 0.5f, h * 0.8f),
                strokeWidth = 2f
            )
            drawRect(
                color = primaryColor.copy(alpha = 0.05f),
                topLeft = Offset(w * 0.22f, h * 0.22f),
                size = Size(w * 0.15f, h * 0.24f)
            )
            drawRect(
                color = primaryColor,
                topLeft = Offset(w * 0.22f, h * 0.22f),
                size = Size(w * 0.15f, h * 0.24f),
                style = Stroke(width = 2f)
            )
            drawRoundRect(
                color = primaryColor.copy(alpha = 0.1f),
                topLeft = Offset(w * 0.32f, h * 0.5f),
                size = Size(w * 0.36f, h * 0.22f),
                cornerRadius = CornerRadius(24f, 24f)
            )
            drawRoundRect(
                color = primaryColor,
                topLeft = Offset(w * 0.32f, h * 0.5f),
                size = Size(w * 0.36f, h * 0.22f),
                cornerRadius = CornerRadius(24f, 24f),
                style = Stroke(width = 2f)
            )
            for (i in 0..2) {
                val chairX = w * 0.36f + i * (w * 0.14f)
                drawCircle(color = secondaryColor.copy(alpha = 0.5f), center = Offset(chairX, h * 0.42f), radius = 14f)
                drawCircle(color = secondaryColor.copy(alpha = 0.5f), center = Offset(chairX, h * 0.8f), radius = 14f)
            }
        } else {
            drawRoundRect(
                color = Color.White,
                topLeft = Offset(w * 0.1f, h * 0.12f),
                size = Size(w * 0.8f, h * 0.52f),
                cornerRadius = CornerRadius(14f, 14f)
            )
            drawRoundRect(
                color = outlineColor,
                topLeft = Offset(w * 0.1f, h * 0.12f),
                size = Size(w * 0.8f, h * 0.52f),
                cornerRadius = CornerRadius(14f, 14f),
                style = Stroke(width = 2f)
            )
            drawLine(
                color = primaryColor,
                start = Offset(w * 0.16f, h * 0.64f),
                end = Offset(w * 0.84f, h * 0.64f),
                strokeWidth = 6f
            )
            drawRoundRect(
                color = Color.DarkGray,
                topLeft = Offset(w * 0.28f, h * 0.24f),
                size = Size(w * 0.2f, h * 0.28f),
                cornerRadius = CornerRadius(6f, 6f)
            )
            drawRoundRect(
                color = Color.DarkGray,
                topLeft = Offset(w * 0.52f, h * 0.24f),
                size = Size(w * 0.2f, h * 0.28f),
                cornerRadius = CornerRadius(6f, 6f)
            )
            drawLine(color = Color.Gray, start = Offset(w * 0.22f, h * 0.64f), end = Offset(w * 0.22f, h * 0.9f), strokeWidth = 4f)
            drawLine(color = Color.Gray, start = Offset(w * 0.78f, h * 0.64f), end = Offset(w * 0.78f, h * 0.9f), strokeWidth = 4f)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
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

    var showConflictDialog by remember { mutableStateOf(false) }
    var conflictMessage by remember { mutableStateOf("") }
    var showSuccessDialog by remember { mutableStateOf(false) }
    var showLockedDialog by remember { mutableStateOf(false) }
    var lockedDialogMessage by remember { mutableStateOf("") }
    
    var showSummarySheet by remember { mutableStateOf(false) }
    var meetingPurpose by remember { mutableStateOf("") }

    val primaryColor = Color(0xFF2563EB)
    val backgroundLight = Color(0xFFF8FAFC)
    val cardSurface = Color(0xFFFFFFFF)

    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var selectedPillTab by remember { mutableStateOf(0) }
    val pillTabs = listOf("Chi tiết", "Sơ đồ vị trí", "Đánh giá")

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
    val sdfDayLabel = SimpleDateFormat("EEE", Locale.getDefault())
    val sdfDayNum = SimpleDateFormat("dd", Locale.getDefault())

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        modifier = modifier
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundLight)
                .padding(innerPadding)
        ) {
            when (val res = resourceState) {
                is Resource.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = primaryColor, strokeWidth = 2.dp, modifier = Modifier.size(32.dp))
                    }
                }
                is Resource.Error -> {
                    Text(
                        text = res.message ?: "Không thể tải thông tin không gian.",
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                is Resource.Success -> {
                    val resource = res.data!!

                    LazyColumn(
                        contentPadding = PaddingValues(bottom = 96.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        // Header Vector maps banner
                        item {
                            Box(modifier = Modifier.fillMaxWidth()) {
                                WorkspaceMapsIllustration(type = resource.type)
                                
                                // Back icon circular badge
                                IconButton(
                                    onClick = onNavigateBack,
                                    modifier = Modifier
                                        .padding(16.dp)
                                        .size(40.dp)
                                        .clip(CircleShape)
                                        .background(Color.White.copy(alpha = 0.9f))
                                        .align(Alignment.TopStart)
                                ) {
                                    Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Quay lại", tint = Color.Black)
                                }
                            }
                        }

                        // Account Status / Violation Banner if applicable
                        if (currentUser?.userStatus == "ĐÃ KHÓA" || currentUser?.userStatus == "CẢNH BÁO") {
                            val isLocked = currentUser?.userStatus == "ĐÃ KHÓA"
                            item {
                                Card(
                                    shape = RoundedCornerShape(16.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = if (isLocked) Color(0xFFFEF2F2) else Color(0xFFFFFBEB)
                                    ),
                                    border = BorderStroke(1.dp, if (isLocked) Color(0xFFFCA5A5) else Color(0xFFFDE68A)),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 24.dp, vertical = 8.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(14.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Warning,
                                            contentDescription = null,
                                            tint = if (isLocked) Color(0xFFEF4444) else Color(0xFFD97706),
                                            modifier = Modifier.size(24.dp)
                                        )
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Column {
                                            Text(
                                                text = if (isLocked) "Tài khoản đang bị KHÓA đặt chỗ" else "Cảnh báo vi phạm No-show (${currentUser?.violations}/3)",
                                                fontWeight = FontWeight.Bold,
                                                style = MaterialTheme.typography.bodySmall,
                                                color = if (isLocked) Color(0xFF991B1B) else Color(0xFF92400E)
                                            )
                                            Text(
                                                text = if (isLocked) "Bạn không thể đặt phòng do vi phạm No-show quá số lần quy định. Vui lòng liên hệ Admin FPT Software." else "Nếu vắng mặt không check-in quá 3 lần, hệ thống sẽ tự động khóa quyền đặt tài nguyên.",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = if (isLocked) Color(0xFFB91C1C) else Color(0xFFB45309),
                                                fontSize = 11.sp
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        // Core Space Information details card
                        item {
                            Card(
                                shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
                                colors = CardDefaults.cardColors(containerColor = cardSurface),
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 24.dp)
                                    .border(1.dp, Color(0xFFF1F5F9), RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
                            ) {
                                Column(modifier = Modifier.padding(24.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.Top
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = resource.name,
                                                style = MaterialTheme.typography.titleLarge.copy(
                                                    fontWeight = FontWeight.ExtraBold,
                                                    fontSize = 22.sp
                                                ),
                                                color = Color(0xFF1E293B)
                                            )
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                modifier = Modifier.padding(top = 4.dp)
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.LocationOn,
                                                    contentDescription = null,
                                                    tint = primaryColor,
                                                    modifier = Modifier.size(14.dp)
                                                )
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text(
                                                    text = "${resource.building} • ${resource.floor}",
                                                    style = MaterialTheme.typography.bodySmall,
                                                    color = Color(0xFF64748B)
                                                )
                                            }
                                        }

                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(20.dp))
                                                .background(primaryColor.copy(alpha = 0.1f))
                                                .padding(horizontal = 12.dp, vertical = 6.dp)
                                        ) {
                                            Text(
                                                text = if (resource.status == ResourceStatus.AVAILABLE) "Trống" else "Đã đặt",
                                                color = primaryColor,
                                                fontWeight = FontWeight.Bold,
                                                style = MaterialTheme.typography.labelSmall
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(16.dp))

                                    // Quick statistics icons row
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(imageVector = Icons.Default.People, contentDescription = null, tint = Color(0xFF64748B), modifier = Modifier.size(16.dp))
                                            Text(text = " ${resource.capacity} Chỗ ngồi", style = MaterialTheme.typography.labelSmall, color = Color(0xFF475569), fontWeight = FontWeight.Bold)
                                        }
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(imageVector = Icons.Default.Star, contentDescription = null, tint = Color(0xFFF59E0B), modifier = Modifier.size(16.dp))
                                            Text(text = " Đánh giá 4.9", style = MaterialTheme.typography.labelSmall, color = Color(0xFF475569), fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }
                        }

                        // Tab options (Chi tiết, Sơ đồ, Đánh giá) inside body
                        item {
                            Spacer(modifier = Modifier.height(16.dp))
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 24.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color(0xFFF1F5F9))
                                    .padding(4.dp),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                pillTabs.forEachIndexed { idx, title ->
                                    val isSel = selectedPillTab == idx
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(if (isSel) Color.White else Color.Transparent)
                                            .clickable { selectedPillTab = idx }
                                            .padding(vertical = 10.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = title,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp,
                                            color = if (isSel) primaryColor else Color(0xFF64748B)
                                        )
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                        }

                        // Switch screens according to current selected Tab options
                        if (selectedPillTab == 0) {
                            // ABOUT SPECIFICATIONS & SCHEDULE TIMELINE Tab
                            item {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 24.dp)
                                ) {
                                    Text(
                                        text = "Thông tin không gian",
                                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                        color = Color(0xFF1E293B)
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = resource.description,
                                        style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 20.sp),
                                        color = Color(0xFF475569)
                                    )
                                    Spacer(modifier = Modifier.height(24.dp))

                                    // Calendar Picker list
                                    Text(
                                        text = "Ngày đặt phòng/chỗ",
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                        color = Color(0xFF64748B),
                                        modifier = Modifier.padding(bottom = 10.dp)
                                    )

                                    LazyRow(
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        contentPadding = PaddingValues(bottom = 16.dp)
                                    ) {
                                        items(calendarDays) { date ->
                                            val dKey = sdfDateKey.format(date)
                                            val isSel = dKey == selectedDate
                                            val dayName = sdfDayLabel.format(date)
                                            val dayNum = sdfDayNum.format(date)

                                            Column(
                                                horizontalAlignment = Alignment.CenterHorizontally,
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(12.dp))
                                                    .background(if (isSel) primaryColor else Color.White)
                                                    .border(1.dp, if (isSel) Color.Transparent else Color(0xFFE2E8F0), RoundedCornerShape(12.dp))
                                                    .clickable { viewModel.selectDate(dKey) }
                                                    .padding(horizontal = 14.dp, vertical = 10.dp)
                                            ) {
                                                Text(
                                                    text = dayName,
                                                    color = if (isSel) Color.White else Color(0xFF94A3B8),
                                                    fontSize = 10.sp,
                                                    fontWeight = FontWeight.Bold
                                                )
                                                Text(
                                                    text = dayNum,
                                                    color = if (isSel) Color.White else Color(0xFF1E293B),
                                                    fontSize = 16.sp,
                                                    fontWeight = FontWeight.ExtraBold
                                                )
                                            }
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(10.dp))

                                    // Timeline Slots Picker Header
                                    Text(
                                        text = "Khung giờ trong ngày",
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                        color = Color(0xFF64748B),
                                        modifier = Modifier.padding(bottom = 10.dp)
                                    )
                                }
                            }

                            // Dynamic slot renders
                            when (val slotsRes = timeSlotsState) {
                                is Resource.Loading -> {
                                    item {
                                        Box(modifier = Modifier.fillMaxWidth().height(60.dp), contentAlignment = Alignment.Center) {
                                            CircularProgressIndicator(color = primaryColor, strokeWidth = 2.dp)
                                        }
                                    }
                                }
                                is Resource.Error -> {
                                    item {
                                        Text("Không thể tải khung giờ hoạt động.", color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(horizontal = 24.dp))
                                    }
                                }
                                is Resource.Success -> {
                                    val slots = slotsRes.data ?: emptyList()
                                    items(slots) { slot ->
                                        // Mock status: slots modulo determines if it's booked
                                        val isBooked = slot.id == 3 // simulate slot 3 booked
                                        val isSelected = selectedSlotId == slot.id

                                        val slotBorderColor = when {
                                            isBooked -> Color(0xFFF1F5F9)
                                            isSelected -> primaryColor
                                            else -> Color(0xFFE2E8F0)
                                        }
                                        val slotBg = when {
                                            isBooked -> Color(0xFFF8FAFC)
                                            isSelected -> primaryColor.copy(alpha = 0.05f)
                                            else -> Color.White
                                        }

                                        Card(
                                            shape = RoundedCornerShape(12.dp),
                                            colors = CardDefaults.cardColors(containerColor = slotBg),
                                            border = BorderStroke(1.dp, slotBorderColor),
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(horizontal = 24.dp, vertical = 4.dp)
                                                .clickable(enabled = !isBooked) {
                                                    viewModel.selectSlot(slot.id)
                                                }
                                        ) {
                                            Row(
                                                modifier = Modifier.padding(14.dp),
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.SpaceBetween
                                            ) {
                                                Column {
                                                    Text(
                                                        text = "${slot.startTime} - ${slot.endTime}",
                                                        fontWeight = FontWeight.ExtraBold,
                                                        style = MaterialTheme.typography.bodyMedium,
                                                        color = if (isBooked) Color(0xFF94A3B8) else Color(0xFF1E293B)
                                                    )
                                                    Text(
                                                        text = if (isBooked) "Đã có người đặt" else if (isSelected) "Khung giờ đã chọn" else "Có sẵn",
                                                        style = MaterialTheme.typography.labelSmall,
                                                        color = if (isBooked) Color(0xFF94A3B8) else if (isSelected) primaryColor else Color(0xFF64748B),
                                                        fontSize = 10.sp
                                                    )
                                                }
                                                // Icon status indicator
                                                Icon(
                                                    imageVector = if (isBooked) Icons.Default.Close else Icons.Default.CheckCircle,
                                                    contentDescription = null,
                                                    tint = if (isBooked) Color(0xFFCBD5E1) else if (isSelected) primaryColor else Color(0xFF10B981),
                                                    modifier = Modifier.size(18.dp)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        } else if (selectedPillTab == 1) {
                            // Floor Map blueprint layout inside tab
                            item {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 24.dp)
                                ) {
                                    Text(
                                        text = "Sơ đồ vị trí văn phòng",
                                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                        color = Color(0xFF1E293B)
                                    )
                                    Spacer(modifier = Modifier.height(12.dp))

                                    val outlineColor = Color(0xFFCBD5E1)
                                    Canvas(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(200.dp)
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(Color(0xFFF8FAFC))
                                            .border(1.dp, outlineColor, RoundedCornerShape(12.dp))
                                    ) {
                                        val w = size.width
                                        val h = size.height

                                        drawRoundRect(
                                            color = Color(0xFF10B981).copy(alpha = 0.08f),
                                            topLeft = Offset(15f, 15f),
                                            size = Size(w * 0.35f, h * 0.35f),
                                            cornerRadius = CornerRadius(8f, 8f)
                                        )
                                        drawRoundRect(
                                            color = Color(0xFF10B981).copy(alpha = 0.4f),
                                            topLeft = Offset(15f, 15f),
                                            size = Size(w * 0.35f, h * 0.35f),
                                            cornerRadius = CornerRadius(8f, 8f),
                                            style = Stroke(width = 2f)
                                        )

                                        drawLine(
                                            color = outlineColor.copy(alpha = 0.5f),
                                            start = Offset(15f, h * 0.45f),
                                            end = Offset(w - 15f, h * 0.45f),
                                            strokeWidth = 2f
                                        )

                                        val deskY = h * 0.68f
                                        val deskRadius = 12f
                                        for (i in 0..5) {
                                            val deskX = w * 0.12f + i * (w * 0.15f)
                                            val isBooked = i % 3 == 1
                                            val deskColor = if (isBooked) Color(0xFFEF4444) else Color(0xFF10B981)
                                            
                                            drawCircle(
                                                color = deskColor,
                                                center = Offset(deskX, deskY),
                                                radius = deskRadius * 0.8f
                                            )
                                        }
                                    }
                                    
                                    Spacer(modifier = Modifier.height(12.dp))
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.Center
                                    ) {
                                        Box(modifier = Modifier.size(8.dp).background(Color(0xFF10B981), CircleShape))
                                        Text(" Bàn trống ", fontSize = 10.sp, modifier = Modifier.padding(end = 16.dp))
                                        Box(modifier = Modifier.size(8.dp).background(Color(0xFFEF4444), CircleShape))
                                        Text(" Bàn đã đặt", fontSize = 10.sp)
                                    }
                                }
                            }
                        } else {
                            // Reviews tab
                            item {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 24.dp)
                                ) {
                                    Text(
                                        text = "Phản hồi từ đồng nghiệp",
                                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                        color = Color(0xFF1E293B)
                                    )
                                    Spacer(modifier = Modifier.height(12.dp))
                                    
                                    EmployeeReviewRow(name = "Nguyễn Văn A", date = "Hôm qua", rating = 5, comment = "Phòng họp rất đẹp, kết nối wifi cực kỳ nhanh và màn hình trình chiếu sắc nét!")
                                    Spacer(modifier = Modifier.height(10.dp))
                                    EmployeeReviewRow(name = "Trần Thị B", date = "3 ngày trước", rating = 4, comment = "Khu bàn làm việc yên tĩnh, ghế ngồi công thái học rất êm khi làm việc muộn.")
                                }
                            }
                        }
                    }

                    // Floating Bottom Capsule Book button bar (Vibrant style overlay)
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .fillMaxWidth()
                            .padding(20.dp)
                    ) {
                        val isUserLocked = currentUser?.userStatus == "ĐÃ KHÓA"
                        val buttonColor = when {
                            selectedSlotId == null -> Color(0xFF94A3B8)
                            isUserLocked -> Color(0xFFEF4444)
                            else -> primaryColor
                        }

                        Card(
                            shape = RoundedCornerShape(30.dp),
                            colors = CardDefaults.cardColors(containerColor = buttonColor),
                            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp)
                                .clickable(enabled = selectedSlotId != null) {
                                    if (isUserLocked) {
                                        lockedDialogMessage = "Tài khoản của bạn (${currentUser?.name}) đang bị KHÓA do có ${currentUser?.violations}/3 lần vi phạm No-show. Bạn không thể thực hiện đặt tài nguyên. Vui lòng liên hệ Admin FPT Software."
                                        showLockedDialog = true
                                    } else {
                                        showSummarySheet = true
                                    }
                                }
                        ) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                if (bookingResult is Resource.Loading) {
                                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
                                } else {
                                    val buttonLabel = when {
                                        selectedSlotId == null -> "Vui Lòng Chọn Khung Giờ"
                                        isUserLocked -> "Tài Khoản Đang Bị Khóa"
                                        else -> "Đặt Chỗ Ngay"
                                    }
                                    Text(
                                        text = buttonLabel,
                                        color = Color.White,
                                        fontWeight = FontWeight.ExtraBold,
                                        style = MaterialTheme.typography.titleSmall
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // M3 standard bottom sheet for confirmation
        if (showSummarySheet && resourceState is Resource.Success) {
            val resourceData = (resourceState as Resource.Success).data!!
            val selectedSlot = (timeSlotsState as? Resource.Success)?.data?.find { it.id == selectedSlotId }
            val timeLabel = selectedSlot?.let { "${it.startTime} - ${it.endTime}" } ?: ""

            ModalBottomSheet(
                onDismissRequest = { showSummarySheet = false },
                sheetState = sheetState,
                containerColor = Color.White
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 24.dp, end = 24.dp, bottom = 44.dp)
                ) {
                    Text(
                        text = "Tóm tắt đặt chỗ (Summary)",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold, color = Color(0xFF1E293B))
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    Text(
                        text = "Tài nguyên: ${resourceData.name}",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold, color = Color(0xFF1E293B))
                    )
                    Text(
                        text = "Thời gian: $selectedDate • $timeLabel",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF64748B)
                    )
                    Text(
                        text = "Vị trí: ${resourceData.building} • ${resourceData.floor}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF64748B)
                    )
                    
                    Spacer(modifier = Modifier.height(20.dp))
                    
                    FTextField(
                        value = meetingPurpose,
                        onValueChange = { meetingPurpose = it },
                        label = "Mục đích cuộc họp (Purpose)",
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
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
                            text = "Hoàn tất đặt",
                            onClick = {
                                coroutineScope.launch {
                                    sheetState.hide()
                                    showSummarySheet = false
                                    viewModel.submitBooking(resourceId, meetingPurpose.ifBlank { "Họp công việc nội bộ" })
                                }
                            },
                            modifier = Modifier.weight(1.5f)
                        )
                    }
                }
            }
        }

        // Success dialog custom render
        if (showSuccessDialog) {
            androidx.compose.ui.window.Dialog(onDismissRequest = {
                showSuccessDialog = false
                viewModel.resetBookingResult()
                onBookingSuccess()
            }) {
                Card(
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = cardSurface),
                    border = BorderStroke(width = 1.dp, color = Color(0xFFE2E8F0)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = Color(0xFF10B981),
                            modifier = Modifier.size(54.dp)
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                        Text(
                            text = "Đặt Chỗ Thành Công!",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold),
                            color = Color(0xFF10B981)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Yêu cầu đặt phòng của bạn đã được xác nhận thành công. Xem chi tiết tại mục Lịch Trình.",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF475569)
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                        FButton(
                            text = "Đồng ý",
                            onClick = {
                                showSuccessDialog = false
                                viewModel.resetBookingResult()
                                onBookingSuccess()
                            },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }

        // Collision dialog custom render
        if (showConflictDialog) {
            androidx.compose.ui.window.Dialog(onDismissRequest = {
                showConflictDialog = false
                viewModel.resetBookingResult()
            }) {
                Card(
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = cardSurface),
                    border = BorderStroke(width = 1.dp, color = Color(0xFFE2E8F0)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = "Warning",
                            tint = Color(0xFFEF4444),
                            modifier = Modifier.size(44.dp)
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                        Text(
                            text = "Trùng lịch đặt chỗ",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold),
                            color = Color(0xFFEF4444)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = conflictMessage,
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF475569)
                        )
                        Spacer(modifier = Modifier.height(24.dp))
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

        // Account locked warning dialog
        if (showLockedDialog) {
            androidx.compose.ui.window.Dialog(onDismissRequest = {
                showLockedDialog = false
                viewModel.resetBookingResult()
            }) {
                Card(
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = cardSurface),
                    border = BorderStroke(width = 1.dp, color = Color(0xFFEF4444).copy(alpha = 0.5f)),
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
                                .size(64.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFFEF2F2)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = "Tài khoản bị khóa",
                                tint = Color(0xFFEF4444),
                                modifier = Modifier.size(36.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "TÀI KHOẢN BỊ KHÓA ĐẶT CHỖ",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold),
                            color = Color(0xFFEF4444)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = if (lockedDialogMessage.isNotBlank()) lockedDialogMessage else "Tài khoản của bạn đã vi phạm quy chế No-show vượt quá giới hạn (4 lần). Hệ thống đã tự động khóa quyền đặt phòng. Vui lòng liên hệ Ban Quản Trị FPT Software để mở khóa tài khoản.",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF475569),
                            lineHeight = 18.sp
                        )
                        Spacer(modifier = Modifier.height(24.dp))
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
    date: String,
    rating: Int,
    comment: String
) {
    FCard(modifier = Modifier.fillMaxWidth()) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(text = name, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium, color = Color(0xFF1E293B))
                    Text(text = date, style = MaterialTheme.typography.labelSmall, color = Color(0xFF94A3B8))
                }
                
                Row {
                    repeat(rating) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = Color(0xFFF59E0B),
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = comment,
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFF475569),
                lineHeight = 16.sp
            )
        }
    }
}
