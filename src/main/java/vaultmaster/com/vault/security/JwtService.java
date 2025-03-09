package vaultmaster.com.vault.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import org.springframework.stereotype.Service;
import vaultmaster.com.vault.model.User;

import java.util.Date;

@Service
public class JwtService {
    private static final Key SECRET_KEY = Keys.secretKeyFor(SignatureAlgorithm.HS256);  // ✅ Generates a 256-bit key automatically

    public String generateToken(User user) {
        return Jwts.builder()
                .setSubject(user.getUserId().toString())  // Store user ID in token
                .claim("email", user.getEmail())  // Optional: Store user email
                .setIssuedAt(new Date())  // Token issue time
                .setExpiration(new Date(System.currentTimeMillis() + 1000L * 60 * 60 * 24))  // 24-hour expiration
                .signWith(SECRET_KEY, SignatureAlgorithm.HS256)  // ✅ Secure key
                .compact();
    }

// 🔹 Validate the token and extract the user ID
    public String extractUserId(String token) {
        return Jwts.parserBuilder()
            .setSigningKey(SECRET_KEY)  // ✅ Use the correct Key object
            .build()
            .parseClaimsJws(token)
            .getBody()
            .getSubject();  // Extract user ID from token
    }

}
