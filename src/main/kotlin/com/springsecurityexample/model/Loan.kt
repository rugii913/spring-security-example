package com.springsecurityexample.model

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import java.time.LocalDate

@Entity
class Loan(
    val customerId: Int,
    val startDate: LocalDate,
    val loanType: String,
    val totalLoan: Int,
    val amountPaid: Int,
    val outstandingAmount: Int,
    val createDate: LocalDate,
) {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var loanNumber: Long? = null
}
