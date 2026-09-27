package ru.javavlsu.kb.gateway.config;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.JWTVerifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * Валидирует JWT на входе. Сервисы тоже проверяют токен — это defense in depth,
 * но шлюз отсекает невалидный трафик до внутренней сети.
 */
@Component
public class JwtAuthFilterFactory extends AbstractGatewayFilterFactory<JwtAuthFilterFactory.Config> {

    private static final Logger log = LoggerFactory.getLogger(JwtAuthFilterFactory.class);

    private static final List<String> PERMIT_ALL = List.of(
            "/api/auth/login",
            "/api/auth/registration/clinic",
            "/api/auth/password/reset",
            "/api/chat"
    );

    private final JWTVerifier verifier;

    public JwtAuthFilterFactory(GatewaySecurityProperties properties) {
        super(Config.class);
        this.verifier = JWT.require(Algorithm.HMAC256(properties.getSecret()))
                .withSubject("Person details")
                .withIssuer(properties.getIssuer())
                .build();
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            String path = exchange.getRequest().getPath().value();
            if (isPermitted(path)) {
                return chain.filter(exchange);
            }
            String header = exchange.getRequest().getHeaders().getFirst("Authorization");
            if (header == null || !header.startsWith("Bearer ")) {
                return unauthorized(exchange, "Missing bearer token");
            }
            try {
                verifier.verify(header.substring(7));
            } catch (JWTVerificationException e) {
                return unauthorized(exchange, "Invalid JWT token");
            }
            return chain.filter(exchange);
        };
    }

    private boolean isPermitted(String path) {
        for (String pattern : PERMIT_ALL) {
            if (pattern.endsWith("/**")) {
                if (path.startsWith(pattern.substring(0, pattern.length() - 3))) {
                    return true;
                }
            } else if (pattern.equals(path) || path.startsWith(pattern + "/")) {
                return true;
            }
        }
        return false;
    }

    private Mono<Void> unauthorized(ServerWebExchange exchange, String message) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        log.debug("Rejected {} {}: {}", exchange.getRequest().getMethod(), exchange.getRequest().getURI(), message);
        return response.setComplete();
    }

    public static class Config {
    }
}
