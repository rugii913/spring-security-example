package com.springsecurityexample.filter

import jakarta.servlet.Filter
import jakarta.servlet.FilterChain
import jakarta.servlet.ServletRequest
import jakarta.servlet.ServletResponse
import org.springframework.security.core.context.SecurityContextHolder
import java.util.logging.Logger

class AuthoritiesLoggingAfterFilter : Filter {

    private val logger: Logger = Logger.getLogger(AuthoritiesLoggingAfterFilter::class.qualifiedName)

    override fun doFilter(request: ServletRequest?, response: ServletResponse?, chain: FilterChain) {
        val authentication = SecurityContextHolder.getContext().authentication
        if (null != authentication) {
            logger.info("User ${authentication.name} is successfully authenticated and has the authorities ${authentication.authorities}")
        }

        chain.doFilter(request, response)
    }
}
