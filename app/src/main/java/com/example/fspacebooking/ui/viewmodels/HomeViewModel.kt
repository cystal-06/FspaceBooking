package com.example.fspacebooking.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fspacebooking.domain.model.Booking
import com.example.fspacebooking.domain.model.User
import com.example.fspacebooking.domain.model.WorkspaceResource
import com.example.fspacebooking.domain.repository.AuthRepository
import com.example.fspacebooking.domain.repository.BookingRepository
import com.example.fspacebooking.domain.repository.ResourceRepository
import com.example.fspacebooking.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val bookingRepository: BookingRepository,
    private val resourceRepository: ResourceRepository
) : ViewModel() {

    val currentUser: StateFlow<User?> = authRepository.getSessionUser()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    private val _todayBookings = MutableStateFlow<Resource<List<Booking>>>(Resource.Loading())
    val todayBookings: StateFlow<Resource<List<Booking>>> = _todayBookings.asStateFlow()

    private val _favorites = MutableStateFlow<List<WorkspaceResource>>(emptyList())
    val favorites: StateFlow<List<WorkspaceResource>> = _favorites.asStateFlow()

    init {
        viewModelScope.launch {
            bookingRepository.getTodayBookings().collectLatest {
                _todayBookings.value = it
            }
        }
        viewModelScope.launch {
            resourceRepository.getFavoriteResources().collectLatest {
                _favorites.value = it
            }
        }
    }

    fun toggleFavorite(resourceId: String, isFavorite: Boolean) {
        viewModelScope.launch {
            resourceRepository.toggleFavorite(resourceId, isFavorite)
        }
    }

    fun cancelBooking(bookingId: String) {
        viewModelScope.launch {
            bookingRepository.cancelBooking(bookingId)
        }
    }
}
