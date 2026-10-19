package com.example.fspacebooking.ui.screens.booking

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.LaptopMac
import androidx.compose.material.icons.filled.MeetingRoom
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Warning
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.text.style.TextAlign
import com.example.fspacebooking.ui.components.FTextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fspacebooking.domain.model.Booking
import com.example.fspacebooking.domain.model.BookingStatus
import com.example.fspacebooking.domain.model.ResourceType
import com.example.fspacebooking.ui.components.FButton
import com.example.fspacebooking.ui.components.FCard
import com.example.fspacebooking.ui.components.StatusBadge
import com.example.fspacebooking.ui.viewmodels.MyBookingsViewModel
import com.example.fspacebooking.utils.Resource
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyBookingsScreen(
    viewModel: MyBookingsViewModel,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val bookingsState by viewModel.bookingsState.collectAsState()

    val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val todayStr = sdf.format(Date())

    var selectedCalendarDate by remember { mutableStateOf(todayStr) }
    var bookingToCancel by remember { mutableStateOf<Booking?>(null) }
    var bookingToCheckIn by remember { mutableStateOf<Booking?>(null) }
    var isCheckingIn by remember { mutableStateOf(false) }
    var checkInSuccess by remember { mutableStateOf(false) }
    var manualQrInput by remember { mutableStateOf("") }
    var scanErrorMessage by remember { mutableStateOf<String?>(null) }

    var checkedInBookings by remember { mutableStateOf(setOf<String>()) }

    // Generates 7 calendar days scroll strip
    val calendarDays = remember {
        val list = mutableListOf<Date>()
        val cal = Calendar.getInstance()
        for (i in 0..6) {
            list.add(cal.time)
            cal.add(Calendar.DAY_OF_YEAR, 1)
        }
        list
    }
    val sdfDayName = SimpleDateFormat("EEE", Locale.getDefault())
    val sdfDayNum = SimpleDateFormat("dd", Locale.getDefault())

    val primaryColor = Color(0xFF2563EB)
    val backgroundLight = Color(0xFFF8FAFC)
    val cardSurface = Color(0xFFFFFFFF)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Lịch trình của tôi", fontWeight = FontWeight.ExtraBold, fontSize = 18.sp, color = Color(0xFF1E293B)) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Quay lại", tint = Color(0xFF1E293B))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = backgroundLight)
            )
        },
        modifier = modifier
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundLight)
                .padding(innerPadding)
        ) {
            when (val res = bookingsState) {
                is Resource.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = primaryColor, strokeWidth = 2.dp, modifier = Modifier.size(32.dp))
                    }
                }
                is Resource.Error -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("Lỗi: ${res.message}", color = MaterialTheme.colorScheme.error)
                    }
                }
                is Resource.Success -> {
                    val allBookings = res.data ?: emptyList()

                    // Section 1: Horizontal Scrollable Calendar View strip
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(calendarDays) { date ->
                            val dateStr = sdf.format(date)
                            val isSelected = dateStr == selectedCalendarDate
                            val dayName = sdfDayName.format(date)
                            val dayNum = sdfDayNum.format(date)

                            // Check if this date has any confirmed active bookings
                            val hasBooking = allBookings.any { it.date == dateStr && it.status == BookingStatus.CONFIRMED }

                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(if (isSelected) primaryColor else cardSurface)
                                    .border(
                                        width = 1.dp,
                                        color = if (isSelected) Color.Transparent else Color(0xFFE2E8F0),
                                        shape = RoundedCornerShape(14.dp)
                                    )
                                    .clickable { selectedCalendarDate = dateStr }
                                    .padding(horizontal = 16.dp, vertical = 12.dp)
                            ) {
                                Text(
                                    text = dayName.uppercase(),
                                    color = if (isSelected) Color.White else Color(0xFF94A3B8),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = dayNum,
                                    color = if (isSelected) Color.White else Color(0xFF1E293B),
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.ExtraBold
                                )
                                
                                if (hasBooking) {
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Box(
                                        modifier = Modifier
                                            .size(6.dp)
                                            .clip(CircleShape)
                                            .background(if (isSelected) Color.White else Color(0xFFEF4444))
                                    )
                                } else {
                                    Spacer(modifier = Modifier.height(10.dp))
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Section 2: Selected Date Agenda list
                    val selectedBookings = allBookings.filter { it.date == selectedCalendarDate }

                    if (selectedBookings.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    imageVector = Icons.Default.CalendarToday,
                                    contentDescription = null,
                                    tint = Color(0xFF94A3B8).copy(alpha = 0.5f),
                                    modifier = Modifier.size(56.dp)
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = "Không có lịch đặt chỗ hôm nay",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = Color(0xFF94A3B8)
                                )
                                Text(
                                    text = "Nhấp qua tab đặt phòng để đăng ký vị trí mới.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color(0xFFCBD5E1),
                                    modifier = Modifier.padding(top = 4.dp)
                                )
                            }
                        }
                    } else {
                        LazyColumn(
                            contentPadding = PaddingValues(bottom = 32.dp),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(selectedBookings) { booking ->
                                val isPast = booking.date < todayStr
                                val isCancelled = booking.status == BookingStatus.CANCELLED
                                val isCheckedIn = checkedInBookings.contains(booking.id)
                                val isToday = booking.date == todayStr

                                BookingAgendaCard(
                                    booking = booking,
                                    isPastOrCancelled = isPast || isCancelled,
                                    isToday = isToday,
                                    isCheckedIn = isCheckedIn,
                                    onCancelClick = { bookingToCancel = booking },
                                    onCheckInClick = { bookingToCheckIn = booking }
                                )
                            }
                        }
                    }
                }
            }
        }

        // Cancel Reservation Confirmation dialog
        bookingToCancel?.let { booking ->
            androidx.compose.ui.window.Dialog(onDismissRequest = { bookingToCancel = null }) {
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
                            text = "Hủy Đặt Chỗ?",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = Color(0xFF1E293B))
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Bạn có chắc chắn muốn giải phóng phòng ${booking.resource.name} vào ngày ${booking.date} (${booking.timeSlot.startTime})?",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF64748B)
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            FButton(
                                text = "Giữ lại",
                                onClick = { bookingToCancel = null },
                                isSecondary = true,
                                modifier = Modifier.weight(1f)
                            )
                            FButton(
                                text = "Hủy lịch",
                                onClick = {
                                    viewModel.cancelBooking(booking.id)
                                    bookingToCancel = null
                                },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }
        }

        // QR Code Check-in Camera Scanner dialog
        bookingToCheckIn?.let { booking ->
            val expectedQr = "QR_${booking.resource.id.uppercase()}"
            androidx.compose.ui.window.Dialog(onDismissRequest = { 
                bookingToCheckIn = null 
                isCheckingIn = false
                checkInSuccess = false
                scanErrorMessage = null
                manualQrInput = ""
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
                        Text(
                            text = "QUÉT MÃ QR CHECK-IN",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Black, color = primaryColor)
                        )
                        Text(
                            text = "Xác nhận hiện diện tại ${booking.resource.name}",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF64748B),
                            modifier = Modifier.padding(top = 4.dp, bottom = 14.dp)
                        )

                        if (checkInSuccess) {
                            Box(
                                modifier = Modifier
                                    .size(72.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFDCFCE7)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = Color(0xFF10B981),
                                    modifier = Modifier.size(48.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(14.dp))
                            Text(
                                text = "Check-in thành công!",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold),
                                color = Color(0xFF10B981)
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Vị trí: ${booking.resource.name} • ${booking.resource.building} (${booking.resource.floor})\nSự kiện đã được ghi nhận vào Audit Log hệ thống.",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFF475569),
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(24.dp))
                            FButton(
                                text = "Hoàn tất",
                                onClick = {
                                    checkedInBookings = checkedInBookings + booking.id
                                    bookingToCheckIn = null
                                    checkInSuccess = false
                                    manualQrInput = ""
                                    scanErrorMessage = null
                                },
                                modifier = Modifier.fillMaxWidth()
                            )
                        } else {
                            // Scanner viewfinder box
                            Box(
                                modifier = Modifier
                                    .size(190.dp)
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(Color(0xFF0F172A))
                                    .border(2.dp, primaryColor, RoundedCornerShape(16.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                // Animated scanning laser line
                                val infiniteTransition = rememberInfiniteTransition(label = "laser")
                                val laserOffset by infiniteTransition.animateFloat(
                                    initialValue = 0.1f,
                                    targetValue = 0.9f,
                                    animationSpec = infiniteRepeatable(
                                        animation = tween(1500, easing = LinearEasing),
                                        repeatMode = RepeatMode.Reverse
                                    ),
                                    label = "laser_y"
                                )

                                Canvas(modifier = Modifier.fillMaxSize()) {
                                    val y = size.height * laserOffset
                                    drawLine(
                                        color = Color(0xFF38BDF8),
                                        start = Offset(20f, y),
                                        end = Offset(size.width - 20f, y),
                                        strokeWidth = 3f
                                    )
                                }

                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier.padding(16.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.QrCodeScanner,
                                        contentDescription = null,
                                        tint = Color.White.copy(alpha = 0.8f),
                                        modifier = Modifier.size(48.dp)
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "Hướng camera vào mã QR",
                                        color = Color.White.copy(alpha = 0.7f),
                                        fontSize = 11.sp
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Demo Quick Scan helper chip
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(Color(0xFFEFF6FF))
                                    .border(1.dp, Color(0xFFBFDBFE), RoundedCornerShape(10.dp))
                                    .clickable {
                                        manualQrInput = expectedQr
                                        scanErrorMessage = null
                                        isCheckingIn = true
                                        viewModel.checkInBooking(booking.id, expectedQr) { res ->
                                            isCheckingIn = false
                                            when (res) {
                                                is Resource.Success -> {
                                                    checkInSuccess = true
                                                    checkedInBookings = checkedInBookings + booking.id
                                                }
                                                is Resource.Error -> {
                                                    scanErrorMessage = res.message ?: "Mã QR không hợp lệ!"
                                                }
                                                else -> {}
                                            }
                                        }
                                    }
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(imageVector = Icons.Default.QrCodeScanner, contentDescription = null, tint = primaryColor, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "⚡ Quét nhanh mã: $expectedQr",
                                        color = primaryColor,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // Manual QR Text Input Field
                            FTextField(
                                value = manualQrInput,
                                onValueChange = {
                                    manualQrInput = it
                                    scanErrorMessage = null
                                },
                                label = "Nhập mã QR hoặc quét thủ công",
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth()
                            )

                            // Error alert if invalid QR
                            if (scanErrorMessage != null) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(Color(0xFFFEF2F2))
                                        .padding(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(imageVector = Icons.Default.Warning, contentDescription = null, tint = Color(0xFFEF4444), modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = scanErrorMessage ?: "",
                                        color = Color(0xFFDC2626),
                                        fontSize = 11.sp,
                                        lineHeight = 14.sp
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                FButton(
                                    text = "Đóng",
                                    onClick = { 
                                        bookingToCheckIn = null
                                        manualQrInput = ""
                                        scanErrorMessage = null
                                    },
                                    isSecondary = true,
                                    modifier = Modifier.weight(1f)
                                )
                                FButton(
                                    text = if (isCheckingIn) "Đang xác thực..." else "Xác nhận Check-in",
                                    onClick = {
                                        val codeToSubmit = manualQrInput.ifBlank { expectedQr }
                                        isCheckingIn = true
                                        scanErrorMessage = null
                                        viewModel.checkInBooking(booking.id, codeToSubmit) { res ->
                                            isCheckingIn = false
                                            when (res) {
                                                is Resource.Success -> {
                                                    checkInSuccess = true
                                                    checkedInBookings = checkedInBookings + booking.id
                                                }
                                                is Resource.Error -> {
                                                    scanErrorMessage = res.message ?: "Mã QR không khớp!"
                                                }
                                                else -> {}
                                            }
                                        }
                                    },
                                    isLoading = isCheckingIn,
                                    modifier = Modifier.weight(1.4f)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun BookingAgendaCard(
    booking: Booking,
    isPastOrCancelled: Boolean,
    isToday: Boolean,
    isCheckedIn: Boolean,
    onCancelClick: () -> Unit,
    onCheckInClick: () -> Unit
) {
    val alphaModifier = if (isPastOrCancelled) Modifier.alpha(0.6f) else Modifier
    val primaryColor = Color(0xFF2563EB)
    val cardSurface = Color(0xFFFFFFFF)

    val isAlreadyCheckedIn = booking.status == BookingStatus.CHECKED_IN || isCheckedIn

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = cardSurface),
        border = BorderStroke(width = 1.dp, color = Color(0xFFE2E8F0)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 6.dp)
            .then(alphaModifier)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(primaryColor.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (booking.resource.type == ResourceType.ROOM) Icons.Default.MeetingRoom else Icons.Default.LaptopMac,
                        contentDescription = null,
                        tint = primaryColor,
                        modifier = Modifier.size(22.dp)
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = booking.resource.name,
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color(0xFF1E293B)
                        )
                        
                        // Accurate Status Badges (UC04, UC05, UC12)
                        val (badgeLabel, badgeBg, badgeText) = when (booking.status) {
                            BookingStatus.CHECKED_IN -> Triple("Đã Check-in", Color(0xFFDCFCE7), Color(0xFF16A34A))
                            BookingStatus.NO_SHOW -> Triple("No-show", Color(0xFFFEF3C7), Color(0xFFD97706))
                            BookingStatus.CANCELLED -> Triple("Đã hủy", Color(0xFFFEF2F2), Color(0xFFEF4444))
                            BookingStatus.CONFIRMED -> {
                                if (isAlreadyCheckedIn) Triple("Đã Check-in", Color(0xFFDCFCE7), Color(0xFF16A34A))
                                else Triple("Đã xác nhận", Color(0xFFEFF6FF), Color(0xFF2563EB))
                            }
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(badgeBg)
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = badgeLabel,
                                color = badgeText,
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                fontSize = 10.sp
                            )
                        }
                    }
                    Text(
                        text = "${booking.resource.building} • ${booking.resource.floor} • Khung giờ: ${booking.timeSlot.startTime} - ${booking.timeSlot.endTime}",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFF64748B),
                        modifier = Modifier.padding(top = 2.dp)
                    )
                    if (booking.purpose.isNotBlank()) {
                        Text(
                            text = "Mục đích: ${booking.purpose}",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color(0xFF475569),
                            modifier = Modifier.padding(top = 2.dp),
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            if (!isPastOrCancelled) {
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (isToday) {
                        if (isAlreadyCheckedIn) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF10B981), modifier = Modifier.size(18.dp))
                                Text(" Đã Check-in", fontSize = 11.sp, color = Color(0xFF10B981), fontWeight = FontWeight.Bold)
                            }
                        } else {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(primaryColor)
                                    .clickable { onCheckInClick() }
                                    .padding(horizontal = 14.dp, vertical = 8.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(imageVector = Icons.Default.QrCodeScanner, contentDescription = null, tint = Color.White, modifier = Modifier.size(12.dp))
                                    Text(" Check-in QR", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                    }

                    // Cancel - only allowed if not checked-in yet
                    if (!isAlreadyCheckedIn) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(14.dp))
                                .border(1.dp, Color(0xFFEF4444), RoundedCornerShape(14.dp))
                                .clickable { onCancelClick() }
                                .padding(horizontal = 14.dp, vertical = 8.dp)
                        ) {
                            Text("Hủy lịch", color = Color(0xFFEF4444), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}
