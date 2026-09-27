package ru.javavlsu.kb.gateway.config;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Единая точка входа. Фронт ходит только сюда и не знает о внутренних сервисах.
 * Публичные пути (/api/auth/login, /api/chat) идут без JWT-фильтра.
 */
@Configuration
public class GatewayRoutesConfig {

    @Bean
    public RouteLocator esapRoutes(RouteLocatorBuilder builder,
                                  GatewayRoutesProperties properties,
                                  JwtAuthFilterFactory jwtAuthFilterFactory) {
        // В Spring Cloud Gateway 5.x у GatewayFilterSpec больше нет overload filter(factory, config),
        // поэтому фильтр получаем напрямую из фабрики.
        GatewayFilter jwt = jwtAuthFilterFactory.apply(new JwtAuthFilterFactory.Config());
        return builder.routes()
                .route("auth", r -> r.path("/api/auth/**", "/api/clinic/**", "/api/doctor/**", "/api/patient/**")
                        .filters(f -> f.filter(jwt))
                        .uri(properties.getAuth()))
                .route("clinic", r -> r.path("/api/medicalCard/**")
                        .filters(f -> f.filter(jwt))
                        .uri(properties.getClinic()))
                .route("schedule", r -> r.path("/api/schedule/**")
                        .filters(f -> f.filter(jwt))
                        .uri(properties.getSchedule()))
                .route("core", r -> r.path("/api/chat/**", "/api/notification/**")
                        .uri(properties.getCore()))
                .build();
    }
}
