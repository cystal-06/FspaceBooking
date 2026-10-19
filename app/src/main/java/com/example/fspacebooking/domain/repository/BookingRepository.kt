package com.example.fspacebooking.domain.repository

import com.example.fspacebooking.domain.model.Booking
import com.example.fspacebooking.domain.model.TimeSlot
import com.example.fspacebooking.utils.Resource
import kotlinx.coroutines.flow.Flow

interface BookingRepository {
    suspend fun createBooking(
        resourceId: String,
        date: String,
        timeSlotId: Int,
        purpose: String = "Họp công việc nội bộ"
    ): Resource<Booking>

    suspend fun checkInBooking(
        bookingId: String,
        qrCode: String
    ): Resource<Booking>

    suspend fun cancelBooking(bookingId: String): Resource<Unit>

    fun getMyBookings(): Flow<Resource<List<Booking>>>

    fun getTodayBookings(): Flow<Resource<List<Booking>>>

    // Admin Operations
    fun getAllBookings(): Flow<Resource<List<Booking>>>
    
    fun getAuditLogs(): Flow<List<com.example.fspacebooking.domain.model.AuditLogEntry>>

    suspend fun getResourceUtilizationStats(): Resource<Map<String, Float>>

    // Time Slot Catalog
    suspend fun getTimeSlots(): Resource<List<TimeSlot>>
}
