package com.example.groupgo.di

import com.example.groupgo.data.repository.AuthRepositoryImpl
import com.example.groupgo.data.repository.EmailSenderImpl
import com.example.groupgo.data.repository.ExpenseRepositoryImpl
import com.example.groupgo.data.repository.GroupRepositoryImpl
import com.example.groupgo.data.repository.RouteRepositoryImpl
import com.example.groupgo.data.repository.SettingsRepositoryImpl
import com.example.groupgo.data.repository.TripRepositoryImpl
import com.example.groupgo.domain.repository.AuthRepository
import com.example.groupgo.domain.repository.EmailSender
import com.example.groupgo.domain.repository.ExpenseRepository
import com.example.groupgo.domain.repository.GroupRepository
import com.example.groupgo.domain.repository.RouteRepository
import com.example.groupgo.domain.repository.SettingsRepository
import com.example.groupgo.domain.repository.TripRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindAuthRepository(impl: AuthRepositoryImpl): AuthRepository

    @Binds
    @Singleton
    abstract fun bindRouteRepository(impl: RouteRepositoryImpl): RouteRepository

    @Binds
    @Singleton
    abstract fun bindGroupRepository(impl: GroupRepositoryImpl): GroupRepository

    @Binds
    @Singleton
    abstract fun bindTripRepository(impl: TripRepositoryImpl): TripRepository

    @Binds
    @Singleton
    abstract fun bindExpenseRepository(impl: ExpenseRepositoryImpl): ExpenseRepository

    @Binds
    @Singleton
    abstract fun bindSettingsRepository(impl: SettingsRepositoryImpl): SettingsRepository

    @Binds
    @Singleton
    abstract fun bindEmailSender(impl: EmailSenderImpl): EmailSender
}
