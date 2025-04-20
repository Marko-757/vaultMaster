package vaultmaster.com.vault.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;
import vaultmaster.com.vault.model.User;

import java.security.Key;
import java.util.Date;
import java.util.UUID;

@Service
public class JwtService {
    private static final Key SECRET_KEY = Keys.secretKeyFor(SignatureAlgorithm.HS256);

    public String generateToken(User user) {
        return Jwts.builder()
                .setSubject(user.getUserId().toString())
                .claim("email", user.getEmail())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000L * 60 * 60 * 24)) // 24 hours
                .signWith(SECRET_KEY, SignatureAlgorithm.HS256)
                .compact();
    }

    public String generateTokenWithOtpFlag(UUID userId, String email, boolean otpVerified) {
        return Jwts.builder()
                .setSubject(userId.toString())
                .claim("email", email)  // ✅ include email for post-OTP flow
                .claim("otpVerified", otpVerified)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000L * 60 * 60 * 24))
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
            return claims.get("otpVerified", Boolean.class) != null &&
                    claims.get("otpVerified", Boolean.class);
        } catch (Exception e) {
            return false;
        }
    }

    public String extractTokenFromRequest(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("jwtToken".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    public boolean isTokenValid(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(SECRET_KEY)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public String extractUserId(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(SECRET_KEY)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public String getAuthenticatedUserId(HttpServletRequest request) {
        String token = extractTokenFromRequest(request);
        if (token != null && isTokenValid(token)) {
            return extractUserId(token);
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
            return claims.get("email", String.class);
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid or expired token", e);
        }
    }

    public UUID getAuthenticatedUserIdAsUUID(HttpServletRequest request) {
        return UUID.fromString(getAuthenticatedUserId(request));
    }

    public String getAuthenticatedEmail(HttpServletRequest request) {
        String token = extractTokenFromRequest(request);
        if (token != null && isTokenValid(token)) {
            return extractEmail(token);
        }
        throw new IllegalArgumentException("Token is invalid or missing");
    }
}
