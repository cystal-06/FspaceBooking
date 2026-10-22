package com.example.fspacebooking.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "audit_logs",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["actorId"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [
        Index(value = ["timestamp"]),
        Index(value = ["category"]),
        Index(value = ["actorId"])
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
