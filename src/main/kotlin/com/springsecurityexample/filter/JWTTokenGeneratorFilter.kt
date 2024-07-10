package com.springsecurityexample.filter

import com.springsecurityexample.constants.SecurityConstants
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.filter.OncePerRequestFilter
import java.nio.charset.StandardCharsets
import java.util.*

class JWTTokenGeneratorFilter : OncePerRequestFilter() {

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        // BasicAuthenticationFilter에서 credential을 이용한 인증 후, 이를 통해 생성된 Authentication 객체를 이용
        val authentication = SecurityContextHolder.getContext().authentication
        if (null != authentication) {
            val key = Keys.hmacShaKeyFor(SecurityConstants.JWT_KEY.toByteArray(StandardCharsets.UTF_8))
            val now = Date()

            val jwt = Jwts.builder() // builder를 통해 JWT를 만드는 과정
                .setIssuer(SecurityConstants.JWT_ISSUER) // JWT를 발행하는 조직
                .setSubject(SecurityConstants.JWT_SUBJECT)
                .claim("username", authentication.name)
                .claim("authorities", populateAuthorities(authentication.authorities))
                // token에 비밀번호가 들어가서는 안 됨
                .setIssuedAt(now)
                .setExpiration(Date(now.time + 28_000_000)) // 8시간(ms 단위) 만료 시간
                .signWith(key) // 생성된 SecretKey를 이용해 디지털 서명
                .compact() // JWT 생성

            response.setHeader(SecurityConstants.JWT_HEADER, jwt)
        }

        filterChain.doFilter(request, response)
    }
    
    override fun shouldNotFilter(request: HttpServletRequest): Boolean {
        /*
        * - 이 filter를 통한 JWT 생성은 로그인 과정 중에만 실행되도록 함
        *   - 다른 요청에서는 이 filter를 사용하지 못하도록 함
        *   - 현재 프로젝트에서 클라이언트 애플리케이션이 로그인 시 호출하는 경로가 "/user"
        * - 즉 하나의 요청 중 앞선 필터에서 자격 증명(credential)을 이용해 인증했을 때만, 이 filter에서 JWT를 발급
        * */
        return !request.servletPath.equals("/user") // 호출된 endpoint가 "/user"가 아닌 경우 이 filter를 거치지 않음
    }

    private fun populateAuthorities(authorities: Collection<GrantedAuthority>): String {
        val authoritiesSet = mutableSetOf<String>()
        for (authority in authorities) {
            authoritiesSet.add(authority.authority)
        }
        return authoritiesSet.joinToString(separator = ".")
    }
}
