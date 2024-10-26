package com.springsecurityexample.repository

import com.springsecurityexample.model.Contact
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface ContactRepository : CrudRepository<Contact, String>
