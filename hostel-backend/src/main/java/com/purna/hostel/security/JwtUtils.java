package com.purna.hostel.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

@Component
public class JwtUtils {

    // 🔐 256-bit secret (KEEP SAFE)
    private static final String JWT_SECRET =
            "hostel_management_system_secret_key_1234567890_secure";

    // ⏳ 24 hours
    private static final long JWT_EXPIRATION_MS = 24 * 60 * 60 * 1000;

    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(JWT_SECRET.getBytes(StandardCharsets.UTF_8));
    }

    // =========================
    // ✅ GENERATE TOKEN (EMAIL + ROLE)
    // =========================
    public String generateToken(String email, String role) {

        return Jwts.builder()
                .setSubject(email)               // ✅ email as identity
                .claim("role", role)             // ROLE_STUDENT / ROLE_WARDEN / ROLE_ADMIN
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + JWT_EXPIRATION_MS))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    // =========================
    // ✅ EXTRACT EMAIL
    // =========================
    public String extractEmail(String token) {
        return getClaims(token).getSubject();
    }

    // =========================
    // ✅ EXTRACT ROLE
    // =========================
    public String extractRole(String token) {
        return getClaims(token).get("role", String.class);
    }

    // =========================
    // ✅ VALIDATE TOKEN
    // =========================
    public boolean validateToken(String token) {
        try {
            getClaims(token);
            return true;
        } catch (ExpiredJwtException e) {
            System.out.println("❌ JWT expired");
        } catch (UnsupportedJwtException e) {
            System.out.println("❌ JWT unsupported");
        } catch (MalformedJwtException e) {
            System.out.println("❌ JWT malformed");
        } catch (SecurityException e) {
            System.out.println("❌ JWT signature invalid");
        } catch (IllegalArgumentException e) {
            System.out.println("❌ JWT empty");
        }
        return false;
    }

    // =========================
    // 🔒 INTERNAL
    // =========================
    private Claims getClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
