package com.springsecurityexample

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity

// - Spring Boot를 사용한다면 SpringSecurity를 사용하더라도 @EnableWebSecurity를 굳이 명시할 필요 없음
// @EnableWebSecurity(debug = true) // debug = true 옵션을 주기 위해 명시, 기본값 false // **debug 옵션은 운영 환경에서는 사용 x**
// - 패키지가 적절하다면 아래는 명시할 필요 없음
// @EnableJpaRepositories(basePackages = ["com.springsecurityexample.repository"])
// @EntityScan(basePackages = ["com.springsecurityexample.model"])
@SpringBootApplication
class SpringsecurityexampleApplication

fun main(args: Array<String>) {
    runApplication<SpringsecurityexampleApplication>(*args)
}
