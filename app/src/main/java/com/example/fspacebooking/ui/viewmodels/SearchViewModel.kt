package com.example.fspacebooking.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fspacebooking.domain.model.ResourceType
import com.example.fspacebooking.domain.model.WorkspaceResource
import com.example.fspacebooking.domain.repository.ResourceRepository
import com.example.fspacebooking.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class SearchViewModel @Inject constructor(
    private val resourceRepository: ResourceRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedType = MutableStateFlow<ResourceType?>(null)
    val selectedType: StateFlow<ResourceType?> = _selectedType.asStateFlow()

    private val _selectedBuilding = MutableStateFlow<String?>(null)
    val selectedBuilding: StateFlow<String?> = _selectedBuilding.asStateFlow()

    private val _selectedFloor = MutableStateFlow<String?>(null)
    val selectedFloor: StateFlow<String?> = _selectedFloor.asStateFlow()

    private val _selectedCapacity = MutableStateFlow<Int?>(null)
    val selectedCapacity: StateFlow<Int?> = _selectedCapacity.asStateFlow()

    private val _resourcesState = MutableStateFlow<Resource<List<WorkspaceResource>>>(Resource.Loading())
    val resourcesState: StateFlow<Resource<List<WorkspaceResource>>> = _resourcesState.asStateFlow()

    init {
        viewModelScope.launch {
            // FlatMapLatest updates trigger search on filter state updates
            combineFlows().collectLatest {
                _resourcesState.value = it
            }
        }
    }

    private fun combineFlows() = kotlinx.coroutines.flow.combine(
        _selectedType,
        _selectedBuilding,
        _selectedFloor,
        _selectedCapacity,
        _searchQuery
    ) { type, building, floor, capacity, query ->
        val b = if (building == "All Buildings" || building == "Tất cả tòa nhà" || building.isNullOrBlank()) null else building
        val f = if (floor == "All Floors" || floor == "Tất cả các tầng" || floor == "Tất cả tầng" || floor.isNullOrBlank()) null else floor
        Triple(type, b, f) to Pair(capacity, query)
    }.flatMapLatest { pair ->
        val type = pair.first.first
        val building = pair.first.second
        val floor = pair.first.third
        val capacity = pair.second.first
        val query = pair.second.second
        resourceRepository.getResources(type, building, floor, capacity, query)
    }

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    fun onTypeSelect(type: ResourceType?) {
        _selectedType.value = type
    }

    fun onBuildingSelect(building: String?) {
        _selectedBuilding.value = building
    }

    fun onFloorSelect(floor: String?) {
        _selectedFloor.value = floor
    }

    fun onCapacitySelect(capacity: Int?) {
        _selectedCapacity.value = capacity
    }

    fun toggleFavorite(resourceId: String, isFavorite: Boolean) {
        viewModelScope.launch {
            resourceRepository.toggleFavorite(resourceId, isFavorite)
        }
    }
}
