package com.example.fspacebooking.data.repository

import com.example.fspacebooking.data.local.dao.BookingDao
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
    private val resourceDao: ResourceDao,
    private val bookingDao: BookingDao
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

            // 2. Offline Fallback from SQLite Room DB
            resourceDao.getAllResources().map { entities ->
                var domainList = entities.map { it.toDomain() }
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

        val entity = resourceDao.getResourceById(id)
        return if (entity != null) {
            Resource.Success(entity.toDomain())
        } else {
            Resource.Error("Không tìm thấy thông tin phòng họp")
        }
    }

    override suspend fun checkResourceAvailability(
        resourceId: String,
        date: String,
        timeSlotId: Int
    ): Resource<Boolean> {
        val conflict = bookingDao.findConflictingBooking(resourceId, date, timeSlotId)
        return Resource.Success(conflict == null)
    }

    override fun getFavoriteResources(): Flow<List<WorkspaceResource>> {
        return resourceDao.getFavoriteResources().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun toggleFavorite(resourceId: String, isFavorite: Boolean) {
        val entity = resourceDao.getResourceById(resourceId)
        if (entity != null) {
            resourceDao.updateResource(entity.copy(isFavorite = isFavorite))
        }
    }

    override suspend fun addResource(resource: WorkspaceResource): Resource<WorkspaceResource> {
        try {
            val created = apiService.addResource(resource)
            resourceDao.insertResource(ResourceEntity.fromDomain(created))
            return Resource.Success(created)
        } catch (_: Exception) {}
        resourceDao.insertResource(ResourceEntity.fromDomain(resource))
        return Resource.Success(resource)
    }

    override suspend fun updateResource(resource: WorkspaceResource): Resource<WorkspaceResource> {
        try {
            val updated = apiService.updateResource(resource.id, resource)
            resourceDao.updateResource(ResourceEntity.fromDomain(updated))
            return Resource.Success(updated)
        } catch (_: Exception) {}
        resourceDao.updateResource(ResourceEntity.fromDomain(resource))
        return Resource.Success(resource)
    }

    override suspend fun deleteResource(id: String): Resource<Unit> {
        try {
            apiService.deleteResource(id)
        } catch (_: Exception) {}
        resourceDao.deleteResourceById(id)
        return Resource.Success(Unit)
    }
}
