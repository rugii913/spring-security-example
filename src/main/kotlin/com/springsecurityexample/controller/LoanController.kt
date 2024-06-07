package com.springsecurityexample.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class LoanController {

    @GetMapping("/my-loans")
    fun getLoanDetails(): String {
        return "Here are the loan details from the DB"
    }
}
