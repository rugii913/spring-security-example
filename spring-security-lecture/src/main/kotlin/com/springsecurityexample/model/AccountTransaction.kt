package com.springsecurityexample.model

import jakarta.persistence.Entity
import jakarta.persistence.Id
import java.time.LocalDate

@Entity
class AccountTransaction(
    val accountNumber: Long,
    val customerId: Int,
    val transactionDate: LocalDate,
    val transactionSummary: String,
    val transactionType: String,
    val transactionAmount: Int,
    val closingBalance: Int,
    val createDate: LocalDate,
) {

    @Id
    var transactionId: String? = null
}
