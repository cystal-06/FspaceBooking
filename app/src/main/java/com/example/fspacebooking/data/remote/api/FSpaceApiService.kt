package com.example.fspacebooking.data.remote.api

import com.example.fspacebooking.domain.model.Booking
import com.example.fspacebooking.domain.model.TimeSlot
import com.example.fspacebooking.domain.model.User
import com.example.fspacebooking.domain.model.WorkspaceResource
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

data class LoginRequest(
    val username: String,
    val securityHash: String
)

data class BookingRequest(
    val resourceId: String,
    val date: String,
    val timeSlotId: Int
)

interface FSpaceApiService {
    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): User

    @POST("auth/logout")
    suspend fun logout(): Unit

    @GET("resources")
    suspend fun getResources(
        @Query("type") type: String? = null,
        @Query("building") building: String? = null,
        @Query("floor") floor: String? = null,
        @Query("capacity") capacity: Int? = null,
        @Query("search") search: String? = null
    ): List<WorkspaceResource>

    @GET("resources/{id}")
    suspend fun getResourceById(@Path("id") id: String): WorkspaceResource

    @POST("resources")
    suspend fun addResource(@Body resource: WorkspaceResource): WorkspaceResource

    @PUT("resources/{id}")
    suspend fun updateResource(
        @Path("id") id: String,
        @Body resource: WorkspaceResource
    ): WorkspaceResource

    @DELETE("resources/{id}")
    suspend fun deleteResource(@Path("id") id: String): Unit

    @POST("bookings")
    suspend fun createBooking(@Body request: BookingRequest): Booking

    @DELETE("bookings/{id}")
    suspend fun cancelBooking(@Path("id") id: String): Unit

    @GET("bookings")
    suspend fun getAllBookings(): List<Booking>

    @GET("timeslots")
    suspend fun getTimeSlots(): List<TimeSlot>
}
