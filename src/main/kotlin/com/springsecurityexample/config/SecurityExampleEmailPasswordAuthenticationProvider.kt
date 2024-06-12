package com.springsecurityexample.config

import com.springsecurityexample.repository.CustomerRepository
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component

@Component // DaoAuthenticationProvider와는 다르게 명시적으로 스프링 빈이 되도록 해야 함
class SecurityExampleEmailPasswordAuthenticationProvider(
    private val customerRepository: CustomerRepository,
    private val passwordEncoder: PasswordEncoder,
) : AuthenticationProvider {

    /*
    * - DaoAuthenticationProvider가 상속하는 AbstractUserDetailsAuthenticationProvider의 authenticate()에서
    *   - retrieveUser()를 호출하고
    *   - DaoAuthenticationProvider는 retrieveUser()를 UserDetailsService을 이용하는 방식과는 다르게
    * - 이 SecurityExampleEmailPasswordAuthenticationProvider에서는
    *   - 직접 CustomerRepository에 접근하는 방식을 사용하고 있음
    * */
    override fun authenticate(authentication: Authentication): Authentication {
        val inputEmail = authentication.name
        val foundCustomerList = customerRepository.findByEmail(inputEmail)
            .also { check(it.isNotEmpty()) { throw BadCredentialsException("No user registered with this details!") } }

        val foundCustomer = foundCustomerList[0]
        val inputPassword = authentication.credentials.toString()
        check(passwordEncoder.matches(inputPassword, foundCustomer.password)) { throw BadCredentialsException("Invalid password!") }

        return UsernamePasswordAuthenticationToken(inputEmail, inputPassword, listOf(SimpleGrantedAuthority(foundCustomer.role)))
    }

    override fun supports(authentication: Class<*>) =
        UsernamePasswordAuthenticationToken::class.java.isAssignableFrom(authentication)
}
