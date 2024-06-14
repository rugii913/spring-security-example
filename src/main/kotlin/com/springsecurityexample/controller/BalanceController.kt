package com.springsecurityexample.controller

import com.springsecurityexample.model.AccountTransaction
import com.springsecurityexample.repository.AccountTransactionRepository
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class BalanceController(
    private val accountTransactionRepository: AccountTransactionRepository,
) {

    @GetMapping("/my-balance")
    fun getBalanceDetails(@RequestParam(name = "id") customerId: Int): List<AccountTransaction> {
        return accountTransactionRepository.findByCustomerIdOrderByTransactionDateDesc(customerId)
    }
}
