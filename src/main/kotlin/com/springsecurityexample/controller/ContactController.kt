package com.springsecurityexample.controller

import com.springsecurityexample.model.Contact
import com.springsecurityexample.repository.ContactRepository
import org.springframework.security.access.prepost.PostFilter
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

    // 메서드에서 특정 contactName에 대해서는 작업하지 않게 하고자 함
    // @PreFilter를 사용하려면 parameter의 type이 collection이어야 함, @PostFilter도 마찬가지로 return type이 collection이어야 함
    /*
    * // @PostFilter 사용해보면서 주석 처리
    * @PreFilter("filterObject.contactName != 'Test'") // 디버깅으로 parameter를 확인해보면, collection에서 조건에 맞는 객체만 필터링된 collection을 받음을 볼 수 있음
    * - @PreFilter는 collection 데이터를 받을 때, 특정 조건에 해당하는 객체에 대해서는 동작하지 않아야 하는 상황에 사용
    * */
    @PostFilter("filterObject.contactName != 'Test'") // parmeter에서 필터링하지 않고, 메서드가 return하는 collection에서 조건에 맞는 객체만 필터링해서 내보냄 - contactName이 "Test"인 요청을 보낼 경우 응답으로 빈 array를 받게 됨
    // - @PostFilter를 사용할 경우, 메서드 parameter나 비즈니스 로직 동작 여부를 통제하지 않음 → return하는 collection을 필터링할 뿐임
    @PostMapping("/contact")
    fun saveContactInquiryDetails(@RequestBody contacts: List<Contact>): List<Contact> {
        val contact = contacts[0]
        contact.contactId = getServiceRequestNumber()
        contact.createDate = LocalDate.now()

        // return listOf(contactRepository.save(contact))
        return mutableListOf(contactRepository.save(contact)) // @PostFilter를 이용해 필터링하려면 Kotlin의 List는 불가능(UnsupportedOperationException 발생), MutableList 사용
    }

    private fun getServiceRequestNumber() = "SR" + Random().nextInt(999_999_999 - 9_999) + 9_999
}
