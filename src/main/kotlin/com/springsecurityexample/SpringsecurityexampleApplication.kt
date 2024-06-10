package com.springsecurityexample

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

// - 패키지가 적절하다면 명시할 필요 없음
// @EnableJpaRepositories(basePackages = ["com.springsecurityexample.repository"])
// @EntityScan(basePackages = ["com.springsecurityexample.model"])
// - Spring Boot를 사용한다면 SpringSecurity를 사용하더라도 굳이 명시할 필요 없음
// @EnableWebSecurity
@SpringBootApplication
class SpringsecurityexampleApplication

fun main(args: Array<String>) {
    runApplication<SpringsecurityexampleApplication>(*args)
}
