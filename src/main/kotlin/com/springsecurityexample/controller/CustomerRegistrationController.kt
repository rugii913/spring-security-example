package com.springsecurityexample.controller

import com.springsecurityexample.dto.CustomerRegistrationRequest
import com.springsecurityexample.model.Customer
import com.springsecurityexample.repository.CustomerRepository
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDate

@RestController
class CustomerRegistrationController(
    private val customerRepository: CustomerRepository,
    private val passwordEncoder: PasswordEncoder, // 사용자 등록을 위한 코드를 Spring Security의 제어를 받지 않도록 별도로 작성했으므로, PasswordEncoder도 명시적으로 사용해줘야 함
) {

    /*
    * - SecurityExampleUserDetailsService가 UserDetailsService를 구현하고, UserDetailsManager는 구현하지 않도록 함
    *   - 회원 가입 작업을 위한 코드는 여기에 별도로 작성
    *   - 사용자 생성, 수정, 삭제 등 모든 관리 작업에서 Spring Security를 사용할 필요는 없도록 하기 위함
    * */
    @PostMapping("/register")
    fun registerUser(@RequestBody registrationRequest: CustomerRegistrationRequest): ResponseEntity<String> {
        try {
            val encodedPassword = passwordEncoder.encode(registrationRequest.password)
            val customer = Customer(registrationRequest.email, "", "", encodedPassword, registrationRequest.role, LocalDate.now())
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
