package com.springsecurityexample.controller

import com.springsecurityexample.model.Customer
import com.springsecurityexample.repository.CustomerRepository
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class CustomerRegistrationController(
    private val customerRepository: CustomerRepository
) {

    /*
    * - SecurityExampleUserDetailsService가 UserDetailsService를 구현하고, UserDetailsManager는 구현하지 않도록 함
    *   - 회원 가입 작업을 위한 코드는 여기에 별도로 작성
    *   - 사용자 생성, 수정, 삭제 등 모든 관리 작업에서 Spring Security를 사용할 필요는 없도록 하기 위함
    * */
    @PostMapping("/register")
    fun registerUser(@RequestBody customer: Customer): ResponseEntity<String> {
        try {
            customerRepository.save(customer)
        } catch (exception: Exception) {
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("An exception occured due to ${exception.message}")
        }

        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body("Given user details are successfully registered")
    }
}
