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

    /**
     * Handles user login.
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            AuthResponse authResponse = userService.login(request.getEmail(), request.getPassword());
            return ResponseEntity.ok(authResponse); //
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }


    /**
     * Verify user registration.
     /**
     */
    @GetMapping("/verify")
    public ResponseEntity<String> verifyUser(@RequestParam String token) {
        boolean isVerified = userService.verifyUser(token);

        if (isVerified) {
            return ResponseEntity.ok("User verified successfully.");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid or expired verification token.");
        }
    }

}
