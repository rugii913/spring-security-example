package com.springsecurityexample.config

import com.springsecurityexample.filter.*
import org.springframework.boot.autoconfigure.security.SecurityProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.annotation.Order
import org.springframework.security.config.Customizer
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configurers.CorsConfigurer
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter
import org.springframework.security.web.csrf.CookieCsrfTokenRepository
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler
import org.springframework.web.cors.CorsConfiguration

@EnableMethodSecurity(prePostEnabled = true, /*securedEnabled = true, jsr250Enabled = true,*/)
/*
* - method level security 활성화 및 @PreAuthorize, @PostAuthorize 등 개별 annotation 동작 설정
* - securedEnabled, jsr250Enabled element들은 @Secured, @RoleAllowed 등의 annotation을 사용해야 하는 경우에 true 값 지정
* */
@EnableWebSecurity
/*
* - @EnableMethodSecurity, @EnableWebSecurty는 configuration class 아무 곳에 붙여도 됨
*   - 보통 @SpringBootApplication가 붙은 main 함수가 있는 클래스에 붙이는 것으로 보임
* */
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
            /*
            * // session 기반으로 인증하지 않고, JWT 기반으로 인증하며 주석처리
            * .securityContext { it.requireExplicitSave(false) }
            * .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.ALWAYS) }
            * */
            .sessionManagement{ it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) } // SessionCreationPolicy.STATELESS - HttpSession을 생성하지 않고, SecurityContext에서도 이를 활용하지 않음
            .cors { getCorsConfigurer(it) }
            .csrf { getCsrfConfigurer(it) }
            .addFilterBefore(RequestValidationBeforeFilter(), BasicAuthenticationFilter::class.java)
            // credential을 이용한 인증 filter 전에 JWT를 이용한 인증 filter를 거치도록 함
            .addFilterBefore(JWTTokenValidatorFilter(), BasicAuthenticationFilter::class.java)
            /*
            *  addFilterAt()은 atFilter의 argument로 지정한 그 위치에 filter를 등록함
            * - 같은 위치의 filter들은 어떤 순서대로 등록될지 모름(non-deterministic), 같은 위치의 filter를 갈아 끼우는 게 아님에 유의, filter를 사용하지 않으려면 아예 등록하지 말아야 함
            * - 실무에서 거의 사용할 일이 없을 것이고, 만약 사용한다면 등록 순서에 문제가 없는지, 비즈니스 로직에 다른 영향은 없는지 유의해야 함
            * */
            .addFilterAt(AuthoritiesLoggingAtFilter(), BasicAuthenticationFilter::class.java)
            // addFilterAfter(..)의 afterFilter의 argument로 같은 built-in filter를 지정한 경우, addFilterAfter() 순서대로 등록
            .addFilterAfter(CsrfCookieFilter(), BasicAuthenticationFilter::class.java)
            .addFilterAfter(AuthoritiesLoggingAfterFilter(), BasicAuthenticationFilter::class.java)
            .addFilterAfter(JWTTokenGeneratorFilter(), BasicAuthenticationFilter::class.java)
            .authorizeHttpRequests { requests ->
                requests
//                    .requestMatchers("/my-account").hasAuthority("VIEWACCOUNT")
//                    .requestMatchers("/my-balance").hasAnyAuthority("VIEWACCOUNT", "VIEWBALANCE")
//                    .requestMatchers("/my-loans").hasAuthority("VIEWLOANS")
//                    .requestMatchers("/my-cards").hasAuthority("VIEWCARDS")
                    .requestMatchers("/my-account").hasRole("USER")
                    .requestMatchers("/my-balance").hasAnyRole("USER", "ADMIN")
                    .requestMatchers("/my-loans").authenticated()//.hasRole("USER") // method level security 예제를 위해 변경 
                    .requestMatchers("/my-cards").hasRole("USER")
                    .requestMatchers("/contact", "/user").authenticated()
                    .requestMatchers("/notices", "/register").permitAll()
            }
            .formLogin(Customizer.withDefaults())
            .httpBasic(Customizer.withDefaults())
            .build()
    }

    private fun getCorsConfigurer(configurer: CorsConfigurer<HttpSecurity>) = configurer
        .configurationSource {
            CorsConfiguration()
                .also { it.addAllowedOrigin("http://localhost:4200") }
                .also { it.addAllowedMethod("*") }
                .also { it.allowCredentials = true }
                .also { it.addAllowedHeader("*") }
                .also { it.addExposedHeader("Authorization") } // 이 HTTP header에 JWT를 담아서 보낼 것 // cf. CSRF 관련 header는 framework에서 추가한 header이므로 framework에서 알아서 해결할 것 → 신경쓰지 않아도 됨  
                .also { it.maxAge = 3600L } // Access-Control-Max-Age를 뜻함(Cache-Control의 max-age가 아님)
        }

    private fun getCsrfConfigurer(configurer: CsrfConfigurer<HttpSecurity>) = configurer
        .ignoringRequestMatchers("/register", "/contact") // 일단 "/contact" 엔드포인트를 csrf 보호 무시 대상으로 설정
        .csrfTokenRequestHandler(CsrfTokenRequestAttributeHandler().also { it.setCsrfRequestAttributeName("_csrf") }) // csrfRequestAttributeName property의 기본 값이 "_csrf"이지만 명시적으로 보여주기 위해 작성한 코드
        // CSRF 토큰을 처리하는 로직이 있는 객체를 지정
        // - CsrfFilter의 doFilterInternal() 안에서 CsrfTokenRequestAttributeHandler의 handle()을 호출
        //   - cf. https://docs.spring.io/spring-security/reference/servlet/exploits/csrf.html#csrf-token-request-handler
        //   - 다른 구현 클래스로 XorCsrfTokenRequestAttributeHandler이 있으며, 이 객체를 사용하는 것이 기본값
        // - handle()에서 csrfRequestAttributeName property의 값을 이용해 토큰을 처리함
        .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
        // CSRF 토큰을 담을 공간을 지정
        // - HttpSessionCsrfTokenRepository는 서버의 세션에 저장, CookieCsrfTokenRepository는 클라이언트의 세션 쿠키에 저장하도록 동작
        // - HttpOnly 속성을 false로 해야 스크립트로 쿠키 값을 읽을 수 있음


/*
- InMemoryUserDetailsManager 관련 주석 처리
    /*
    * - org.springframework.boot.autoconfigure.security 패키지의 User가 아닌 org.springframework.security.core.userdetails 패키지의 User
    *   - org.springframework.security.core.userdetails.User는 UserDetails 인터페이스를 구현하는 클래스
    *   - User의 빌더 패턴 내부 static 클래스인 UserBuilder를 이용하여 UserDetails type 객체를 반환
    *     - 빌더 패턴 UserBuilder 객체를 이용하여 username, password, authorities 등을 property로 설정하고
    *     - build() 호출 시 입력된 property를 활용하여 User 객체를 생성하여 반환하는 코드 확인 가능
    * - InMemoryUserDetailsManager() 생성자에서는 다음의 로직을 실행
    *   - createUser()를 호출하여 인자로 받은 UserDetails type 객체를
    *   - InMemoryUserDetailsManager 객체에서 알고 있는 HashMap 자료 구조 user property에 put 해둠
    * */
    @Bean
    fun userDetailsService(): InMemoryUserDetailsManager {
        /*
        * InMemoryUserDetailsManager 사용 방법 1
        * - User.withDefaultPasswordEncoder() 호출 시 return으로 받은 UserBuilder 객체 사용
        * - 간단하게 DefaultPasswordEncoder를 사용하는 형태
        *   - withDefaultPasswordEncoder() 메서드 내에서는 기본적인 PasswordEncoder()를 property로 갖고 있도록 UserBuilder 객체를 설정
        * - cf. UserBuilder type을 반환하는 withDefaultPasswordEncoder() 메서드는 deprecated 표시가 되어있지만, production에서 사용하기 부적합함을 경고하기 위해 표시한 것이고, 지원 중단 예정은 아님
        * */
//        val admin1: UserDetails = User
//            .withDefaultPasswordEncoder()
//            .username("admin1")
//            .password("admin1")
//            .authorities("admin")
//            .build()
//
//        val user1: UserDetails = User
//            .withDefaultPasswordEncoder()
//            .username("user1")
//            .password("user1")
//            .authorities("read")
//            .build()

        /*
        * InMemoryUserDetailsManager 사용 방법 2
        * - User.withUsername() 호출 시 return으로 받은 UserBuilder 객체 사용
        * - cf. 동작 관련하여 다음 참고 - https://velog.io/@fastdodge7/Spring-Security-PasswordEncoder를-빈으로-등록하는-이유는
        *   - UserDetails를 등록할 때는 passwordEncoder property를 직접 등록해줬고
        *   - InitializeUserDetailsBeanManagerConfigurer의 configure() 메서드에서 볼 수 있듯
        *     - 빈으로 등록된 PasswordEncoder가 있다면 DaoAuthenticationProvider의 setPasswordEncoder()를 호출하여 
        *     - 등록된 PasswordEncoder 빈을 DaoAuthenticationProvider(AuthenticationProvider의 하위 type)의 property로 set 해줌
        * - PasswordEncoder는 NoOpPasswordEncoder를 빈으로 등록하여 사용
        * */
        val admin1: UserDetails = User
            .withUsername("admin1")
            .password("admin1")
            .passwordEncoder { password -> password } // 예제와 다르게 명확하게 추가해둠 - 하지만 UserBuilder 초기화 시 property로 갖고 있는 passwordEncoder와 같기 때문에 없어도 상관 없음
            .authorities("admin")
            .build()

        val user1: UserDetails = User
            .withUsername("user1")
            .password("user1")
            .passwordEncoder { password -> password } // 예제와 다르게 명확하게 추가해둠 - 하지만 UserBuilder 초기화 시 property로 갖고 있는 passwordEncoder와 같기 때문에 의미는 상관 없음
            .authorities("read")
            .build()

        return InMemoryUserDetailsManager(admin1, user1)
    }
*/

/*
- JdbcUserDetailsManager 관련 주석 처리 - 이후 커스텀 UserDetailsService인 SercurityExampleUserDetailsService 사용
    /* UserDetailsService로 JdbcUserDetailsManager 객체를 빈으로 등록하여 사용 */
    @Bean
    fun userDetailsService(dataSource: DataSource): UserDetailsService = JdbcUserDetailsManager(dataSource)
*/
    
/*
- NoOpPasswordEncoder 빈 등록 주석 처리
    /*
    * - cf. NoOpPasswordEncoder 역시 deprecated 표시가 되어있지만, production에서 사용하기 부적합함을 경고하기 위해 표시한 것이고, 지원 중단 예정은 아님
    *   - 단지 CharSequence type rawPassword에 대해 toString()을 호출하여 단지 plain text로 다루도록 함
    * */
    @Bean
    fun passwordEncoder(): PasswordEncoder = NoOpPasswordEncoder.getInstance()    
*/

    @Bean
    fun passwordEncoder(): PasswordEncoder = BCryptPasswordEncoder()

/*
- MySQL로 변경하며 주석 처리
    /*
    * - 참고 https://dukcode.github.io/spring/h2-console-with-spring-security/
    *   - h2 console에 대한 요청이 Spring Security 필터를 통과하지 않도록 함
    * - cf. Spring 시작 시 WARN 메시지로 ignore 하는 방식은 권장되지 않는다는 출력 발생, 하지만 production에서 사용할 것이 아니므로 괜찮음
    * */
    @Bean
    @ConditionalOnProperty(name = ["spring.h2.console.enabled"], havingValue = "true")
    fun configureH2ConsoleEnable() =
        WebSecurityCustomizer { web -> web.ignoring().requestMatchers(PathRequest.toH2Console()) }
*/
}
