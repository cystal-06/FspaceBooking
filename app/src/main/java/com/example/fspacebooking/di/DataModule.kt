package com.example.fspacebooking.di

import android.content.Context
import android.content.SharedPreferences
import com.example.fspacebooking.data.local.dao.AuditLogDao
import com.example.fspacebooking.data.local.dao.BookingDao
import com.example.fspacebooking.data.local.dao.ResourceDao
import com.example.fspacebooking.data.local.dao.TimeSlotDao
import com.example.fspacebooking.data.local.dao.UserDao
import com.example.fspacebooking.data.local.db.FSpaceDatabase
import com.example.fspacebooking.data.remote.api.FSpaceApiService
import com.example.fspacebooking.data.repository.AuthRepositoryImpl
import com.example.fspacebooking.data.repository.BookingRepositoryImpl
import com.example.fspacebooking.data.repository.ResourceRepositoryImpl
import com.example.fspacebooking.domain.repository.AuthRepository
import com.example.fspacebooking.domain.repository.BookingRepository
import com.example.fspacebooking.domain.repository.ResourceRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import android.os.Build
import java.util.concurrent.TimeUnit
import javax.inject.Provider
import javax.inject.Qualifier
import javax.inject.Singleton

@Retention(AnnotationRetention.BINARY)
@Qualifier
annotation class ApplicationScope

@Module
@InstallIn(SingletonComponent::class)
object DataModule {

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        return OkHttpClient.Builder()
            .connectTimeout(3, TimeUnit.SECONDS)
            .readTimeout(4, TimeUnit.SECONDS)
            .writeTimeout(4, TimeUnit.SECONDS)
            .addInterceptor(logging)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        val isEmulator = Build.FINGERPRINT.startsWith("generic")
                || Build.FINGERPRINT.startsWith("unknown")
                || Build.MODEL.contains("google_sdk")
                || Build.MODEL.contains("Emulator")
                || Build.MODEL.contains("Android SDK built for x86")
                || Build.MANUFACTURER.contains("Genymotion")
                || (Build.BRAND.startsWith("generic") && Build.DEVICE.startsWith("generic"))
                || "google_sdk" == Build.PRODUCT

        val host = if (isEmulator) "10.0.2.2" else "127.0.0.1"
        return Retrofit.Builder()
            .baseUrl("http://$host:8088/api/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideFSpaceApiService(retrofit: Retrofit): FSpaceApiService {
        return retrofit.create(FSpaceApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideSharedPreferences(@ApplicationContext context: Context): SharedPreferences {
        return context.getSharedPreferences("fspace_booking_preferences", Context.MODE_PRIVATE)
    }

    @Provides
    @Singleton
    @ApplicationScope
    fun provideApplicationScope(): CoroutineScope {
        return CoroutineScope(SupervisorJob() + Dispatchers.Default)
    }
    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context,
        @ApplicationScope scope: CoroutineScope,
        resourceDaoProvider: Provider<ResourceDao>,
        timeSlotDaoProvider: Provider<TimeSlotDao>,
        userDaoProvider: Provider<UserDao>,
        auditLogDaoProvider: Provider<AuditLogDao>,
        bookingDaoProvider: Provider<BookingDao>
    ): FSpaceDatabase {
        return FSpaceDatabase.buildDatabase(
            context,
            scope,
            resourceDaoProvider,
            timeSlotDaoProvider,
            userDaoProvider,
            auditLogDaoProvider,
            bookingDaoProvider
        )
    }

    @Provides
    fun provideResourceDao(database: FSpaceDatabase): ResourceDao {
        return database.resourceDao()
    }

    @Provides
    fun provideBookingDao(database: FSpaceDatabase): BookingDao {
        return database.bookingDao()
    }

    @Provides
    fun provideTimeSlotDao(database: FSpaceDatabase): TimeSlotDao {
        return database.timeSlotDao()
    }

    @Provides
    fun provideUserDao(database: FSpaceDatabase): UserDao {
        return database.userDao()
    }

    @Provides
    fun provideAuditLogDao(database: FSpaceDatabase): AuditLogDao {
        return database.auditLogDao()
    }
}

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindAuthRepository(
        impl: AuthRepositoryImpl
    ): AuthRepository

    @Binds
    @Singleton
    abstract fun bindResourceRepository(
        impl: ResourceRepositoryImpl
    ): ResourceRepository

    @Binds
    @Singleton
    abstract fun bindBookingRepository(
        impl: BookingRepositoryImpl
    ): BookingRepository
}
