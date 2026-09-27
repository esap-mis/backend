package ru.javavlsu.kb.notificationservice.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer
import org.springframework.security.web.SecurityFilterChain

/**
 * Сервис не выставлен наружу: трафик идёт через API Gateway и Kafka.
 * Авторизацию на нём оставляем выключенной, но цепочку фильтров задаём явно,
 * чтобы actuator и внутренние эндпоинты не оказались за требованием пароля.
 */
@Configuration
class InternalSecurityConfig {

    @Bean
    fun internalSecurityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http.csrf(AbstractHttpConfigurer::disable)
            .authorizeHttpRequests { it.anyRequest().permitAll() }
            .httpBasic(AbstractHttpConfigurer::disable)
            .formLogin(AbstractHttpConfigurer::disable)
        return http.build()
    }
}
