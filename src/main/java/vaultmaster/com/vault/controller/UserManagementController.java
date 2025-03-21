package vaultmaster.com.vault.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vaultmaster.com.vault.model.User;
import vaultmaster.com.vault.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import vaultmaster.com.vault.security.JwtService;


@RestController
@RequestMapping("/api/users")
public class UserManagementController {

    private final UserService userService;
    private final JwtService jwtService;  // Inject JwtService

    public UserManagementController(UserService userService, JwtService jwtService) {
        this.userService = userService;
        this.jwtService = jwtService;  // Assign injected JwtService to the class field
    }


    @GetMapping("/profile")
    public ResponseEntity<?> getUserProfile(HttpServletRequest request) {
        // Extract token from request
        String token = jwtService.extractTokenFromRequest(request);

        if (token == null || !jwtService.isTokenValid(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or expired token");
        }

        try {
            // Get the user based on the token
            User user = userService.getUserFromToken(token);

            // Return the user's profile response
            return ResponseEntity.ok(new UserProfileResponse(user));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error retrieving user profile: " + e.getMessage());
        }
    }

    // DTO for User Profile Response
    public static class UserProfileResponse {
        private String fullName;
        private String email;
        private String phoneNumber;

        public UserProfileResponse(User user) {
            this.fullName = user.getFullName();
            this.email = user.getEmail();
            this.phoneNumber = user.getPhoneNumber();
        }

        // Getters and Setters
        public String getFullName() {
            return fullName;
        }

        public void setFullName(String fullName) {
            this.fullName = fullName;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getPhoneNumber() {
            return phoneNumber;
        }

        public void setPhoneNumber(String phoneNumber) {
            this.phoneNumber = phoneNumber;
        }
    }
}

