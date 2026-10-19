package com.example.fspacebooking.data.repository.mock

import com.example.fspacebooking.domain.model.ResourceStatus
import com.example.fspacebooking.domain.model.ResourceType
import com.example.fspacebooking.domain.model.WorkspaceResource
import com.example.fspacebooking.domain.repository.ResourceRepository
import com.example.fspacebooking.utils.Resource
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MockResourceRepository @Inject constructor() : ResourceRepository {

    private val resourcesList = mutableListOf(
        WorkspaceResource(
            id = "res_1",
            name = "Lovelace Room",
            type = ResourceType.ROOM,
            capacity = 12,
            building = "F-Town 1",
            floor = "Floor 2",
            status = ResourceStatus.AVAILABLE,
            description = "High-end video conference meeting room equipped with dual screens, sound system, and whiteboards.",
            amenities = listOf("Projector", "Video Conference", "Whiteboard", "High-speed Wi-Fi")
        ),
        WorkspaceResource(
            id = "res_2",
            name = "Turing Room",
            type = ResourceType.ROOM,
            capacity = 8,
            building = "F-Town 1",
            floor = "Floor 3",
            status = ResourceStatus.BOOKED,
            description = "Medium-sized meeting room suitable for sprint planning and collaborative work.",
            amenities = listOf("TV Screen", "Whiteboard", "High-speed Wi-Fi")
        ),
        WorkspaceResource(
            id = "res_3",
            name = "Hopper Room",
            type = ResourceType.ROOM,
            capacity = 6,
            building = "F-Town 2",
            floor = "Floor 1",
            status = ResourceStatus.AVAILABLE,
            description = "Cozy huddle room perfect for quick syncs and brainstorming sessions.",
            amenities = listOf("Whiteboard", "TV Screen")
        ),
        WorkspaceResource(
            id = "res_4",
            name = "Gosling Boardroom",
            type = ResourceType.ROOM,
            capacity = 25,
            building = "F-Town 3",
            floor = "Floor 5",
            status = ResourceStatus.AVAILABLE,
            description = "Executive boardroom with premium seating, advanced audio-visual routing, and spectacular views.",
            amenities = listOf("Projector", "Video Conference", "Sound System", "Whiteboard", "Coffee Machine")
        ),
        WorkspaceResource(
            id = "res_5",
            name = "Knuth Room",
            type = ResourceType.ROOM,
            capacity = 4,
            building = "F-Town 3",
            floor = "Floor 2",
            status = ResourceStatus.MAINTENANCE,
            description = "Small interview and consultation room. Currently undergoing network maintenance.",
            amenities = listOf("Whiteboard", "High-speed Wi-Fi")
        ),
        WorkspaceResource(
            id = "res_6",
            name = "Hot Desk A-101",
            type = ResourceType.DESK,
            capacity = 1,
            building = "F-Town 1",
            floor = "Floor 1",
            status = ResourceStatus.AVAILABLE,
            description = "Standard hot desk with ergonomic chair, USB charging hub, and a 27-inch external monitor.",
            amenities = listOf("Ergonomic Chair", "External Monitor", "USB-C Hub")
        ),
        WorkspaceResource(
            id = "res_7",
            name = "Hot Desk A-102",
            type = ResourceType.DESK,
            capacity = 1,
            building = "F-Town 1",
            floor = "Floor 1",
            status = ResourceStatus.AVAILABLE,
            description = "Standard hot desk next to window. Ergonomic layout.",
            amenities = listOf("Ergonomic Chair", "External Monitor", "USB-C Hub")
        ),
        WorkspaceResource(
            id = "res_8",
            name = "Hot Desk B-201",
            type = ResourceType.DESK,
            capacity = 1,
            building = "F-Town 2",
            floor = "Floor 2",
            status = ResourceStatus.BOOKED,
            description = "Premium workspace desk located in a quiet developer zone.",
            amenities = listOf("Ergonomic Chair", "External Monitor", "Mechanical Keyboard")
        ),
        WorkspaceResource(
            id = "res_9",
            name = "Hot Desk C-301",
            type = ResourceType.DESK,
            capacity = 1,
            building = "F-Town 3",
            floor = "Floor 3",
            status = ResourceStatus.AVAILABLE,
            description = "Dual-monitor developer station with height-adjustable standing desk.",
            amenities = listOf("Standing Desk", "Dual Monitors", "USB-C Hub")
        ),
        WorkspaceResource(
            id = "res_10",
            name = "Ritchie Lab",
            type = ResourceType.ROOM,
            capacity = 15,
            building = "F-Town 3",
            floor = "Floor 4",
            status = ResourceStatus.AVAILABLE,
            description = "Hardware validation testing laboratory with professional equipment and isolated power rings.",
            amenities = listOf("AC Isolated Rings", "Whiteboard", "High-speed Wi-Fi", "Soldering Ventilation")
        ),
        WorkspaceResource(
            id = "res_11",
            name = "Dijkstra Auditoria",
            type = ResourceType.ROOM,
            capacity = 50,
            building = "F-Town 2",
            floor = "Floor 5",
            status = ResourceStatus.AVAILABLE,
            description = "Spacious training hall with visual stage routing, dual 4K projectors, and surround sound.",
            amenities = listOf("Dual Projector", "Stage Sound System", "Whiteboard", "Capacity Seating")
        ),
        WorkspaceResource(
            id = "res_12",
            name = "Phone Booth 1A",
            type = ResourceType.DESK,
            capacity = 1,
            building = "F-Town 1",
            floor = "Floor 2",
            status = ResourceStatus.AVAILABLE,
            description = "Sound-insulated telephone booth for confidential business calls and private virtual meetings.",
            amenities = listOf("Acoustic Foam", "Desk Lamp", "Power Outlet", "High-speed Wi-Fi")
        ),
        WorkspaceResource(
            id = "res_13",
            name = "Phone Booth 1B",
            type = ResourceType.DESK,
            capacity = 1,
            building = "F-Town 1",
            floor = "Floor 2",
            status = ResourceStatus.BOOKED,
            description = "Sound-insulated booth next to the coffee station. Quiet zone.",
            amenities = listOf("Acoustic Foam", "Desk Lamp", "Power Outlet")
        ),
        WorkspaceResource(
            id = "res_14",
            name = "Team Space Alpha",
            type = ResourceType.ROOM,
            capacity = 10,
            building = "F-Town 2",
            floor = "Floor 3",
            status = ResourceStatus.AVAILABLE,
            description = "Dedicated scrum room with smart display interfaces, post-it panels, and rolling whiteboards.",
            amenities = listOf("Smart Display", "Whiteboard", "High-speed Wi-Fi")
        ),
        WorkspaceResource(
            id = "res_15",
            name = "Silent Space S-01",
            type = ResourceType.DESK,
            capacity = 1,
            building = "F-Town 1",
            floor = "Floor 3",
            status = ResourceStatus.AVAILABLE,
            description = "High-concentration silent zone desk featuring acoustic dividers, desk lamp, and ergonomic seating.",
            amenities = listOf("Noise Partition", "Ergonomic Chair", "Desk Lamp")
        )
    )

    private val _resources = MutableStateFlow<List<WorkspaceResource>>(resourcesList)
    private val favoriteIds = MutableStateFlow<Set<String>>(setOf("res_1", "res_6"))

    override fun getResources(
        type: ResourceType?,
        building: String?,
        floor: String?,
        capacity: Int?,
        searchQuery: String?
    ): Flow<Resource<List<WorkspaceResource>>> {
        return _resources.asStateFlow().map { list ->
            var filtered = list
            if (type != null) {
                filtered = filtered.filter { it.type == type }
            }
            if (!building.isNullOrBlank()) {
                filtered = filtered.filter { it.building.equals(building, ignoreCase = true) }
            }
            if (!floor.isNullOrBlank()) {
                filtered = filtered.filter { it.floor.equals(floor, ignoreCase = true) }
            }
            if (capacity != null) {
                filtered = filtered.filter { it.capacity >= capacity }
            }
            if (!searchQuery.isNullOrBlank()) {
                filtered = filtered.filter {
                    it.name.contains(searchQuery, ignoreCase = true) ||
                    it.description.contains(searchQuery, ignoreCase = true)
                }
            }
            // Map favorites state
            val favorites = favoriteIds.value
            val mapped = filtered.map { it.copy(isFavorite = favorites.contains(it.id)) }
            Resource.Success(mapped)
        }
    }

    override suspend fun getResourceById(id: String): Resource<WorkspaceResource> {
        delay(300)
        val item = _resources.value.find { it.id == id }
            ?: return Resource.Error("Resource not found")
        val isFav = favoriteIds.value.contains(item.id)
        return Resource.Success(item.copy(isFavorite = isFav))
    }

    override suspend fun checkResourceAvailability(
        resourceId: String,
        date: String,
        timeSlotId: Int
    ): Resource<Boolean> {
        delay(500)
        // For demonstration, let's say:
        // Turing Room (res_2) is occupied on any date at slot 2 & 3.
        // Also simulate generic random booked status.
        if (resourceId == "res_2" && (timeSlotId == 2 || timeSlotId == 3)) {
            return Resource.Success(false)
        }
        return Resource.Success(true)
    }

    override fun getFavoriteResources(): Flow<List<WorkspaceResource>> {
        return _resources.asStateFlow().map { list ->
            val favs = favoriteIds.value
            list.filter { favs.contains(it.id) }.map { it.copy(isFavorite = true) }
        }
    }

    override suspend fun toggleFavorite(resourceId: String, isFavorite: Boolean) {
        val currentFavs = favoriteIds.value.toMutableSet()
        if (isFavorite) {
            currentFavs.add(resourceId)
        } else {
            currentFavs.remove(resourceId)
        }
        favoriteIds.value = currentFavs
        // Emit trigger
        _resources.value = _resources.value.toList()
    }

    override suspend fun addResource(resource: WorkspaceResource): Resource<WorkspaceResource> {
        delay(800)
        val newResource = resource.copy(id = "res_${System.currentTimeMillis()}")
        val currentList = _resources.value.toMutableList()
        currentList.add(newResource)
        _resources.value = currentList
        return Resource.Success(newResource)
    }

    override suspend fun updateResource(resource: WorkspaceResource): Resource<WorkspaceResource> {
        delay(800)
        val currentList = _resources.value.toMutableList()
        val index = currentList.indexOfFirst { it.id == resource.id }
        if (index != -1) {
            currentList[index] = resource
            _resources.value = currentList
            return Resource.Success(resource)
        }
        return Resource.Error("Resource to update not found")
    }

    override suspend fun deleteResource(id: String): Resource<Unit> {
        delay(600)
        val currentList = _resources.value.toMutableList()
        val removed = currentList.removeIf { it.id == id }
        if (removed) {
            _resources.value = currentList
            return Resource.Success(Unit)
        }
        return Resource.Error("Resource to delete not found")
    }
}
