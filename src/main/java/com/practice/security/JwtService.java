package com.practice.security;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.practice.entity.User;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {

    private final SecretKey secretKey;
    private final long expiration;

    public JwtService(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.expiration}") long expiration) {

        this.secretKey = Keys.hmacShaKeyFor(
                secret.getBytes(StandardCharsets.UTF_8)
        );

        this.expiration = expiration;
    }

    // =========================================================
    // GENERATE TOKEN
    // =========================================================

    public String generateToken(User user) {

        Date now = new Date();

        Date expiry = new Date(
                now.getTime() + expiration
        );

        return Jwts.builder()
                .subject(user.getEmail())
                .claim("role", user.getRole())
                .issuedAt(now)
                .expiration(expiry)
                .signWith(secretKey)
                .compact();
    }

    // =========================================================
    // GET CLAIMS
    // =========================================================

    private Claims getClaims(String token) {

        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    // =========================================================
    // EXTRACT EMAIL
    // =========================================================

    public String extractEmail(String token) {

        return getClaims(token).getSubject();
    }

    // =========================================================
    // EXTRACT ROLE
    // =========================================================

    public String extractRole(String token) {

        String role = getClaims(token)
                .get("role", String.class);

        if (role == null) {
            return "CUSTOMER";
        }

        return role;
    }

    // =========================================================
    // VALIDATE TOKEN
    // =========================================================

    public boolean isTokenValid(String token) {

        try {

            Claims claims = getClaims(token);

            Date expirationDate =
                    claims.getExpiration();

            return expirationDate != null
                    && expirationDate.after(new Date());

        } catch (Exception e) {

            return false;
        }
    }
}