package com.springsecurityexample.filter

import jakarta.servlet.Filter
import jakarta.servlet.FilterChain
import jakarta.servlet.ServletRequest
import jakarta.servlet.ServletResponse
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpHeaders
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.util.StringUtils
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets
import java.util.*

class RequestValidationBeforeFilter : Filter {

    /*
     * init(), destroy()는 default 메서드이므로 구현하지 않아도 됨
     * */

    private val credentialsCharset: Charset = StandardCharsets.UTF_8

    override fun doFilter(request: ServletRequest, response: ServletResponse, chain: FilterChain) {
        val httpServletRequest = request as HttpServletRequest
        val httpServletResponse = response as HttpServletResponse
        var authorizationHeader = httpServletRequest.getHeader(HttpHeaders.AUTHORIZATION)

        if (authorizationHeader != null) {
            authorizationHeader = authorizationHeader.trim()
            if (StringUtils.startsWithIgnoreCase(authorizationHeader, AUTHENTICATION_SCHEME_BASIC)) {
                val base64Token = authorizationHeader.substring(6).toByteArray(StandardCharsets.UTF_8)
                val decoded: ByteArray
                try {
                    decoded = Base64.getDecoder().decode(base64Token)
                    val token = String(decoded, credentialsCharset)
                    val positionOfDelimiter = token.indexOf(":")
                    if (positionOfDelimiter == -1) {
                        throw BadCredentialsException("Invalid basic authentication token")
                    }
                    val email = token.substring(0, positionOfDelimiter)
                    if (email.lowercase(Locale.getDefault()).contains("test")) {
                        httpServletResponse.status = HttpServletResponse.SC_BAD_REQUEST
                        return
                    }
                } catch (e: IllegalArgumentException) {
                    throw BadCredentialsException("Failed to decode basic authentication token")
                }
            }
        }

        chain.doFilter(request, response)
    }

    companion object {
        const val AUTHENTICATION_SCHEME_BASIC: String = "Basic"
    }
}
