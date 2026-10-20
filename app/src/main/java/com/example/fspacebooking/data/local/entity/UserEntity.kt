package com.example.fspacebooking.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.fspacebooking.domain.model.User
import com.example.fspacebooking.domain.model.UserRole

@Entity(
    tableName = "users",
    indices = [
        Index(value = ["username"], unique = true),
        Index(value = ["employeeId"], unique = true)
    ]
)
data class UserEntity(
    @PrimaryKey
    val id: String,
    val username: String,
    val password: String = "123456",
    val email: String,
    val name: String,
    val role: UserRole,
    val employeeId: String,
    val token: String? = null,
    val violations: Int = 0,
    val userStatus: String = "ĐANG HOẠT ĐỘNG", // "ĐANG HOẠT ĐỘNG", "CẢNH BÁO", "ĐÃ KHÓA"
    val fsu: String = "FSU1.BU2",
    val createdAt: String = System.currentTimeMillis().toString()
) {
    fun toDomain(): User = User(
        id = id,
        username = username,
        email = email,
        name = name,
        role = role,
        employeeId = employeeId,
        token = token ?: "session-token",
        violations = violations,
        userStatus = userStatus,
        fsu = fsu
    )
}
