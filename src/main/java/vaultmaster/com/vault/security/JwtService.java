package vaultmaster.com.vault.security; // or vaultmaster.com.vault.security, whichever you use

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;
import vaultmaster.com.vault.model.User;

import java.security.Key;
import java.util.Base64;
import java.util.Date;

@Service
public class JwtService {

    private static final String SECRET_KEY = System.getenv("JWT_SECRET");

    public String generateToken(User user) {
        if (SECRET_KEY == null) {
            throw new IllegalStateException("JWT Secret Key is not set in the environment variables.");
        }
        byte[] decodedKey = Base64.getDecoder().decode(SECRET_KEY);
        Key key = Keys.hmacShaKeyFor(decodedKey);

        return Jwts.builder()
                .setSubject(user.getEmail())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 86400000)) // 1 day expiration
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }
}
