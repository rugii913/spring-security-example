package com.springsecurityexample.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class AccountController {

    @GetMapping("/my-account")
    fun getAccountDetails(): String {
        return "Here are the account details from the DB"
    }
}
