package com.springsecurityexample.repository

import com.springsecurityexample.model.Card
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface CardRepository : CrudRepository<Card, Int> {

    fun findByCustomerId(customerId: Int): List<Card>
}
