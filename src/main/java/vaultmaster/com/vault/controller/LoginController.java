package vaultmaster.com.vault.controller;

import vaultmaster.com.vault.model.User;
import vaultmaster.com.vault.service.UserService;
import vaultmaster.com.vault.service.EmailService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class LoginController {

    private final UserService userService;
    private final EmailService emailService;

    // Constructor for dependency injection
    public LoginController(UserService userService, EmailService emailService) {
        this.userService = userService;
        this.emailService = emailService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(
            @RequestParam String email,
            @RequestParam String password
    ) {
        try {
            // Call the instance method login
            User user = userService.login(email, password);
            if (user != null) {
                // Call the instance method generateVerificationCode
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
