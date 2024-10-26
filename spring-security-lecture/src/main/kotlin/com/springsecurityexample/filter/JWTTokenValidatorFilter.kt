package com.springsecurityexample.filter

import com.springsecurityexample.constants.SecurityConstants
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.AuthorityUtils
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.filter.OncePerRequestFilter
import java.nio.charset.StandardCharsets

class JWTTokenValidatorFilter : OncePerRequestFilter() {

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val jwt = request.getHeader(SecurityConstants.JWT_HEADER)

        if (null != jwt) {
            try { // 이 과정에서 예외가 발생할 경우 validation이 안 된 것으로 판단하여 BadCredentialsException을 던짐
                  // 발생할만한 예외들을 JwtParser 인터페이스의 parseClaimsJwt()에서 던지는 예외로 확인해볼 수 있음
                val key = Keys.hmacShaKeyFor(SecurityConstants.JWT_KEY.toByteArray(StandardCharsets.UTF_8))

                val claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(jwt) // A Claims JWS is a JWT with a Claims body that has been cryptographically signed.
                    // .parseClaimsJwt(jwt) // parseClaimsJwt()가 아닌 parseClaimsJws()를 사용해야 함 - 자세한 내용은 JwtParser 인터페이스의 javadoc 참고
                    .body // JWT에서 header와 signature를 제외한 payload 부분만 가져옴

                val username = claims["username"].toString()
                val authorities = claims["authorities"] as String
                
                // JWT payload에서 얻은 username과 authorities를 이용해 UsernamePasswordAuthenticationToken(Authentication의 subtype인 인증객체)을 생성
                val authentication = UsernamePasswordAuthenticationToken(
                    username,
                    null,
                     AuthorityUtils.commaSeparatedStringToAuthorityList(authorities)
                )

                // 생성한 UsernamePasswordAuthenticationToken 객체를 SecurityContext에 두어, 인증된 요청임을 표시
                SecurityContextHolder.getContext().authentication = authentication
            } catch (e: Exception) {
                throw BadCredentialsException("Invalid Token received!")
            }
        }

        filterChain.doFilter(request, response)
    }

    override fun shouldNotFilter(request: HttpServletRequest): Boolean {
        return request.servletPath.equals("/user") // 로그인 요청에서는 이 filter가 동작하지 않도록 함
    }
}
