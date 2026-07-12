package com.example.groupgo.di

import android.content.Context
import androidx.room.Room
import com.example.groupgo.data.local.AppDatabase
import com.example.groupgo.data.local.dao.ExpenseBalanceDao
import com.example.groupgo.data.local.dao.TravelGroupDao
import com.example.groupgo.data.local.dao.TripRecordDao
import com.example.groupgo.data.remote.RouteApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    private const val OSM_BASE_URL = "https://routing.openstreetmap.de/"

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient =
        OkHttpClient.Builder()
            .addInterceptor { chain ->
                val request = chain.request().newBuilder()
                    .header("User-Agent", "GroupGo/1.0 (Android; com.example.groupgo)")
                    .build()
                chain.proceed(request)
            }
            .addInterceptor(
                HttpLoggingInterceptor().apply {
                    level = HttpLoggingInterceptor.Level.BODY
                }
            )
            .build()

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit =
        Retrofit.Builder()
            .baseUrl(OSM_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    @Provides
    @Singleton
    fun provideRouteApiService(retrofit: Retrofit): RouteApiService =
        retrofit.create(RouteApiService::class.java)

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "groupgo.db"
        ).fallbackToDestructiveMigration().build()

    @Provides
    fun provideTravelGroupDao(database: AppDatabase): TravelGroupDao =
        database.travelGroupDao()

    @Provides
    fun provideTripRecordDao(database: AppDatabase): TripRecordDao =
        database.tripRecordDao()

    @Provides
    fun provideExpenseBalanceDao(database: AppDatabase): ExpenseBalanceDao =
        database.expenseBalanceDao()
}
