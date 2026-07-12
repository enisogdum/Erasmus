package com.example.groupgo.data.local.mapper

import com.example.groupgo.data.local.entity.ExpenseBalanceEntity
import com.example.groupgo.domain.model.ExpenseBalance

object ExpenseMapper {

    fun toDomain(entity: ExpenseBalanceEntity): ExpenseBalance = ExpenseBalance(
        groupId = entity.groupId,
        memberName = entity.memberName,
        amountOwedPln = entity.amountOwedPln,
        amountPaidPln = entity.amountPaidPln
    )

    fun toEntity(balance: ExpenseBalance): ExpenseBalanceEntity = ExpenseBalanceEntity(
        groupId = balance.groupId,
        memberName = balance.memberName,
        amountOwedPln = balance.amountOwedPln,
        amountPaidPln = balance.amountPaidPln
    )
}
