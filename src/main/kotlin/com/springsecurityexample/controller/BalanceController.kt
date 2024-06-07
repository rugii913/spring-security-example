package com.springsecurityexample.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class BalanceController {

    @GetMapping("/my-balance")
    fun getBalanceDetails(): String {
        return "Here are the balance details from the DB"
    }
}
