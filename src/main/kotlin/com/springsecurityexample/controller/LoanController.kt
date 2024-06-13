package com.springsecurityexample.controller

import com.springsecurityexample.model.Loan
import com.springsecurityexample.repository.LoanRepository
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class LoanController(
    private val loanRepository: LoanRepository,
) {

    @GetMapping("/my-loans")
    fun getLoanDetails(@RequestParam customerId: Int): List<Loan> {
        return loanRepository.findByCustomerIdOrderByStartDateDesc(customerId)
    }
}
