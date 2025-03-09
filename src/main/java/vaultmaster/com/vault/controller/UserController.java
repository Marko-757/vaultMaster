package vaultmaster.com.vault.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vaultmaster.com.vault.dto.LoginRequest;
import vaultmaster.com.vault.dto.AuthResponse;
import vaultmaster.com.vault.model.User;
import vaultmaster.com.vault.service.UserService;
import vaultmaster.com.vault.dto.AuthResponse;

import jakarta.validation.Valid;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/auth")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Register a new user with verification.
     */
    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody User user) {
        System.out.println("Received Registration Request: " + user.toString()); // 🔍 Log incoming user data

        if (user.getPasswordHash() == null || user.getPasswordHash().isEmpty()) {
            System.out.println("Error: Password is NULL or empty!"); // 🚨 Check for empty passwords
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: Password is NULL or empty!");
        }

        userService.registerUser(user.getEmail(), user.getPasswordHash(), user.getFullName(), user.getPhoneNumber(), null);
        return ResponseEntity.ok("User registered successfully!");
    }




    /**
     * Verify user registration.
     */
    @GetMapping("/verify")
    public ResponseEntity<?> verifyUser(@RequestParam String token) {
        try {
            boolean isVerified = userService.verifyUser(token);
            if (isVerified) {
                return ResponseEntity.ok("User verified successfully.");
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid or expired verification token.");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred during verification.");
        }
    }

    /**
     * User Login - Authenticate and return JWT Token.
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        Optional<User> userOptional = userService.getUserByEmail(email);

        if (userOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid email or password");
        }

        User user = userOptional.get();
        return ResponseEntity.ok(new AuthResponse(user, null));  // ✅ Send hashed password but no token yet
    }



}
