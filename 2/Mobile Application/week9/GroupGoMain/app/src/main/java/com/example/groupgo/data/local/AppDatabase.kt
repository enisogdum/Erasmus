package com.example.groupgo.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.groupgo.data.local.dao.ExpenseBalanceDao
import com.example.groupgo.data.local.dao.TravelGroupDao
import com.example.groupgo.data.local.dao.TripRecordDao
import com.example.groupgo.data.local.entity.ExpenseBalanceEntity
import com.example.groupgo.data.local.entity.TravelGroupEntity
import com.example.groupgo.data.local.entity.TripRecordEntity

@Database(
    entities = [
        TravelGroupEntity::class,
        TripRecordEntity::class,
        ExpenseBalanceEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun travelGroupDao(): TravelGroupDao
    abstract fun tripRecordDao(): TripRecordDao
    abstract fun expenseBalanceDao(): ExpenseBalanceDao
}
