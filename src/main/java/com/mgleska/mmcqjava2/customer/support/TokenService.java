package com.mgleska.mmcqjava2.customer.support;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import com.mgleska.mmcqjava2.shared.exception.AppAuthException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;

@Service
public class TokenService {

    private static final String TYPE_ACCESS = "acc";

    @Value("${app.secret}")
    private String appSecret;

    @Value("${app.mm-cq.access-token-ttl}")
    private int tokenTtl;

    public String newAccessToken(int customerId, int storeId) {
        var algorithm = Algorithm.HMAC256(appSecret);

        return JWT.create()
                .withSubject(TYPE_ACCESS)
                .withClaim("uid", customerId)
                .withClaim("stid", storeId)
                .withExpiresAt(Instant.now().plusSeconds(tokenTtl))
                .sign(algorithm);
    }

    public Map<String, Claim> decodeAccessToken(String jwt) throws AppAuthException {

        DecodedJWT decodedJWT;
        try {
            var algorithm = Algorithm.HMAC256(appSecret);
            JWTVerifier verifier = JWT.require(algorithm).build();
            decodedJWT = verifier.verify(jwt);
        }
        catch (JWTVerificationException _) {
            throw new AppAuthException("JWT token is invalid or expired.");
        }

        if (decodedJWT.getSubject() == null || ! decodedJWT.getSubject().equals(TYPE_ACCESS)) {
            throw new AppAuthException("JWT token is not access token.");
        }

        return decodedJWT.getClaims();
    }
}
