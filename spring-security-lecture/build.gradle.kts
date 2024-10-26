import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot") version "3.2.6"
    id("io.spring.dependency-management") version "1.1.5"
    kotlin("jvm") version "1.9.24"
    kotlin("plugin.spring") version "1.9.24"
    kotlin("plugin.jpa") version "1.9.24" // 추가하지 않으면 JPA entity 클래스에 no-arg 생성자 관련 경고 발생
}

group = "com"
version = "0.0.1-SNAPSHOT"

java {
    sourceCompatibility = JavaVersion.VERSION_17
}

repositories {
    mavenCentral()
}

dependencies {
    /* Kotlin 관련 */
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")

    /* Spring Web */
    implementation("org.springframework.boot:spring-boot-starter-web")
    /* Spring Security */
    implementation("org.springframework.boot:spring-boot-starter-security")

    /* JWT 관련 */
    implementation("io.jsonwebtoken:jjwt-api:0.11.5")
    runtimeOnly("io.jsonwebtoken:jjwt-impl:0.11.5")
    runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.11.5")

    /* ldap 관련 */
    implementation("org.springframework.ldap:spring-ldap-core")
    implementation("org.springframework.security:spring-security-ldap")

    /* JDBC */
    implementation("org.springframework.boot:spring-boot-starter-jdbc")
    /* MySQL DBMS */
    runtimeOnly("com.mysql:mysql-connector-j")
    /* Spring Data JPA */
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")

    /* test 관련 */
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.security:spring-security-test") // Spring Security 관련 test 종속성
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs += "-Xjsr305=strict"
        jvmTarget = "17"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}
