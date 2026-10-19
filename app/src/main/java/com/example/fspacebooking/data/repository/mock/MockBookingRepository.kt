package com.example.fspacebooking.data.repository.mock

import com.example.fspacebooking.domain.model.Booking
import com.example.fspacebooking.domain.model.BookingStatus
import com.example.fspacebooking.domain.model.TimeSlot
import com.example.fspacebooking.domain.model.WorkspaceResource
import com.example.fspacebooking.domain.repository.BookingRepository
import com.example.fspacebooking.domain.repository.ResourceRepository
import com.example.fspacebooking.data.local.MockDataStore
import com.example.fspacebooking.utils.Resource
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MockBookingRepository @Inject constructor(
    private val resourceRepository: ResourceRepository
) : BookingRepository {

    private val timeSlots = listOf(
        TimeSlot(1, "08:00", "09:30", "Slot 1 (08:00 - 09:30)"),
        TimeSlot(2, "09:45", "11:15", "Slot 2 (09:45 - 11:15)"),
        TimeSlot(3, "13:00", "14:30", "Slot 3 (13:00 - 14:30)"),
        TimeSlot(4, "14:45", "16:15", "Slot 4 (14:45 - 16:15)"),
        TimeSlot(5, "16:30", "18:00", "Slot 5 (16:30 - 18:00)")
    )

    private val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    private val todayStr = sdf.format(Date())

    private val bookingsList = mutableListOf<Booking>()

    private val _bookings = MutableStateFlow<List<Booking>>(emptyList())

    init {
        // Pre-populate some bookings for demo
        val calendar = Calendar.getInstance()
        val formattedToday = sdf.format(calendar.time)
        
        calendar.add(Calendar.DAY_OF_YEAR, 1)
        val formattedTomorrow = sdf.format(calendar.time)

        // We fetch resource list synchronously from MockResourceRepository for setup
        val mockResourcesRepo = resourceRepository as MockResourceRepository
        
        // Find Turing Room
        val lovelaceRoomRes = Resource.Success(emptyList<WorkspaceResource>()) // fallback
        
        // Let's create mock bookings using known IDs
        bookingsList.add(
            Booking(
                id = "book_1",
                userId = "user_123",
                username = "employee",
                resource = WorkspaceResource(
                    id = "res_1",
                    name = "Lovelace Room",
                    type = com.example.fspacebooking.domain.model.ResourceType.ROOM,
                    capacity = 12,
                    building = "F-Town 1",
                    floor = "Floor 2",
                    status = com.example.fspacebooking.domain.model.ResourceStatus.AVAILABLE,
                    description = "",
                    amenities = emptyList()
                ),
                timeSlot = timeSlots[0], // Slot 1
                date = formattedToday,
                status = BookingStatus.CONFIRMED,
                createdAt = "2026-08-12 07:30:00"
            )
        )

        bookingsList.add(
            Booking(
                id = "book_2",
                userId = "user_123",
                username = "employee",
                resource = WorkspaceResource(
                    id = "res_6",
                    name = "Hot Desk A-101",
                    type = com.example.fspacebooking.domain.model.ResourceType.DESK,
                    capacity = 1,
                    building = "F-Town 1",
                    floor = "Floor 1",
                    status = com.example.fspacebooking.domain.model.ResourceStatus.AVAILABLE,
                    description = "",
                    amenities = emptyList()
                ),
                timeSlot = timeSlots[2], // Slot 3
                date = formattedTomorrow,
                status = BookingStatus.CONFIRMED,
                createdAt = "2026-08-12 08:45:00"
            )
        )
        
        // Add another booking from a different user to mock double-booking
        bookingsList.add(
            Booking(
                id = "book_3",
                userId = "user_456",
                username = "johndoe",
                resource = WorkspaceResource(
                    id = "res_2",
                    name = "Turing Room",
                    type = com.example.fspacebooking.domain.model.ResourceType.ROOM,
                    capacity = 8,
                    building = "F-Town 1",
                    floor = "Floor 3",
                    status = com.example.fspacebooking.domain.model.ResourceStatus.BOOKED,
                    description = "",
                    amenities = emptyList()
                ),
                timeSlot = timeSlots[1], // Slot 2
                date = formattedToday,
                status = BookingStatus.CONFIRMED,
                createdAt = "2026-08-12 06:15:00"
            )
        )

        _bookings.value = bookingsList
    }

    override suspend fun createBooking(
        resourceId: String,
        date: String,
        timeSlotId: Int,
        purpose: String
    ): Resource<Booking> {
        delay(600) // Simulate network request delay
        
        val resourceResult = resourceRepository.getResourceById(resourceId)
        if (resourceResult is Resource.Error) {
            return Resource.Error(resourceResult.message ?: "Resource not found")
        }
        val targetResource = resourceResult.data!!

        val slot = timeSlots.find { it.id == timeSlotId }
            ?: return Resource.Error("Invalid time slot")

        // Crucial logic: Double booking check (HTTP 409 Conflict Simulation)
        val conflictExists = _bookings.value.any {
            it.resource.id == resourceId &&
            it.date == date &&
            it.timeSlot.id == timeSlotId &&
            it.status == BookingStatus.CONFIRMED
        }

        if (conflictExists) {
            // Simulate HTTP 409 Conflict
            return Resource.Error("HTTP 409 Conflict: This resource has already been booked by another user for the selected time slot. Please choose another slot or resource.")
        }

        val newBooking = Booking(
            id = "book_${System.currentTimeMillis()}",
            userId = "user_123",
            username = "employee",
            resource = targetResource,
            timeSlot = slot,
            date = date,
            status = BookingStatus.CONFIRMED,
            createdAt = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
        )

        val updated = _bookings.value.toMutableList()
        updated.add(newBooking)
        bookingsList.add(newBooking)
        _bookings.value = updated

        return Resource.Success(newBooking)
    }

    override suspend fun cancelBooking(bookingId: String): Resource<Unit> {
        delay(800)
        val updated = _bookings.value.toMutableList()
        val index = updated.indexOfFirst { it.id == bookingId }
        if (index != -1) {
            val oldBooking = updated[index]
            updated[index] = oldBooking.copy(status = BookingStatus.CANCELLED)
            
            // update master list
            val mIndex = bookingsList.indexOfFirst { it.id == bookingId }
            if (mIndex != -1) {
                bookingsList[mIndex] = bookingsList[mIndex].copy(status = BookingStatus.CANCELLED)
            }
            
            _bookings.value = updated
            return Resource.Success(Unit)
        }
        return Resource.Error("Booking not found")
    }

    override suspend fun checkInBooking(bookingId: String, qrCode: String): Resource<Booking> {
        delay(600)
        val updated = _bookings.value.toMutableList()
        val index = updated.indexOfFirst { it.id == bookingId }
        if (index != -1) {
            val oldBooking = updated[index]
            val checkedIn = oldBooking.copy(status = BookingStatus.CHECKED_IN)
            updated[index] = checkedIn
            _bookings.value = updated
            return Resource.Success(checkedIn)
        }
        return Resource.Error("Booking not found")
    }

    override fun getAuditLogs(): Flow<List<com.example.fspacebooking.domain.model.AuditLogEntry>> {
        return MockDataStore.auditLogsFlow
    }

    override fun getMyBookings(): Flow<Resource<List<Booking>>> {
        return _bookings.asStateFlow().map { list ->
            // Filter by current user
            Resource.Success(list.filter { it.userId == "user_123" })
        }
    }

    override fun getTodayBookings(): Flow<Resource<List<Booking>>> {
        return _bookings.asStateFlow().map { list ->
            Resource.Success(list.filter { it.userId == "user_123" && it.date == todayStr && it.status == BookingStatus.CONFIRMED })
        }
    }

    override fun getAllBookings(): Flow<Resource<List<Booking>>> {
        return _bookings.asStateFlow().map { Resource.Success(it) }
    }

    override suspend fun getResourceUtilizationStats(): Resource<Map<String, Float>> {
        delay(500)
        // Hardcode representation rates for each resource based on booking counts
        val stats = mapOf(
            "Lovelace Room" to 65.0f,
            "Turing Room" to 85.0f,
            "Hopper Room" to 40.0f,
            "Gosling Boardroom" to 90.0f,
            "Knuth Room" to 10.0f,
            "Hot Desks Zone A" to 55.0f,
            "Hot Desks Zone B" to 70.0f,
            "Hot Desks Zone C" to 45.0f
        )
        return Resource.Success(stats)
    }

    override suspend fun getTimeSlots(): Resource<List<TimeSlot>> {
        return Resource.Success(timeSlots)
    }
}
