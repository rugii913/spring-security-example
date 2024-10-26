package com.springsecurityexample.model

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.persistence.*
import java.time.LocalDate

@Entity
class Customer(
    val name: String,
    val email: String,
    val mobileNumber: String,
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY) val password: String, // TODO access가 무슨 역할인지 알아보기 - HTTP 요청에는 포함, 응답에는 제외
    val role: String,
    val createDate: LocalDate,
    @JsonIgnore @OneToMany(mappedBy = "customer", fetch = FetchType.EAGER) var authorities: Set<Authority> = setOf(), // TODO JsonIgnore가 무슨 역할인지 알아보기 - HTTP 요청, 응답에서 해당 필드 제외
) {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // @GeneratedValue strategy에 따른 persistence context의 작동 방식은 다음 참고 - https://velog.io/@mainfn/jpa-basic-5
    // 만약 @GeneratedValue(strategy = GenerationType.AUTO)를 사용하고 싶다면 아래와 같은 방식 가능
    // - @GenericGenerator에 대한 자세한 설명은 다음 참고 - https://0soo.tistory.com/178
//    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
//    @GenericGenerator(name = "native", strategy = "native")
    @JsonProperty(value = "id") // client 쪽으로 JSON property 이름을 맞춰줬음
    var customerId: Int? = null
}
