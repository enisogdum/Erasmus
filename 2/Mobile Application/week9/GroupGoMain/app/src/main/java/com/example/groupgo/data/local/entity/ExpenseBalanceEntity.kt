package com.example.groupgo.data.local.entity

import androidx.room.Entity

@Entity(
    tableName = "expense_balances",
    primaryKeys = ["groupId", "memberName"]
)
data class ExpenseBalanceEntity(
    val groupId: String,
    val memberName: String,
    val amountOwedPln: Double,
    val amountPaidPln: Double
)
