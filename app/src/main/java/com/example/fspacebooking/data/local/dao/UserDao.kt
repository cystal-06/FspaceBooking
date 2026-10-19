package com.example.fspacebooking.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.fspacebooking.data.local.entity.UserEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM users")
    fun getAllUsers(): Flow<List<UserEntity>>

    @Query("SELECT * FROM users WHERE id = :id LIMIT 1")
    fun getUserById(id: String): UserEntity?

    @Query("SELECT * FROM users WHERE username = :username LIMIT 1")
    fun getUserByUsername(username: String): UserEntity?

    @Query("SELECT * FROM users WHERE username = :username AND password = :password LIMIT 1")
    fun authenticate(username: String, password: String): UserEntity?

    @Query("SELECT * FROM users WHERE violations > 0 OR userStatus != 'ĐANG HOẠT ĐỘNG'")
    fun getViolators(): Flow<List<UserEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertUser(user: UserEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertUsers(users: List<UserEntity>): List<Long>

    @Update
    fun updateUser(user: UserEntity): Int

    @Query("UPDATE users SET userStatus = :status, violations = :violations WHERE id = :userId")
    fun updateUserStatus(userId: String, status: String, violations: Int): Int

    @Query("UPDATE users SET violations = violations + 1, userStatus = CASE WHEN violations + 1 >= 3 THEN 'ĐÃ KHÓA' ELSE 'CẢNH BÁO' END WHERE id = :userId")
    fun incrementViolation(userId: String): Int

    @Query("UPDATE users SET userStatus = 'ĐANG HOẠT ĐỘNG', violations = 0 WHERE id = :userId")
    fun resetViolations(userId: String): Int

    @Query("DELETE FROM users WHERE id = :id")
    fun deleteUserById(id: String): Int
}
