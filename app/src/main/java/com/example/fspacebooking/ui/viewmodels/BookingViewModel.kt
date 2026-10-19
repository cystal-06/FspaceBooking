package com.example.fspacebooking.ui.viewmodels

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fspacebooking.domain.model.Booking
import com.example.fspacebooking.domain.model.TimeSlot
import com.example.fspacebooking.domain.model.User
import com.example.fspacebooking.domain.model.WorkspaceResource
import com.example.fspacebooking.domain.repository.AuthRepository
import com.example.fspacebooking.domain.repository.BookingRepository
import com.example.fspacebooking.domain.repository.ResourceRepository
import com.example.fspacebooking.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class BookingViewModel @Inject constructor(
    private val bookingRepository: BookingRepository,
    private val resourceRepository: ResourceRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    val currentUser: StateFlow<User?> = authRepository.getSessionUser()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    private val _resourceState = mutableStateOf<Resource<WorkspaceResource>>(Resource.Loading())
    val resourceState: State<Resource<WorkspaceResource>> = _resourceState

    private val _timeSlotsState = mutableStateOf<Resource<List<TimeSlot>>>(Resource.Loading())
    val timeSlotsState: State<Resource<List<TimeSlot>>> = _timeSlotsState

    private val _bookingResultState = mutableStateOf<Resource<Booking>?>(null)
    val bookingResultState: State<Resource<Booking>?> = _bookingResultState

    private val _selectedDate = mutableStateOf(SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()))
    val selectedDate: State<String> = _selectedDate

    private val _selectedSlotId = mutableStateOf<Int?>(null)
    val selectedSlotId: State<Int?> = _selectedSlotId

    fun loadResourceDetails(resourceId: String) {
        viewModelScope.launch {
            _resourceState.value = Resource.Loading()
            _resourceState.value = resourceRepository.getResourceById(resourceId)
            
            _timeSlotsState.value = Resource.Loading()
            _timeSlotsState.value = bookingRepository.getTimeSlots()
        }
    }

    fun selectDate(date: String) {
        _selectedDate.value = date
        // Reset slot selection upon date changing
        _selectedSlotId.value = null
    }

    fun selectSlot(slotId: Int) {
        _selectedSlotId.value = slotId
    }

    fun resetBookingResult() {
        _bookingResultState.value = null
    }

    fun submitBooking(resourceId: String, purpose: String = "Họp công việc nội bộ") {
        val slotId = _selectedSlotId.value ?: return
        viewModelScope.launch {
            _bookingResultState.value = Resource.Loading()
            val result = bookingRepository.createBooking(resourceId, _selectedDate.value, slotId, purpose)
            _bookingResultState.value = result
        }
    }
}
