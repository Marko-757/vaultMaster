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
    private final EmailService emailService;

    public TwoFactorAuthService(TwoFactorAuthRepository twoFactorAuthRepository, EmailService emailService) {
        this.twoFactorAuthRepository = twoFactorAuthRepository;
        this.emailService = emailService;
    }

    private String generateOTP() {
        SecureRandom random = new SecureRandom();
        int otp = 100000 + random.nextInt(900000);
        return String.valueOf(otp);
    }

    public void generateAndSendOTP(UUID userId, String email) {
        String otp = generateOTP();

        TwoFactorAuth auth = TwoFactorAuth.builder()
                .userId(userId)
                .token(otp)
                .expiresAt(LocalDateTime.now().plusMinutes(5))
                .isValid(true)
                .createdAt(LocalDateTime.now())
                .build();

        twoFactorAuthRepository.save(auth); // Don't set authId manually


        // Send OTP to user's email
        String subject = "Your VaultMaster OTP Code";
        String body = "Your OTP code is: " + otp + "\nThis code is valid for 5 minutes.";
        emailService.sendEmail(email, subject, body);
    }

    public boolean verifyOTP(UUID userId, String otp) {
        Optional<TwoFactorAuth> authOptional = twoFactorAuthRepository.findTopByUserIdOrderByCreatedAtDesc(userId);
        if (authOptional.isPresent()) {
            TwoFactorAuth auth = authOptional.get();

            boolean isValid = auth.isValid()
                    && auth.getToken().equals(otp)
                    && LocalDateTime.now().isBefore(auth.getExpiresAt());

            if (isValid) {
                auth.setValid(false); // Invalidate after use
                twoFactorAuthRepository.save(auth);
                return true;
            }
        }

        return false;
    }

}
