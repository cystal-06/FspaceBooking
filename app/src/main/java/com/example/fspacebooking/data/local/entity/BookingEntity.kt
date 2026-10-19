package com.example.fspacebooking.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.fspacebooking.domain.model.BookingStatus

@Entity(tableName = "bookings")
data class BookingEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val username: String,
    val resourceId: String,
    val timeSlotId: Int,
    val date: String,
    val status: BookingStatus,
    val createdAt: String
)
