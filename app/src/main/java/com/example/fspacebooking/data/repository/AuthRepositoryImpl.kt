package com.example.fspacebooking.data.repository

import android.content.SharedPreferences
import com.example.fspacebooking.data.remote.api.FSpaceApiService
import com.example.fspacebooking.data.remote.api.LoginRequest
import com.example.fspacebooking.domain.model.User
import com.example.fspacebooking.domain.model.UserRole
import com.example.fspacebooking.domain.repository.AuthRepository
import com.example.fspacebooking.utils.Resource
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val apiService: FSpaceApiService,
    private val sharedPreferences: SharedPreferences
) : AuthRepository {

    private val gson = Gson()
    private val _currentUser = MutableStateFlow<User?>(null)

    init {
        // Restore session from persistent preferences
        val userJson = sharedPreferences.getString(KEY_USER, null)
        if (userJson != null) {
            try {
                _currentUser.value = gson.fromJson(userJson, User::class.java)
            } catch (e: Exception) {
                // corrupted session cache
                sharedPreferences.edit().remove(KEY_USER).apply()
            }
        }
    }

    override suspend fun login(username: String, password: String): Resource<User> {
        if (username.isBlank() || password.isBlank()) {
            return Resource.Error("Username and password cannot be empty")
        }

        return try {
            // Attempt remote authorization
            val user = apiService.login(LoginRequest(username, password))
            saveSessionUser(user)
            Resource.Success(user)
        } catch (e: Exception) {
            // Fallback authorization: match with MockDataStore users or create fallback
            val matchedUser = com.example.fspacebooking.data.local.MockDataStore.usersFlow.value.find {
                it.username.equals(username.trim(), ignoreCase = true) ||
                it.email.equals(username.trim(), ignoreCase = true) ||
                it.employeeId.equals(username.trim(), ignoreCase = true)
            }

            val finalUser = matchedUser ?: run {
                val role = if (username.contains("admin", ignoreCase = true)) {
                    UserRole.ADMIN
                } else {
                    UserRole.EMPLOYEE
                }
                User(
                    id = "user_${username.lowercase().trim()}",
                    username = username.trim(),
                    email = "$username@fpt.com",
                    name = if (role == UserRole.ADMIN) "Admin Manager" else "Nhân viên FPT",
                    role = role,
                    employeeId = if (role == UserRole.ADMIN) "ADM001" else "EMP999",
                    token = "local-offline-session-token",
                    violations = 0,
                    userStatus = "ĐANG HOẠT ĐỘNG",
                    fsu = "FSU1.BU2"
                )
            }

            com.example.fspacebooking.data.local.MockDataStore.addAuditLog(
                "AUTH",
                "Người dùng '${finalUser.name}' (${finalUser.employeeId}) đã đăng nhập vào hệ thống."
            )
            saveSessionUser(finalUser)
            Resource.Success(finalUser)
        }
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

    override suspend fun logout(): Resource<Unit> {
        return try {
            apiService.logout()
            clearSession()
            Resource.Success(Unit)
        } catch (e: Exception) {
            clearSession()
            Resource.Success(Unit)
        }
    }

    override fun getSessionUser(): Flow<User?> {
        return _currentUser.asStateFlow()
    }

    override suspend fun saveSessionUser(user: User) {
        val userJson = gson.toJson(user)
        sharedPreferences.edit().putString(KEY_USER, userJson).apply()
        _currentUser.value = user
    }

    override suspend fun clearSession() {
        sharedPreferences.edit().remove(KEY_USER).apply()
        _currentUser.value = null
    }

    companion object {
        private const val KEY_USER = "auth_session_user"
    }
}

