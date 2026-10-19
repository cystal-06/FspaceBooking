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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LaptopMac
import androidx.compose.material.icons.filled.MeetingRoom
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fspacebooking.domain.model.ResourceStatus
import com.example.fspacebooking.domain.model.ResourceType
import com.example.fspacebooking.domain.model.WorkspaceResource
import com.example.fspacebooking.ui.components.FButton
import com.example.fspacebooking.ui.components.FCard
import com.example.fspacebooking.ui.components.FTextField
import com.example.fspacebooking.ui.components.StatusBadge
import com.example.fspacebooking.ui.viewmodels.AdminViewModel
import com.example.fspacebooking.utils.Resource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResourceManagementScreen(
    viewModel: AdminViewModel,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val resourcesState by viewModel.allResources.collectAsState()
    val operationResult by viewModel.operationResult

    var showAddEditDialog by remember { mutableStateOf(false) }
    var editingResource by remember { mutableStateOf<WorkspaceResource?>(null) }

    // Dialog form values
    var formName by remember { mutableStateOf("") }
    var formType by remember { mutableStateOf(ResourceType.ROOM) }
    var formCapacity by remember { mutableStateOf("") }
    var formBuilding by remember { mutableStateOf("") }
    var formFloor by remember { mutableStateOf("") }
    var formDescription by remember { mutableStateOf("") }
    var formAmenities by remember { mutableStateOf("") }

    LaunchedEffect(operationResult) {
        if (operationResult is Resource.Success) {
            showAddEditDialog = false
            viewModel.resetOperationResult()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        text = "QUẢN LÝ DANH MỤC TÀI NGUYÊN", 
                        fontWeight = FontWeight.Bold, 
                        color = AdminTextPrimary,
                        fontSize = 16.sp
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
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    editingResource = null
                    formName = ""
                    formType = ResourceType.ROOM
                    formCapacity = ""
                    formBuilding = ""
                    formFloor = ""
                    formDescription = ""
                    formAmenities = ""
                    showAddEditDialog = true
                },
                containerColor = AdminPrimary,
                contentColor = Color.Black
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Thêm không gian")
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
            when (val state = resourcesState) {
                is Resource.Loading -> {
                    CircularProgressIndicator(color = AdminPrimary, modifier = Modifier.align(Alignment.Center))
                }
                is Resource.Error -> {
                    Text(
                        text = "Lỗi tải danh mục: ${state.message}",
                        color = Color(0xFFEF4444),
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                is Resource.Success -> {
                    val list = state.data ?: emptyList()
                    if (list.isEmpty()) {
                        Text(
                            text = "Danh mục trống. Nhấn '+' để thêm không gian làm việc mới.",
                            color = AdminTextSecondary,
                            modifier = Modifier.align(Alignment.Center)
                        )
                    } else {
                        LazyColumn(
                            contentPadding = PaddingValues(start = 20.dp, end = 20.dp, bottom = 80.dp),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(list) { resource ->
                                ManagementItemCard(
                                    resource = resource,
                                    onEditClick = {
                                        editingResource = resource
                                        formName = resource.name
                                        formType = resource.type
                                        formCapacity = resource.capacity.toString()
                                        formBuilding = resource.building
                                        formFloor = resource.floor
                                        formDescription = resource.description
                                        formAmenities = resource.amenities.joinToString(", ")
                                        showAddEditDialog = true
                                    },
                                    onDeleteClick = { viewModel.deleteResource(resource.id) },
                                    onToggleMaintenance = { viewModel.toggleMaintenance(resource) }
                                )
                            }
                        }
                    }
                }
            }
        }

        // Dark Theme Add/Edit Dialog
        if (showAddEditDialog) {
            androidx.compose.ui.window.Dialog(onDismissRequest = { showAddEditDialog = false }) {
                Card(
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = AdminSurface),
                    border = BorderStroke(1.dp, AdminBorder),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp)
                ) {
                    LazyColumn(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        item {
                            Text(
                                text = if (editingResource == null) "THÊM TÀI NGUYÊN MỚI" else "CHỈNH SỬA TÀI NGUYÊN",
                                color = AdminTextPrimary,
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold),
                                modifier = Modifier.padding(bottom = 12.dp)
                            )
                        }

                        item {
                            // Resource Type Option
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Start
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    RadioButton(
                                        selected = formType == ResourceType.ROOM,
                                        onClick = { formType = ResourceType.ROOM },
                                        colors = RadioButtonDefaults.colors(selectedColor = AdminPrimary, unselectedColor = AdminBorder)
                                    )
                                    Text("Phòng họp", color = AdminTextPrimary, fontSize = 13.sp)
                                }
                                Spacer(modifier = Modifier.width(16.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    RadioButton(
                                        selected = formType == ResourceType.DESK,
                                        onClick = { formType = ResourceType.DESK },
                                        colors = RadioButtonDefaults.colors(selectedColor = AdminPrimary, unselectedColor = AdminBorder)
                                    )
                                    Text("Bàn làm việc", color = AdminTextPrimary, fontSize = 13.sp)
                                }
                            }
                        }

                        item {
                             FTextField(
                                value = formName,
                                onValueChange = { formName = it },
                                label = "Tên phòng / mã bàn làm việc (vd: Gosling, Desk-01)"
                            )
                        }

                        item {
                            FTextField(
                                value = formCapacity,
                                onValueChange = { formCapacity = it },
                                label = "Sức chứa (người, ví dụ: 15)",
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                            )
                        }

                        item {
                            FTextField(
                                value = formBuilding,
                                onValueChange = { formBuilding = it },
                                label = "Tòa nhà (ví dụ: F-Town 3)"
                            )
                        }

                        item {
                            FTextField(
                                value = formFloor,
                                onValueChange = { formFloor = it },
                                label = "Tầng (ví dụ: Tầng 4)"
                            )
                        }

                        item {
                            FTextField(
                                value = formDescription,
                                onValueChange = { formDescription = it },
                                label = "Mô tả chi tiết và tiện nghi",
                                singleLine = false
                            )
                        }

                        item {
                            FTextField(
                                value = formAmenities,
                                onValueChange = { formAmenities = it },
                                label = "Tiện ích (phân tách bằng dấu phẩy, ví dụ: Wi-Fi, Tivi)",
                                singleLine = false
                            )
                        }

                        item {
                            Spacer(modifier = Modifier.height(16.dp))
                            if (operationResult is Resource.Error) {
                                Text(
                                    text = operationResult?.message ?: "",
                                    color = Color(0xFFEF4444),
                                    style = MaterialTheme.typography.bodySmall,
                                    modifier = Modifier.padding(bottom = 8.dp)
                                )
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                FButton(
                                    text = "Hủy bỏ",
                                    onClick = { showAddEditDialog = false },
                                    isSecondary = true,
                                    modifier = Modifier.weight(1f)
                                )
                                FButton(
                                    text = "Lưu lại",
                                    onClick = {
                                        val capVal = formCapacity.toIntOrNull() ?: 1
                                        val amenList = formAmenities.split(",")
                                            .map { it.trim() }
                                            .filter { it.isNotBlank() }
                                        
                                        val editRes = editingResource
                                        if (editRes == null) {
                                            viewModel.addResource(
                                                name = formName,
                                                type = formType,
                                                capacity = capVal,
                                                building = formBuilding,
                                                floor = formFloor,
                                                description = formDescription,
                                                amenities = amenList
                                            )
                                        } else {
                                            viewModel.updateResource(
                                                editRes.copy(
                                                    name = formName,
                                                    type = formType,
                                                    capacity = capVal,
                                                    building = formBuilding,
                                                    floor = formFloor,
                                                    description = formDescription,
                                                    amenities = amenList
                                                )
                                            )
                                        }
                                    },
                                    isLoading = operationResult is Resource.Loading,
                                    modifier = Modifier.weight(1f)
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
fun ManagementItemCard(
    resource: WorkspaceResource,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onToggleMaintenance: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = AdminSurface),
        border = BorderStroke(0.5.dp, AdminBorder),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(AdminBg),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (resource.type == ResourceType.ROOM) Icons.Default.MeetingRoom else Icons.Default.LaptopMac,
                        contentDescription = null,
                        tint = AdminPrimary
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically, 
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = resource.name,
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleMedium,
                            color = AdminTextPrimary
                        )
                        StatusBadge(status = resource.status)
                    }
                    Text(
                        text = "${resource.building} • ${resource.floor} • Sức chứa: ${resource.capacity}",
                        style = MaterialTheme.typography.bodySmall,
                        color = AdminTextSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Action row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Toggle maintenance icon
                IconButton(onClick = onToggleMaintenance) {
                    Icon(
                        imageVector = Icons.Default.Build,
                        contentDescription = "Toggle Maintenance",
                        tint = if (resource.status == ResourceStatus.MAINTENANCE) {
                            Color(0xFFF59E0B) // Amber
                        } else {
                            AdminTextSecondary
                        }
                    )
                }

                // Edit
                IconButton(onClick = onEditClick) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit Space",
                        tint = AdminPrimary
                    )
                }

                // Delete
                IconButton(onClick = onDeleteClick) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete Space",
                        tint = Color(0xFFEF4444)
                    )
                }
            }
        }
    }
}
