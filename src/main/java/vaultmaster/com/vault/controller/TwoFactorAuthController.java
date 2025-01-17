/*package vaultmaster.com.vault.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vaultmaster.com.vault.service.TwoFactorAuthService;

import java.util.UUID;

@RestController
@RequestMapping("/api/2fa")
public class TwoFactorAuthController {

    private final TwoFactorAuthService twoFactorAuthService;

    public TwoFactorAuthController(TwoFactorAuthService twoFactorAuthService) {
        this.twoFactorAuthService = twoFactorAuthService;
    }

    @PostMapping("/send-otp")
    public ResponseEntity<?> sendOTP(@RequestParam UUID userId, @RequestParam String contactMethod) {
        try {
            twoFactorAuthService.generateAndSendOTP(userId, contactMethod);
            return ResponseEntity.ok("OTP sent successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to send OTP.");
        }
    }

    @PostMapping("/verify")
    public ResponseEntity<?> verifyOTP(@RequestParam UUID userId, @RequestParam String otp) {
        boolean isValid = twoFactorAuthService.verifyOTP(userId, otp);

        if (isValid) {
            return ResponseEntity.ok("OTP verified successfully.");
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or expired OTP.");
        }
    }
}
 */
