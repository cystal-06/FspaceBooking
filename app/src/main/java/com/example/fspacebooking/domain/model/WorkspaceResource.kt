package com.example.fspacebooking.domain.model

enum class ResourceType {
    ROOM,
    DESK
}

enum class ResourceStatus {
    AVAILABLE,
    BOOKED,
    MAINTENANCE
}

data class WorkspaceResource(
    val id: String,
    val name: String,
    val type: ResourceType,
    val capacity: Int,
    val building: String,
    val floor: String,
    val status: ResourceStatus,
    val description: String = "",
    val amenities: List<String> = emptyList(),
    val isFavorite: Boolean = false,
    val imageUrl: String = "https://images.unsplash.com/photo-1497366216548-37526070297c?auto=format&fit=crop&w=600&q=80"
)
