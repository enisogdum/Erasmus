package com.example.groupgo.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.groupgo.data.local.entity.ExpenseBalanceEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ExpenseBalanceDao {
    @Query("SELECT * FROM expense_balances WHERE groupId = :groupId")
    fun getBalancesForGroup(groupId: String): Flow<List<ExpenseBalanceEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertBalance(entity: ExpenseBalanceEntity)

    @Query("DELETE FROM expense_balances WHERE groupId = :groupId")
    suspend fun clearBalancesForGroup(groupId: String)
}
