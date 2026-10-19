package com.example.fspacebooking.ui.viewmodels

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fspacebooking.domain.model.Booking
import com.example.fspacebooking.domain.model.ResourceStatus
import com.example.fspacebooking.domain.model.ResourceType
import com.example.fspacebooking.domain.model.WorkspaceResource
import com.example.fspacebooking.domain.model.AuditLogEntry
import com.example.fspacebooking.domain.model.User
import com.example.fspacebooking.domain.repository.AuthRepository
import com.example.fspacebooking.domain.repository.BookingRepository
import com.example.fspacebooking.domain.repository.ResourceRepository
import com.example.fspacebooking.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AdminViewModel @Inject constructor(
    private val bookingRepository: BookingRepository,
    private val resourceRepository: ResourceRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _allBookings = MutableStateFlow<Resource<List<Booking>>>(Resource.Loading())
    val allBookings: StateFlow<Resource<List<Booking>>> = _allBookings.asStateFlow()

    private val _allResources = MutableStateFlow<Resource<List<WorkspaceResource>>>(Resource.Loading())
    val allResources: StateFlow<Resource<List<WorkspaceResource>>> = _allResources.asStateFlow()

    private val _utilizationStats = MutableStateFlow<Resource<Map<String, Float>>>(Resource.Loading())
    val utilizationStats: StateFlow<Resource<Map<String, Float>>> = _utilizationStats.asStateFlow()

    private val _violatorsList = MutableStateFlow<List<User>>(emptyList())
    val violatorsList: StateFlow<List<User>> = _violatorsList.asStateFlow()

    private val _auditLogs = MutableStateFlow<List<AuditLogEntry>>(emptyList())
    val auditLogs: StateFlow<List<AuditLogEntry>> = _auditLogs.asStateFlow()

    private val _operationResult = mutableStateOf<Resource<WorkspaceResource>?>(null)
    val operationResult: State<Resource<WorkspaceResource>?> = _operationResult

    init {
        loadDashboardData()
    }

    fun loadDashboardData() {
        loadAllBookings()
        loadResources()
        loadStats()
        loadViolators()
        loadAuditLogs()
    }

    fun loadViolators() {
        viewModelScope.launch {
            authRepository.getViolators().collectLatest {
                _violatorsList.value = it
            }
        }
    }

    fun loadAuditLogs() {
        viewModelScope.launch {
            bookingRepository.getAuditLogs().collectLatest {
                _auditLogs.value = it
            }
        }
    }

    fun unlockUser(userId: String) {
        viewModelScope.launch {
            authRepository.unlockUser(userId)
            loadViolators()
        }
    }

    fun lockUser(userId: String) {
        viewModelScope.launch {
            authRepository.lockUser(userId)
            loadViolators()
        }
    }

    fun sendWarningEmail(userId: String) {
        viewModelScope.launch {
            authRepository.sendWarningEmail(userId)
        }
    }

    fun loadAllBookings() {
        viewModelScope.launch {
            bookingRepository.getAllBookings().collectLatest {
                _allBookings.value = it
            }
        }
    }

    fun loadResources() {
        viewModelScope.launch {
            resourceRepository.getResources(null, null, null, null, null).collectLatest {
                _allResources.value = it
            }
        }
    }

    fun loadStats() {
        viewModelScope.launch {
            _utilizationStats.value = Resource.Loading()
            _utilizationStats.value = bookingRepository.getResourceUtilizationStats()
        }
    }

    fun addResource(
        name: String,
        type: ResourceType,
        capacity: Int,
        building: String,
        floor: String,
        description: String,
        amenities: List<String>
    ) {
        viewModelScope.launch {
            _operationResult.value = Resource.Loading()
            val newResource = WorkspaceResource(
                id = "",
                name = name,
                type = type,
                capacity = capacity,
                building = building,
                floor = floor,
                status = ResourceStatus.AVAILABLE,
                description = description,
                amenities = amenities
            )
            val result = resourceRepository.addResource(newResource)
            _operationResult.value = result
            if (result is Resource.Success) {
                loadResources()
            }
        }
    }

    fun updateResource(resource: WorkspaceResource) {
        viewModelScope.launch {
            _operationResult.value = Resource.Loading()
            val result = resourceRepository.updateResource(resource)
            _operationResult.value = result
            if (result is Resource.Success) {
                loadResources()
            }
        }
    }

    fun deleteResource(id: String) {
        viewModelScope.launch {
            val result = resourceRepository.deleteResource(id)
            if (result is Resource.Success) {
                loadResources()
            }
        }
    }

    fun resetOperationResult() {
        _operationResult.value = null
    }

    fun toggleMaintenance(resource: WorkspaceResource) {
        viewModelScope.launch {
            val nextStatus = if (resource.status == ResourceStatus.MAINTENANCE) {
                ResourceStatus.AVAILABLE
            } else {
                ResourceStatus.MAINTENANCE
            }
            resourceRepository.updateResource(resource.copy(status = nextStatus))
            loadResources()
        }
    }
}
