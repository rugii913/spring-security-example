package com.springsecurityexample.controller

import com.springsecurityexample.model.Loan
import com.springsecurityexample.repository.LoanRepository
import org.springframework.security.access.prepost.PostAuthorize
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class LoanController(
    private val loanRepository: LoanRepository,
) {

    /* controller layer의 method에 method level security를 적용해도 layer 상관 없이 동작함을 확인할 수 있음 */
    /* @PreAuthorize와는 달리 method가 호출은 되지만 return 후  */
    // @PostAuthorize("hasRole(\"ROOT\")") // 존재하지 않는 role에만 인가하여 method level security 동작 확인
    @PostAuthorize("hasRole(\"USER\")") // 존재하는 role로 인가하여 method level security 동작 확인
    @GetMapping("/my-loans")
    fun getLoanDetails(@RequestParam(name = "id") customerId: Int): List<Loan> {
        return loanRepository.findByCustomerIdOrderByStartDateDesc(customerId)
    }
}
