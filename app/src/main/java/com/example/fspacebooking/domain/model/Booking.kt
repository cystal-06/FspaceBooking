package com.example.fspacebooking.domain.model

enum class BookingStatus {
    CONFIRMED,
    CHECKED_IN,
    CANCELLED,
    NO_SHOW
}

data class Booking(
    val id: String,
    val userId: String,
    val username: String,
    val resource: WorkspaceResource,
    val timeSlot: TimeSlot,
    val date: String,
    val status: BookingStatus,
    val purpose: String = "Họp công việc nội bộ",
    val createdAt: String
)

data class AuditLogEntry(
    val id: String,
    val timestamp: String,
    val category: String, // AUTH, SYS, ADMIN, BOOKING, VIOLATION
    val message: String
)

