/*
package vaultmaster.com.vault.service;

import org.springframework.stereotype.Service;
import vaultmaster.com.vault.model.TwoFactorAuth;
import vaultmaster.com.vault.repository.TwoFactorAuthRepository;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class TwoFactorAuthService {

    private final TwoFactorAuthRepository twoFactorAuthRepository;
    private final EmailService emailService; // Optional if you're using email for OTPs

    public TwoFactorAuthService(TwoFactorAuthRepository twoFactorAuthRepository, EmailService emailService) {
        this.twoFactorAuthRepository = twoFactorAuthRepository;
        this.emailService = emailService;
    }

    // Generate a random 6-digit OTP
    private String generateOTP() {
        SecureRandom random = new SecureRandom();
        int otp = 100000 + random.nextInt(900000);
        return String.valueOf(otp);
    }

    // Generate and send OTP
    public void generateAndSendOTP(UUID userId, String contactMethod) {
        // Generate OTP
        String otp = generateOTP();

        // Save OTP in the database
        TwoFactorAuth twoFactorAuth = new TwoFactorAuth();
        twoFactorAuth.setAuthId(UUID.randomUUID());
        twoFactorAuth.setUserId(userId);
        twoFactorAuth.setToken(otp);
        twoFactorAuth.setExpiresAt(LocalDateTime.now().plusMinutes(5)); // OTP expires in 5 minutes
        twoFactorAuth.setValid(true);
        twoFactorAuth.setCreatedAt(LocalDateTime.now());

        twoFactorAuthRepository.save(twoFactorAuth);

        // Send OTP via email or SMS
        if (contactMethod.contains("@")) {
            // Send via email
            emailService.sendEmail(contactMethod, "Your OTP Code", "Your OTP is: " + otp);
        } else {
            // Send via SMS (e.g., using Twilio)
            // twilioService.sendSMS(contactMethod, "Your OTP is: " + otp);
        }
    }

    // Verify the OTP
    public boolean verifyOTP(UUID userId, String otp) {
        Optional<TwoFactorAuth> authOptional = twoFactorAuthRepository.findByUserId(userId);

        if (authOptional.isPresent()) {
            TwoFactorAuth auth = authOptional.get();
            if (auth.getToken().equals(otp) &&
                    auth.getExpiresAt().isAfter(LocalDateTime.now()) &&
                    auth.isValid()) {
                auth.setValid(false); // Invalidate the OTP after successful verification
                twoFactorAuthRepository.save(auth);
                return true;
            }
        }
        return false; // OTP is invalid or expired
    }
}
*/