package vaultmaster.com.vault.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vaultmaster.com.vault.dto.LoginRequest;
import vaultmaster.com.vault.model.User;
import vaultmaster.com.vault.service.UserService;
import vaultmaster.com.vault.service.EmailService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth") // Common base path
public class LoginController {

    private final UserService userService;
    private final EmailService emailService;

    // Constructor injection
    public LoginController(UserService userService, EmailService emailService) {
        this.userService = userService;
        this.emailService = emailService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            // Extract email and password from the DTO
            String email = loginRequest.getEmail();
            String password = loginRequest.getPassword();

            // Authenticate the user
            User user = userService.login(email, password);
            if (user != null) {
                // Generate and send a verification code
                String verificationCode = userService.generateVerificationCode();
                emailService.sendEmail(user.getEmail(), "Verification Code", "Your code: " + verificationCode);

                return ResponseEntity.ok("Login successful. Verification email sent to " + email);
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid email or password.");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred during login: " + e.getMessage());
        }
    }
}
