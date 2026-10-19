package com.example.fspacebooking.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.fspacebooking.domain.model.ResourceType
import com.example.fspacebooking.domain.model.ResourceStatus
import com.example.fspacebooking.domain.model.WorkspaceResource

@Entity(tableName = "resources")
data class ResourceEntity(
    @PrimaryKey val id: String,
    val name: String,
    val type: ResourceType,
    val capacity: Int,
    val building: String,
    val floor: String,
    val status: ResourceStatus,
    val description: String,
    val amenities: List<String>,
    val isFavorite: Boolean = false
) {
    fun toDomain(): WorkspaceResource {
        return WorkspaceResource(
            id = id,
            name = name,
            type = type,
            capacity = capacity,
            building = building,
            floor = floor,
            status = status,
            description = description,
            amenities = amenities,
            isFavorite = isFavorite
        )
    }

    companion object {
        fun fromDomain(domain: WorkspaceResource): ResourceEntity {
            return ResourceEntity(
                id = domain.id,
                name = domain.name,
                type = domain.type,
                capacity = domain.capacity,
                building = domain.building,
                floor = domain.floor,
                status = domain.status,
                description = domain.description,
                amenities = domain.amenities,
                isFavorite = domain.isFavorite
            )
        }
    }
}
