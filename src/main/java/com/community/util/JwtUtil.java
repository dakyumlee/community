package com.community.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtUtil {
    
    private final String secretKey = "mySecretKey123456789012345678901234567890";
    private final long expiration = 24 * 60 * 60 * 1000;
    
    public String generateToken(String email, Long userId, boolean isAdmin) {
        return Jwts.builder()
            .setSubject(email)
            .claim("userId", userId)
            .claim("isAdmin", isAdmin)
            .setIssuedAt(new Date())
            .setExpiration(new Date(System.currentTimeMillis() + expiration))
            .signWith(SignatureAlgorithm.HS512, secretKey)
            .compact();
    }
    
    public String extractEmail(String token) {
        return extractClaims(token).getSubject();
    }
    
    public Long extractUserId(String token) {
        return extractClaims(token).get("userId", Long.class);
    }
    
    public boolean extractIsAdmin(String token) {
        Boolean isAdmin = extractClaims(token).get("isAdmin", Boolean.class);
        return isAdmin != null ? isAdmin : false;
    }
    
    public boolean isTokenValid(String token) {
        try {
            extractClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    private Claims extractClaims(String token) {
        return Jwts.parser()
            .setSigningKey(secretKey)
            .build()
            .parseClaimsJws(token)
            .getBody();
    }
}