package com.example.fspacebooking.data.local.converter

import androidx.room.TypeConverter
import com.example.fspacebooking.domain.model.ResourceType
import com.example.fspacebooking.domain.model.ResourceStatus
import com.example.fspacebooking.domain.model.BookingStatus

class Converters {
    @TypeConverter
    fun fromStringList(value: List<String>): String {
        return value.joinToString(",")
    }

    @TypeConverter
    fun toStringList(value: String): List<String> {
        return if (value.isBlank()) emptyList() else value.split(",")
    }

    @TypeConverter
    fun fromResourceType(type: ResourceType): String {
        return type.name
    }

    @TypeConverter
    fun toResourceType(value: String): ResourceType {
        return ResourceType.valueOf(value)
    }

    @TypeConverter
    fun fromResourceStatus(status: ResourceStatus): String {
        return status.name
    }

    @TypeConverter
    fun toResourceStatus(value: String): ResourceStatus {
        return ResourceStatus.valueOf(value)
    }

    @TypeConverter
    fun fromBookingStatus(status: BookingStatus): String {
        return status.name
    }

    @TypeConverter
    fun toBookingStatus(value: String): BookingStatus {
        return BookingStatus.valueOf(value)
    }
}
