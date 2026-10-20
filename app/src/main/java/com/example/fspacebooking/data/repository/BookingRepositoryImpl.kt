package com.example.fspacebooking.data.repository

import com.example.fspacebooking.data.local.dao.AuditLogDao
import com.example.fspacebooking.data.local.dao.BookingDao
import com.example.fspacebooking.data.local.dao.ResourceDao
import com.example.fspacebooking.data.local.entity.AuditLogEntity
import com.example.fspacebooking.data.local.entity.BookingEntity
import com.example.fspacebooking.data.remote.api.BookingRequest
import com.example.fspacebooking.data.remote.api.CheckInRequest
import com.example.fspacebooking.data.remote.api.FSpaceApiService
import com.example.fspacebooking.domain.model.Booking
import com.example.fspacebooking.domain.model.BookingStatus
import com.example.fspacebooking.domain.model.TimeSlot
import com.example.fspacebooking.domain.model.WorkspaceResource
import com.example.fspacebooking.domain.repository.AuthRepository
import com.example.fspacebooking.domain.repository.BookingRepository
import com.example.fspacebooking.utils.Resource
import com.google.gson.JsonParser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BookingRepositoryImpl @Inject constructor(
    private val authRepository: AuthRepository,
    private val apiService: FSpaceApiService,
    private val bookingDao: BookingDao,
    private val auditLogDao: AuditLogDao,
    private val resourceDao: ResourceDao
) : BookingRepository {

    override suspend fun createBooking(
        resourceId: String,
        date: String,
        timeSlotId: Int,
        purpose: String
    ): Resource<Booking> = withContext(Dispatchers.IO) {
        val currentUser = authRepository.getSessionUser().firstOrNull()
        if (currentUser == null) {
            return@withContext Resource.Error("Phiên đăng nhập hết hạn. Vui lòng đăng nhập lại.")
        }

        if (currentUser.userStatus == "ĐÃ KHÓA") {
            return@withContext Resource.Error("Tài khoản của bạn đã bị KHÓA do vi phạm quy chế check-in quá 3 lần. Vui lòng liên hệ Quản trị viên (Admin) để mở khóa!")
        }

        // 1. First attempt: Remote Backend Server API (PostgreSQL with Row-level Lock)
        try {
            val remoteBooking = apiService.createBooking(
                BookingRequest(
                    resourceId = resourceId,
                    date = date,
                    timeSlotId = timeSlotId,
                    purpose = purpose,
                    userId = currentUser.id
                )
            )

            // Cache to SQLite Room Database
            try {
                bookingDao.insertBooking(
                    BookingEntity(
                        id = remoteBooking.id,
                        userId = remoteBooking.userId,
                        username = remoteBooking.username,
                        resourceId = remoteBooking.resource.id,
                        timeSlotId = remoteBooking.timeSlot.id,
                        date = remoteBooking.date,
                        status = remoteBooking.status,
                        purpose = remoteBooking.purpose,
                        qrCode = "QR_${remoteBooking.id}_${remoteBooking.resource.id}",
                        createdAt = remoteBooking.createdAt
                    )
                )
                auditLogDao.insertLog(
                    AuditLogEntity(
                        id = "log_${System.currentTimeMillis()}",
                        timestamp = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date()),
                        category = "BOOKING",
                        message = "Người dùng '${currentUser.name}' đặt thành công '${remoteBooking.resource.name}' (${remoteBooking.date} [${remoteBooking.timeSlot.startTime}-${remoteBooking.timeSlot.endTime}]).",
                        actorId = currentUser.id
                    )
                )
            } catch (_: Exception) {}

            return@withContext Resource.Success(remoteBooking)
        } catch (e: Exception) {
            if (e is HttpException) {
                val errorBody = e.response()?.errorBody()?.string() ?: ""
                val errorMsg = try {
                    JsonParser.parseString(errorBody).asJsonObject.get("message").asString
                } catch (_: Exception) {
                    "Khung giờ này đã được đồng nghiệp khác đặt trước hoặc tài khoản bị giới hạn."
                }
                return@withContext Resource.Error(errorMsg)
            }

            // 2. Offline Fallback: Room SQLite database
            val conflict = bookingDao.findConflictingBooking(resourceId, date, timeSlotId)
            if (conflict != null) {
                return@withContext Resource.Error("Khung giờ này đã được đồng nghiệp khác đặt trước.")
            }

            val resEntity = resourceDao.getResourceById(resourceId)
            val resourceDomain = resEntity?.toDomain() ?: WorkspaceResource(
                id = resourceId,
                name = "Phòng $resourceId",
                type = com.example.fspacebooking.domain.model.ResourceType.ROOM,
                capacity = 6,
                building = "F-Town 1",
                floor = "Tầng 2",
                status = com.example.fspacebooking.domain.model.ResourceStatus.BOOKED
            )
            val slot = TimeSlot.DEFAULT_SLOTS.find { it.id == timeSlotId }
                ?: TimeSlot(timeSlotId, "08:00", "10:00", "Slot $timeSlotId")
            val newId = "b_${System.currentTimeMillis()}"
            val nowStr = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())

            val bookingEntity = BookingEntity(
                id = newId,
                userId = currentUser.id,
                username = currentUser.username,
                resourceId = resourceId,
                timeSlotId = timeSlotId,
                date = date,
                status = BookingStatus.CONFIRMED,
                purpose = purpose,
                qrCode = "QR_${newId}_${resourceId}",
                createdAt = nowStr
            )
            bookingDao.insertBooking(bookingEntity)

            try {
                auditLogDao.insertLog(
                    AuditLogEntity(
                        id = "log_${System.currentTimeMillis()}",
                        timestamp = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date()),
                        category = "BOOKING",
                        message = "Người dùng '${currentUser.name}' đặt thành công '${resourceDomain.name}' ($date [${slot.startTime}-${slot.endTime}]).",
                        actorId = currentUser.id
                    )
                )
            } catch (_: Exception) {}

            val booking = Booking(
                id = newId,
                userId = currentUser.id,
                username = currentUser.username,
                resource = resourceDomain,
                timeSlot = slot,
                date = date,
                status = BookingStatus.CONFIRMED,
                purpose = purpose,
                createdAt = nowStr
            )
            return@withContext Resource.Success(booking)
        }
    }

    override suspend fun checkInBooking(bookingId: String, qrCode: String): Resource<Booking> = withContext(Dispatchers.IO) {
        // Attempt Remote Backend Server Check-in first
        try {
            val remoteBooking = apiService.checkInBooking(bookingId, CheckInRequest(qrHash = qrCode))
            val nowTime = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
            try {
                bookingDao.checkInBooking(bookingId, BookingStatus.CHECKED_IN, nowTime)
                auditLogDao.insertLog(
                    AuditLogEntity(
                        id = "log_${System.currentTimeMillis()}",
                        timestamp = nowTime,
                        category = "CHECKIN",
                        message = "Quét mã QR Check-in thành công cho phòng '${remoteBooking.resource.name}' (Mã: $bookingId).",
                        actorId = remoteBooking.userId
                    )
                )
            } catch (_: Exception) {}
            return@withContext Resource.Success(remoteBooking)
        } catch (e: Exception) {
            if (e is HttpException) {
                val errorBody = e.response()?.errorBody()?.string() ?: ""
                val errorMsg = try {
                    JsonParser.parseString(errorBody).asJsonObject.get("message").asString
                } catch (_: Exception) {
                    "Check-in không thành công."
                }
                return@withContext Resource.Error(errorMsg)
            }

            // Fallback: Room SQLite database
            val entity = bookingDao.getBookingById(bookingId)
            return@withContext if (entity != null) {
                val nowTime = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
                bookingDao.checkInBooking(bookingId, BookingStatus.CHECKED_IN, nowTime)
                val res = resourceDao.getResourceById(entity.resourceId)?.toDomain()
                    ?: WorkspaceResource(
                        id = entity.resourceId,
                        name = "Phòng ${entity.resourceId}",
                        type = com.example.fspacebooking.domain.model.ResourceType.ROOM,
                        capacity = 6,
                        building = "F-Town 1",
                        floor = "Tầng 2",
                        status = com.example.fspacebooking.domain.model.ResourceStatus.BOOKED
                    )
                val slot = TimeSlot.DEFAULT_SLOTS.find { it.id == entity.timeSlotId }
                    ?: TimeSlot(entity.timeSlotId, "08:00", "10:00", "Slot ${entity.timeSlotId}")
                try {
                    auditLogDao.insertLog(
                        AuditLogEntity(
                            id = "log_${System.currentTimeMillis()}",
                            timestamp = nowTime,
                            category = "CHECKIN",
                            message = "Quét mã QR Check-in thành công cho phòng '${res.name}' (Mã: $bookingId).",
                            actorId = entity.userId
                        )
                    )
                } catch (_: Exception) {}
                val updatedBooking = Booking(
                    id = entity.id,
                    userId = entity.userId,
                    username = entity.username,
                    resource = res,
                    timeSlot = slot,
                    date = entity.date,
                    status = BookingStatus.CHECKED_IN,
                    purpose = entity.purpose,
                    createdAt = entity.createdAt
                )
                Resource.Success(updatedBooking)
            } else {
                Resource.Error("Không tìm thấy mã đặt chỗ để thực hiện Check-in.")
            }
        }
    }

    override suspend fun cancelBooking(bookingId: String): Resource<Unit> = withContext(Dispatchers.IO) {
        try {
            apiService.cancelBooking(bookingId)
        } catch (_: Exception) {}

        try {
            bookingDao.updateBookingStatus(bookingId, BookingStatus.CANCELLED)
            auditLogDao.insertLog(
                AuditLogEntity(
                    id = "log_${System.currentTimeMillis()}",
                    timestamp = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date()),
                    category = "BOOKING",
                    message = "Hủy lịch đặt chỗ thành công (Mã: $bookingId)."
                )
            )
        } catch (_: Exception) {}
        Resource.Success(Unit)
    }

    override fun getAuditLogs(): Flow<List<com.example.fspacebooking.domain.model.AuditLogEntry>> {
        return flow {
            try {
                val remoteLogs = apiService.getAuditLogs()
                if (remoteLogs.isNotEmpty()) {
                    emit(remoteLogs)
                    return@flow
                }
            } catch (_: Exception) {}
            auditLogDao.getAllLogs().collect { entities ->
                emit(entities.map {
                    com.example.fspacebooking.domain.model.AuditLogEntry(
                        id = it.id,
                        timestamp = it.timestamp,
                        category = it.category,
                        message = it.message
                    )
                })
            }
        }.flowOn(Dispatchers.IO)
    }

    override fun getMyBookings(): Flow<Resource<List<Booking>>> {
        return flow {
            emit(Resource.Loading())
            val currentUser = authRepository.getSessionUser().firstOrNull()
            val userId = currentUser?.id ?: ""

            // Attempt remote fetch
            try {
                val remoteBookings = apiService.getAllBookings(userId = userId)
                if (remoteBookings.isNotEmpty()) {
                    emit(Resource.Success(remoteBookings))
                    return@flow
                }
            } catch (_: Exception) {}

            // Fallback to SQLite Room DB
            bookingDao.getBookingsByUserId(userId).collect { entities ->
                val list = entities.map { entity ->
                    val res = resourceDao.getResourceById(entity.resourceId)?.toDomain()
                        ?: WorkspaceResource(
                            id = entity.resourceId,
                            name = "Phòng ${entity.resourceId}",
                            type = com.example.fspacebooking.domain.model.ResourceType.ROOM,
                            capacity = 6,
                            building = "F-Town 1",
                            floor = "Tầng 2",
                            status = com.example.fspacebooking.domain.model.ResourceStatus.BOOKED
                        )
                    val slot = TimeSlot.DEFAULT_SLOTS.find { it.id == entity.timeSlotId }
                        ?: TimeSlot(entity.timeSlotId, "08:00", "10:00", "Slot ${entity.timeSlotId}")
                    Booking(
                        id = entity.id,
                        userId = entity.userId,
                        username = entity.username,
                        resource = res,
                        timeSlot = slot,
                        date = entity.date,
                        status = entity.status,
                        purpose = entity.purpose,
                        createdAt = entity.createdAt
                    )
                }
                emit(Resource.Success(list))
            }
        }.flowOn(Dispatchers.IO)
    }

    override fun getTodayBookings(): Flow<Resource<List<Booking>>> {
        return flow {
            emit(Resource.Loading())
            val todayStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
            val currentUser = authRepository.getSessionUser().firstOrNull()
            val userId = currentUser?.id ?: ""

            try {
                val remoteBookings = apiService.getAllBookings(userId = userId, date = todayStr)
                if (remoteBookings.isNotEmpty()) {
                    emit(Resource.Success(remoteBookings.filter { it.status == BookingStatus.CONFIRMED }))
                    return@flow
                }
            } catch (_: Exception) {}

            bookingDao.getBookingsByDate(todayStr).collect { entities ->
                val list = entities.filter {
                    it.userId == userId && it.status == BookingStatus.CONFIRMED
                }.map { entity ->
                    val res = resourceDao.getResourceById(entity.resourceId)?.toDomain()
                        ?: WorkspaceResource(
                            id = entity.resourceId,
                            name = "Phòng ${entity.resourceId}",
                            type = com.example.fspacebooking.domain.model.ResourceType.ROOM,
                            capacity = 6,
                            building = "F-Town 1",
                            floor = "Tầng 2",
                            status = com.example.fspacebooking.domain.model.ResourceStatus.BOOKED
                        )
                    val slot = TimeSlot.DEFAULT_SLOTS.find { it.id == entity.timeSlotId }
                        ?: TimeSlot(entity.timeSlotId, "08:00", "10:00", "Slot ${entity.timeSlotId}")
                    Booking(
                        id = entity.id,
                        userId = entity.userId,
                        username = entity.username,
                        resource = res,
                        timeSlot = slot,
                        date = entity.date,
                        status = entity.status,
                        purpose = entity.purpose,
                        createdAt = entity.createdAt
                    )
                }
                emit(Resource.Success(list))
            }
        }.flowOn(Dispatchers.IO)
    }

    override fun getAllBookings(): Flow<Resource<List<Booking>>> {
        return flow {
            emit(Resource.Loading())
            try {
                val remoteBookings = apiService.getAllBookings()
                if (remoteBookings.isNotEmpty()) {
                    emit(Resource.Success(remoteBookings))
                    return@flow
                }
            } catch (_: Exception) {}

            bookingDao.getAllBookings().collect { entities ->
                val list = entities.map { entity ->
                    val res = resourceDao.getResourceById(entity.resourceId)?.toDomain()
                        ?: WorkspaceResource(
                            id = entity.resourceId,
                            name = "Phòng ${entity.resourceId}",
                            type = com.example.fspacebooking.domain.model.ResourceType.ROOM,
                            capacity = 6,
                            building = "F-Town 1",
                            floor = "Tầng 2",
                            status = com.example.fspacebooking.domain.model.ResourceStatus.BOOKED
                        )
                    val slot = TimeSlot.DEFAULT_SLOTS.find { it.id == entity.timeSlotId }
                        ?: TimeSlot(entity.timeSlotId, "08:00", "10:00", "Slot ${entity.timeSlotId}")
                    Booking(
                        id = entity.id,
                        userId = entity.userId,
                        username = entity.username,
                        resource = res,
                        timeSlot = slot,
                        date = entity.date,
                        status = entity.status,
                        purpose = entity.purpose,
                        createdAt = entity.createdAt
                    )
                }
                emit(Resource.Success(list))
            }
        }.flowOn(Dispatchers.IO)
    }

    override suspend fun getResourceUtilizationStats(): Resource<Map<String, Float>> = withContext(Dispatchers.IO) {
        try {
            val remoteStats = apiService.getUtilizationStats()
            if (remoteStats.isNotEmpty()) {
                return@withContext Resource.Success(remoteStats)
            }
        } catch (_: Exception) {}

        val totalResources = resourceDao.getAllResources().firstOrNull() ?: emptyList()
        val allBookings = bookingDao.getAllBookings().firstOrNull() ?: emptyList()

        val statsMap = totalResources.associate { res ->
            val count = allBookings.count { it.resourceId == res.id && it.status == BookingStatus.CONFIRMED }
            val rate = (30f + (count * 20f)).coerceAtMost(95f)
            res.name to rate
        }
        Resource.Success(statsMap)
    }

    override suspend fun getTimeSlots(): Resource<List<TimeSlot>> = withContext(Dispatchers.IO) {
        try {
            val remoteSlots = apiService.getTimeSlots()
            if (remoteSlots.isNotEmpty()) {
                return@withContext Resource.Success(remoteSlots)
            }
        } catch (_: Exception) {}
        Resource.Success(TimeSlot.DEFAULT_SLOTS)
    }
}
