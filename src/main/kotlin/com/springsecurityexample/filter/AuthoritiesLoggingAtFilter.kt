package com.springsecurityexample.filter

import jakarta.servlet.Filter
import jakarta.servlet.FilterChain
import jakarta.servlet.ServletRequest
import jakarta.servlet.ServletResponse
import java.util.logging.Logger

class AuthoritiesLoggingAtFilter : Filter {

    private val logger: Logger = Logger.getLogger(AuthoritiesLoggingAtFilter::class.qualifiedName)

    override fun doFilter(request: ServletRequest?, response: ServletResponse?, chain: FilterChain) {
        logger.info("Authentication validation is in progress.")

        chain.doFilter(request, response)
    }
}