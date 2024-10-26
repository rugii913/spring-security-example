package com.springsecurityexample.model

import jakarta.persistence.Entity
import jakarta.persistence.Id
import java.time.LocalDate

@Entity
class Account(
    val customerId: Int,
    val accountType: String,
    val branchAddress: String,
    val createDate: LocalDate,
) {

    @Id
    var accountNumber: Long? = null
}
