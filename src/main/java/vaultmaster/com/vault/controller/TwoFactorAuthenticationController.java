package vaultmaster.com.vault.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vaultmaster.com.vault.service.TwoFactorAuthenticationService;

@RestController
@RequestMapping("/api/2fa")
public class TwoFactorAuthenticationController {

    private final TwoFactorAuthenticationService twoFactorAuthenticationService;

    // Constructor for dependency injection
    public TwoFactorAuthenticationController(TwoFactorAuthenticationService twoFactorAuthenticationService) {
        this.twoFactorAuthenticationService = twoFactorAuthenticationService;
    }

    /**
     * Endpoint to send a verification code to the user's email.
     *
     * @param userId The ID of the user to send the code to.
     * @param email  The email address to which the verification code will be sent.
     * @return ResponseEntity with a confirmation message.
     */
    @PostMapping("/send-code")
    public ResponseEntity<?> sendVerificationCode(
            @RequestParam Long userId,
            @RequestParam String email
    ) {
        twoFactorAuthenticationService.sendVerificationCode(userId, email);
        return ResponseEntity.ok("Verification code sent to " + email);
    }

    /**
     * Endpoint to verify the provided token for a user.
     *
     * @param userId The ID of the user attempting to verify.
     * @param token  The verification token provided by the user.
     * @return ResponseEntity with a success or error message.
     */
    @PostMapping("/verify")
    public ResponseEntity<?> verifyCode(
            @RequestParam Long userId,
            @RequestParam String token
    ) {
        boolean isVerified = twoFactorAuthenticationService.verifyCode(userId, token);

        if (isVerified) {
            return ResponseEntity.ok("Verification successful");
        } else {
            return ResponseEntity.status(401).body("Invalid or expired verification code");
        }
    }
}
