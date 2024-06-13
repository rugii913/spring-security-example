package com.springsecurityexample.model

import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.LocalDate

@Entity
@Table(name = "contact_message")
class Contact(
    val contactName: String,
    val contactEmail: String,
    val subject: String,
    val message: String,
    val createDate: LocalDate,
) {

    @Id
    val contactId: String? = null
}
