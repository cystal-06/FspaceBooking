package com.example.fspacebooking.domain.model

enum class UserRole {
    EMPLOYEE,
    ADMIN
}

data class User(
    val id: String,
    val username: String,
    val email: String,
    val name: String,
    val role: UserRole,
    val employeeId: String,
    val token: String? = null,
    val violations: Int = 0,
    val userStatus: String = "ĐANG HOẠT ĐỘNG", // "ĐANG HOẠT ĐỘNG", "CẢNH BÁO", "ĐÃ KHÓA", "THEO DÕI"
    val fsu: String = "FSU1.BU2"
)

