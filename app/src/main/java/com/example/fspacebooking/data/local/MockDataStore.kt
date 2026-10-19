package com.example.fspacebooking.data.local

import com.example.fspacebooking.domain.model.Booking
import com.example.fspacebooking.domain.model.BookingStatus
import com.example.fspacebooking.domain.model.ResourceStatus
import com.example.fspacebooking.domain.model.ResourceType
import com.example.fspacebooking.domain.model.TimeSlot
import com.example.fspacebooking.domain.model.WorkspaceResource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

object MockDataStore {

    val timeSlots = listOf(
        TimeSlot(1, "08:00", "10:00", "Khung giờ 1"),
        TimeSlot(2, "10:00", "12:00", "Khung giờ 2"),
        TimeSlot(3, "13:00", "15:00", "Khung giờ 3"),
        TimeSlot(4, "15:00", "17:00", "Khung giờ 4"),
        TimeSlot(5, "18:00", "20:00", "Khung giờ 5")
    )

    private val initialResources = listOf(
        WorkspaceResource(
            id = "res_1",
            name = "Phòng họp Gosling",
            type = ResourceType.ROOM,
            capacity = 12,
            building = "F-Town 3",
            floor = "Tầng 3",
            status = ResourceStatus.AVAILABLE,
            description = "Phòng họp cao cấp trang bị đầy đủ màn hình TV, bảng viết, hệ thống mic họp trực tuyến chuyên nghiệp.",
            amenities = listOf("Wi-Fi", "Tivi", "Bảng viết", "Microphone", "Điều hòa"),
            imageUrl = "https://images.unsplash.com/photo-1497366216548-37526070297c?auto=format&fit=crop&w=600&q=80"
        ),
        WorkspaceResource(
            id = "res_2",
            name = "Phòng họp Turing",
            type = ResourceType.ROOM,
            capacity = 8,
            building = "F-Town 1",
            floor = "Tầng 2",
            status = ResourceStatus.AVAILABLE,
            description = "Thích hợp cho các buổi thảo luận nhóm kỹ thuật (scrum meetings). Không gian khép kín, cách âm tốt.",
            amenities = listOf("Wi-Fi", "Tivi", "Bảng viết", "Điều hòa"),
            imageUrl = "https://images.unsplash.com/photo-1497366811353-6870744d04b2?auto=format&fit=crop&w=600&q=80"
        ),
        WorkspaceResource(
            id = "res_3",
            name = "Phòng họp Lovelace",
            type = ResourceType.ROOM,
            capacity = 6,
            building = "F-Town 3",
            floor = "Tầng 2",
            status = ResourceStatus.AVAILABLE,
            description = "Không gian nhỏ ấm cúng cho các cuộc đối thoại 1-1 hoặc họp nhóm nhỏ. Có máy chiếu chất lượng cao.",
            amenities = listOf("Wi-Fi", "Máy chiếu", "Bảng viết", "Điều hòa"),
            imageUrl = "https://images.unsplash.com/photo-1517502884422-41eaaced0168?auto=format&fit=crop&w=600&q=80"
        ),
        WorkspaceResource(
            id = "res_4",
            name = "Phòng Lab Ritchie",
            type = ResourceType.ROOM,
            capacity = 15,
            building = "F-Town 2",
            floor = "Tầng 1",
            status = ResourceStatus.AVAILABLE,
            description = "Không gian nghiên cứu phát triển lớn phục vụ cho các dự án lab R&D. Thiết bị đầy đủ.",
            amenities = listOf("Wi-Fi", "Tivi", "Bảng viết", "Microphone", "Thiết bị test", "Điều hòa"),
            imageUrl = "https://images.unsplash.com/photo-1542744094-3a31f103e35f?auto=format&fit=crop&w=600&q=80"
        ),
        WorkspaceResource(
            id = "res_5",
            name = "Phòng họp Hopper",
            type = ResourceType.ROOM,
            capacity = 4,
            building = "F-Town 1",
            floor = "Tầng 1",
            status = ResourceStatus.AVAILABLE,
            description = "Phòng họp nhanh không cần đặt trước lâu. Tiện nghi cơ bản đáp ứng thảo luận nhanh.",
            amenities = listOf("Wi-Fi", "Bảng viết", "Điều hòa"),
            imageUrl = "https://images.unsplash.com/photo-1497366216548-37526070297c?auto=format&fit=crop&w=600&q=80"
        ),
        WorkspaceResource(
            id = "res_6",
            name = "Bàn làm việc Desk-A101",
            type = ResourceType.DESK,
            capacity = 1,
            building = "F-Town 3",
            floor = "Tầng 1",
            status = ResourceStatus.AVAILABLE,
            description = "Chỗ ngồi làm việc cá nhân yên tĩnh, trang bị ghế công thái học Herman Miller thoải mái.",
            amenities = listOf("Wi-Fi", "Ổ điện", "Ghế công thái học"),
            imageUrl = "https://images.unsplash.com/photo-1524758631624-e2822e304c36?auto=format&fit=crop&w=600&q=80"
        ),
        WorkspaceResource(
            id = "res_7",
            name = "Bàn làm việc Desk-A102",
            type = ResourceType.DESK,
            capacity = 1,
            building = "F-Town 3",
            floor = "Tầng 1",
            status = ResourceStatus.AVAILABLE,
            description = "Không gian ngồi cá nhân linh hoạt gần cửa sổ thoáng mát, đầy đủ ánh sáng tự nhiên.",
            amenities = listOf("Wi-Fi", "Ổ điện"),
            imageUrl = "https://images.unsplash.com/photo-1497215728101-856f4ea42174?auto=format&fit=crop&w=600&q=80"
        ),
        WorkspaceResource(
            id = "res_8",
            name = "Bàn làm việc Desk-B201",
            type = ResourceType.DESK,
            capacity = 1,
            building = "F-Town 2",
            floor = "Tầng 2",
            status = ResourceStatus.AVAILABLE,
            description = "Bàn làm việc cá nhân khu vực FSU, thích hợp lập trình viên cần tập trung cao độ.",
            amenities = listOf("Wi-Fi", "Ổ điện", "Ghế công thái học"),
            imageUrl = "https://images.unsplash.com/photo-1539635278303-d4002c07eae3?auto=format&fit=crop&w=600&q=80"
        )
    )

    private val _resources = MutableStateFlow<List<WorkspaceResource>>(initialResources)
    val resourcesFlow = _resources.asStateFlow()

    // Danh sách tài khoản người dùng theo báo cáo
    private val initialUsers = listOf(
        com.example.fspacebooking.domain.model.User(
            id = "usr_admin",
            username = "admin",
            email = "admin@fpt.com",
            name = "Admin Manager",
            role = com.example.fspacebooking.domain.model.UserRole.ADMIN,
            employeeId = "ADM001",
            token = "jwt-admin-token-1",
            violations = 0,
            userStatus = "ĐANG HOẠT ĐỘNG",
            fsu = "FSU1.BU2"
        ),
        com.example.fspacebooking.domain.model.User(
            id = "usr_nam",
            username = "employee",
            email = "namtv@fpt.com",
            name = "Trần Văn Nam",
            role = com.example.fspacebooking.domain.model.UserRole.EMPLOYEE,
            employeeId = "EMP302",
            token = "jwt-emp-token-2",
            violations = 0,
            userStatus = "ĐANG HOẠT ĐỘNG",
            fsu = "FSU1.BU2"
        ),
        com.example.fspacebooking.domain.model.User(
            id = "usr_duc",
            username = "ducbt",
            email = "ducbt@fpt.com",
            name = "Bùi Thế Đức",
            role = com.example.fspacebooking.domain.model.UserRole.EMPLOYEE,
            employeeId = "EMP002",
            token = "jwt-emp-token-3",
            violations = 4,
            userStatus = "ĐÃ KHÓA",
            fsu = "FSU1.BU2"
        ),
        com.example.fspacebooking.domain.model.User(
            id = "usr_hung",
            username = "hungnv",
            email = "hungnv@fpt.com",
            name = "Nguyễn Văn Hùng",
            role = com.example.fspacebooking.domain.model.UserRole.EMPLOYEE,
            employeeId = "EMP093",
            token = "jwt-emp-token-4",
            violations = 3,
            userStatus = "CẢNH BÁO",
            fsu = "FSU2.BU8"
        ),
        com.example.fspacebooking.domain.model.User(
            id = "usr_lan",
            username = "lanlt",
            email = "lanlt@fpt.com",
            name = "Lê Thị Lan",
            role = com.example.fspacebooking.domain.model.UserRole.EMPLOYEE,
            employeeId = "EMP102",
            token = "jwt-emp-token-5",
            violations = 3,
            userStatus = "CẢNH BÁO",
            fsu = "FSU1.BU3"
        ),
        com.example.fspacebooking.domain.model.User(
            id = "usr_khanh",
            username = "khanhdq",
            email = "khanhdq@fpt.com",
            name = "Đặng Quốc Khánh",
            role = com.example.fspacebooking.domain.model.UserRole.EMPLOYEE,
            employeeId = "EMP025",
            token = "jwt-emp-token-6",
            violations = 2,
            userStatus = "THEO DÕI",
            fsu = "FSU3.BU5"
        )
    )

    private val _users = MutableStateFlow<List<com.example.fspacebooking.domain.model.User>>(initialUsers)
    val usersFlow = _users.asStateFlow()

    private val _auditLogs = MutableStateFlow<List<com.example.fspacebooking.domain.model.AuditLogEntry>>(
        listOf(
            com.example.fspacebooking.domain.model.AuditLogEntry("log_1", "09:42:01", "AUTH", "Nhân viên 'Trần Văn Nam' đã đăng nhập vào hệ thống."),
            com.example.fspacebooking.domain.model.AuditLogEntry("log_2", "09:30:15", "SYS", "Đã quét 8 tài nguyên; đồng bộ trạng thái thành công."),
            com.example.fspacebooking.domain.model.AuditLogEntry("log_3", "09:12:44", "ADMIN", "Cập nhật trạng thái 'Phòng họp Turing' thành HOẠT ĐỘNG."),
            com.example.fspacebooking.domain.model.AuditLogEntry("log_4", "08:45:00", "ADMIN", "Đã mở khóa đặt phòng cho nhân viên 'Bùi Thế Đức'."),
            com.example.fspacebooking.domain.model.AuditLogEntry("log_5", "08:15:32", "DB", "Tự động sao lưu dữ liệu giao dịch thành công.")
        )
    )
    val auditLogsFlow = _auditLogs.asStateFlow()

    fun addAuditLog(category: String, message: String) {
        val time = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
        val entry = com.example.fspacebooking.domain.model.AuditLogEntry("log_${System.currentTimeMillis()}", time, category, message)
        _auditLogs.value = listOf(entry) + _auditLogs.value
    }

    private val _myBookings = MutableStateFlow<List<Booking>>(
        run {
            val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val todayStr = formatter.format(Date())
            
            val cal = Calendar.getInstance()
            cal.add(Calendar.DAY_OF_YEAR, -1)
            val yesterdayStr = formatter.format(cal.time)
            
            cal.time = Date()
            cal.add(Calendar.DAY_OF_YEAR, 1)
            val tomorrowStr = formatter.format(cal.time)

            listOf(
                Booking(
                    id = "book_1",
                    userId = "usr_nam",
                    username = "employee",
                    resource = initialResources[0].copy(status = ResourceStatus.BOOKED), // Phòng họp Gosling
                    timeSlot = timeSlots[1], // 10:00 - 12:00
                    date = todayStr,
                    status = BookingStatus.CONFIRMED,
                    purpose = "Họp Sprint Review dự án FPT Automotive",
                    createdAt = formatter.format(Date())
                ),
                Booking(
                    id = "book_2",
                    userId = "usr_nam",
                    username = "employee",
                    resource = initialResources[2].copy(status = ResourceStatus.BOOKED), // Phòng họp Lovelace
                    timeSlot = timeSlots[3], // 15:00 - 17:00
                    date = todayStr,
                    status = BookingStatus.CONFIRMED,
                    purpose = "Phỏng vấn ứng viên Senior Kotlin Developer",
                    createdAt = formatter.format(Date())
                ),
                Booking(
                    id = "book_3",
                    userId = "usr_nam",
                    username = "employee",
                    resource = initialResources[5].copy(status = ResourceStatus.BOOKED), // Bàn làm việc Desk-A101
                    timeSlot = timeSlots[0], // 08:00 - 10:00
                    date = yesterdayStr,
                    status = BookingStatus.CHECKED_IN,
                    purpose = "Làm việc cá nhân dự án FSU1",
                    createdAt = formatter.format(Date())
                ),
                Booking(
                    id = "book_4",
                    userId = "usr_nam",
                    username = "employee",
                    resource = initialResources[1].copy(status = ResourceStatus.BOOKED), // Phòng họp Turing
                    timeSlot = timeSlots[2], // 13:00 - 15:00
                    date = tomorrowStr,
                    status = BookingStatus.CONFIRMED,
                    purpose = "Họp trao đổi kỹ thuật với Tech Lead",
                    createdAt = formatter.format(Date())
                ),
                // Lịch đặt cố định ngày 17/02/2026 cho demo
                Booking(
                    id = "book_demo_1",
                    userId = "usr_admin",
                    username = "admin",
                    resource = initialResources[0].copy(status = ResourceStatus.BOOKED),
                    timeSlot = timeSlots[1],
                    date = "2026-02-17",
                    status = BookingStatus.CONFIRMED,
                    purpose = "Họp điều phối cơ sở vật chất quý 1",
                    createdAt = "2026-02-17 08:30:00"
                ),
                Booking(
                    id = "book_demo_2",
                    userId = "usr_admin",
                    username = "admin",
                    resource = initialResources[2].copy(status = ResourceStatus.BOOKED),
                    timeSlot = timeSlots[3],
                    date = "2026-02-17",
                    status = BookingStatus.CONFIRMED,
                    purpose = "Lập kế hoạch bảo trì thiết bị F-Town 3",
                    createdAt = "2026-02-17 09:00:00"
                )
            )
        }
    )
    val bookingsFlow = _myBookings.asStateFlow()

    fun bookResource(
        resourceId: String,
        date: String,
        timeSlotId: Int,
        userId: String,
        username: String,
        purpose: String = "Họp công việc nội bộ"
    ): Booking? {
        val user = _users.value.find { it.id == userId || it.username == username }
        if (user != null && user.userStatus == "ĐÃ KHÓA") {
            return null // Bị khóa không cho đặt
        }

        val resource = _resources.value.find { it.id == resourceId } ?: return null
        val timeSlot = timeSlots.find { it.id == timeSlotId } ?: return null

        val bookingId = "book_${System.currentTimeMillis()}"
        val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val newBooking = Booking(
            id = bookingId,
            userId = userId,
            username = username,
            resource = resource.copy(status = ResourceStatus.BOOKED),
            timeSlot = timeSlot,
            date = date,
            status = BookingStatus.CONFIRMED,
            purpose = purpose.ifBlank { "Họp công việc nội bộ" },
            createdAt = formatter.format(Date())
        )

        // Cập nhật trạng thái phòng thành BOOKED trong danh sách resources
        _resources.value = _resources.value.map {
            if (it.id == resourceId) it.copy(status = ResourceStatus.BOOKED) else it
        }

        // Thêm vào danh sách đặt lịch
        _myBookings.value = listOf(newBooking) + _myBookings.value
        addAuditLog("BOOKING", "Nhân viên '$username' đã đặt thành công '${resource.name}' ($date, ${timeSlot.startTime}).")
        return newBooking
    }

    fun checkInBooking(bookingId: String, qrCode: String): Result<Booking> {
        val booking = _myBookings.value.find { it.id == bookingId }
            ?: return Result.failure(Exception("Không tìm thấy thông tin lịch đặt!"))

        // Kiểm tra mã QR: cho phép khớp với resource id (res_1), hoặc QR code chuẩn như "QR_GOSLING", "QR_RES_1", hoặc tên phòng
        val cleanQr = qrCode.trim().uppercase()
        val expectedId = booking.resource.id.uppercase()
        val expectedName = booking.resource.name.uppercase()
        val expectedQrPattern = "QR_${expectedId}"
        val expectedQrPatternName = "QR_${expectedName.replace(" ", "_")}"

        val isQrValid = cleanQr == expectedId ||
                cleanQr == expectedQrPattern ||
                cleanQr == expectedQrPatternName ||
                cleanQr.contains(expectedId) ||
                cleanQr.contains(expectedName) ||
                cleanQr == "QUICK_CHECKIN" ||
                cleanQr == "DEMO_QR"

        if (!isQrValid) {
            return Result.failure(Exception("Mã QR không khớp với phòng đã đặt (${booking.resource.name})! Vui lòng quét đúng mã tại vị trí."))
        }

        val updated = booking.copy(status = BookingStatus.CHECKED_IN)
        _myBookings.value = _myBookings.value.map {
            if (it.id == bookingId) updated else it
        }

        addAuditLog("SYS", "Quét mã QR check-in thành công tại '${booking.resource.name}' (${booking.username}).")
        return Result.success(updated)
    }

    fun cancelBooking(bookingId: String) {
        val targetBooking = _myBookings.value.find { it.id == bookingId } ?: return
        
        // Cập nhật trạng thái phòng thành AVAILABLE trở lại nếu không còn lịch nào khác chiếm
        _resources.value = _resources.value.map {
            if (it.id == targetBooking.resource.id) it.copy(status = ResourceStatus.AVAILABLE) else it
        }

        // Đổi trạng thái lịch đặt thành CANCELLED
        _myBookings.value = _myBookings.value.map {
            if (it.id == bookingId) it.copy(status = BookingStatus.CANCELLED) else it
        }

        addAuditLog("BOOKING", "Đã hủy lịch đặt '${targetBooking.resource.name}' ngày ${targetBooking.date}.")
    }

    fun unlockUser(userId: String) {
        _users.value = _users.value.map {
            if (it.id == userId || it.employeeId == userId) {
                addAuditLog("ADMIN", "Đã mở khóa đặt phòng cho nhân viên '${it.name}' (${it.employeeId}).")
                it.copy(violations = 0, userStatus = "ĐANG HOẠT ĐỘNG")
            } else it
        }
    }

    fun lockUser(userId: String) {
        _users.value = _users.value.map {
            if (it.id == userId || it.employeeId == userId) {
                addAuditLog("ADMIN", "Đã khóa quyền đặt phòng của nhân viên '${it.name}' do vi phạm No-show.")
                it.copy(violations = 3, userStatus = "ĐÃ KHÓA")
            } else it
        }
    }

    fun sendWarningEmail(userId: String) {
        val target = _users.value.find { it.id == userId || it.employeeId == userId }
        if (target != null) {
            addAuditLog("MAIL", "Đã gửi email cảnh cáo vi phạm check-in tới '${target.email}'.")
        }
    }

    fun toggleFavorite(resourceId: String, isFavorite: Boolean) {
        _resources.value = _resources.value.map {
            if (it.id == resourceId) it.copy(isFavorite = isFavorite) else it
        }
    }

    fun addResource(resource: WorkspaceResource) {
        val id = if (resource.id.isBlank()) "res_${System.currentTimeMillis()}" else resource.id
        val newRes = resource.copy(id = id)
        _resources.value = _resources.value + newRes
        addAuditLog("ADMIN", "Thêm mới tài nguyên '${newRes.name}' tại ${newRes.building}.")
    }

    fun updateResource(resource: WorkspaceResource) {
        _resources.value = _resources.value.map {
            if (it.id == resource.id) resource else it
        }
        addAuditLog("ADMIN", "Cập nhật thông tin tài nguyên '${resource.name}'.")
    }

    fun deleteResource(resourceId: String) {
        val target = _resources.value.find { it.id == resourceId }
        _resources.value = _resources.value.filter { it.id != resourceId }
        target?.let { addAuditLog("ADMIN", "Đã xóa tài nguyên '${it.name}'.") }
    }
}

