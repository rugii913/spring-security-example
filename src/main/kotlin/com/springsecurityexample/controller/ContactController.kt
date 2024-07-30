package com.springsecurityexample.controller

import com.springsecurityexample.model.Contact
import com.springsecurityexample.repository.ContactRepository
import org.springframework.security.access.prepost.PreFilter
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDate
import java.util.Random

@RestController
class ContactController(
    private val contactRepository: ContactRepository,
) {

    // 특정 contactName에 대해서는 호출되지 않게 하고자 함
    // @PreFilter를 사용하려면 parameter의 type이 collection이어야 함
    // - collection 데이터를 받고, 특정 조건에 해당하는 객체에 대해서는 동작하지 않아야 하는 상황을 가정한 것
    // - 디버깅으로 parameter를 확인해보면, collection에서 조건에 맞는 객체만 필터링된 collection을 받음을 볼 수 있음
    @PreFilter("filterObject.contactName != 'Test'")
    @PostMapping("/contact")
    fun saveContactInquiryDetails(@RequestBody contacts: List<Contact>): List<Contact> {
        val contact = contacts[0]
        contact.contactId = getServiceRequestNumber()
        contact.createDate = LocalDate.now()

        return listOf(contactRepository.save(contact))
    }

    private fun getServiceRequestNumber() = "SR" + Random().nextInt(999_999_999 - 9_999) + 9_999
}
