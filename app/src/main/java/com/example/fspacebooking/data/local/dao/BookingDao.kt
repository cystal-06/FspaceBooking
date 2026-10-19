package com.example.fspacebooking.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.fspacebooking.data.local.entity.BookingEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BookingDao {
    @Query("SELECT * FROM bookings")
    fun getAllBookings(): Flow<List<BookingEntity>>

    @Query("SELECT * FROM bookings WHERE userId = :userId ORDER BY date DESC, timeSlotId ASC")
    fun getBookingsByUserId(userId: String): Flow<List<BookingEntity>>

    @Query("SELECT * FROM bookings WHERE date = :date")
    fun getBookingsByDate(date: String): Flow<List<BookingEntity>>

    @Query("SELECT * FROM bookings WHERE resourceId = :resourceId AND date = :date AND timeSlotId = :timeSlotId AND status != 'CANCELLED' LIMIT 1")
    fun findConflictingBooking(resourceId: String, date: String, timeSlotId: Int): BookingEntity?

    @Query("SELECT * FROM bookings WHERE id = :id LIMIT 1")
    fun getBookingById(id: String): BookingEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertBooking(booking: BookingEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertBookings(bookings: List<BookingEntity>): List<Long>

    @Update
    fun updateBooking(booking: BookingEntity): Int

    @Query("UPDATE bookings SET status = :status, checkedInAt = :checkedInAt WHERE id = :id")
    fun checkInBooking(id: String, status: com.example.fspacebooking.domain.model.BookingStatus, checkedInAt: String): Int

    @Query("UPDATE bookings SET status = :status WHERE id = :id")
    fun updateBookingStatus(id: String, status: com.example.fspacebooking.domain.model.BookingStatus): Int

    @Query("DELETE FROM bookings WHERE id = :id")
    fun deleteBookingById(id: String): Int
}
