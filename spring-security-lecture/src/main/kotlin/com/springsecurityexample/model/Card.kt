package com.springsecurityexample.model

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import java.time.LocalDate

@Entity
class Card(
    val cardNumber: String,
    val customerId: Int,
    val cardType: String,
    val totalLimit: Int,
    val amountUsed: Int,
    val availableAmount: Int,
    val createDate: LocalDate,
) {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var cardId: Int? = null
}
