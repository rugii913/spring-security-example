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
    var createDate: LocalDate, // TODO 나중에 DTO 만들게 되면 val로 수정할 것
) {

    @Id
    var contactId: String? = null
}
