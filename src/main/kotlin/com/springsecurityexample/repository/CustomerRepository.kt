package com.springsecurityexample.repository

import com.springsecurityexample.model.Customer
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface CustomerRepository : CrudRepository<Customer, Long> {

    fun findByEmail(email: String): List<Customer>
}
