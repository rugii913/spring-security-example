package action.`in`.spring.security.security

import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Component

@Component
class CustomAuthenticationProvider : AuthenticationProvider {

    override fun authenticate(authentication: Authentication): Authentication {
        val username = authentication.name
        val password = authentication.credentials.toString()

        if ("john" == username && "12345" == password) {
            return UsernamePasswordAuthenticationToken(username, password, emptyList())
        } else {
            throw AuthenticationCredentialsNotFoundException("Error!")
        }
    }

    override fun supports(authentication: Class<*>): Boolean =
        UsernamePasswordAuthenticationToken::class.java.isAssignableFrom(authentication)
}
