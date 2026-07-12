package com.example.groupgo.domain.repository

import com.example.groupgo.domain.model.ExpenseBalance
import kotlinx.coroutines.flow.Flow

interface ExpenseRepository {
    fun getBalancesForGroup(groupId: String): Flow<List<ExpenseBalance>>
    suspend fun updateAmountPaid(groupId: String, memberName: String, totalPaid: Double)
    suspend fun addExpenseToGroup(groupId: String, totalAmount: Double, payerName: String = "")
    suspend fun initializeGroupBalances(groupId: String, members: List<String>)
    suspend fun clearGroupBalances(groupId: String)
}
