package com.example.fspacebooking.data.repository

import com.example.fspacebooking.data.local.dao.BookingDao
import com.example.fspacebooking.data.local.dao.ResourceDao
import com.example.fspacebooking.data.local.entity.ResourceEntity
import com.example.fspacebooking.data.remote.api.FSpaceApiService
import com.example.fspacebooking.domain.model.ResourceType
import com.example.fspacebooking.domain.model.WorkspaceResource
import com.example.fspacebooking.domain.repository.ResourceRepository
import com.example.fspacebooking.utils.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
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
        }.flowOn(Dispatchers.IO)
    }

    override suspend fun getResourceById(id: String): Resource<WorkspaceResource> = withContext(Dispatchers.IO) {
        try {
            val remote = apiService.getResourceById(id)
            return@withContext Resource.Success(remote)
        } catch (_: Exception) {}

        val entity = resourceDao.getResourceById(id)
        if (entity != null) {
            Resource.Success(entity.toDomain())
        } else {
            Resource.Error("Không tìm thấy thông tin phòng họp")
        }
    }

    override suspend fun checkResourceAvailability(
        resourceId: String,
        date: String,
        timeSlotId: Int
    ): Resource<Boolean> = withContext(Dispatchers.IO) {
        val conflict = bookingDao.findConflictingBooking(resourceId, date, timeSlotId)
        Resource.Success(conflict == null)
    }

    override fun getFavoriteResources(): Flow<List<WorkspaceResource>> {
        return resourceDao.getFavoriteResources().map { entities ->
            entities.map { it.toDomain() }
        }.flowOn(Dispatchers.IO)
    }

    override suspend fun toggleFavorite(resourceId: String, isFavorite: Boolean) = withContext(Dispatchers.IO) {
        val entity = resourceDao.getResourceById(resourceId)
        if (entity != null) {
            resourceDao.updateResource(entity.copy(isFavorite = isFavorite))
        }
    }

    override suspend fun addResource(resource: WorkspaceResource): Resource<WorkspaceResource> = withContext(Dispatchers.IO) {
        try {
            val created = apiService.addResource(resource)
            resourceDao.insertResource(ResourceEntity.fromDomain(created))
            return@withContext Resource.Success(created)
        } catch (_: Exception) {}
        resourceDao.insertResource(ResourceEntity.fromDomain(resource))
        Resource.Success(resource)
    }

    override suspend fun updateResource(resource: WorkspaceResource): Resource<WorkspaceResource> = withContext(Dispatchers.IO) {
        try {
            val updated = apiService.updateResource(resource.id, resource)
            resourceDao.updateResource(ResourceEntity.fromDomain(updated))
            return@withContext Resource.Success(updated)
        } catch (_: Exception) {}
        resourceDao.updateResource(ResourceEntity.fromDomain(resource))
        Resource.Success(resource)
    }

    override suspend fun deleteResource(id: String): Resource<Unit> = withContext(Dispatchers.IO) {
        try {
            apiService.deleteResource(id)
        } catch (_: Exception) {}
        resourceDao.deleteResourceById(id)
        Resource.Success(Unit)
    }
}
