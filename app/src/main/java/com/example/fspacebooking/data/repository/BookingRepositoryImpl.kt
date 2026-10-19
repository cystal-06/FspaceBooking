package com.example.fspacebooking.data.repository

import com.example.fspacebooking.data.local.MockDataStore
import com.example.fspacebooking.domain.model.Booking
import com.example.fspacebooking.domain.model.BookingStatus
import com.example.fspacebooking.domain.model.TimeSlot
import com.example.fspacebooking.domain.repository.AuthRepository
import com.example.fspacebooking.domain.repository.BookingRepository
import com.example.fspacebooking.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BookingRepositoryImpl @Inject constructor(
    private val authRepository: AuthRepository
) : BookingRepository {

    override suspend fun createBooking(
        resourceId: String,
        date: String,
        timeSlotId: Int,
        purpose: String
    ): Resource<Booking> {
        val currentUser = authRepository.getSessionUser().firstOrNull()
        if (currentUser == null) {
            return Resource.Error("Phiên đăng nhập hết hạn. Vui lòng đăng nhập lại.")
        }

        if (currentUser.userStatus == "ĐÃ KHÓA") {
            return Resource.Error("Tài khoản của bạn đã bị KHÓA do vi phạm quy chế check-in quá 3 lần. Vui lòng liên hệ Quản trị viên (Admin) để mở khóa!")
        }

        val booking = MockDataStore.bookResource(
            resourceId = resourceId,
            date = date,
            timeSlotId = timeSlotId,
            userId = currentUser.id,
            username = currentUser.username,
            purpose = purpose
        )

        return if (booking != null) {
            Resource.Success(booking)
        } else {
            Resource.Error("Không thể thực hiện đặt chỗ. Khung giờ này có thể đã bị chiếm hoặc tài khoản bị giới hạn.")
        }
    }

    override suspend fun checkInBooking(bookingId: String, qrCode: String): Resource<Booking> {
        val result = MockDataStore.checkInBooking(bookingId, qrCode)
        return if (result.isSuccess) {
            Resource.Success(result.getOrThrow())
        } else {
            Resource.Error(result.exceptionOrNull()?.message ?: "Check-in không thành công.")
        }
    }

    override suspend fun cancelBooking(bookingId: String): Resource<Unit> {
        MockDataStore.cancelBooking(bookingId)
        return Resource.Success(Unit)
    }

    override fun getAuditLogs(): Flow<List<com.example.fspacebooking.domain.model.AuditLogEntry>> {
        return MockDataStore.auditLogsFlow
    }

    override fun getMyBookings(): Flow<Resource<List<Booking>>> {
        return flow {
            emit(Resource.Loading())
            val currentUser = authRepository.getSessionUser().firstOrNull()
            val userId = currentUser?.id ?: ""
            MockDataStore.bookingsFlow.map { list ->
                Resource.Success(list.filter { it.userId == userId })
            }.collect {
                emit(it)
            }
        }
    }

    override fun getTodayBookings(): Flow<Resource<List<Booking>>> {
        return flow {
            emit(Resource.Loading())
            val todayStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
            val currentUser = authRepository.getSessionUser().firstOrNull()
            val userId = currentUser?.id ?: ""
            MockDataStore.bookingsFlow.map { list ->
                Resource.Success(
                    list.filter {
                        it.userId == userId &&
                        it.date == todayStr &&
                        it.status == BookingStatus.CONFIRMED
                    }
                )
            }.collect {
                emit(it)
            }
        }
    }

    override fun getAllBookings(): Flow<Resource<List<Booking>>> {
        return flow {
            emit(Resource.Loading())
            MockDataStore.bookingsFlow.map { list ->
                Resource.Success(list)
            }.collect {
                emit(it)
            }
        }
    }

    override suspend fun getResourceUtilizationStats(): Resource<Map<String, Float>> {
        val bookings = MockDataStore.bookingsFlow.value
        val totalResources = MockDataStore.resourcesFlow.value

        val statsMap = totalResources.associate { res ->
            val count = bookings.count { it.resource.id == res.id && it.status == BookingStatus.CONFIRMED }
            val rate = (30f + (count * 20f)).coerceAtMost(95f)
            res.name to rate
        }
        return Resource.Success(statsMap)
    }

    override suspend fun getTimeSlots(): Resource<List<TimeSlot>> {
        return Resource.Success(MockDataStore.timeSlots)
    }
}
