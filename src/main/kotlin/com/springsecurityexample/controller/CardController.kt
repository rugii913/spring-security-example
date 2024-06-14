package com.springsecurityexample.controller

import com.springsecurityexample.model.Card
import com.springsecurityexample.repository.CardRepository
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class CardController(
    private val cardRepository: CardRepository,
) {

    @GetMapping("/my-cards")
    fun getCardDetails(@RequestParam(name = "id") customerId: Int): List<Card> {
        return cardRepository.findByCustomerId(customerId)
    }
}
