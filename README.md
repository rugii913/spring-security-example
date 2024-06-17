# spring-security-example

## authentication 관련

### 기억해둘 interface들

#### (1) HttpSecurityBuilder
- SecurityBuilder의 subtype
- HttpSecurity 등이 구현 클래스

#### (2) Authentication
- Principal의 subtype
- UsernamePasswordAuthenticationToken 등이 구현 클래스

#### (3) Authentication 처리 관련
- (3-1) AuthenticationManager
  - ProviderManager 등이 구현 클래스
    - AuthenticationProvider type 객체들을 호출하여 작업 수행
    - providers property를 갖고 있고, 이는 List<AuthenticationProvider> type

- (3-2) AuthenticationProvider
  - property들을 호출하여 인증을 진행하고, 인증 성공 여부에 따라 Authentication 객체를 수정해주는 역할 
  - 추상 클래스 AbstractUserDetailsAuthenticationProvider의 supertype
  - DaoAuthenticationProvider 등이 구현 클래스
    - UserDetailsService, PasswordEncoder type property들을 호출하여 작업 수행
      - HttpSecurityConfiguration 구성 시 InitializeUserDetailsManagerConfigurer의 configure() 메서드에서
      - UserDetailsService 타입 빈을 가져와서 DaoAuthenticationProvider의 setUserDetailsService()을 이용해 userDetailsService property를 set 해줌 
    - DaoAuthenticationProvider에서 명확하게 UserDetailsService type의 property를 이용하기 때문에
      - UserDetailsService를 이용하고 싶지 않다면 AuthenticationProvider도 DaoAuthenticationProvider가 아닌 다른 구현 클래스를 사용해야함   
    
- (3-3) UserDetailsService
  - loadUserByUsername() 메서드를 정의
    - storage에 저장되어 있는 사용자 정보를 바탕으로 UserDetails 객체를 가져오는 역할 
  - UserDetailsManager의 supertype
    - UserDetails 객체를 가져오는 역할 외의 사용자 정보에 대한 CRUD 작업 역할 메서드들을 정의
    - createUser(), updateUser(), deleteUser(), changePassword(), userExists() 등 정의
  - InMemoryUserDetailsManager, JdbcUserDetailsManager, LdapUserDetailsService 등 샘플 구현 클래스 있음
    - 위 UserDetailsService, UserDetailsManager 인터페이스의 역할들을 구현
    - cf. JdbcUserDetailsManager를 사용할 경우 특정 schema를 따라야 함
      - spring-security-core-6.2.4.jar/org/springframework/security/core/userdetails/jdbc/users.ddl(JdbcDaoImpl의 DEFAULT_USER_SCHEMA_DDL_LOCATION에서 참조하는 경로) 파일 참고
      - 혹은 https://docs.spring.io/spring-security/reference/servlet/authentication/passwords/jdbc.html 문서의 schema 참고
      - MySQL처럼 위의 script와 호환이 안 되는 경우, 별도로 DDL 작성 후 실행해야 함
        - id 컬럼을 추가하는 등 기본 schema와 충돌하지 않는 선에서 다른 schema를 구성할 수 있음
        - 루트/src/main/resources/sql/scripts.sql 파일 참고
    - cf. LdapUserDetailsService는 별도 라이브러리 구성 및 storage로 LDAP 서버를 갖고 있어야 함

- (3-4) UserDetails
  - User 등 샘플 구현 클래스 있음

- (3-5) PasswordEncoder
  - BCryptPasswordEncoder, NoOpPasswordEncoder 등이 구현 클래스

#### (4) Security Context 관련
- (4-1) SecurityContextHolder
  - cf. supertype, subtype 없이 구현 클래스 자체로 존재함
- (4-2) SecurityContext
  - SecurityContextImpl 등이 구현 클래스

### 동작 방식(!추후 보완 작성 필요!)
- ~~HttpSecurityBuilder, SecurityContext 등에 대한 보완 작성 필요~~
  - AbstractAuthenticationProcessingFilter(ex. UsernamePasswordAuthenticationFilter) 빈은
    - attemptAuthentication() 메서드에서(ex. 세부 구현은 UsernamePasswordAuthenticationFilter의 attemptAuthentication() 참고)
    - AuthenticationManager 객체의 authenticate() 호출 후 return으로 Authentication 객체를 받음
    - 인증 성공인 경우 자신의 successfulAuthentication() 메서드를 호출하여 SecurityContext에 대한 작업 처리
    - 인증 실패인 경우 자신의 unsuccessfulAuthentication() 메서드를 호출하여 이후 로직 처리
#### AuthenticationManager(ProviderManager) 및 AuthenticationProvider의 책임
- 개요
  - AuthenticationProvider 객체는
    - UserDetailsService type 객체가 storage에 저장된 사용자 정보를 UserDetails type으로 불러오도록 시킴
    - 이후 불러온 UserDetails type 객체와 인증 요청을 비교 후 그 결과를 Authentication type으로 변환
  - ProviderManager 객체는
    - AuthenticationProvider 객체가 변환해준 Authentication 객체를
    - 자신을 호출한 AbstractAuthenticationProcessingFilter type 빈으로 return
- ProviderManger(AuthenticationManager의 구현 클래스)에서
  - authentication() 메서드 호출 시 Authentication type 객체를 parameter로 받아
  - AuthenticationProvider type 객체의 authenticate()을 호출
    - cf. AbstractUserDetailsAuthenticationProvider의 authenticate()에서 구현 코드 확인 가능
- AbstractUserDetailsAuthenticationProvider의 경우 authenticate() 메서드 내부에서 retrieveUser(), createSuccessAuthentication() 등 호출
  - AbstractUserDetailsAuthenticationProvider의 각 구현 클래스에 따라 서로 다른 retrieveUser() 구현 코드 등 수행
    - AuthenticationProvider의 각 구현 객체(ex. DaoAuthenticationProvider 객체)에서
    - property로 갖고 있는 UserDetailsService type, PasswordEncoder type 등을 활용
    - 적절하게 Authentication type 객체에 대한 인증 작업을 진행
      - UserDetailsService를 통해 UserDetails를 가져와 인증 요청과 비교
      - PasswordEncoder를 통해 인증 요청에서 입력된 password와 UserDetails의 password 확인 등
  - 인증 성공으로 판단될 경우
    - createSuccessAuthentication()에 Authentication 객체를 인자로 넘겨 호출하며
    - Authentication 객체를 적절하게 수정 후 return

## password를 다루는 방법

### Encoding vs. Encryption vs. Hashing
- 각각을 구분할 것

### PasswordEncoder 인터페이스의 기능
- 세 가지 기능 → encode(), matches(), upgradeEncoding()

### PasswordEncoder의 여러 구현 클래스들
- cf. DelegatingPasswordEncoder 및 PasswordEncoderFactories에서 여러 구현 클래스들을 확인해볼 수 있음
- NoOp, Standard, Pbkdf2, BCrypt, SCrypt, Argon2, ... 등 여러 구현 클래스
  - NoOp은 운영 환경에서 사용 x, Standard는 레거시 시스템 지원용이므로 사용 x, Pbkdf2는 하드웨어 발전에 따라 brute force 공격에 대해 안전하지 않음
  - BCrypt, Scrypt, Argon2 등은 각각의 특징에 따라 CPU, 메모리, 멀티 스레드 등 자원 사용을 강제하여 brute force 공격을 어렵게 하는 방법들
    - 이들 모두 spring-security-crypto:x.x.x의 org.springframework.security.crypto 패키지에 있음

### BCryptPasswordEncoder
  - BCrypt 사용 후 결과값 중 첫 세 글자(ex. $2a)는 알고리즘 버전과 관련, 그 다음 세 글자(ex. $10)는 로그 라운드 수와 관련
    - BCryptPasswordEncoder의 BCRYPT_PATTERN에서 확인 가능
  - BCryptPasswordEncoder 객체 생성 시, 알고리즘 버전 및 로그 라운드 등을 생성자를 이용해 설정 가능

## AuthenticationProvider 인터페이스와 커스텀 AuthenticationProvider

### 커스텀 AuthenticationProvider의 필요성
- 샘플로 존재하는 AuthenticationProvider로는 만족시킬 수 없는 특정한 요구사항이 있는 경우
  - ex. 특정 나이 이상의 사용자만 접근을 허용하는 로직
  - ex. 허용 국가 목록에 포함된 국가의 사용자만 접근을 허용하는 로직
  - UserDetailsManager는 UserDetails에 대한 CRUD 기능을 담당하므로
    - 단지 커스텀한 UserDetailsManager를 작성하는 것만으로는 충분하지 않고,
    - 커스텀 AuthenticationProvider에서 커스텀한 인증 로직을 작성해야 함
- 요구사항에 따라 여러 인증 방식을 사용하는 경우
  - ex. username과 password를 사용하는 인증 방식, OAuth2를 사용하는 인증 방식, OTP를 사용하는 인증 방식을 함께 사용
    - 각 인증 방식에 대해 적절한 Authentication 객체를 생성하여 사용하도록 하고,
    - 각 인증 방식에 대해 커스텀 AuthenticationProvider를 작성,
    - ProviderManager가 Authentication 객체에 적합한 AuthenticationProvider를 사용할 수 있도록 설정해줘야 함

### cf. Authentication 인터페이스
- Authentication extends Principal, Serializable
  - Principal interface는 java.security 패키지에 있으며, java.base라는 기본적인 라이브러리에 속함
  - Principal이 의존하고 있는 Subject class 역시 java.base 라이브러리에 속하며 javax.security.auth 패키지에 위치함

### AuthenticationProvider 인터페이스의 기능
- 두 가지 기능 → authenticate(), supports()
  - authenticate()로 인증이 실패한 경우 ProviderManager는 다른 AuthenticationProvider 구현체로 인증을 시도함
- cf. 테스트 환경에서는 TestingAuthenticationProvider와 TestingAuthenticationToken을 사용하는 것을 고려
  - 테스트에서 불필요하게 보완 관련 설정까지 신경써야할 필요성을 줄여줌

### AuthenticationProvider type 객체를 호출하는 곳 - ProviderManager(AuthenticationManager의 subtype)
- ProviderManager의 authenticate() 메서드에서 각 AuthenticationProvider에 대해 supports()를 호출하고
  - return으로 true가 돌아오면 AuthenticationProvider의 authenticate()를 호출 
- ProviderManager의 providers property는 언제 초기화 되는가?
  - HttpSecurity configuration 과정에서 AuthenticationManagerBuilder의 List인 providers에 add 된 후
  - AuthenticationManagerBuilder를 이용해 ProviderManager가 생성되는 것으로 보임
  - ProviderManager의 생성자 및 AuthenticationManagerBuilder의 authenticationProvider()에 중단점 걸고 확인 필요
- ProviderManager의 authenticate()에서 인증이 성공한 뒤
  - CredentialsContainer type 객체의 eraseCredentials()을 통해 credential을 제거함
  - 또한 AuthenticationEventPublisher 객체의 publishAuthenticationSuccess()를 호출하는 로직이 있어, 이벤트로 인해 특정 작업이 시작되도록 할 수 있음

### cf. 커스텀 AuthenticationProvider 빈의 등록 과정
- AuthenticationManagerBuilder의 authenticationProvider() 메서드에 중단점 걸고 따라가보면
  - AuthenticationConfiguration의 getAuthenticationManager() 호출 시
  - authBuilder.build()를 통해 AuthenticationManager를 빌더 패턴으로 만든 뒤 property로 갖고 있음
  - 이렇게 AuthenticationManager를 빌더 패턴으로 만드는 과정에서 configure() 과정이 있음
  - 이 configure 과정에서 AuthenticationProvider type 빈을 모두 가져온 뒤
    - 빌더 객체 안의 authenticationProviders property에 AuthenticationProvider 빈 객체를 추가해주는 과정이 있음
    - 따라서 AuthenticationProvider type 객체가 빈으로 등록되면
    - 알아서 AuthenticationManager(구현 클래스는 ProviderManager type)의 providers property에 추가됨 
- 그렇다면 DaoAuthenticationProvider는 왜 명시적인 @Bean, @Component가 필요 없는가?
  - AuthenticationConfiguration의 getAuthenticationManager()에서 authBuilder.build()할 때 흐름이 달라짐
  - 커스텀 AuthenticationProvider가 빈으로 등록되지 않은 경우 
    - InitializeUserDetailsBeanManagerConfigurer의 내부 클래스 InitializeUserDetailsManagerConfigurer type 객체가 작동
    - configure() 메서드에서 DaoAuthenticationProvider 객체를 직접 생성하고 AuthenticationManagerBuilder의 authenticationProviders property에 추가함
- 똑같이 supports()에서 UsernamePasswordAuthenticationToken을 지원할 경우, DaoAuthenticationProvider와의 순서 문제?
  - 커스텀 AuthenticationProvider를 등록할 경우
  - DaoAuthenticationProvider가 ProviderManager의 providers property의 요소로 등록되지 않아 순서 문제도 발생하지 않음
- cf. 실제 런타임에 인증에 사용되는 객체는 정확하게 해당 type의 객체는 아님
  - 빈으로 등록한 객체는 parent로 활용, Security 구성 과정에서 프록시 객체를 한 번 더 생성하고, 실제 인증에는 프록시 객체를 사용   

## CORS와 CSRF

### CORS(Cross-Origin Resource Sharing, 교차 출처 리소스 공유)
- CORS란?
  - 서로 다른 두 origin이 resource를 공유하는 상황을 처리하는 메커니즘
    - origin은 (프로토콜 + 도메인 + 포트)로 판단됨
    - resource는 HTTP 명세에서 말하는 서로 구분하여 식별할 수 있는 것들로 생각하면 될 것
  - CORS가 보안 공격의 일종인 게 아니라 리소스 공유에 대한 규칙
    - 현대 브라우저에서 기본으로 제공하는 기능으로 다른 origin에 대한 데이터 공유, 통신을 가능하게 하는 것
    - 규칙을 설정하지 않으면 기본적으로 다른 origin과의 데이터 공유, 통신은 불가능하도록 브라우저가 작동
  - 참고
    - [MDN 문서 - Cross-Origin Resource Sharing(CORS)](https://developer.mozilla.org/en-US/docs/Web/HTTP/CORS)
    - [W3C Recommendation - Cross-Origin Resource Sharing](https://www.w3.org/TR/2020/SPSD-cors-20200602/)
    - [W3C Wiki 문서 - CORS](https://www.w3.org/wiki/CORS)
    - [기타 블로그 - CSRF, CORS 개념](https://redbinalgorithm.tistory.com/448)
- CORS 규칙을 설정하지 않으면 왜 기본적으로 통신을 막는가?
  - CORS는 W3C 표준으로 정해져있음
    - 현대 브라우저들이 이 표준을 따르고 있음
    - 해당 표준의 목적은 통신을 막는 것이 아니라 오히려 same origin restriction(SOP, Same-Origin Policy)을 제거하기 위한 것
  - 보안 위협 및 기존에 널리 퍼진 same origin restriction을 고려하여
    - 기본 설정으로는 cross-origin 간 resource 공유를 막되
    - 표준을 따르는 일정한 규칙에 의거하여 안전하고 편리하게 cross-origin 간 resource를 공유하자는 것
- Spring 프레임워크에서 CORS를 설정하는 방법
  - (1) org.springframework.web.bind.annotation.CrossOrigin 어노테이션 이용(→ Spring Security 필요 없음)
    - origins property에 값을 지정하여 특정 origin 혹은 모든 origin에 대해서 resource 공유를 허가할 수 있음
      - class 혹은 method에 붙일 수 있는 어노테이션
    - 하지만 컨트롤러 역할을 하는 클래스에 @CrossOrigin을 붙이고, origin을 설정하는 것은 피곤한 작업 
  - (2) Spring Security를 이용하여 전역으로 CORS 설정
    - SecurityFilterChain 빈 등록 시 http.cors() 메서드를 이용해 허용되는 origin, method, header 등을 설정
- cf. CORS 가능한 요청에 해당하지 않아 요청이 막히는 경우 서버 쪽에서는 DB로 쿼리하지도 않음
  - 브라우저는 cross-origin 요청으로 판단되는 경우 OPTIONS 메서드로 preflight 요청을 보내고
  - preflight에 대한 응답 HTTP 메시지의 header로 cross-origin 요청 처리가 가능한 origin인지 아닌지 판단,
  - 요청 처리가 불가능한 origin인 경우 주된 요청을 보내지 않는 것
    - 따라서 서버 쪽의 비즈니스 로직 코드가 실행되지도 않고, DB에 쿼리하는 코드도 당연히 실행되지 않음
  - 반대로 말하면 preflight 요청에서 적절한 Access-Control-Allow-Origin 헤더 등을 발견할 수 있다면
    - 즉 백엔드 서버에서 적절히 헤더를 설정해줬다면 
    - 브라우저가 preflight 요청 후 주된 요청을 보내는 데에 문제가 없음 

### CSRF 또는 XSRF(Cross-Site Request Forgery, 사이트 간 요청 위조)
- CSRF란?
  - (CORS와는 다르게) CSRF는 보안 공격의 한 종류
  - CSRF 기술을 이용한 공격 단계
    - 참고 [기타 블로그 - CSRF란, CSRF 동작원리, CSRF 방어방법](https://devscb.tistory.com/123) 
    - (1) 사용자가 특정 사이트 A에 정상적인 접근을 통해 해당 오리지널 도메인의 cookie가 브라우저에 저장됨
      - cookie는 요청을 받을 도메인 별로 구분되어 저장, 그리고 해당 도메인에 요청 시 해당 도메인에 대한 cookie가 자동으로 담겨 보내짐
    - (2) 사용자가 같은 브라우저에서 악성 사이트에 접속, 악성 링크를 클릭
      - 악성 링크의 내용은 A 도메인 사이트에서 A의 정보를 변경하는 등의 악성 form, 악성 script 등
      - 사용자가 링크 내의 뭔가를 클릭하면 A 사이트 내에서 form을 submit 하거나, A 사이트 내에서 자동으로 해당 내용이 실행되도록 함
    - (3) 악성 링크의 악성 작업이 동작
      - 사용자의 브라우저에 A 도메인에 대한 cookie가 저장되어 있기 때문에, 악성 요청을 위한 A에서의 인증도 문제 없음
      - 다른 도메인에서 A의 API를 호출한 것이 아니라 A 사이트 내에서 A의 API를 호출하는 것이므로 CORS로도 막을 수 없음
        - cf. Origin으로는 구분할 수 없으나, Referer 헤더에 링크를 클릭한 도메인이 기록되므로 1차적으로 Referer를 이용해 막을 수 있음
- CSRF 공격 방어 방법
  - (1) Referer 헤더 이용
    - Referer 헤더에 링크를 클릭한 도메인이 기록되므로 1차적으로 Referer를 이용하여 보호 가능
  - (2) CSRF 토큰 활용 - Spring Security의 CsrfFilter 및 CookieCsrfTokenRepository를 사용한 예시
    - 인증 후 백엔드 서버에서 인증 정보와 함께 CSRF 토큰을 Set-Cookie 헤더로 함께 전송
    - 클라이언트 브라우저는 해당 도메인 cookie에 있는 CSRF 토큰을 session storage에 저장
    - 클라이언트에서 CSRF 보호 필요 API 호출 시
      - session storage에 있는 토큰을 정해진 규칙에 따른 이름을 가진 헤더(ex. X-Xsrf-Token)에 담도록 스크립트 작성
      - cf. CookieCsrfTokenRepository를 사용할 경우 X-Xsrf-Token 헤더 사용
    - Spring Security의 CSRF 필터는 CSRF 보호 필요 API가 호출된 경우
      - 헤더에 담긴 CSRF 토큰과 쿠키에 담긴 CSRF 토큰을 비교
      - 한 쪽에 토큰이 없거나, 토큰이 불일치하는 경우 예외 처리
- CSRF 토큰을 이용한 보호 대상 엔드포인트 지정
  - cf. CSRF 공격 보호 시 Spring Security 기본 동작(요청 HTTP 메서드 관련)
    - CSRF 보안이 적용된 경우 기본 동작으로 server의 resource의 상태 변화를 일으킬 수 있는 요청에는 403 에러로 응답
    - 즉 요청 HTTP 메서드 중 POST, PUT, PATCH, DELETE 등 메서드에 대해서는 security filter 선에서 API 호출을 막음
    - GET 메서드는 resource 상태 변화를 일으키지 않으므로 호출을 막지 않음
  - POST 등 메서드를 이용하더라도 공개된(public) API라면 CSRF 토큰을 이용한 보호 대상에서 제외
    - 민감한 정보 변경을 다루지 않을 것이기 때문
- Spring Security X-Xsrf-Token으로 검색해서 찾은 내용들
  - [기타 블로그 - Cross Site Request Forgery \(CSRF\)](https://kdev.ing/spring-boot-security-csrf/) → Spring Security에서 작동 과정 간략하게 살펴볼 수 있음
  - [기타 블로그 - \[SpringSecurity\] CSRF란? / CSRF Filter 처리 방식](https://tlatmsrud.tistory.com/77) → CSRF 공격 방법에 대한 이해
  - [기타 블로그 - \[스프링 Security\] CSRF 토큰 이야기 - 그래서 개발자는 뭘 하면 되죠](https://blog.paimon.studio/46) → 전체적인 흐름
  - CSRF 공격 자세한 코드
    - [HackTricks - CSRF \(Cross Site Request Forgery\)](https://book.hacktricks.xyz/v/kr/pentesting-web/csrf-cross-site-request-forgery)
    - [기타 블로그 - CSRF\(Cross Site Request Forgery\)](https://xn--ex3bt1ov9l.kr/352)
    - [기타 블로그 - CSRF01 & CSRF02 공격 시나리오](https://jisu069.tistory.com/86)
    - [기타 블로그 - CSRF](https://jisu069.tistory.com/80)
  - [기타 블로그 - Side-project : Spring Security + CSRF Token 활용하기\(1\)](https://velog.io/@kimujin99/Side-project-Spring-Security-CSRF-Token-활용하기1)
  - [기타 블로그 - \[Spring Security\] CORS, CSRF란?](https://jaykaybaek.tistory.com/29)
  - [기타 블로그 - Spring Security CSRF 설정](https://cheese10yun.github.io/spring-csrf/)
- 더 알아볼 것들
  - [Spring Security 공식 문서 - Cross Site Request Forgery \(CSRF\)](https://docs.spring.io/spring-security/reference/servlet/exploits/csrf.html)
  - 클래스들 더 자세히 파보기
    - CsrfFilter, CsrfConfigurer, CsrfTokenRequestAttributeHandler, XorCsrfTokenRequestAttributeHandler, CookieCsrfTokenRepository 등
