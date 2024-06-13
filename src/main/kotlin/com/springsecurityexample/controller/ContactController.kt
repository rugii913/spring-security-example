package com.springsecurityexample.controller

import com.springsecurityexample.model.Contact
import com.springsecurityexample.repository.ContactRepository
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDate
import java.util.Random

@RestController
class ContactController(
    private val contactRepository: ContactRepository,
) {

    @PostMapping("/contact")
    fun saveContactInquiryDetails(@RequestBody contact: Contact): Contact {
        contact.contactId = getServiceRequestNumber()
        contact.createDate = LocalDate.now()
        return contactRepository.save(contact)
    }

    private fun getServiceRequestNumber() = "SR" + Random().nextInt(999_999_999 - 9_999) + 9_999
}
