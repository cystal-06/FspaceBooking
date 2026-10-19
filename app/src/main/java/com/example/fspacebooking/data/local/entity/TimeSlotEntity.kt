package com.example.fspacebooking.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.fspacebooking.domain.model.TimeSlot

@Entity(tableName = "timeslots")
data class TimeSlotEntity(
    @PrimaryKey val id: Int,
    val startTime: String,
    val endTime: String,
    val label: String
) {
    fun toDomain(): TimeSlot {
        return TimeSlot(
            id = id,
            startTime = startTime,
            endTime = endTime,
            label = label
        )
    }

    companion object {
        fun fromDomain(domain: TimeSlot): TimeSlotEntity {
            return TimeSlotEntity(
                id = domain.id,
                startTime = domain.startTime,
                endTime = domain.endTime,
                label = domain.label
            )
        }
    }
}
