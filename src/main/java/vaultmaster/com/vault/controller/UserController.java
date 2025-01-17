package vaultmaster.com.vault.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vaultmaster.com.vault.model.User;
import vaultmaster.com.vault.service.UserService;

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
    public ResponseEntity<?> register(@RequestBody User user) {
        try {
            // Call the service method to register the user and generate a verification token
            String verificationToken = userService.registerUserWithVerification(
                    user.getEmail(),
                    user.getPasswordHash(),
                    user.getFullName(),
                    user.getPhoneNumber(),
                    "System" // Created by system for self-registration
            );

            return ResponseEntity.ok("User registered successfully. Please check your email for the verification link.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred during registration.");
        }
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
}
