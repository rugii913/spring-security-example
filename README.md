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
