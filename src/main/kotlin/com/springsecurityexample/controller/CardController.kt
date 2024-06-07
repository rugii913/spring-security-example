package com.springsecurityexample.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class CardController {

    @GetMapping("/my-cards")
    fun getCardDetails(): String {
        return "Here are the card details from the DB"
    }
}
