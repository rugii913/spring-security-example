package com.springsecurityexample.config

import com.springsecurityexample.repository.CustomerRepository
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Component

@Component
class SecurityExampleUserDetailsService(
    private val customerRepository: CustomerRepository
) : UserDetailsService {

    override fun loadUserByUsername(username: String): UserDetails {
        val foundCustomerList = customerRepository.findByEmail(username)
            .also { check(it.isNotEmpty()) { throw UsernameNotFoundException("User details not found for the user: $username") } }

        val foundCustomer = foundCustomerList[0]
        val foundCustomerAuthorities = listOf(SimpleGrantedAuthority(foundCustomer.role))

        return User(foundCustomer.email, foundCustomer.password, foundCustomerAuthorities)
    }
}
