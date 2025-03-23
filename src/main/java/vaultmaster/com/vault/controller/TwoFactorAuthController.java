package vaultmaster.com.vault.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vaultmaster.com.vault.service.TwoFactorAuthService;
import vaultmaster.com.vault.security.JwtService;
import java.util.UUID;

@RestController
@RequestMapping("/api/2fa")
public class TwoFactorAuthController {

    private final TwoFactorAuthService twoFactorAuthService;
    private final JwtService jwtService;

    public TwoFactorAuthController(TwoFactorAuthService twoFactorAuthService, JwtService jwtService) {
        this.twoFactorAuthService = twoFactorAuthService;
        this.jwtService = jwtService;
    }

    // Endpoint to send OTP via email
    @PostMapping("/send-otp")
    public ResponseEntity<String> sendOtp(@RequestParam String email, HttpServletRequest request) {
        try {
            // Get the userId from the authenticated user's JWT token
            String userId = jwtService.getAuthenticatedUserId(request); // Now 'request' is passed here
            twoFactorAuthService.generateAndSendOTP(UUID.fromString(userId), email);
            return ResponseEntity.ok("OTP sent successfully to " + email);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Failed to send OTP: " + e.getMessage());
        }
    }

    @PostMapping("/verify")
    public ResponseEntity<String> verifyOtp(@RequestParam String otp, HttpServletResponse response, HttpServletRequest request) {
        try {
            // Get the userId from the authenticated user's JWT token
            String userId = jwtService.getAuthenticatedUserId(request); // Now 'request' is passed here
            boolean isValid = twoFactorAuthService.verifyOTP(UUID.fromString(userId), otp);

            if (isValid) {
                // Generate new token and set it as a cookie
                String newToken = jwtService.generateTokenWithOtpFlag(UUID.fromString(userId), true);
                ResponseCookie jwtCookie = ResponseCookie.from("jwtToken", newToken)
                        .httpOnly(true)
                        .secure(true)
                        .path("/")
                        .maxAge(24 * 60 * 60)
                        .sameSite("Strict")
                        .build();

                response.addHeader(HttpHeaders.SET_COOKIE, jwtCookie.toString());

                return ResponseEntity.ok("OTP verified and session unlocked.");
            } else {
                return ResponseEntity.status(401).body("Invalid or expired OTP.");
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Internal error: " + e.getMessage());
        }
    }

}
