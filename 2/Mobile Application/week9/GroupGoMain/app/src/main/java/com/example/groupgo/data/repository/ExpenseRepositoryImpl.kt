package com.example.groupgo.data.repository

import com.example.groupgo.data.local.dao.ExpenseBalanceDao
import com.example.groupgo.data.local.entity.ExpenseBalanceEntity
import com.example.groupgo.data.local.mapper.ExpenseMapper
import com.example.groupgo.domain.model.ExpenseBalance
import com.example.groupgo.domain.repository.ExpenseRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ExpenseRepositoryImpl @Inject constructor(
    private val expenseBalanceDao: ExpenseBalanceDao
) : ExpenseRepository {

    override fun getBalancesForGroup(groupId: String): Flow<List<ExpenseBalance>> =
        expenseBalanceDao.getBalancesForGroup(groupId).map { entities ->
            entities.map(ExpenseMapper::toDomain)
        }

    override suspend fun updateAmountPaid(groupId: String, memberName: String, totalPaid: Double) {
        val currentEntities = expenseBalanceDao.getBalancesForGroup(groupId).first()
        if (currentEntities.isEmpty()) return
        
        // 1. Map to new states with updated totalPaid
        val updatedEntities = currentEntities.map { entity ->
            if (entity.memberName == memberName) {
                entity.copy(amountPaidPln = totalPaid)
            } else {
                entity
            }
        }
        
        // 2. Recalculate
        val newTotalPaid = updatedEntities.sumOf { it.amountPaidPln }
        val newShare = newTotalPaid / updatedEntities.size
        
        // 3. Save all back
        updatedEntities.forEach { entity ->
            val balanced = entity.copy(amountOwedPln = newShare - entity.amountPaidPln)
            expenseBalanceDao.upsertBalance(balanced)
        }
    }

    override suspend fun addExpenseToGroup(groupId: String, totalAmount: Double, payerName: String) {
        val currentEntities = expenseBalanceDao.getBalancesForGroup(groupId).first()
        if (currentEntities.isEmpty()) return

        // 1. Map to new states with increased amountPaidPln for payer
        val updatedEntities = currentEntities.map { entity ->
            if (entity.memberName == payerName) {
                entity.copy(amountPaidPln = entity.amountPaidPln + totalAmount)
            } else {
                entity
            }
        }
        
        // 2. Recalculate
        val newTotalPaid = updatedEntities.sumOf { it.amountPaidPln }
        val newShare = newTotalPaid / updatedEntities.size
        
        // 3. Save all back
        updatedEntities.forEach { entity ->
            val balanced = entity.copy(amountOwedPln = newShare - entity.amountPaidPln)
            expenseBalanceDao.upsertBalance(balanced)
        }
    }

    override suspend fun initializeGroupBalances(groupId: String, members: List<String>) {
        members.forEach { name ->
            expenseBalanceDao.upsertBalance(
                ExpenseBalanceEntity(
                    groupId = groupId,
                    memberName = name,
                    amountOwedPln = 0.0,
                    amountPaidPln = 0.0
                )
            )
        }
    }

    override suspend fun clearGroupBalances(groupId: String) {
        expenseBalanceDao.clearBalancesForGroup(groupId)
    }
}
