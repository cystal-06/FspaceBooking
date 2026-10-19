package com.example.fspacebooking.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "audit_logs",
    indices = [
        Index(value = ["timestamp"]),
        Index(value = ["category"])
    ]
)
data class AuditLogEntity(
    @PrimaryKey
    val id: String,
    val timestamp: String,
    val category: String, // AUTH, SYS, ADMIN, BOOKING, VIOLATION
    val message: String,
    val actorId: String? = null
)
