package com.example.fspacebooking.domain.repository

import com.example.fspacebooking.domain.model.User
import com.example.fspacebooking.utils.Resource
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    suspend fun login(username: String, password: String): Resource<User>
    suspend fun logout(): Resource<Unit>
    fun getSessionUser(): Flow<User?>
    suspend fun saveSessionUser(user: User)
    suspend fun clearSession()

    // Admin Account & Violations Management
    fun getViolators(): Flow<List<User>>
    suspend fun unlockUser(userId: String): Resource<Unit>
    suspend fun lockUser(userId: String): Resource<Unit>
    suspend fun sendWarningEmail(userId: String): Resource<Unit>
}
