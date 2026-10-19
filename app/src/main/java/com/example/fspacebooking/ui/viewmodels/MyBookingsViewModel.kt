package com.example.fspacebooking.ui.viewmodels

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fspacebooking.domain.model.Booking
import com.example.fspacebooking.domain.repository.BookingRepository
import com.example.fspacebooking.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyBookingsViewModel @Inject constructor(
    private val bookingRepository: BookingRepository
) : ViewModel() {

    private val _bookingsState = MutableStateFlow<Resource<List<Booking>>>(Resource.Loading())
    val bookingsState: StateFlow<Resource<List<Booking>>> = _bookingsState.asStateFlow()

    private val _cancelResultState = mutableStateOf<Resource<Unit>?>(null)
    val cancelResultState: State<Resource<Unit>?> = _cancelResultState

    init {
        loadBookings()
    }

    fun loadBookings() {
        viewModelScope.launch {
            bookingRepository.getMyBookings().collectLatest {
                _bookingsState.value = it
            }
        }
    }

    fun cancelBooking(bookingId: String) {
        viewModelScope.launch {
            _cancelResultState.value = Resource.Loading()
            val result = bookingRepository.cancelBooking(bookingId)
            _cancelResultState.value = result
            if (result is Resource.Success) {
                loadBookings()
            }
        }
    }

    private val _checkInResultState = mutableStateOf<Resource<Booking>?>(null)
    val checkInResultState: State<Resource<Booking>?> = _checkInResultState

    fun checkInBooking(bookingId: String, qrCode: String, onComplete: ((Resource<Booking>) -> Unit)? = null) {
        viewModelScope.launch {
            _checkInResultState.value = Resource.Loading()
            val result = bookingRepository.checkInBooking(bookingId, qrCode)
            _checkInResultState.value = result
            onComplete?.invoke(result)
            if (result is Resource.Success) {
                loadBookings()
            }
        }
    }

    fun resetCheckInResult() {
        _checkInResultState.value = null
    }

    fun resetCancelResult() {
        _cancelResultState.value = null
    }
}
