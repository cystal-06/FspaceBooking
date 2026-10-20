package com.example.fspacebooking.data.local.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.fspacebooking.data.local.converter.Converters
import com.example.fspacebooking.data.local.dao.AuditLogDao
import com.example.fspacebooking.data.local.dao.BookingDao
import com.example.fspacebooking.data.local.dao.ResourceDao
import com.example.fspacebooking.data.local.dao.TimeSlotDao
import com.example.fspacebooking.data.local.dao.UserDao
import com.example.fspacebooking.data.local.entity.AuditLogEntity
import com.example.fspacebooking.data.local.entity.BookingEntity
import com.example.fspacebooking.data.local.entity.ResourceEntity
import com.example.fspacebooking.data.local.entity.TimeSlotEntity
import com.example.fspacebooking.data.local.entity.UserEntity
import com.example.fspacebooking.domain.model.BookingStatus
import com.example.fspacebooking.domain.model.ResourceStatus
import com.example.fspacebooking.domain.model.ResourceType
import com.example.fspacebooking.domain.model.UserRole
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Provider

@Database(
    entities = [
        ResourceEntity::class, 
        BookingEntity::class, 
        TimeSlotEntity::class,
        UserEntity::class,
        AuditLogEntity::class
    ],
    version = 2,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class FSpaceDatabase : RoomDatabase() {
    abstract fun resourceDao(): ResourceDao
    abstract fun bookingDao(): BookingDao
    abstract fun timeSlotDao(): TimeSlotDao
    abstract fun userDao(): UserDao
    abstract fun auditLogDao(): AuditLogDao

    companion object {
        private const val DATABASE_NAME = "fspace_db"

        fun buildDatabase(
            context: Context,
            scope: CoroutineScope,
            resourceDaoProvider: Provider<ResourceDao>,
            timeSlotDaoProvider: Provider<TimeSlotDao>,
            userDaoProvider: Provider<UserDao>,
            auditLogDaoProvider: Provider<AuditLogDao>,
            bookingDaoProvider: Provider<BookingDao>
        ): FSpaceDatabase {
            return Room.databaseBuilder(
                context.applicationContext,
                FSpaceDatabase::class.java,
                DATABASE_NAME
            )
            .fallbackToDestructiveMigration()
            .allowMainThreadQueries()
            .addCallback(object : Callback() {
                override fun onCreate(db: SupportSQLiteDatabase) {
                    super.onCreate(db)
                    // Seed initial workspaces, time slots, users, bookings, and audit logs
                    scope.launch(Dispatchers.IO) {
                        seedInitialData(
                            resourceDaoProvider.get(), 
                            timeSlotDaoProvider.get(),
                            userDaoProvider.get(),
                            auditLogDaoProvider.get(),
                            bookingDaoProvider.get()
                        )
                    }
                }
            }).build()
        }

        private suspend fun seedInitialData(
            resourceDao: ResourceDao, 
            timeSlotDao: TimeSlotDao,
            userDao: UserDao,
            auditLogDao: AuditLogDao,
            bookingDao: BookingDao
        ) {
            // Seed Users (Admin, Normal Employee, Locked User, Warning User, Monitored User)
            val users = listOf(
                UserEntity(
                    id = "user_admin",
                    username = "admin",
                    password = "admin123",
                    email = "admin@fpt.com",
                    name = "Hoàng Minh Tuấn",
                    role = UserRole.ADMIN,
                    employeeId = "ADM001",
                    violations = 0,
                    userStatus = "ĐANG HOẠT ĐỘNG",
                    fsu = "ADM.CORP"
                ),
                UserEntity(
                    id = "user_namtv",
                    username = "employee",
                    password = "emp123",
                    email = "namtv@fpt.com",
                    name = "Trần Văn Nam",
                    role = UserRole.EMPLOYEE,
                    employeeId = "EMP302",
                    violations = 0,
                    userStatus = "ĐANG HOẠT ĐỘNG",
                    fsu = "FSU1.BU2"
                ),
                UserEntity(
                    id = "user_ducbt",
                    username = "ducbt",
                    password = "123456",
                    email = "ducbt@fpt.com",
                    name = "Bùi Thế Đức",
                    role = UserRole.EMPLOYEE,
                    employeeId = "EMP002",
                    violations = 4,
                    userStatus = "ĐÃ KHÓA",
                    fsu = "FSU2.DX"
                ),
                UserEntity(
                    id = "user_maitt",
                    username = "mai.tt",
                    password = "123456",
                    email = "mai.tt@fpt.com",
                    name = "Trương Thị Mai",
                    role = UserRole.EMPLOYEE,
                    employeeId = "EMP114",
                    violations = 2,
                    userStatus = "CẢNH BÁO",
                    fsu = "FSU1.BU9"
                ),
                UserEntity(
                    id = "user_lannt",
                    username = "lannt",
                    password = "password123",
                    email = "lannt@fpt.com",
                    name = "Nguyễn Thị Lan",
                    role = UserRole.EMPLOYEE,
                    employeeId = "EMP045",
                    violations = 1,
                    userStatus = "THEO DÕI",
                    fsu = "FSU3.AUTOMOTIVE"
                )
            )
            userDao.insertUsers(users)

            // Seed Initial Audit Logs (UC08)
            val initialLogs = listOf(
                AuditLogEntity("log_1", "08:00:00", "SYS", "Khởi động hệ thống cơ sở dữ liệu F-Space Booking (Version 2)."),
                AuditLogEntity("log_2", "08:15:30", "AUTH", "Đồng bộ thành công danh sách nhân sự FPT Software."),
                AuditLogEntity("log_3", "08:30:00", "ADMIN", "Kích hoạt chính sách chống No-Show: Khóa sau 3 lần vắng mặt (UC12).")
            )
            auditLogDao.insertLogs(initialLogs)

            // Seed 5 Time slots
            val slots = listOf(
                TimeSlotEntity(1, "08:00", "10:00", "Slot 1"),
                TimeSlotEntity(2, "10:00", "12:00", "Slot 2"),
                TimeSlotEntity(3, "13:00", "15:00", "Slot 3"),
                TimeSlotEntity(4, "15:00", "17:00", "Slot 4"),
                TimeSlotEntity(5, "18:00", "20:00", "Slot 5")
            )
            timeSlotDao.insertTimeSlots(slots)

            // Seed 15 Workspaces
            val spaces = listOf(
                ResourceEntity(
                    id = "res_1",
                    name = "Lovelace Room",
                    type = ResourceType.ROOM,
                    capacity = 12,
                    building = "F-Town 1",
                    floor = "Floor 2",
                    status = ResourceStatus.AVAILABLE,
                    description = "High-end video conference meeting room equipped with dual screens, sound system, and whiteboards.",
                    amenities = listOf("Projector", "Video Conference", "Whiteboard", "High-speed Wi-Fi")
                ),
                ResourceEntity(
                    id = "res_2",
                    name = "Turing Room",
                    type = ResourceType.ROOM,
                    capacity = 8,
                    building = "F-Town 1",
                    floor = "Floor 3",
                    status = ResourceStatus.BOOKED,
                    description = "Medium-sized meeting room suitable for sprint planning and collaborative work.",
                    amenities = listOf("TV Screen", "Whiteboard", "High-speed Wi-Fi")
                ),
                ResourceEntity(
                    id = "res_3",
                    name = "Hopper Room",
                    type = ResourceType.ROOM,
                    capacity = 6,
                    building = "F-Town 2",
                    floor = "Floor 1",
                    status = ResourceStatus.AVAILABLE,
                    description = "Cozy huddle room perfect for quick syncs and brainstorming sessions.",
                    amenities = listOf("Whiteboard", "TV Screen")
                ),
                ResourceEntity(
                    id = "res_4",
                    name = "Gosling Boardroom",
                    type = ResourceType.ROOM,
                    capacity = 25,
                    building = "F-Town 3",
                    floor = "Floor 5",
                    status = ResourceStatus.AVAILABLE,
                    description = "Executive boardroom with premium seating, advanced audio-visual routing, and spectacular views.",
                    amenities = listOf("Projector", "Video Conference", "Sound System", "Whiteboard", "Coffee Machine")
                ),
                ResourceEntity(
                    id = "res_5",
                    name = "Knuth Room",
                    type = ResourceType.ROOM,
                    capacity = 4,
                    building = "F-Town 3",
                    floor = "Floor 2",
                    status = ResourceStatus.MAINTENANCE,
                    description = "Small interview and consultation room. Currently undergoing network maintenance.",
                    amenities = listOf("Whiteboard", "High-speed Wi-Fi")
                ),
                ResourceEntity(
                    id = "res_6",
                    name = "Hot Desk A-101",
                    type = ResourceType.DESK,
                    capacity = 1,
                    building = "F-Town 1",
                    floor = "Floor 1",
                    status = ResourceStatus.AVAILABLE,
                    description = "Standard hot desk with ergonomic chair, USB charging hub, and a 27-inch external monitor.",
                    amenities = listOf("Ergonomic Chair", "External Monitor", "USB-C Hub")
                ),
                ResourceEntity(
                    id = "res_7",
                    name = "Hot Desk A-102",
                    type = ResourceType.DESK,
                    capacity = 1,
                    building = "F-Town 1",
                    floor = "Floor 1",
                    status = ResourceStatus.AVAILABLE,
                    description = "Standard hot desk next to window. Ergonomic layout.",
                    amenities = listOf("Ergonomic Chair", "External Monitor", "USB-C Hub")
                ),
                ResourceEntity(
                    id = "res_8",
                    name = "Hot Desk B-201",
                    type = ResourceType.DESK,
                    capacity = 1,
                    building = "F-Town 2",
                    floor = "Floor 2",
                    status = ResourceStatus.BOOKED,
                    description = "Premium workspace desk located in a quiet developer zone.",
                    amenities = listOf("Ergonomic Chair", "External Monitor", "Mechanical Keyboard")
                ),
                ResourceEntity(
                    id = "res_9",
                    name = "Hot Desk C-301",
                    type = ResourceType.DESK,
                    capacity = 1,
                    building = "F-Town 3",
                    floor = "Floor 3",
                    status = ResourceStatus.AVAILABLE,
                    description = "Dual-monitor developer station with height-adjustable standing desk.",
                    amenities = listOf("Standing Desk", "Dual Monitors", "USB-C Hub")
                ),
                ResourceEntity(
                    id = "res_10",
                    name = "Ritchie Lab",
                    type = ResourceType.ROOM,
                    capacity = 15,
                    building = "F-Town 3",
                    floor = "Floor 4",
                    status = ResourceStatus.AVAILABLE,
                    description = "Hardware validation testing laboratory with professional equipment and isolated power rings.",
                    amenities = listOf("AC Isolated Rings", "Whiteboard", "High-speed Wi-Fi", "Soldering Ventilation")
                ),
                ResourceEntity(
                    id = "res_11",
                    name = "Dijkstra Auditoria",
                    type = ResourceType.ROOM,
                    capacity = 50,
                    building = "F-Town 2",
                    floor = "Floor 5",
                    status = ResourceStatus.AVAILABLE,
                    description = "Spacious training hall with visual stage routing, dual 4K projectors, and surround sound.",
                    amenities = listOf("Dual Projector", "Stage Sound System", "Whiteboard", "Capacity Seating")
                ),
                ResourceEntity(
                    id = "res_12",
                    name = "Phone Booth 1A",
                    type = ResourceType.DESK,
                    capacity = 1,
                    building = "F-Town 1",
                    floor = "Floor 2",
                    status = ResourceStatus.AVAILABLE,
                    description = "Sound-insulated telephone booth for confidential business calls and private virtual meetings.",
                    amenities = listOf("Acoustic Foam", "Desk Lamp", "Power Outlet", "High-speed Wi-Fi")
                ),
                ResourceEntity(
                    id = "res_13",
                    name = "Phone Booth 1B",
                    type = ResourceType.DESK,
                    capacity = 1,
                    building = "F-Town 1",
                    floor = "Floor 2",
                    status = ResourceStatus.BOOKED,
                    description = "Sound-insulated booth next to the coffee station. Quiet zone.",
                    amenities = listOf("Acoustic Foam", "Desk Lamp", "Power Outlet")
                ),
                ResourceEntity(
                    id = "res_14",
                    name = "Team Space Alpha",
                    type = ResourceType.ROOM,
                    capacity = 10,
                    building = "F-Town 2",
                    floor = "Floor 3",
                    status = ResourceStatus.AVAILABLE,
                    description = "Dedicated scrum room with smart display interfaces, post-it panels, and rolling whiteboards.",
                    amenities = listOf("Smart Display", "Whiteboard", "High-speed Wi-Fi")
                ),
                ResourceEntity(
                    id = "res_15",
                    name = "Silent Space S-01",
                    type = ResourceType.DESK,
                    capacity = 1,
                    building = "F-Town 1",
                    floor = "Floor 3",
                    status = ResourceStatus.AVAILABLE,
                    description = "High-concentration silent zone desk featuring acoustic dividers, desk lamp, and ergonomic seating.",
                    amenities = listOf("Noise Partition", "Ergonomic Chair", "Desk Lamp")
                )
            )
            resourceDao.insertResources(spaces)

            // Seed Initial Rich Bookings
            val initialBookings = listOf(
                BookingEntity(
                    id = "b_1001",
                    userId = "user_admin",
                    username = "admin",
                    resourceId = "res_4",
                    timeSlotId = 1,
                    date = "2026-10-20",
                    status = BookingStatus.CHECKED_IN,
                    purpose = "Họp chiến lược ban giám đốc quý 4",
                    checkedInAt = "08:05:12",
                    createdAt = "2026-10-19 09:00:00"
                ),
                BookingEntity(
                    id = "b_1002",
                    userId = "user_namtv",
                    username = "employee",
                    resourceId = "res_1",
                    timeSlotId = 2,
                    date = "2026-10-20",
                    status = BookingStatus.CONFIRMED,
                    purpose = "Daily standup và demo sprint FPT Cloud",
                    createdAt = "2026-10-19 14:20:00"
                ),
                BookingEntity(
                    id = "b_1003",
                    userId = "user_ducbt",
                    username = "ducbt",
                    resourceId = "res_2",
                    timeSlotId = 1,
                    date = "2026-10-20",
                    status = BookingStatus.NO_SHOW,
                    purpose = "Phỏng vấn ứng viên Senior AI Engineer",
                    createdAt = "2026-10-18 10:15:00"
                ),
                BookingEntity(
                    id = "b_1004",
                    userId = "user_maitt",
                    username = "mai.tt",
                    resourceId = "res_6",
                    timeSlotId = 3,
                    date = "2026-10-20",
                    status = BookingStatus.CONFIRMED,
                    purpose = "Làm việc tập trung dự án Banking FPT",
                    createdAt = "2026-10-19 16:45:00"
                ),
                BookingEntity(
                    id = "b_1005",
                    userId = "user_namtv",
                    username = "employee",
                    resourceId = "res_7",
                    timeSlotId = 4,
                    date = "2026-10-20",
                    status = BookingStatus.CONFIRMED,
                    purpose = "Code review trực tiếp cùng Tech Lead",
                    createdAt = "2026-10-20 07:30:00"
                ),
                BookingEntity(
                    id = "b_1006",
                    userId = "user_admin",
                    username = "admin",
                    resourceId = "res_11",
                    timeSlotId = 3,
                    date = "2026-10-21",
                    status = BookingStatus.CONFIRMED,
                    purpose = "Townhall toàn thể đơn vị FPT Software HCM",
                    createdAt = "2026-10-15 11:00:00"
                ),
                BookingEntity(
                    id = "b_1007",
                    userId = "user_namtv",
                    username = "employee",
                    resourceId = "res_10",
                    timeSlotId = 2,
                    date = "2026-10-21",
                    status = BookingStatus.CONFIRMED,
                    purpose = "Kiểm thử bo mạch IoT trên giàn test lab F-Town 3",
                    createdAt = "2026-10-19 15:10:00"
                ),
                BookingEntity(
                    id = "b_1008",
                    userId = "user_ducbt",
                    username = "ducbt",
                    resourceId = "res_3",
                    timeSlotId = 4,
                    date = "2026-10-19",
                    status = BookingStatus.NO_SHOW,
                    purpose = "Sync nhanh tiến độ microservices",
                    createdAt = "2026-10-18 17:00:00"
                ),
                BookingEntity(
                    id = "b_1009",
                    userId = "user_maitt",
                    username = "mai.tt",
                    resourceId = "res_12",
                    timeSlotId = 1,
                    date = "2026-10-21",
                    status = BookingStatus.CONFIRMED,
                    purpose = "Cuộc gọi phỏng vấn khách hàng đối tác Nhật Bản",
                    createdAt = "2026-10-20 08:00:00"
                ),
                BookingEntity(
                    id = "b_1010",
                    userId = "user_namtv",
                    username = "employee",
                    resourceId = "res_14",
                    timeSlotId = 5,
                    date = "2026-10-20",
                    status = BookingStatus.CONFIRMED,
                    purpose = "Retro cuối tuần BU2",
                    createdAt = "2026-10-20 09:15:00"
                ),
                BookingEntity(
                    id = "b_1011",
                    userId = "user_admin",
                    username = "admin",
                    resourceId = "res_2",
                    timeSlotId = 2,
                    date = "2026-10-19",
                    status = BookingStatus.CANCELLED,
                    purpose = "Họp nhà cung cấp phần cứng",
                    createdAt = "2026-10-18 08:30:00"
                )
            )
            bookingDao.insertBookings(initialBookings)
        }
    }
}
