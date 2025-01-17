package vaultmaster.com.vault.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vaultmaster.com.vault.model.User;
import vaultmaster.com.vault.service.EmailService;
import vaultmaster.com.vault.service.UserService;

@RestController
@RequestMapping("/api/auth")
public class UserController {

    private final UserService userService;
    private final EmailService emailService;

    // Constructor injection
    public UserController(UserService userService, EmailService emailService) {
        this.userService = userService;
        this.emailService = emailService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(
            @RequestParam String email,
            @RequestParam String password,
            @RequestParam String fullName,
            @RequestParam String phoneNumber
    ) {
        try {
            User newUser = userService.registerUser(email, password, fullName, phoneNumber);
            return ResponseEntity.ok("User registered successfully: " + newUser.getEmail());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(400).body(e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(
            @RequestParam String email,
            @RequestParam String password
    ) {
        try {
            User user = userService.login(email, password);

            if (user != null) {
                String verificationCode = userService.generateVerificationCode();
                emailService.sendEmail(user.getEmail(), "Verification Code", "Your code: " + verificationCode);

                return ResponseEntity.ok("Login successful. Verification email sent to " + email);
            } else {
                return ResponseEntity.status(401).body("Invalid email or password.");
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).body("An error occurred during login: " + e.getMessage());
        }
    }
}
