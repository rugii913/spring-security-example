package com.springsecurityexample.repository

import com.springsecurityexample.model.Account
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface AccountRepository : CrudRepository<Account, Long> {

    fun findByCustomerId(customerId: Int): Account
}
