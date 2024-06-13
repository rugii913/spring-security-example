package com.springsecurityexample.controller

import com.springsecurityexample.model.Account
import com.springsecurityexample.repository.AccountRepository
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class AccountController(
    private val accountRepository: AccountRepository,
) {

    @GetMapping("/my-account")
    fun getAccountDetails(@RequestParam customerId: Int): Account? {
        return accountRepository.findByCustomerId(customerId)
    }
}
