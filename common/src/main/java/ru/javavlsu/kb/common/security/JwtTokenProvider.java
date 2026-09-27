package ru.javavlsu.kb.common.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * Генерация и валидация JWT. Токен самодостаточен: содержит id, login, роли и клинику,
 * поэтому сервисы не ходят в auth-service за профилем пользователя на каждом запросе.
 */
@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

    public static final String CLAIM_LOGIN = "login";
    public static final String CLAIM_USER_ID = "user_id";
    public static final String CLAIM_ROLES = "roles";
    public static final String CLAIM_CLINIC_ID = "clinic_id";
    private static final String SUBJECT = "Person details";

    private final EsapProperties properties;

    private Algorithm algorithm() {
        return Algorithm.HMAC256(properties.getJwt().getSecret());
    }

    public String generateToken(AuthenticatedUser user) {
        Date expirationDate = Date.from(java.time.Instant.now().plus(properties.getJwt().getExpiration()));
        return JWT.create()
                .withSubject(SUBJECT)
                .withClaim(CLAIM_LOGIN, user.login())
                .withClaim(CLAIM_USER_ID, user.id())
                .withClaim(CLAIM_ROLES, List.copyOf(user.roles()))
                .withClaim(CLAIM_CLINIC_ID, user.clinicId())
                .withIssuedAt(new Date())
                .withIssuer(properties.getJwt().getIssuer())
                .withExpiresAt(expirationDate)
                .sign(algorithm());
    }

    public AuthenticatedUser validateToken(String token) throws JWTVerificationException {
        JWTVerifier verifier = JWT.require(algorithm())
                .withSubject(SUBJECT)
                .withIssuer(properties.getJwt().getIssuer())
                .build();
        DecodedJWT jwt = verifier.verify(token);
        List<String> roles = jwt.getClaim(CLAIM_ROLES).asList(String.class);
        Long clinicId = jwt.getClaim(CLAIM_CLINIC_ID).asLong();
        return new AuthenticatedUser(
                jwt.getClaim(CLAIM_USER_ID).asLong(),
                jwt.getClaim(CLAIM_LOGIN).asString(),
                roles == null ? Set.of() : Set.copyOf(roles),
                clinicId
        );
    }
}
