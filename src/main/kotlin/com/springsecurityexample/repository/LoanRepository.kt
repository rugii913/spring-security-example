package com.springsecurityexample.repository

import com.springsecurityexample.model.Loan
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface LoanRepository : CrudRepository<Loan, Long> {

    fun findByCustomerIdOrderByStartDateDesc(customerId: Int)
}
