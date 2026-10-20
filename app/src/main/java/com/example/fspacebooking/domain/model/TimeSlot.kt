package com.example.fspacebooking.domain.model

data class TimeSlot(
    val id: Int,
    val startTime: String,
    val endTime: String,
    val label: String
) {
    companion object {
        val DEFAULT_SLOTS = listOf(
            TimeSlot(1, "08:00", "10:00", "Slot 1 (08:00 - 10:00)"),
            TimeSlot(2, "10:00", "12:00", "Slot 2 (10:00 - 12:00)"),
            TimeSlot(3, "13:00", "15:00", "Slot 3 (13:00 - 15:00)"),
            TimeSlot(4, "15:00", "17:00", "Slot 4 (15:00 - 17:00)"),
            TimeSlot(5, "18:00", "20:00", "Slot 5 (18:00 - 20:00)")
        )
    }
}
