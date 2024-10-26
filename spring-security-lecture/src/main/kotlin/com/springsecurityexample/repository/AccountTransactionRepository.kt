package com.springsecurityexample.repository

import com.springsecurityexample.model.AccountTransaction
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface AccountTransactionRepository : CrudRepository<AccountTransaction, String> {

    fun findByCustomerIdOrderByTransactionDateDesc(customerId: Int): List<AccountTransaction>
}
