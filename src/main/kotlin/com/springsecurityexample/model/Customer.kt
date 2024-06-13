package com.springsecurityexample.model

import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import java.time.LocalDate

@Entity
class Customer(
    val name: String,
    val email: String,
    val mobileNumber: String,
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY) val password: String,
    val role: String,
    val createDate: LocalDate,
) {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // @GeneratedValue strategy에 따른 persistence context의 작동 방식은 다음 참고 - https://velog.io/@mainfn/jpa-basic-5
    // 만약 @GeneratedValue(strategy = GenerationType.AUTO)를 사용하고 싶다면 아래와 같은 방식 가능
    // - @GenericGenerator에 대한 자세한 설명은 다음 참고 - https://0soo.tistory.com/178
//    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
//    @GenericGenerator(name = "native", strategy = "native")
    var customerId: Int? = null
}
