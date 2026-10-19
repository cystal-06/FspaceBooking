package com.example.fspacebooking.domain.repository

import com.example.fspacebooking.domain.model.WorkspaceResource
import com.example.fspacebooking.domain.model.ResourceType
import com.example.fspacebooking.utils.Resource
import kotlinx.coroutines.flow.Flow

interface ResourceRepository {
    fun getResources(
        type: ResourceType?,
        building: String?,
        floor: String?,
        capacity: Int?,
        searchQuery: String?
    ): Flow<Resource<List<WorkspaceResource>>>

    suspend fun getResourceById(id: String): Resource<WorkspaceResource>

    // Real-time status check
    suspend fun checkResourceAvailability(resourceId: String, date: String, timeSlotId: Int): Resource<Boolean>

    // Favorite caching
    fun getFavoriteResources(): Flow<List<WorkspaceResource>>
    suspend fun toggleFavorite(resourceId: String, isFavorite: Boolean)

    // Admin Operations
    suspend fun addResource(resource: WorkspaceResource): Resource<WorkspaceResource>
    suspend fun updateResource(resource: WorkspaceResource): Resource<WorkspaceResource>
    suspend fun deleteResource(id: String): Resource<Unit>
}
