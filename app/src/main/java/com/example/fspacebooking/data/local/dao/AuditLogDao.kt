package com.example.fspacebooking.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.fspacebooking.data.local.entity.AuditLogEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AuditLogDao {
    @Query("SELECT * FROM audit_logs ORDER BY timestamp DESC")
    fun getAllLogs(): Flow<List<AuditLogEntity>>

    @Query("SELECT * FROM audit_logs WHERE category = :category ORDER BY timestamp DESC")
    fun getLogsByCategory(category: String): Flow<List<AuditLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertLog(log: AuditLogEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertLogs(logs: List<AuditLogEntity>): List<Long>

    @Query("DELETE FROM audit_logs")
    fun clearAllLogs(): Int
}
