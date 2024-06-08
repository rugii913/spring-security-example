package com.springsecurityexample.config

import org.springframework.boot.autoconfigure.security.SecurityProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.annotation.Order
import org.springframework.security.config.Customizer
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.web.SecurityFilterChain

@Configuration
class ProjectSecurityConfig {
    /*
    * - default configuration은
    *   - 라이브러리 org.springframework.boot:spring-boot-autoconfigure:3.2.6의 spring-boot-autoconfigure-3.2.6.jar
    *   - org.springframework.boot.autoconfigure.security.servlet 패키지의
    *   - SpringBootWebSecurityConfiguration 클래스의 내부 static 클래스인 SecurityFilterChainConfiguration의
    *   - defaultSecurityFilterChain() 메서드
    * - 개발자가 SecurityFilterChain 빈을 정의할 경우 default 설정은 무시되고, security configuration의 모든 사항을 명시해줘야 함
    * - Spring Security 6.1, Spring Boot 3.1.0 버전부터 lambda DSL 스타일 사용 권장
    *   - 자동 들여쓰기로 구성의 가독성 향상
    *   - 설정 옵션 연결 시 .and() 사용할 필요 없음
    *   - Spring Integration, Spring Cloud Gateway와 같은 다른 Spring DSL과 유사한 설정 방식
    *   - (참고) https://docs.spring.io/spring-security/reference/migration-7/configuration.html
    *     - Spring Security 7부터 기존 방식 완전 제거
    * */

    @Bean
    @Order(SecurityProperties.BASIC_AUTH_ORDER)
    fun defaultSecurityFilterChain(http: HttpSecurity): SecurityFilterChain {
        /* SecurityFilterChainConfiguration의 defaultSecurityFilterChain() 메서드 그대로 가져온 형태 */
        http.authorizeHttpRequests { requests ->
            requests.anyRequest().authenticated()
        }
        http.formLogin(Customizer.withDefaults())
        http.httpBasic(Customizer.withDefaults())
        return http.build()
    }
}
