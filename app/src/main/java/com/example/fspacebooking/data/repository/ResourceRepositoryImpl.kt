package com.example.fspacebooking.data.repository

import com.example.fspacebooking.data.local.MockDataStore
import com.example.fspacebooking.domain.model.ResourceType
import com.example.fspacebooking.domain.model.WorkspaceResource
import com.example.fspacebooking.domain.repository.ResourceRepository
import com.example.fspacebooking.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ResourceRepositoryImpl @Inject constructor() : ResourceRepository {

    override fun getResources(
        type: ResourceType?,
        building: String?,
        floor: String?,
        capacity: Int?,
        searchQuery: String?
    ): Flow<Resource<List<WorkspaceResource>>> {
        return MockDataStore.resourcesFlow.map { list ->
            var domainList = list
            if (type != null) {
                domainList = domainList.filter { it.type == type }
            }
            if (!building.isNullOrBlank()) {
                domainList = domainList.filter { it.building.equals(building, ignoreCase = true) }
            }
            if (!floor.isNullOrBlank()) {
                domainList = domainList.filter { it.floor.equals(floor, ignoreCase = true) }
            }
            if (capacity != null) {
                domainList = domainList.filter { it.capacity >= capacity }
            }
            if (!searchQuery.isNullOrBlank()) {
                domainList = domainList.filter {
                    it.name.contains(searchQuery, ignoreCase = true) ||
                    it.description.contains(searchQuery, ignoreCase = true)
                }
            }
            Resource.Success(domainList)
        }
    }

    override suspend fun getResourceById(id: String): Resource<WorkspaceResource> {
        val resource = MockDataStore.resourcesFlow.value.find { it.id == id }
        return if (resource != null) {
            Resource.Success(resource)
        } else {
            Resource.Error("Không tìm thấy thông tin phòng họp")
        }
    }

    override suspend fun checkResourceAvailability(
        resourceId: String,
        date: String,
        timeSlotId: Int
    ): Resource<Boolean> {
        val hasConflict = MockDataStore.bookingsFlow.value.any {
            it.resource.id == resourceId &&
            it.date == date &&
            it.timeSlot.id == timeSlotId &&
            it.status == com.example.fspacebooking.domain.model.BookingStatus.CONFIRMED
        }
        return Resource.Success(!hasConflict)
    }

    override fun getFavoriteResources(): Flow<List<WorkspaceResource>> {
        return MockDataStore.resourcesFlow.map { list ->
            list.filter { it.isFavorite }
        }
    }

    override suspend fun toggleFavorite(resourceId: String, isFavorite: Boolean) {
        MockDataStore.toggleFavorite(resourceId, isFavorite)
    }

    override suspend fun addResource(resource: WorkspaceResource): Resource<WorkspaceResource> {
        MockDataStore.addResource(resource)
        return Resource.Success(resource)
    }

    override suspend fun updateResource(resource: WorkspaceResource): Resource<WorkspaceResource> {
        MockDataStore.updateResource(resource)
        return Resource.Success(resource)
    }

    override suspend fun deleteResource(id: String): Resource<Unit> {
        MockDataStore.deleteResource(id)
        return Resource.Success(Unit)
    }
}
