package com.mgleska.mmcqjava2.customer.support;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.mgleska.mmcqjava2.shared.exception.AppAuthException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class TokenServiceTest {

    private static final String SECRET = "test-secret";
    private static final Instant FUTURE_INSTANT = Instant.parse("2099-01-01T00:00:00Z");
    private static final Instant PAST_INSTANT = Instant.parse("2000-01-01T00:00:00Z");

    private final TokenService tokenService = new TokenService();

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(tokenService, "appSecret", SECRET);
        ReflectionTestUtils.setField(tokenService, "tokenTtl", 3600);
    }

    @Test
    void decodesAccessTokenIssuedByNewAccessToken() {
        var jwt = tokenService.newAccessToken(12, 2);

        var claims = tokenService.decodeAccessToken(jwt);

        assertThat(claims.get("uid").asInt()).isEqualTo(12);
        assertThat(claims.get("stid").asInt()).isEqualTo(2);
    }

    @Test
    void throwsWhenTokenSignedWithDifferentSecret() {
        var jwt = JWT.create()
            .withSubject("acc")
            .withClaim("uid", 12)
            .withClaim("stid", 2)
            .withExpiresAt(FUTURE_INSTANT)
            .sign(Algorithm.HMAC256("other-secret"));

        assertThatThrownBy(() -> tokenService.decodeAccessToken(jwt))
            .isInstanceOf(AppAuthException.class);
    }

    @Test
    void throwsWhenTokenIsExpired() {
        var jwt = JWT.create()
            .withSubject("acc")
            .withClaim("uid", 12)
            .withClaim("stid", 2)
            .withExpiresAt(PAST_INSTANT)
            .sign(Algorithm.HMAC256(SECRET));

        assertThatThrownBy(() -> tokenService.decodeAccessToken(jwt))
            .isInstanceOf(AppAuthException.class);
    }

    @Test
    void throwsWhenTokenSubjectIsNotAccessType() {
        var jwt = JWT.create()
            .withSubject("other")
            .withClaim("uid", 12)
            .withExpiresAt(FUTURE_INSTANT)
            .sign(Algorithm.HMAC256(SECRET));

        assertThatThrownBy(() -> tokenService.decodeAccessToken(jwt))
            .isInstanceOf(AppAuthException.class);
    }

    @Test
    void throwsWhenTokenHasNoSubject() {
        var jwt = JWT.create()
            .withClaim("uid", 12)
            .withExpiresAt(FUTURE_INSTANT)
            .sign(Algorithm.HMAC256(SECRET));

        assertThatThrownBy(() -> tokenService.decodeAccessToken(jwt))
            .isInstanceOf(AppAuthException.class);
    }
}
