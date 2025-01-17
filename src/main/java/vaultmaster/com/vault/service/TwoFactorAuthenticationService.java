package vaultmaster.com.vault.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import vaultmaster.com.vault.model.TwoFactorAuthentication;
import vaultmaster.com.vault.repository.TwoFactorAuthenticationRepository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

@Service
public class TwoFactorAuthenticationService {

    private final JavaMailSender mailSender;
    private final TwoFactorAuthenticationRepository twoFactorAuthenticationRepository;

    // Constructor injection
    public TwoFactorAuthenticationService(JavaMailSender mailSender, TwoFactorAuthenticationRepository twoFactorAuthenticationRepository) {
        this.mailSender = mailSender;
        this.twoFactorAuthenticationRepository = twoFactorAuthenticationRepository;
    }

    public void sendVerificationCode(Long userId, String email) {
        String token = generateVerificationCode();

        // Remove any existing token for the user
        twoFactorAuthenticationRepository.findByUserId(userId).ifPresent(auth -> {
            auth.setIsValid(false);
            twoFactorAuthenticationRepository.save(auth);
        });

        // Create and save a new token
        TwoFactorAuthentication auth = new TwoFactorAuthentication();
        auth.setUserId(userId);
        auth.setToken(token);
        auth.setExpires(LocalDateTime.now().plusMinutes(5));
        auth.setIsValid(true);
        twoFactorAuthenticationRepository.save(auth);

        // Send the token via email
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Your Verification Code");
        message.setText("Your verification code is: " + token);
        mailSender.send(message);
    }

    public boolean verifyCode(Long userId, String token) {
        Optional<TwoFactorAuthentication> authOptional = twoFactorAuthenticationRepository.findByUserId(userId);

        if (authOptional.isPresent()) {
            TwoFactorAuthentication auth = authOptional.get();
            if (auth.getToken().equals(token) && auth.getExpires().isAfter(LocalDateTime.now()) && auth.getIsValid()) {
                auth.setIsValid(false); // Invalidate token after successful use
                twoFactorAuthenticationRepository.save(auth);
                return true;
            }
        }

        return false;
    }

    private String generateVerificationCode() {
        Random random = new Random();
        int code = 100000 + random.nextInt(900000); // Generate a 6-digit random number
        return String.valueOf(code);
    }
}
