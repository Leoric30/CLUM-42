package com.clum.clum.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * Servicio para generar y validar JSON Web Tokens (JWT).
 *
 * Usa JJWT 0.12.x con firma HMAC-SHA256.
 * El secreto y la duración se configuran en application.properties:
 *   jwt.secret          — mínimo 32 caracteres para HS256
 *   jwt.expiration-ms   — 86400000 = 24 horas
 */
@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration-ms}")
    private long expirationMs;

    /**
     * Genera un JWT firmado para el email dado.
     * El token contiene:
     *   - subject: email del usuario
     *   - issuedAt: timestamp de creación
     *   - expiration: creación + expirationMs
     */
    public String generateToken(String email) {
        Date now = new Date();
        return Jwts.builder()
                .subject(email)
                .issuedAt(now)
                .expiration(new Date(now.getTime() + expirationMs))
                .signWith(getSigningKey())
                .compact();
    }

    /**
     * Extrae el email (subject) del token.
     * Lanza JwtException si el token es inválido o expiró.
     */
    public String extractEmail(String token) {
        return parseClaims(token).getSubject();
    }

    /**
     * Retorna true solo si el token tiene firma válida y no expiró.
     * No lanza excepción — los errores se absorben silenciosamente.
     */
    public boolean isTokenValid(String token) {
        try {
            parseClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    private Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }
}
