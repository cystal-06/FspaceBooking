package com.example.fspacebooking.ui.screens.booking

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.ui.text.style.TextOverflow
import com.example.fspacebooking.ui.components.AmenityChip
import com.example.fspacebooking.ui.components.ModernSegmentedControl
import com.example.fspacebooking.ui.components.StatusBadge
import com.example.fspacebooking.ui.theme.AppBackground
import com.example.fspacebooking.ui.theme.AppBorder
import com.example.fspacebooking.ui.theme.AppBorderSubtle
import com.example.fspacebooking.ui.theme.AppCardSurface
import com.example.fspacebooking.ui.theme.AppTextMuted
import com.example.fspacebooking.ui.theme.AppTextPrimary
import com.example.fspacebooking.ui.theme.AppTextSecondary
import com.example.fspacebooking.ui.theme.FptBlue
import com.example.fspacebooking.ui.theme.FptEmerald
import com.example.fspacebooking.ui.theme.FptRose
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fspacebooking.domain.model.TimeSlot
import com.example.fspacebooking.domain.model.ResourceType
import com.example.fspacebooking.domain.model.ResourceStatus
import com.example.fspacebooking.domain.model.WorkspaceResource
import com.example.fspacebooking.ui.components.FButton
import com.example.fspacebooking.ui.components.FTextField
import com.example.fspacebooking.ui.viewmodels.SearchViewModel
import com.example.fspacebooking.utils.Resource
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun ResourceSearchScreen(
    viewModel: SearchViewModel,
    initialType: ResourceType?,
    onNavigateBack: (() -> Unit)? = null,
    onResourceSelect: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedType by viewModel.selectedType.collectAsState()
    val selectedBuilding by viewModel.selectedBuilding.collectAsState()
    val selectedFloor by viewModel.selectedFloor.collectAsState()
    val selectedCapacity by viewModel.selectedCapacity.collectAsState()
    val resourcesState by viewModel.resourcesState.collectAsState()

    var showFilters by remember { mutableStateOf(false) }
    var showMapDialog by remember { mutableStateOf(false) }
    
    // Search simulation loading
    var isSearchLoading by remember { mutableStateOf(false) }

    // Onboarding filters
    var filterDate by remember { mutableStateOf("Hôm nay") }
    var filterTime by remember { mutableStateOf("09:00 - 11:00") }
    var needProjector by remember { mutableStateOf(false) }
    var needWhiteboard by remember { mutableStateOf(false) }

    val buildings = listOf("Tất cả tòa nhà", "F-Town 1", "F-Town 2", "F-Town 3")
    val capacities = listOf(1, 4, 8, 12, 25)

    val currentTabIndex = when (selectedType) {
        ResourceType.ROOM -> 0
        ResourceType.DESK -> 1
        else -> 0
    }

    LaunchedEffect(initialType) {
        if (initialType != null) {
            viewModel.onTypeSelect(initialType)
        } else {
            viewModel.onTypeSelect(ResourceType.ROOM)
        }
    }

    val primaryColor = Color(0xFF2563EB)
    val backgroundLight = Color(0xFFF8FAFC)
    val cardSurface = Color(0xFFFFFFFF)

    // SnackBar and BottomSheet states
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showBottomSheet by remember { mutableStateOf(false) }
    var selectedResourceForBooking by remember { mutableStateOf<WorkspaceResource?>(null) }
    var selectedSheetSlotId by remember { mutableStateOf<Int?>(null) }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Đặt Phòng & Chỗ Ngồi", fontWeight = FontWeight.ExtraBold, fontSize = 18.sp, color = Color(0xFF1E293B)) },
                navigationIcon = {
                    if (onNavigateBack != null) {
                        IconButton(onClick = onNavigateBack) {
                            Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Quay lại", tint = Color(0xFF1E293B))
                        }
                    }
                },
                actions = {
                    IconButton(onClick = { showFilters = !showFilters }) {
                        Icon(
                            imageVector = Icons.Default.FilterList,
                            contentDescription = "Bộ lọc nâng cao",
                            tint = if (showFilters) primaryColor else Color(0xFF1E293B)
                        )
                    }
                    IconButton(onClick = { showMapDialog = true }) {
                        Icon(imageVector = Icons.Default.Map, contentDescription = "Sơ đồ mặt bằng", tint = Color(0xFF1E293B))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = backgroundLight
                )
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
            // Modern Segmented Control for Space Type
            ModernSegmentedControl(
                items = listOf("Phòng Họp", "Bàn Làm Việc"),
                selectedIndex = currentTabIndex,
                onSelect = { index ->
                    viewModel.onTypeSelect(if (index == 0) ResourceType.ROOM else ResourceType.DESK)
                },
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp)
            )

            // Search Bar: Modern rounded outline with clear icon
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.onSearchQueryChange(it) },
                placeholder = { Text("Tìm kiếm phòng họp, bàn làm việc, tầng...", color = AppTextMuted, fontSize = 14.sp) },
                leadingIcon = {
                    Icon(imageVector = Icons.Default.Search, contentDescription = null, tint = AppTextSecondary, modifier = Modifier.size(20.dp))
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { viewModel.onSearchQueryChange("") }) {
                            Icon(imageVector = Icons.Default.Close, contentDescription = "Xóa tìm kiếm", tint = AppTextSecondary, modifier = Modifier.size(18.dp))
                        }
                    }
                },
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = FptBlue,
                    unfocusedBorderColor = AppBorder,
                    focusedContainerColor = AppCardSurface,
                    unfocusedContainerColor = AppCardSurface,
                    focusedTextColor = AppTextPrimary,
                    unfocusedTextColor = AppTextPrimary
                ),
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 6.dp)
            )

            // Animated Filter blocks
            AnimatedVisibility(
                visible = showFilters,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White)
                        .border(width = 1.dp, color = Color(0xFFF1F5F9))
                        .padding(horizontal = 24.dp, vertical = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Date & Time selectors row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        FTextField(
                            value = filterDate,
                            onValueChange = { filterDate = it },
                            label = "Chọn ngày",
                            leadingIcon = Icons.Default.CalendarToday,
                            modifier = Modifier.weight(1f)
                        )
                        FTextField(
                            value = filterTime,
                            onValueChange = { filterTime = it },
                            label = "Khung giờ",
                            leadingIcon = Icons.Default.Schedule,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    // Campus details filters (Building & Capacity)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Building list selection chip scroll
                        Column(modifier = Modifier.weight(1f)) {
                            Text("TÒA NHÀ", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = Color(0xFF64748B))
                            LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.padding(top = 4.dp)) {
                                items(buildings) { b ->
                                    val isSel = (selectedBuilding == b) || (b == "Tất cả tòa nhà" && selectedBuilding == null)
                                    FilterChip(
                                        selected = isSel,
                                        onClick = { viewModel.onBuildingSelect(if (b == "Tất cả tòa nhà") null else b) },
                                        label = { Text(b, fontSize = 10.sp) },
                                        colors = FilterChipDefaults.filterChipColors(
                                            selectedContainerColor = primaryColor.copy(alpha = 0.15f),
                                            selectedLabelColor = primaryColor
                                        )
                                    )
                                }
                            }
                        }

                        // Min Seats Capacity filter list
                        Column(modifier = Modifier.weight(1f)) {
                            Text("SỨC CHỨA TỐI THIỂU", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = Color(0xFF64748B))
                            LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.padding(top = 4.dp)) {
                                item {
                                    FilterChip(
                                        selected = selectedCapacity == null,
                                        onClick = { viewModel.onCapacitySelect(null) },
                                        label = { Text("Bất kỳ", fontSize = 10.sp) },
                                        colors = FilterChipDefaults.filterChipColors(
                                            selectedContainerColor = primaryColor.copy(alpha = 0.15f),
                                            selectedLabelColor = primaryColor
                                        )
                                    )
                                }
                                items(capacities) { cap ->
                                    FilterChip(
                                        selected = selectedCapacity == cap,
                                        onClick = { viewModel.onCapacitySelect(cap) },
                                        label = { Text("$cap+", fontSize = 10.sp) },
                                        colors = FilterChipDefaults.filterChipColors(
                                            selectedContainerColor = primaryColor.copy(alpha = 0.15f),
                                            selectedLabelColor = primaryColor
                                        )
                                    )
                                }
                            }
                        }
                    }

                    // Amenities selecting chips
                    Column {
                        Text("TIỆN ÍCH ĐI KÈM", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = Color(0xFF64748B))
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.padding(top = 4.dp)
                        ) {
                            FilterChip(
                                selected = needProjector,
                                onClick = { needProjector = !needProjector },
                                label = { Text("Máy chiếu") },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = primaryColor.copy(alpha = 0.15f),
                                    selectedLabelColor = primaryColor
                                )
                            )
                            FilterChip(
                                selected = needWhiteboard,
                                onClick = { needWhiteboard = !needWhiteboard },
                                label = { Text("Bảng viết") },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = primaryColor.copy(alpha = 0.15f),
                                    selectedLabelColor = primaryColor
                                )
                            )
                        }
                    }

                    // Search confirmation button
                    FButton(
                        text = "Áp dụng bộ lọc",
                        onClick = {
                            isSearchLoading = true
                            viewModel.onSearchQueryChange(searchQuery)
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            // Simulate loader spinning
            LaunchedEffect(isSearchLoading) {
                if (isSearchLoading) {
                    delay(500)
                    isSearchLoading = false
                    showFilters = false
                }
            }

            // Results List Container
            if (isSearchLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = primaryColor, strokeWidth = 2.dp, modifier = Modifier.size(36.dp))
                }
            } else {
                when (val state = resourcesState) {
                    is Resource.Loading -> {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(color = primaryColor, strokeWidth = 2.dp, modifier = Modifier.size(32.dp))
                        }
                    }
                    is Resource.Error -> {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text("Lỗi tải danh mục: ${state.message}", color = MaterialTheme.colorScheme.error)
                        }
                    }
                    is Resource.Success -> {
                        var list = state.data ?: emptyList()
                        
                        // Local chip criteria filters
                        if (needProjector) {
                            list = list.filter { item -> item.amenities.any { it.contains("Projector", ignoreCase = true) || it.contains("chiếu", ignoreCase = true) } }
                        }
                        if (needWhiteboard) {
                            list = list.filter { item -> item.amenities.any { it.contains("Whiteboard", ignoreCase = true) || it.contains("trắng", ignoreCase = true) } }
                        }

                        if (list.isEmpty()) {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Text("Không tìm thấy phòng họp phù hợp.", color = Color(0xFF64748B))
                            }
                        } else {
                            LazyColumn(
                                contentPadding = PaddingValues(bottom = 24.dp),
                                modifier = Modifier.fillMaxSize()
                            ) {
                                items(list) { resource ->
                                    ResourceItemCard(
                                        resource = resource,
                                        onToggleFavorite = { viewModel.toggleFavorite(resource.id, !resource.isFavorite) },
                                        onBookClick = { onResourceSelect(resource.id) },
                                        onCardClick = { onResourceSelect(resource.id) }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Vector Floor Blueprint Dialog simulation
        if (showMapDialog) {
            androidx.compose.ui.window.Dialog(onDismissRequest = { showMapDialog = false }) {
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
                            text = "Sơ đồ mặt bằng F-Space",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold, color = Color(0xFF1E293B))
                        )
                        Spacer(modifier = Modifier.height(16.dp))

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

                            // Draw floor plan rooms partitions
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

                            drawRoundRect(
                                color = Color(0xFFEF4444).copy(alpha = 0.08f),
                                topLeft = Offset(w * 0.62f, 15f),
                                size = Size(w * 0.35f, h * 0.35f),
                                cornerRadius = CornerRadius(8f, 8f)
                            )
                            drawRoundRect(
                                color = Color(0xFFEF4444).copy(alpha = 0.4f),
                                topLeft = Offset(w * 0.62f, 15f),
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

                        Spacer(modifier = Modifier.height(16.dp))

                        FButton(
                            text = "Đóng sơ đồ",
                            onClick = { showMapDialog = false },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }

        // M3 standard bottom sheet for confirmation
        if (showBottomSheet && selectedResourceForBooking != null) {
            val resource = selectedResourceForBooking!!
            ModalBottomSheet(
                onDismissRequest = { showBottomSheet = false },
                sheetState = sheetState,
                containerColor = Color.White
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 24.dp, end = 24.dp, bottom = 44.dp)
                ) {
                    Text(
                        text = "Xác nhận đặt chỗ",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF1E293B)
                        )
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Không gian: ${resource.name} (${resource.building} • ${resource.floor})",
                        style = MaterialTheme.typography.bodyMedium.copy(color = Color(0xFF475569))
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Chọn khung giờ đặt chỗ",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = Color(0xFF64748B))
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    // Time slots display selector
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(TimeSlot.DEFAULT_SLOTS) { slot ->
                            val isSel = selectedSheetSlotId == slot.id
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(if (isSel) primaryColor else Color(0xFFF1F5F9))
                                    .clickable { selectedSheetSlotId = slot.id }
                                    .padding(horizontal = 14.dp, vertical = 10.dp)
                            ) {
                                Text(
                                    text = "${slot.startTime} - ${slot.endTime}",
                                    color = if (isSel) Color.White else Color(0xFF475569),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        FButton(
                            text = "Hủy bỏ",
                            onClick = {
                                coroutineScope.launch { sheetState.hide() }.invokeOnCompletion {
                                    showBottomSheet = false
                                }
                            },
                            isSecondary = true,
                            modifier = Modifier.weight(1f)
                        )

                        FButton(
                            text = "Tiếp tục đặt",
                            onClick = {
                                coroutineScope.launch {
                                    sheetState.hide()
                                    showBottomSheet = false
                                    onResourceSelect(resource.id)
                                }
                            },
                            modifier = Modifier.weight(1.5f)
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ResourceItemCard(
    resource: WorkspaceResource,
    onToggleFavorite: () -> Unit,
    onBookClick: () -> Unit,
    onCardClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = AppCardSurface),
        border = BorderStroke(width = 1.dp, color = AppBorder),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 6.dp)
            .clickable { onCardClick() }
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = resource.name,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            ),
                            color = AppTextPrimary
                        )
                        StatusBadge(status = resource.status)
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(top = 4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = null,
                            tint = AppTextSecondary,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = "${resource.building} • ${resource.floor}",
                            style = MaterialTheme.typography.bodySmall,
                            color = AppTextSecondary,
                            fontSize = 12.sp
                        )
                    }
                }

                IconButton(
                    onClick = onToggleFavorite,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = if (resource.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = null,
                        tint = if (resource.isFavorite) FptRose else AppTextMuted,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Text(
                text = resource.description,
                style = MaterialTheme.typography.bodySmall.copy(lineHeight = 18.sp),
                color = AppTextSecondary,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(vertical = 10.dp)
            )

            // Amenities outline chips using custom AmenityChip
            FlowRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 14.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                AmenityChip(
                    name = "${resource.capacity} Chỗ ngồi",
                    icon = Icons.Default.People,
                    highlight = true
                )

                resource.amenities.forEach { amenity ->
                    AmenityChip(name = amenity)
                }
            }

            // Primary Book button
            FButton(
                text = if (resource.status == ResourceStatus.MAINTENANCE) "Đang bảo trì" else "Đặt ngay",
                onClick = onBookClick,
                enabled = resource.status != ResourceStatus.MAINTENANCE,
                modifier = Modifier.fillMaxWidth(),
                isSecondary = resource.status == ResourceStatus.BOOKED,
                leadingIcon = Icons.Default.CalendarToday
            )
        }
    }
}
