package com.carddemo.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

/**
 * JWT token provider — issues and validates Bearer tokens.
 * Tokens carry userId, userType claims used for role-based access control.
 */
@Slf4j
@Component
public class JwtTokenProvider {

    private final SecretKey key;
    private final long      expirationMs;

    public JwtTokenProvider(
            @Value("${carddemo.jwt.secret}") String secret,
            @Value("${carddemo.jwt.expiration-ms}") long expirationMs) {
        byte[] keyBytes = Decoders.BASE64.decode(
                java.util.Base64.getEncoder().encodeToString(secret.getBytes()));
        this.key          = Keys.hmacShaKeyFor(keyBytes);
        this.expirationMs = expirationMs;
    }

    /** Generate a signed JWT for the given user. */
    public String generateToken(String userId, String userType) {
        Date now    = new Date();
        Date expiry = new Date(now.getTime() + expirationMs);

        return Jwts.builder()
                .subject(userId)
                .claim("userType", userType)
                .issuedAt(now)
                .expiration(expiry)
                .signWith(key)
                .compact();
    }

    /** Extract the userId (subject) from a token. */
    public String getUserId(String token) {
        return parseClaims(token).getSubject();
    }

    /** Extract the userType claim from a token. */
    public String getUserType(String token) {
        return (String) parseClaims(token).get("userType");
    }

    /** Return expiry instant from the token. */
    public Date getExpiry(String token) {
        return parseClaims(token).getExpiration();
    }

    /** Validate the token signature and expiry. Returns true if valid. */
    public boolean validate(String token) {
        try {
            parseClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException ex) {
            log.warn("JWT validation failed: {}", ex.getMessage());
            return false;
        }
    }

    private Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
