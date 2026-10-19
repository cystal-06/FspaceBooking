package com.example.fspacebooking.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.fspacebooking.data.local.entity.ResourceEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ResourceDao {
    @Query("SELECT * FROM resources")
    fun getAllResources(): Flow<List<ResourceEntity>>

    @Query("SELECT * FROM resources WHERE id = :id")
    fun getResourceById(id: String): ResourceEntity?

    @Query("SELECT * FROM resources WHERE isFavorite = 1")
    fun getFavoriteResources(): Flow<List<ResourceEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertResource(resource: ResourceEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertResources(resources: List<ResourceEntity>): List<Long>

    @Update
    fun updateResource(resource: ResourceEntity): Int

    @Query("DELETE FROM resources WHERE id = :id")
    fun deleteResourceById(id: String): Int
}
