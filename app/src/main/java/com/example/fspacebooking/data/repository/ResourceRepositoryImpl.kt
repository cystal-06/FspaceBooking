package com.example.fspacebooking.data.repository

import com.example.fspacebooking.data.local.MockDataStore
import com.example.fspacebooking.data.local.dao.ResourceDao
import com.example.fspacebooking.data.local.entity.ResourceEntity
import com.example.fspacebooking.data.remote.api.FSpaceApiService
import com.example.fspacebooking.domain.model.ResourceType
import com.example.fspacebooking.domain.model.WorkspaceResource
import com.example.fspacebooking.domain.repository.ResourceRepository
import com.example.fspacebooking.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ResourceRepositoryImpl @Inject constructor(
    private val apiService: FSpaceApiService,
    private val resourceDao: ResourceDao
) : ResourceRepository {

    override fun getResources(
        type: ResourceType?,
        building: String?,
        floor: String?,
        capacity: Int?,
        searchQuery: String?
    ): Flow<Resource<List<WorkspaceResource>>> {
        return flow {
            emit(Resource.Loading())

            // 1. Attempt to fetch from real Backend Server (PostgreSQL)
            try {
                val remoteList = apiService.getResources(
                    type = type?.name,
                    building = building,
                    floor = floor,
                    capacity = capacity,
                    search = searchQuery
                )
                if (remoteList.isNotEmpty()) {
                    // Cache to Room DB
                    try {
                        val entities = remoteList.map { ResourceEntity.fromDomain(it) }
                        resourceDao.insertResources(entities)
                    } catch (_: Exception) {}

                    emit(Resource.Success(remoteList))
                    return@flow
                }
            } catch (e: Exception) {
                // Network unavailable or server offline -> fallback to local
            }

            // 2. Offline Fallback from MockDataStore / Room
            MockDataStore.resourcesFlow.map { list ->
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
            }.collect {
                emit(it)
            }
        }
    }

    override suspend fun getResourceById(id: String): Resource<WorkspaceResource> {
        try {
            val remote = apiService.getResourceById(id)
            return Resource.Success(remote)
        } catch (_: Exception) {}

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
        try {
            val created = apiService.addResource(resource)
            return Resource.Success(created)
        } catch (_: Exception) {}
        MockDataStore.addResource(resource)
        return Resource.Success(resource)
    }

    override suspend fun updateResource(resource: WorkspaceResource): Resource<WorkspaceResource> {
        try {
            val updated = apiService.updateResource(resource.id, resource)
            return Resource.Success(updated)
        } catch (_: Exception) {}
        MockDataStore.updateResource(resource)
        return Resource.Success(resource)
    }

    override suspend fun deleteResource(id: String): Resource<Unit> {
        try {
            apiService.deleteResource(id)
            return Resource.Success(Unit)
        } catch (_: Exception) {}
        MockDataStore.deleteResource(id)
        return Resource.Success(Unit)
    }
}
