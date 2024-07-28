package com.springsecurityexample.repository

import com.springsecurityexample.model.Loan
import org.springframework.data.repository.CrudRepository
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Repository

@Repository
interface LoanRepository : CrudRepository<Loan, Long> {

    /* layer 상관 없이 동작함을 확인하기 위해 repository layer의 method에 method level security 적용 */
    // @PreAuthorize("hasRole('ROOT')") // 존재하지 않는 role에만 인가하여 method level security 동작 확인
    // @PreAuthorize("hasRole('USER')") // 존재하는 role로 인가하여 method level security 동작 확인
    fun findByCustomerIdOrderByStartDateDesc(customerId: Int): List<Loan>
}
