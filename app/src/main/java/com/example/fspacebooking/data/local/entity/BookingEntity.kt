package com.example.fspacebooking.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.fspacebooking.domain.model.BookingStatus

@Entity(
    tableName = "bookings",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = ResourceEntity::class,
            parentColumns = ["id"],
            childColumns = ["resourceId"],
            onDelete = ForeignKey.RESTRICT
        ),
        ForeignKey(
            entity = TimeSlotEntity::class,
            parentColumns = ["id"],
            childColumns = ["timeSlotId"],
            onDelete = ForeignKey.RESTRICT
        )
    ],
    indices = [
        Index(value = ["resourceId", "date", "timeSlotId"]),
        Index(value = ["resourceId"]),
        Index(value = ["timeSlotId"]),
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
