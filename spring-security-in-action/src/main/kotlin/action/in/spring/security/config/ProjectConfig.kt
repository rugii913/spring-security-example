package action.`in`.spring.security.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.Customizer
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.core.userdetails.User
import org.springframework.security.crypto.password.NoOpPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.provisioning.InMemoryUserDetailsManager
import org.springframework.security.web.SecurityFilterChain

@Configuration
class ProjectConfig {

    @Bean
    fun configure(http: HttpSecurity): SecurityFilterChain {
        http.httpBasic(Customizer.withDefaults());
        http.authorizeHttpRequests { it.anyRequest().authenticated() }

        val user = User.withUsername("john").password("12345").authorities("read").build()
        val userDetailsService = InMemoryUserDetailsManager(user)
        http.userDetailsService(userDetailsService)

        return http.build()
    }

    @Bean
    fun passwordEncoder(): PasswordEncoder = NoOpPasswordEncoder.getInstance()
}
