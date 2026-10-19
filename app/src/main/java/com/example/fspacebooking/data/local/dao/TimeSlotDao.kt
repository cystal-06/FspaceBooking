package com.example.fspacebooking.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.fspacebooking.data.local.entity.TimeSlotEntity

@Dao
interface TimeSlotDao {
    @Query("SELECT * FROM timeslots")
    fun getAllTimeSlots(): List<TimeSlotEntity>

    @Query("SELECT * FROM timeslots WHERE id = :id")
    fun getTimeSlotById(id: Int): TimeSlotEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertTimeSlots(slots: List<TimeSlotEntity>): List<Long>
}
