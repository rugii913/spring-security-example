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
    /*
    * - http.authorizeHttpRequests() 살펴보기
    *   - 위 메서드의 parameter type은 org.springframework.security.config 패키지의
    *     - Customizer<org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer.AuthorizationManagerRequestMatcherRegistry>
    *   - org.springframework.security.config 패키지의 Functional Interface인 Customizer는
    *     - T를 파라미터로 받고 return type은 void인 abstract 메서드 customize()를 갖고 있음
    *       - cf. 파라미터 T를 받고 아무 일도 하지 않는 static method withDefaults()도 갖고 있음 → default 그대로 이용하고 싶을 때 사용
    *   - HttpSecurity의 authorizeHttpRequests() 메서드의 본문은 간단함
    *     - ApplicationContext context = this.getContext();
    *     - authorizeHttpRequestsCustomizer.customize(((AuthorizeHttpRequestsConfigurer)this.getOrApply(new AuthorizeHttpRequestsConfigurer(context))).getRegistry());
    *     - return this;
    *   - 사용자가 구현한 Customizer를 받아서 customize()를 호출
    *     - 그런데 위에서 확인했듯이 generic type으로 customize()가 받는 parameter type을 AuthorizationManagerRequestMatcherRegistry로 제한해두었음
    *     - 이에 따라 Customizer를 구현할 때 AuthorizationManagerRequestMatcherRegistry type이 갖고 있는
    *     - requestMatchers() 등의 메서드를 활용할 수 있음
    *   - 따라서 실제로 customize()를 구현할 때 조작하는 인자는
    *     - AuthorizationManagerRequestMatcherRegistry type의 변수이지만
    *     - 개발자가 생각하기에는 requests로 추상화시켜서 사용한다고 생각할 수 있음
    * */

    @Bean
    @Order(SecurityProperties.BASIC_AUTH_ORDER)
    fun defaultSecurityFilterChain(http: HttpSecurity): SecurityFilterChain {
        return http
            .authorizeHttpRequests { requests ->
                requests
                    .requestMatchers("/my-account", "my-balance", "/my-loans", "/my-cards").authenticated()
                    .requestMatchers("/notices", "/contact").permitAll()
            }
            .formLogin(Customizer.withDefaults())
            .httpBasic(Customizer.withDefaults())
            .build()
    }
}
