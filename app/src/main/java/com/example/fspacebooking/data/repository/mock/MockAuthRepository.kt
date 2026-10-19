package com.example.fspacebooking.data.repository.mock

import com.example.fspacebooking.domain.model.User
import com.example.fspacebooking.domain.model.UserRole
import com.example.fspacebooking.domain.repository.AuthRepository
import com.example.fspacebooking.utils.Resource
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MockAuthRepository @Inject constructor() : AuthRepository {

    private val _currentUser = MutableStateFlow<User?>(null)

    override suspend fun login(username: String, password: String): Resource<User> {
        delay(1000) // Simulate network delay
        if (username.isBlank() || password.isBlank()) {
            return Resource.Error("Username and password cannot be empty")
        }
        
        val role = if (username.contains("admin", ignoreCase = true)) {
            UserRole.ADMIN
        } else {
            UserRole.EMPLOYEE
        }

        val user = User(
            id = "user_123",
            username = username,
            email = "$username@fpt.com",
            name = if (role == UserRole.ADMIN) "Admin Manager" else "Employee Developer",
            role = role,
            employeeId = if (role == UserRole.ADMIN) "ADM001" else "EMP999",
            token = "fake-jwt-token-12345"
        )
        
        _currentUser.value = user
        return Resource.Success(user)
    }

    override suspend fun logout(): Resource<Unit> {
        delay(500)
        clearSession()
        return Resource.Success(Unit)
    }

    override fun getSessionUser(): Flow<User?> {
        return _currentUser.asStateFlow()
    }

    override suspend fun saveSessionUser(user: User) {
        _currentUser.value = user
    }

    override suspend fun clearSession() {
        _currentUser.value = null
    }

    override fun getViolators(): Flow<List<User>> {
        return com.example.fspacebooking.data.local.MockDataStore.usersFlow
    }

    override suspend fun unlockUser(userId: String): Resource<Unit> {
        com.example.fspacebooking.data.local.MockDataStore.unlockUser(userId)
        return Resource.Success(Unit)
    }

    override suspend fun lockUser(userId: String): Resource<Unit> {
        com.example.fspacebooking.data.local.MockDataStore.lockUser(userId)
        return Resource.Success(Unit)
    }

    override suspend fun sendWarningEmail(userId: String): Resource<Unit> {
        com.example.fspacebooking.data.local.MockDataStore.sendWarningEmail(userId)
        return Resource.Success(Unit)
    }
}
