package vaultmaster.com.vault.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import java.security.Key;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;
import vaultmaster.com.vault.model.User;

import java.util.Date;
import java.util.UUID;

@Service
public class JwtService {
    private static final Key SECRET_KEY = Keys.secretKeyFor(SignatureAlgorithm.HS256);

    public String generateToken(User user) {
        return Jwts.builder()
                .setSubject(user.getUserId().toString())  // Store user ID in token
                .claim("email", user.getEmail())  // Optional: Store user email
                .setIssuedAt(new Date())  // Token issue time
                .setExpiration(new Date(System.currentTimeMillis() + 1000L * 60 * 60 * 24))  // 24-hour expiration
                .signWith(SECRET_KEY, SignatureAlgorithm.HS256)
                .compact();
    }

    public String generateTokenWithOtpFlag(UUID userId, boolean otpVerified) {
        return Jwts.builder()
                .setSubject(userId.toString())
                .claim("otpVerified", otpVerified)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000L * 60 * 60 * 24)) // 24 hours
                .signWith(SECRET_KEY, SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean extractOtpVerified(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(SECRET_KEY)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            return claims.get("otpVerified", Boolean.class) != null
                    && claims.get("otpVerified", Boolean.class);
        } catch (Exception e) {
            return false; // Treat invalid or missing claim as "not verified"
        }
    }

    public String extractTokenFromRequest(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("jwtToken".equals(cookie.getName())) {
                    return cookie.getValue();  // Found token in cookie
                }
            }
        }
        return null;  // Token not found
    }

    public boolean isTokenValid(String token) {
        try {
            // Try parsing and verifying the token
            Jwts.parserBuilder()
                    .setSigningKey(SECRET_KEY)
                    .build()
                    .parseClaimsJws(token);  // Throws exception if token is invalid
            return true;
        } catch (Exception e) {
            return false;  // Token is invalid or expired
        }
    }

    public String extractUserId(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(SECRET_KEY)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();  // Extracts the user ID from token
    }

    // New method to get the authenticated userId directly
    public String getAuthenticatedUserId(HttpServletRequest request) {
        String token = extractTokenFromRequest(request);
        if (token != null && isTokenValid(token)) {
            return extractUserId(token);  // Extract the user ID from the token
        }
        throw new IllegalArgumentException("Token is invalid or missing");
    }

    public String extractEmail(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(SECRET_KEY)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            return claims.get("email", String.class);  // Extract email from claims
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid or expired token", e);
        }
    }
}
