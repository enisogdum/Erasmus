package com.example.groupgo.domain.model

data class ExpenseBalance(
    val groupId: String,
    val memberName: String,
    val amountOwedPln: Double, // positive = owes money
    val amountPaidPln: Double
)
