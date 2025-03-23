package vaultmaster.com.vault.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import vaultmaster.com.vault.dto.LoginRequest;
import vaultmaster.com.vault.dto.AuthResponse;
import vaultmaster.com.vault.model.User;
import vaultmaster.com.vault.security.JwtService;
import vaultmaster.com.vault.service.TwoFactorAuthService;
import vaultmaster.com.vault.service.UserService;
import java.util.Optional;
import java.util.UUID;


@RestController
@RequestMapping("/api/auth")
public class UserController {

    private final UserService userService;
    private final JwtService jwtService;
    private final TwoFactorAuthService twoFactorAuthService;


    @Autowired
    public UserController(UserService userService, JwtService jwtService, TwoFactorAuthService twoFactorAuthService) {
        this.userService = userService;
        this.jwtService = jwtService;
        this.twoFactorAuthService = twoFactorAuthService;
    }
    /**
     * Register a new user.
     */
    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody User user) {
        System.out.println("Received Registration Request: " + user.toString());

        if (user.getPasswordHash() == null || user.getPasswordHash().isEmpty()) {
            System.out.println("Error: Password is NULL or empty!");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: Password is NULL or empty!");
        }

        userService.registerUser(user.getEmail(), user.getPasswordHash(), user.getFullName(), user.getPhoneNumber());
        return ResponseEntity.ok("User registered successfully!");
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request, HttpServletResponse response) {
        try {
            AuthResponse authResponse = userService.login(request.getEmail(), request.getPassword());

            // Send OTP to user's email using their ID and email from DB
            twoFactorAuthService.generateAndSendOTP(authResponse.getUserId(), authResponse.getEmail());

            // Generate a JWT with otpVerified = false
            String jwtWithOtpFlag = jwtService.generateTokenWithOtpFlag(authResponse.getUserId(), false);

            // Create HTTP-only JWT cookie
            ResponseCookie jwtCookie = ResponseCookie.from("jwtToken", jwtWithOtpFlag)
                    .httpOnly(true)
                    .secure(true)
                    .path("/")
                    .maxAge(24 * 60 * 60)
                    .sameSite("Strict")
                    .build();

            response.addHeader(HttpHeaders.SET_COOKIE, jwtCookie.toString());

            return ResponseEntity.ok("OTP sent to your email. Please verify to complete login.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid email or password.");
        }
    }



    /**
     * Logs out the user by clearing the JWT cookie.
     */
    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletResponse response) {
        ResponseCookie jwtCookie = ResponseCookie.from("jwtToken", "")
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(0)
                .sameSite("Strict")
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, jwtCookie.toString());
        return ResponseEntity.ok("Logged out successfully.");
    }


    @GetMapping("/verify")
    public ResponseEntity<String> verifyUser(@RequestParam String token) {
        boolean isVerified = userService.verifyUser(token);

        if (isVerified) {
            return ResponseEntity.ok("User verified successfully.");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid or expired verification token.");
        }
    }

    @GetMapping("/me")
    public ResponseEntity<?> getUserFromToken(HttpServletRequest request) {
        // Extract token from cookie
        String token = jwtService.extractTokenFromRequest(request);

        if (token == null || !jwtService.isTokenValid(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
        }

        // Extract user ID from the token
        String userId = jwtService.extractUserId(token);

        // Fetch user from the database using user ID
        Optional<User> user = userService.getUserById(UUID.fromString(userId));
        if (user.isPresent()) {
            return ResponseEntity.ok(user.get());
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not found");
        }
    }
}
