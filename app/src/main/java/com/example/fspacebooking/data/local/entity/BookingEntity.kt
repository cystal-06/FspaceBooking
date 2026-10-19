package com.example.fspacebooking.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.fspacebooking.domain.model.BookingStatus

@Entity(
    tableName = "bookings",
    indices = [
        Index(value = ["resourceId", "date", "timeSlotId"]),
        Index(value = ["userId"]),
        Index(value = ["date"]),
        Index(value = ["status"])
    ]
)
data class BookingEntity(
    @PrimaryKey
    val id: String,
    val userId: String,
    val username: String,
    val resourceId: String,
    val timeSlotId: Int,
    val date: String,
    val status: BookingStatus,
    val purpose: String = "Họp công việc nội bộ",
    val qrCode: String = "",
    val checkedInAt: String? = null,
    val createdAt: String
)
